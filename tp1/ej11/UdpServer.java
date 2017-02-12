package tp1.ej11;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;

public class UdpServer extends DayTimeServer {
	private static int port;
	private boolean mustExit = false;
	private DatagramSocket datagramSocket;

	public UdpServer (int port) {
		this.port = port;
		
	}

	@Override
	public void run () {
		DatagramPacket receivedPacket;
		InetAddress remoteAddress;
		byte[] receivedData;

		try {
			this.datagramSocket = new DatagramSocket (this.port);
			receivedPacket = new DatagramPacket(new byte[512], 512);
			while (!this.mustExit) {
				this.datagramSocket.receive (receivedPacket);
				String city = this.getCityName (receivedPacket);
				String responseText = this.getDayTime (city);
				DatagramPacket responsePacket = this.getResponsePacket (responseText, receivedPacket);
				this.datagramSocket.send (responsePacket);
			}
		} catch (IOException e) {
			System.err.println("Error de Entrada/Salida.");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.out.println ("Cerrando conexion");
			this.datagramSocket.disconnect();
		}
	}

	private String getCityName (DatagramPacket packet) {
		int length = packet.getLength();
		byte[] data = packet.getData();
		return new String (data, 0, length);
	}

	private DatagramPacket getResponsePacket (String responseText, DatagramPacket srcPacket) {
		DatagramPacket packet = new DatagramPacket (new byte[512], 512);
		InetAddress remoteAddress = srcPacket.getAddress();
		int remotePort = srcPacket.getPort();
		byte[] responseData = responseText.getBytes();
		packet.setData(responseData);
		packet.setLength(responseData.length);
		packet.setAddress(remoteAddress);
		packet.setPort(remotePort);
		return packet;
	}

	public void setExit () {
		this.mustExit = true;
	}
}
