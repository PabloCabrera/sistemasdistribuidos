package tp2.relojes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Cliente {
	
	protected String ip;
	protected Integer puerto;
	protected Socket s;
	
	public static void main(String[] args) {
		Cliente c= new Cliente("localhost",6000);
		c.pedirHora();
	}
	
	public Cliente(){	
	}
	
	public Cliente(String ip, Integer puerto){
		this.ip=ip;
		this.puerto=puerto;
	}
	
	public boolean pedirHora(){
		try {
			s = new Socket(ip,puerto);
		} catch (IOException e) {
			System.out.println("no me pude conectar");
			e.printStackTrace();
			return false;
		}
		try {
			s.setKeepAlive(true);
		} catch (SocketException e) {
			System.out.println("no pude poner el keep alive:");
			e.printStackTrace();
		}
		ObjectOutputStream escribir=null;
		ObjectInputStream leer=null;
		try {
			escribir = new ObjectOutputStream(s.getOutputStream());
			leer = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			System.out.println("no pude obtener los streams del socket");
			e.printStackTrace();
		}
		
		
		try {
			while(true){
				String s="dame la hora wacho!";
				//no hay protocolo, le envio un String con cualquier cosa y recibo un String con la hora
				long inicio = System.currentTimeMillis();
				escribir.writeObject(s);
				String respuesta=(String) leer.readObject();
				long fin = System.currentTimeMillis();
				System.out.println("el servidor me respondio:");
				System.out.println(respuesta);
				long diferencia = fin - inicio;
				System.out.println("el servidor tardo: "+diferencia+"ms en responder");
				Thread.sleep(5000);
			}
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			System.out.println("error al enviar o recibir informacion del servidor");
		}
		System.out.println("bye..");
		return true;
	}
	
}
