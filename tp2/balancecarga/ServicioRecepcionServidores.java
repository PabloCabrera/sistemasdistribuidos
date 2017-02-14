package tp2.balancecarga;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ServicioRecepcionServidores implements Runnable {
	
	protected ArrayList<InfoServidor> servidores;
	protected DatagramSocket socketDatagram;
	public static final Integer puertoBroadcast=3333;
	
	public ServicioRecepcionServidores() throws IOException {
		this.socketDatagram = new DatagramSocket(puertoBroadcast);
	}

	public static void main(String[] args) {
		try {
			//solo sirve para probar si andaba
			ServicioRecepcionServidores server = new ServicioRecepcionServidores();
			server.setServidores(new ArrayList<InfoServidor>());
			new Thread(server).start();
		} catch (IOException e) {
			System.out.println("no pude iniciar el servidor de recepcion de servidores");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		boolean error=false;
		while(!error){
			try{
				DatagramPacket packet;
				byte[] buf = new byte[6];
				packet = new DatagramPacket(buf, buf.length);
				socketDatagram.receive(packet);
				String received = new String(packet.getData());
				//System.out.println("la ip del que me mando le msj es: "+packet.getAddress());
				this.agregarServidor(received,packet.getAddress().toString());
			}catch(SocketTimeoutException e1){
				//no hago nada si sale por timeout
			}catch(IOException e){
				System.out.println("ServicioRecepcionServidores: ERROR:");
				e.printStackTrace();
				error=true;
			}
		}		
	}

	private void agregarServidor(String puerto, String ip) {
		puerto.replace('"', ' ');
		puerto=puerto.trim();
		Integer puertoEmisor=Integer.parseInt(puerto);
		ip=ip.replace('/', ' ');
		InfoServidor nuevoServidor =new InfoServidor(ip,puertoEmisor);
		//me fijo si ya tenia al servidor, si ya lo tenia no lo agrego
		if( !this.servidores.contains(nuevoServidor) ){
			this.servidores.add(nuevoServidor);
			System.out.println("ServicioRecepcionServidor: + agrege el servidor: "+ip+" "+puertoEmisor);
		}else{
			System.out.println("ServicioRecepcionServidor: - ya existe el servidor: "+ip+" "+puertoEmisor);
		}
	}

	public ArrayList<InfoServidor> getServidores() {
		return servidores;
	}

	public void setServidores(ArrayList<InfoServidor> servidores) {
		this.servidores = servidores;
	}

}