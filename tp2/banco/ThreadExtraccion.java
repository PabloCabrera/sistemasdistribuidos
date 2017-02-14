package tp2.banco;

import java.io.IOException;
import java.net.Socket;

public class ThreadExtraccion extends ThreadDeposito {
	
	protected static Integer TiempoEspera=1500;
	
	public ThreadExtraccion(Socket so) {
		super(so);
	}
	
	@Override
	public void run() {
		MensajeBanco msj=null;
		while(conectado){
			try {
				msj = (MensajeBanco) this.inStrm.readObject();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("+ ThreadExtraccion: error al leer el objeto: ");
				e.printStackTrace();
				this.cerrarConexion();
				conectado=false;
			}
			
			if(msj.getOperacion()!=Operacion.Extraccion && msj.getOperacion()!=Operacion.Fin){
				msj.setOperacion(Operacion.Error);
				msj.setError("Operacion incorrecta, yo soy el servidor de deposito!");
				try {
					this.outStrm.writeObject(msj);
				} catch (IOException e) {
					System.out.println("ThreadExtraccion: no pude mandar msj de error!");
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
				if(msj.getMonto()>0){
					monto=msj.getMonto()*-1;
				}else{
					monto=msj.getMonto();
				}
				//le envio el monto anterior de esa cuenta
				msj =ManejadorArchivo.buscarDirectoPorID(msj.getId(), ManejadorArchivo.pathEJ2y3);
				msj.setOperacion(Operacion.Consulta);
				try {
					this.outStrm.writeObject(msj);
				} catch (IOException e) {
					System.out.println("ThreadExtraccion: no pude mandarle el dato de la consulta");
					e.printStackTrace();
					this.cerrarConexion();
					conectado=false;
				}
				//modifico el monto de la cuenta
				if(!ManejadorArchivo.escribirMontoThreadSafe(msj.getId(), monto, ManejadorArchivo.pathEJ2y3, ThreadExtraccion.tiempoEspera)){
					msj.setError("ThreadExtraccion: no hacer la extraccion :(");
					msj.setOperacion(Operacion.Error);
				}else{
					msj =ManejadorArchivo.buscarDirectoPorID(msj.getId(), ManejadorArchivo.pathEJ2y3);
					msj.setOperacion(Operacion.Consulta);
				}
				//le envio el nuevo monto
				try {
					this.outStrm.writeObject(msj);
				} catch (IOException e) {
					System.out.println("ThreadExtraccion: no pude enviarle el nuevo salgo de la cuenta");
					e.printStackTrace();
					this.cerrarConexion();
					conectado=false;
				}
			}
		}//fin while
		System.out.println("ThreadExtraccion: cerrando conexion");
	}//fin metodo
	
}
