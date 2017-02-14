package tp2.relojes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Cliente {
	private final static long TIEMPO_ACTUALIZACION = 5000;
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
		
		SimpleDateFormat formato_hora = new SimpleDateFormat("hh:mm:ss a");
		
		try {
			while(true){
				String s="SOLICITUD DE HORA";
				//no hay protocolo, le envio un String con cualquier cosa y recibo un String con la hora
				long inicio = System.currentTimeMillis();
				Date horaReloj = new Date(inicio);
				escribir.writeObject(s);
				Date respuesta=(Date) leer.readObject();
				long fin = System.currentTimeMillis();
				
				if (respuesta == null) {
					System.err.println("El servidor no envio la hora correctamente.");
				} else {
				
				System.out.println("Hora en reloj local: ");
				System.out.println(formato_hora.format (horaReloj));
				System.out.println();

				System.out.println("Hora en reloj de servidor remoto:");
				System.out.println(formato_hora.format (respuesta));
				System.out.println();

				System.out.print("Diferencia: ");
				long time_respuesta = respuesta.getTime() % (24l*60l*60l*1000l);
				long time_reloj = horaReloj.getTime() % (24l*60l*60l*1000l);
				System.out.println( (time_respuesta - time_reloj) / 1000 + " segundos");
				long diferencia = fin - inicio;
				System.out.println("El servidor tardo: "+diferencia+"ms en responder");
				}
				Thread.sleep(TIEMPO_ACTUALIZACION);
			}
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			System.out.println("error al enviar o recibir informacion del servidor");
		}
		System.out.println("bye..");
		return true;
	}
	
}
