package tp1.ej11; 

import java.util.Scanner;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class DayTimeClient {
	private final static String SERVER_HOST = "localhost";
	private final static int SERVER_PORT = 9013;

	private String serverHost;
	private int serverPort;

	public DayTimeClient (String host, int port) {
		this.serverHost = host;
		this.serverPort = port;
	}

	public String getDayTime (String city) {
		String response = this.getUdpDayTime (city);
		if (response == null) {
			response = "No hubo respuesta del servidor";
		}
		return response;
	}

	private String getUdpDayTime (String city) {
		String response = null;
		try {
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket (new byte[512], 512);
			byte[] data = city.getBytes();
			packet.setAddress (InetAddress.getByName (this.serverHost));
			packet.setPort (this.serverPort);
			packet.setData (data);
			packet.setLength (data.length);
			socket.send (packet);

			packet = new DatagramPacket (new byte[512], 512);
			socket.receive (packet);
			int length = packet.getLength();
			data = packet.getData();
			response = new String (data, 0, length);
		} catch (Exception e) {
			misc.Debugger.debugException (e);
		}
		return response;
	}

	public static void main (String[] args) {
		try {
			DayTimeClient client;
			client = new DayTimeClient (SERVER_HOST, SERVER_PORT);
			Scanner scanner = new Scanner (System.in);
			System.out.println ("Escriba el nombre de una ciudad:");
			String ciudad = scanner.nextLine();
			String daytime = client.getDayTime (ciudad);
			System.out.println (daytime);
		} catch (Exception e) {
			System.err.println ("Ha ocurrido un error: "+e.getMessage());
		}
	}
	
}