package tp1.ej01;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ClientEchoTCP {
	private String serverName;
	private int serverPort;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private Scanner scanner;

	public static void main (String args[]) {
		ClientEchoTCP client = new ClientEchoTCP ("localhost", 9090);
		if (client.connect ()) {
			client.start();
		} else {
			System.err.println ("No se pudo conectar con el servidor");
		}
	}

	public ClientEchoTCP (String server, int port) {
		this.serverName = server;
		this.serverPort = port;
	}

	public boolean connect () {
		try {
			this.socket = new Socket (this.serverName, this.serverPort);
			this.writer = new PrintWriter (this.socket.getOutputStream (), true);
			this.reader = new BufferedReader (new InputStreamReader (this.socket.getInputStream ()));
			this.scanner = new Scanner (System.in);
			System.out.println ("El cliente se ha conectado correctamente a " + this.serverName);
		} catch (UnknownHostException e) {
			System.err.println("Nombre de host '" + this.serverName + "' desconocido.");
			System.err.println(e.getMessage());
			return false;
		} catch (IOException e) {
			System.err.println("Error de entrada/salida.");
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public void start () {
		String keyboardLine;
		String serverLine;

		System.out.println ("Escriba texto para enviar al servidor");

		try {
			while (true) {
				System.out.print (">>> ");
				keyboardLine = this.scanner.nextLine();
				this.writer.println (keyboardLine);
				System.out.print ("<<< ");
				System.out.println (this.reader.readLine());
			}
		} catch (Exception e) {
			System.out.println("Cliente desconectado");
		}
	}

	private void sendMsg() {
	}
}