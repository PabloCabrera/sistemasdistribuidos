package pcabrera.tp1.ej01;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Scanner;
import java.io.IOException;

public class ClientEchoUDP {
	private String serverName;
	private int serverPort;
	private DatagramSocket socket;
	private Scanner scanner;

	public static void main (String args[]) {
		ClientEchoUDP client = new ClientEchoUDP ("localhost", 9094);
		client.start();
	}

	public ClientEchoUDP (String server, int port) {
		this.serverName = server;
		this.serverPort = port;
	}

	public void start () {
		String keyboardLine, receivedLine;
		InetAddress remoteAddress;
		byte[] message, receivedData;
		DatagramPacket sentPacket, receivedPacket;
		int receivedLength;

		try {
			this.scanner = new Scanner (System.in);
			remoteAddress = InetAddress.getByName(this.serverName);
			this.socket = new DatagramSocket ();
			sentPacket = new DatagramPacket (new byte[2048], 2048);
			receivedPacket = new DatagramPacket (new byte[2048], 2048);
			System.out.println ("Escriba texto para enviar al servidor");
			while (true) {
				System.out.print (">>> ");
				keyboardLine = this.scanner.nextLine();
				message = keyboardLine.getBytes();
				sentPacket.setData(message);
				sentPacket.setLength(message.length);
				sentPacket.setAddress(remoteAddress);
				sentPacket.setPort(this.serverPort);
				this.socket.send(sentPacket);
				
				this.socket.receive (receivedPacket);
				receivedLength = receivedPacket.getLength();
				receivedData = receivedPacket.getData();
				receivedLine = new String (receivedData, 0, receivedLength);

				System.out.print ("<<< ");
				System.out.println (receivedLine);
			}
		} catch (Exception e) {
			System.out.println("Cliente desconectado");
		}
	}

	private void sendMsg() {
	}
}