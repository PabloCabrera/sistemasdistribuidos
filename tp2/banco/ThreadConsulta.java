package tp2.banco;

import java.io.IOException;
import java.net.Socket;

public class ThreadConsulta extends ThreadDeposito {
	
	protected static final String pathArchivo="backup/backup-Cuentas.bin";
	
	public ThreadConsulta(Socket so) {
		super(so);
	}

	@Override
	public void run() {
		MensajeBanco msj=null;
		try{
			while(conectado){
				try {
					msj = (MensajeBanco) this.inStrm.readObject();
				} catch (ClassNotFoundException | IOException e) {
					System.out.println("+ ThreadConsulta: error al leer el objeto: ");
					e.printStackTrace();
					this.cerrarConexion();
					conectado=false;
				}
				
				if(msj.getOperacion()!=Operacion.Consulta && msj.getOperacion()!=Operacion.Fin){
					msj.setOperacion(Operacion.Error);
					msj.setError("Operacion incorrecta, yo soy el servidor de consultas!");
					try {
						this.outStrm.writeObject(msj);
					} catch (IOException e) {
						System.out.println("ThreadConsulta: no pude mandar msj de error!");
						e.printStackTrace();
						this.cerrarConexion();
						conectado=false;
					}
				}else if(msj.getOperacion()==Operacion.Fin){
					this.cerrarConexion();
					conectado=false;
				}
				else{
					if(msj.getId()<10){
						msj =ManejadorArchivo.buscarDirectoPorID(msj.getId(), pathArchivo);
						msj.setOperacion(Operacion.Consulta);
						try {
							this.outStrm.writeObject(msj);
						} catch (IOException e) {
							System.out.println("ThreadConsulta: no pude mandarle el dato de la consulta");
							e.printStackTrace();
							this.cerrarConexion();
							conectado=false;
						}
					}else{
						for(int i=0;i<10;i++){
							try {
								msj =ManejadorArchivo.buscarDirectoPorID(i, pathArchivo);
								msj.setOperacion(Operacion.Consulta);
								this.outStrm.writeObject(msj);
							} catch (IOException e) {
								System.out.println("ThreadConsulta: no pude mandarle el dato de la consulta");
								e.printStackTrace();
								this.cerrarConexion();
								conectado=false;
								i=11;//para salir del for
							}
						}
					}
					
				}
			}//fin while
		}catch(Exception e1){
			this.cerrarConexion();
		}
		System.out.println("ThreadConsulta: cerrando conexion");
	}//fin metodo
	
	
}
