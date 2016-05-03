import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ServidorTelnet {
	/* Debe inicializarse y escuchar en un puerto por conexiones entrantes*/
	/* Debe ejecutar comandos que envia el cliente */
	/* Debe registrar todo en un log */

	private PrintStream streamLog;
	private int puerto;
	private ServerSocket serverSocket;
	private boolean terminar = false;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS ");

	public static void main (String[] args) {
		int arg_puerto = 23;
		String arg_logfile = null;
		ServidorTelnet servidor;

		if (args.length > 0) {
			try {
				arg_puerto = Integer.parseInt (args[0]);
			} catch (NumberFormatException e) {
				System.err.println ("Warning: \"" + args[0] + "\" no es un numero de puerto valido. Se utilizara el puerto predeterminado: " + arg_puerto);
			}
		}
		if (args.length > 1) {
			arg_logfile = args[1];
		}

		servidor = new ServidorTelnet (arg_puerto, arg_logfile);
		
	}
	
	public ServidorTelnet (int puerto, String archivoLog) {
		this.puerto = puerto;
		this.inicializarLog (archivoLog);
		try {
			this.inicializarSocket (puerto);
			this.escuchar();
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}

	}

	private void inicializarLog (String archivoLog) {
		try {
			this.streamLog = new PrintStream (new FileOutputStream (archivoLog, true));
			
		} catch (Exception e) {
			System.err.println ("Warning: No se puede abrir el archivo de log. Se utilizara la salida estandar en su lugar.");
			this.streamLog = System.out;
		
		}

	}

	private void inicializarSocket (int puerto) {
		try {
			this.serverSocket = new ServerSocket(this.puerto);
		} catch (IOException e) {
			throw new RuntimeException("No se puede abrir el puerto especificado", e);
		}
	}

	private void escuchar() {
		while (!this.terminar){
			Socket cliente = null;
			try {
				cliente = this.serverSocket.accept();
				new Thread (new ServidorTelnetThread (cliente, this)).start();
			} catch (Exception e) {
					System.err.println("Error intentando aceptar conexion entrante");
					System.err.println(e.getMessage());
					this.terminar = true;
			}
		}
	}

	public String ejecutarComando(String comando) {
		String resultado;
		StringBuffer output = new StringBuffer();
		Process proceso;

		try {
			proceso = Runtime.getRuntime().exec(comando);
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
		this.streamLog.println (this.ahora() + mensaje);
	}

	private String ahora () {
		return ServidorTelnet.dateFormat.format(new Date());
	}
}

