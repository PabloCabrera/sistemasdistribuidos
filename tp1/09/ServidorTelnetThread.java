import java.net.Socket;
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
		String resultado = null;

		this.servidor.log ("CONEXION ABIERTA: " + this.socket.getRemoteSocketAddress());

		while ( (!this.socket.isInputShutdown()) && (!cerrar)) {
			this.mostrarPrompt();
			try {
				linea = this.input.readLine();
				if(linea != null) {
					resultado = this.servidor.ejecutarComando(linea);
					this.output.println(resultado);
				} else {
					cerrar = true;
				}
			} catch (Exception e) {
				cerrar = true;
			}
		}

		this.servidor.log("CONEXION CERRADA: " + this.socket.getRemoteSocketAddress());
	}	

	private void mostrarPrompt() {
		this.output.print("$ ");
	}
	
}
