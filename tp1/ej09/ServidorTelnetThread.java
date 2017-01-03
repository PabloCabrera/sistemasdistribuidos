package tp1.ej09;

import java.net.Socket;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

class ServidorTelnetThread implements Runnable {
	ServidorTelnet servidor;
	Socket socket = null;
	BufferedReader input = null;
	PrintStream output = null;

	public ServidorTelnetThread (Socket socket, ServidorTelnet servidor) {
		this.servidor = servidor;
		this.socket = socket;
		
		try {
			this.input  = new BufferedReader (new InputStreamReader (socket.getInputStream()));
			this.output = new PrintStream (socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error en conexion con cliente");
		}
	}

	public void run() {
		boolean cerrar = false;
		String linea = null;
		String dir = null;
		File fdir = null;
		String resultado = null;

		this.servidor.log ("CONEXION ABIERTA: " + this.socket.getRemoteSocketAddress());

		while ( (!this.socket.isInputShutdown()) && (!cerrar)) {
			this.mostrarPrompt();
			try {
				linea = this.input.readLine();

				if(linea != null) {

					/* Cambiar directorio de trabajo */
					if (linea.matches ("^cd *$")) {
						/* Directorio predeterminado */
						dir = null;
					} else if (linea.matches ("^cd (.:)?[/\\\\]")) {
						/* Path absoluto */
						dir = linea.substring(3);
					} else if (linea.matches ("^cd .*$")) {
						/* Path relativo */
						if (dir == null) {
							dir = linea.substring(3);
						} else {
							dir = dir + "/" + linea.substring(3);
						}
					} else {
						fdir = (dir == null)? null: new File (dir);
						resultado = this.servidor.ejecutarComando(linea, fdir);
						this.output.println(resultado);
					}
				} else {
					cerrar = true;
				}
			} catch (Exception e) {
				System.out.println("Excepcion: " + e.getMessage());
				cerrar = true;
			}
		}

		this.servidor.log("CONEXION CERRADA: " + this.socket.getRemoteSocketAddress());
	}	

	private void mostrarPrompt() {
		this.output.print("$ ");
	}
	
}
