package tp2.balancecarga;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import tp2.otros.Servidor;

public class ServidorBalanceCarga extends Servidor {
	
	protected Integer nrohilo=0;
	protected ArrayList<InfoServidor> servidoresDisponibles= new ArrayList<InfoServidor>();
	protected Integer ultimoServidorConectado=0;
	protected ServicioRecepcionServidores receptorMjsUdp;
	
	public static void main(String[] args) {
		try {
			ServidorBalanceCarga server = new ServidorBalanceCarga(6000);
			new Thread(server).start();
		} catch (IOException e) {
			System.out.println("no pude iniciar el servidor de balance de carga");
			e.printStackTrace();
		}
	}
	
	public ServidorBalanceCarga() throws IOException {
		this(6000);
	}

	public ServidorBalanceCarga(int i) throws IOException {
		super(i);
		this.nombre="servidor balance de carga";
	}
	
	@Override
	public void run(){
		//metodo que crea un hilo que espera mensajes UDP de parte de los servidores
		//los agrega a la lista de servidores, y crea un hilo que cheque que los servidores estan vivos
		if(this.levantarBuscardorServidores()){
			//una vez tengo este servicio levantado, puedo funcionar como balanceador de carga y levanto el servicio
			this.levantarServicio();
		}else{
			System.out.println("Error al levantar el servicio buscador de servidores");
		}
		
	}

	protected boolean levantarBuscardorServidores() {
		try {
			this.receptorMjsUdp = new ServicioRecepcionServidores();
			//le paso mi lista de servidores para que le vaya agregando servidores a medida que recibe msj UDP
			this.receptorMjsUdp.setServidores(this.servidoresDisponibles);
			new Thread(this.receptorMjsUdp).start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	protected void recibiConexion(Socket so){
		System.out.println("+ "+this.nombre+": recibi una conexion desde "+so.getInetAddress()+":"+so.getPort());
		//busco al servidor al q menos le di
		boolean asignado=false;
		while(!asignado && this.servidoresDisponibles.size()>0){
			int subindex= this.buscarServidorMenosConexiones();
			ThreadBalanceCarga thread = new ThreadBalanceCarga(so,this.nrohilo,
						this.servidoresDisponibles.get(subindex).getIp(),this.servidoresDisponibles.get(subindex).getPuerto());
			if(thread.ConectarseAServidor()){
				Thread t= new Thread( thread);
				t.start();
				this.threads.add(t);
				this.servidoresDisponibles.get(subindex).incrementarNroConexionesAsignadas();
				asignado=true;
			}else{ 
				//si ese el hilo no se pudo conectar elimino la informacion del servidor de la lista
				thread.anular();	//anulo los datos que le di a ese hilo
				this.servidoresDisponibles.remove(subindex);
			}
		}//fin while
		//si termino el while y no asigne el socket a ningun hilo, significa que no tengo servidores o no los pude alcanzar
		// entonces cierro el socket al cliente y muestro msj
		if(!asignado){
			try { so.close();	} catch (IOException e) {}
			System.out.println("no me pude conectar con ningun servidor, veirifique que estan funcionando");
		}
	}
	
	protected Integer buscarServidorMenosConexiones(){
		Integer menor=this.servidoresDisponibles.get(0).getNroConexionesAsignadas();
		Integer subindexMenor=0;
		Integer index=0;
		for(InfoServidor is: this.servidoresDisponibles){
			if(is.getNroConexionesAsignadas()<menor){
				menor=is.getNroConexionesAsignadas();
				subindexMenor=index;
			}
			index++;
		}
		return subindexMenor;
	}
	
}