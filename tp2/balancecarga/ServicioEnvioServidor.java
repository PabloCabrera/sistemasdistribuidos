package tp2.balancecarga;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServicioEnvioServidor implements Runnable {
	
	protected String ip;
	protected Integer puerto;
	protected DatagramSocket socket;
	
	public ServicioEnvioServidor(Integer puerto) throws SocketException{
		this.puerto=puerto;		//puerto del servidor que voy a informar por broadcast
		this.socket = new DatagramSocket();		//no es el puerto del DatagramSocket, este es aleatorio
	}
	
	@Override
	public void run() {
		//si se quiere enviar el broadcast cada tanto tiempo o x cantidad de veces
	}

	public static void main(String[] args) {
		try {
			ServicioEnvioServidor s = new ServicioEnvioServidor(6000);
			s.enviarBroadcast();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public boolean enviarBroadcast(){
		DatagramPacket p=null;
		String ip;
		try {			
			ip = InetAddress.getLocalHost().getHostAddress();
			//transformo la ip del servidor a la "IP de la broadcast"(puede no funcionar si la mascara no es de 24)
			String broadcast = ip.substring(0,(ip.lastIndexOf('.')))+".255";
			String mensaje = puerto.toString();
			byte[] msj = mensaje.getBytes();
			p = new DatagramPacket(msj, msj.length,InetAddress.getByName(broadcast), ServicioRecepcionServidores.puertoBroadcast);
			this.socket.send(p);
			System.out.println("ServicioEnvioServidor: +envie el mensaje broadcast con mi direccion: "+ip+":"+puerto);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
