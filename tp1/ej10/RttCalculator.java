package tp1.ej10;

import misc.Debugger;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

class RttCalculator {
	private TransportProtocol protocol = TransportProtocol.TCP;
	private String proxy = null;
	private int numHits = 5;
	private int udpPort = 9094;
	
	public RttCalculator () {
	}

	public long calculateRtt (String host) {
		System.out.println("Calculando RTT con host "+host);
		long rtt = this.calculateRttToHost (host);
		return rtt;
	}

	public void calculateRttList (String filename) {
		System.out.println("Calculando RTT con lista de hosts "+filename);
		try {
			File file = new File (filename);
			Scanner sc = new Scanner (file);
			while (sc.hasNextLine ()) {
				String host = sc.nextLine();
				System.out.println ("Se calculara el RTT al host: "+ host);
				long rtt = this.calculateRttToHost (host);
				System.out.println ("Round Trip Time: "+ rtt + "\n");
			}
		} catch (Exception e) {
			System.err.println ("Error: " + e.getMessage());
		}
		this.pressKeyToContinue ();
	}

	private long calculateRttToHost (String host) {
		long[] results = new long[this.numHits];
		for (int i = 0; i < numHits; i++) {
			results[i] = this.calculateSingleRtt (host, this.protocol);
		}
		return this.meanRtt (results);
	}

	private long calculateSingleRtt (String host, TransportProtocol protocol) {
		switch (this.protocol) {
			case TCP:
				System.out.println ("Calculando tiempo de respuesta con "+host+" via HTTP");
				return this.calculateRttHttp (host);
			case UDP:
				System.out.println ("Calculando tiempo de respuesta con "+host+" via UDP");
				return this.calculateRttUdp (host);
			default:
				return -1;
		}
	}

	private long calculateRttHttp (String host) {
		try {
			Socket socket = this.connectHttp(host);
			PrintWriter writer = new PrintWriter (socket.getOutputStream());
			InputStream input = socket.getInputStream();

			long start = System.currentTimeMillis();
			this.sendHttpHeader (writer, host);
			this.recieveHttpByte (input);
			long end = System.currentTimeMillis();

			writer.close();
			input.close();
			socket.close();
			return (end - start);
		} catch (UnknownHostException exception) {
			System.err.println ("No se puede resolver el nombre de host "+host);
			return -1;
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return -1;
		}
	}

	private Socket connectHttp (String host) {
		Socket socket = null;
		String connectHost = host;
		int connectPort = 80;
		if (this.proxy != null) {
			connectHost = this.proxy.replaceFirst (":.*", "");
			connectPort = Integer.parseInt(this.proxy.replaceFirst (".*:", ""));
		}
		try {
			socket = new Socket (connectHost, connectPort);
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
		return socket;
	}

	private void sendHttpHeader (PrintWriter writer, String host) {
		String send = "GET ";
		if (this.proxy != null) {
			send += "http://" + host;
		}
		send += "/ HTTP/1.1\n\n";
		writer.print (send);
		writer.flush ();
	}

	private void recieveHttpByte (InputStream input) throws IOException {
		input.read ();
	}

	private long calculateRttUdp (String host) {
		try {
			DatagramPacket packet;
			DatagramSocket socket = new DatagramSocket ();

			packet = new DatagramPacket(new byte[2048], 2048);
			packet.setPort (this.udpPort);
			packet.setAddress (InetAddress.getByName (host));

			long start = System.currentTimeMillis ();
			socket.send (packet);
			socket.receive (packet);
			long end = System.currentTimeMillis ();

			socket.close ();

			return (end - start);
		} catch (Exception e) {
			Debugger.debugException (e);
			return -1;
		}
	}

	private long meanRtt (long[] times) {
		int numElements = 0;
		long sum = 0;

		for (int i = 0; i < times.length; i++) {
			if (times[i] >= 0) {
				numElements++;
				sum += times[i];
			}
		}

		if (numElements > 0) {
			return (sum / numElements);
		} else {
			return -1;
		}
	}

	private void pressKeyToContinue () {
		System.out.println("Presione ENTER para continuar");
		try {
			System.in.read();
		} catch (Exception e) {
		}
	}

	public void setProtocol (TransportProtocol protocol) {
		this.protocol = protocol;
	}

	public void setProxy (String proxy) {
		this.proxy = proxy;
	}

	public void setNumHits (int numHits) {
		this.numHits = numHits;
	}

	public void setUdpPort (int port) {
		this.udpPort = udpPort;
	}
}
