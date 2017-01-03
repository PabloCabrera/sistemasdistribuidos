package tp1.ej01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerEchoTCP {
	private int listenPort;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter writer;
	private BufferedReader reader;

	public static void main(String args[]) {
		ServerEchoTCP server = new ServerEchoTCP(9090);
		server.start();
	}

	public ServerEchoTCP (int port) {
		this.listenPort = port;
		try {
			this.serverSocket = new ServerSocket (listenPort);
		} catch (IOException e) {
			System.err.println("Error de Entrada/Salida. No se puede iniciar el socket en el puerto especificado.");
			System.err.println(e.getMessage());
		}
	}

	public void start () {
		System.out.println("Iniciando servidor echo en puerto TCP " + this.listenPort);

		try {
			while (true) {
				this.clientSocket = this.serverSocket.accept();
				System.out.println("Se ha conectado un cliente");
				this.writer = new PrintWriter (clientSocket.getOutputStream(), true);
				this.reader = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
				this.echo(this.reader, this.writer);
				this.clientSocket.close();
			}
		} catch (IOException e) {
			System.err.println("Error de entrada/salida.");
			System.err.println(e.getMessage());
		}
	}

	private void echo (BufferedReader reader, PrintWriter writer) {
		String line;

		try {
			while (this.clientSocket.isConnected()) {
				line = reader.readLine();
				if (line == null) {
					throw new NullPointerException ();
				}
				writer.println(line);
			}
		} catch (Exception e) {
			System.out.println ("Se ha desconectado un cliente");
		}
	}

}
