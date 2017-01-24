package tp1.ej09;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TelnetServer {

	private PrintStream streamLog;
	private int port;
	private ServerSocket serverSocket;
	private boolean end = false;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS ");

	public static void main (String[] args) {
		int arg_port = 23;
		String arg_logfile = null;
		TelnetServer server;

		
		if (args.length > 1) {
			arg_logfile = args[1];
		}

		if (args.length > 0) {
			try {
				arg_port = Integer.parseInt (args[0]);
			} catch (NumberFormatException e) {
				System.err.println ("Warning: \"" + args[0] + "\" no es un numero de port valido. Se utilizara el port predeterminado: " + arg_port);
			}
		} else {
			Scanner scan = new Scanner (System.in);
			try {
				System.out.print ("Ingresar numero de port: ");
				String stringPort = scan.nextLine();
				arg_port = Integer.parseInt(stringPort);
			} catch (NumberFormatException e) {
				System.err.println ("Warning: Port no valido. Se utilizara el port predeterminado: " + arg_port);
			}
				System.out.print ("Ingresar nombre de archivo log: ");
				arg_logfile = scan.nextLine();
		}

		server = new TelnetServer (arg_port, arg_logfile);
		
	}
	
	public TelnetServer (int port, String archivoLog) {
		this.port = port;
		this.initLog (archivoLog);
		try {
			this.initSocket (port);
			this.listen();
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}

	}

	private void initLog (String archivoLog) {
		try {
			this.streamLog = new PrintStream (new FileOutputStream (archivoLog, true));
			
		} catch (Exception e) {
			System.err.println ("Warning: No se puede abrir el archivo de log. Se utilizara la salida estandar en su lugar.");
			this.streamLog = System.out;
		
		}

	}

	private void initSocket (int port) {
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			throw new RuntimeException("No se puede abrir el port especificado", e);
		}
	}

	private void listen() {
		while (!this.end){
			Socket cliente = null;
			try {
				cliente = this.serverSocket.accept();
				new Thread (new TelnetThread (cliente, this)).start();
			} catch (Exception e) {
					System.err.println("Error intentando aceptar conexion entrante");
					System.err.println(e.getMessage());
					this.end = true;
			}
		}
	}

	public String executeCommand(String comando, File dir) {
		String resultado;
		StringBuffer output = new StringBuffer();
		Process proceso;

		try {
			proceso = Runtime.getRuntime().exec(comando, null, dir);
			proceso.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));

			String linea = "";			
			while ((linea = reader.readLine())!= null) {
				output.append(linea + "\n");
			}

		} catch (Exception e) {
			String errorMsg = "ERROR: " + e.getMessage();
			this.log (errorMsg);
			return(errorMsg);
		}

		resultado = output.toString();
		String mensajeLog = 
			"COMANDO: "+ comando +"\n" +
			"SALIDA: " + resultado +
			"\n";

		this.log (mensajeLog);
		return resultado;

	}

	public synchronized void log(String mensaje) {
		this.streamLog.println (this.now() + mensaje);
	}

	private String now () {
		return TelnetServer.dateFormat.format(new Date());
	}
}

