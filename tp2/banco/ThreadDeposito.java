package tp2.banco;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ThreadDeposito implements Runnable {
	
	protected Socket s;
	protected ObjectOutputStream outStrm;
	protected ObjectInputStream inStrm;
	
	protected boolean conectado=true;
	protected Semaphore semaforoArchivo;
	
	protected static Integer tiempoEspera=2000;
	
	public ThreadDeposito(Socket so) {
		this.s=so;
		try {
			this.inStrm=new ObjectInputStream( this.s.getInputStream() );
			this.outStrm=new ObjectOutputStream( this.s.getOutputStream() );
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		MensajeBanco msj=null;
		try{
			while(conectado){
				try {
					msj = (MensajeBanco) this.inStrm.readObject();
				} catch (ClassNotFoundException | IOException e) {
					System.out.println("+ ThreadDeposito: error al leer el objeto: ");
					e.printStackTrace();
					this.cerrarConexion();
					conectado=false;
				}
				
				if(msj.getOperacion()!=Operacion.Deposito && msj.getOperacion()!=Operacion.Fin){
					msj.setOperacion(Operacion.Error);
					msj.setError("Operacion incorrecta, yo soy el servidor de deposito!");
					try {
						this.outStrm.writeObject(msj);
					} catch (IOException e) {
						System.out.println("ThreadDeposito: no pude mandar msj de error!");
						e.printStackTrace();
						this.cerrarConexion();
						conectado=false;
					}
				}else if(msj.getOperacion()==Operacion.Fin){
					this.cerrarConexion();
					conectado=false;
				}
				else{
					Integer monto=0;
					if(msj.getMonto()<0){
						monto=msj.getMonto()*-1;
					}else{
						monto=msj.getMonto();
					}
					System.out.println("estoy buscando el id: "+msj.getId()+" en el archivo para sumarle: "+msj.getMonto());
					//le envio el monto anterior de esa cuenta
					msj =ManejadorArchivo.buscarDirectoPorID(msj.getId(), ManejadorArchivo.pathEJ2y3);
					msj.setOperacion(Operacion.Consulta);
					try {
						this.outStrm.writeObject(msj);
					} catch (IOException e) {
						System.out.println("ThreadDeposito: no pude mandarle el dato de la consulta");
						e.printStackTrace();
						this.cerrarConexion();
						conectado=false;
					}
					//modifico el monto de la cuenta
					if(!ManejadorArchivo.escribirMontoThreadSafe(msj.getId(), monto, ManejadorArchivo.pathEJ2y3, ThreadDeposito.tiempoEspera)){
						msj.setError("ThreadDeposito: no hacer el deposito :(");
						msj.setOperacion(Operacion.Error);
					}else{
						msj =ManejadorArchivo.buscarDirectoPorID(msj.getId(), ManejadorArchivo.pathEJ2y3);
						msj.setOperacion(Operacion.Consulta);
					}
					//le envio el nuevo monto
					try {
						this.outStrm.writeObject(msj);
					} catch (IOException e) {
						System.out.println("ThreadDeposito: no pude enviarle el nuevo salgo de la cuenta");
						e.printStackTrace();
						this.cerrarConexion();
						conectado=false;
					}
				}
			}//fin while
		}catch(Exception e1){
			this.cerrarConexion();
		}
		System.out.println("ThreadDeposito: cerrando conexion");
	}//fin metodo

	protected void cerrarConexion() {
		try {
			if(this.inStrm!=null){
				this.inStrm.close();
			}
			if(this.outStrm!=null){
				this.outStrm.close();
			}
			if(this.s!=null){
				this.s.close();
			}
		} catch (IOException e) {
			//no me interesan los errores q haya aca
		}
	}

}
