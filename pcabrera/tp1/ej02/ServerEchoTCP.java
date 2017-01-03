package pcabrera.tp1.ej02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class ServerEchoTCP {
	private int listenPort;
	private ServerSocket serverSocket;
	private List<ServerEchoTCPThread> listThreads;

	public static void main(String args[]) {
		ServerEchoTCP server = new ServerEchoTCP(9090);
		server.start();
	}

	public ServerEchoTCP (int port) {
		this.listenPort = port;
		this.listThreads = new ArrayList<ServerEchoTCPThread>();

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
				Socket socket = this.serverSocket.accept();
				System.out.println("Se ha conectado un cliente");
				ServerEchoTCPThread thread = new ServerEchoTCPThread (socket, this);
				thread.start();
				this.listThreads.add (thread);
			}
		} catch (IOException e) {
			System.err.println("Error de entrada/salida.");
			System.err.println(e.getMessage());
		}
	}

	public void closeThread (ServerEchoTCPThread thread) {
		this.listThreads.remove(thread);
		System.out.println("Se ha desconectado un cliente");
	}

}
