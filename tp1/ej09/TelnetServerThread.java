package tp1.ej09;

import java.net.Socket;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

class TelnetServerThread implements Runnable {
	private TelnetServer server;
	private Socket socket = null;
	private BufferedReader input = null;
	private PrintStream output = null;
	private static String DELIMITER = "#<END OF COMMAND OUTPUT>#";

	public TelnetServerThread (Socket socket, TelnetServer server) {
		this.server = server;
		this.socket = socket;
		
		try {
			this.input  = new BufferedReader (new InputStreamReader (socket.getInputStream()));
			this.output = new PrintStream (socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error en conexion con cliente");
		}
	}

	public void run() {
		boolean close = false;
		String line = null;
		String dir = null;
		File fdir = null;
		String result = null;

		this.server.log ("CONEXION ABIERTA: " + this.socket.getRemoteSocketAddress());

		while ( (!this.socket.isInputShutdown()) && (!close)) {
			try {
				result = "";
				line = this.input.readLine();

				if(line != null) {

					/* Cambiar directorio de trabajo */
					if (line.matches ("^cd *$")) {
						/* Directorio predeterminado */
						dir = null;
					} else if (line.matches ("^cd (.:)?[/\\\\]")) {
						/* Path absoluto */
						dir = line.substring(3);
					} else if (line.matches ("^cd .*$")) {
						/* Path relativo */
						if (dir == null) {
							dir = line.substring(3);
						} else {
							dir = dir + "/" + line.substring(3);
						}
					} else {
						fdir = (dir == null)? null: new File (dir);
						result = this.server.executeCommand(line, fdir);
					}
					result = this.appendDelimiter (result);
					this.output.println(result);
				} else {
					close = true;
				}
			} catch (Exception e) {
				System.out.println("Excepcion: " + e.getMessage());
				close = true;
			}
		}

		this.server.log("CONEXION CERRADA: " + this.socket.getRemoteSocketAddress());
	}	

	private String appendDelimiter (String text) {
		return text + DELIMITER;
	}
}
