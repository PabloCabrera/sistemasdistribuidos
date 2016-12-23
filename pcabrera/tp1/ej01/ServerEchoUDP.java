package pcabrera.tp1.ej01;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerEchoUDP {
	private int listenPort;
	private DatagramSocket datagramSocket;

	public static void main(String args[]) {
		ServerEchoUDP server = new ServerEchoUDP(9094);
		server.start();
	}

	public ServerEchoUDP (int port) {
		this.listenPort = port;
		try {
			this.datagramSocket = new DatagramSocket (listenPort);
		} catch (IOException e) {
			System.err.println("Error de entrada/salida.");
			System.err.println(e.getMessage());
		}
	}

	public void start () {
		byte[] receivedData;
		DatagramPacket receivedPacket, sentPacket;
		System.out.println("Iniciando servidor echo en puerto UDP " + this.listenPort);
		InetAddress remoteAddress;
		int remotePort, receivedLength;

		receivedPacket = new DatagramPacket(new byte[2048], 2048);
		sentPacket = new DatagramPacket(new byte[2048], 2048);
		try {
			while (true) {
				this.datagramSocket.receive (receivedPacket);
				receivedData = receivedPacket.getData();
				receivedLength = receivedPacket.getLength();
				remoteAddress = receivedPacket.getAddress();
				remotePort = receivedPacket.getPort();
				sentPacket.setData(receivedData);
				sentPacket.setLength(receivedLength);
				sentPacket.setAddress(remoteAddress);
				sentPacket.setPort(remotePort);
				this.datagramSocket.send(sentPacket);
			}
		} catch (IOException e) {
			System.err.println("Error de Entrada/Salida.");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.out.println ("Cerrando conexion");
			this.datagramSocket.disconnect();
		}
	}

}
