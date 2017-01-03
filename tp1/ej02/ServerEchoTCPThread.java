package tp1.ej02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerEchoTCPThread extends Thread {
	private int listenPort;
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private ServerEchoTCP mainServer;

	public ServerEchoTCPThread (Socket socket, ServerEchoTCP mainServer) {
		this.socket = socket;
		this.mainServer = mainServer;
		try {
			this.writer = new PrintWriter (socket.getOutputStream(), true);
			this.reader = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
		} catch (IOException e) {
			System.err.println ("Error al crear un thread para manejar una conexion");
			this.mainServer.closeThread(this);
		}
	}

	@Override
	public void run () {
		String line;

		try {
			while (this.socket.isConnected()) {
				line = this.reader.readLine();
				if (line == null) {
					throw new NullPointerException ();
				}
				this.writer.println(line);
			}
		} catch (Exception e) {
			this.mainServer.closeThread(this);
		}
	}

}
