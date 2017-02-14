package tp2.servidorarchivos;

import tp2.backuparchivos.WatcherArchivo;

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
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class ServidorArchivos {
	public static final String DIRECTORIO_ARCHIVOS = "archivos/";
	private static final int PUERTO_REPLICA = 4444;
	

	private ServerSocket servidor;
	private List<HiloCliente> clientes = new ArrayList<HiloCliente>();
	private PrintStream streamLog;
	private WatcherArchivo watcher;
	private Thread watcherThread;
	private int puerto;
	private boolean terminar = false;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS ");

	public static void main (String[] args) {
		int arg_puerto = 9019;
		String arg_logfile = null;
		String backup_host = null;
		ServidorArchivos servidor_archivos;

		
		if (args.length > 1) {
			arg_logfile = args[1];
		}

		if (args.length > 0) {
			try {
				arg_puerto = Integer.parseInt (args[0]);
			} catch (NumberFormatException e) {
				System.err.println ("Warning: \"" + args[0] + "\" no es un numero de puerto valido. Se utilizara el puerto predeterminado: " + arg_puerto);
			}
		} else {
			Scanner scan = new Scanner (System.in);
			try {
				System.out.print ("Escribir nombre de host de copia de seguridad: ");
				backup_host = scan.nextLine();
				if (backup_host.length() == 0) {
					System.out.println ("No se utilizara ningun servidor de copia de seguridad");
				}
			} catch (Exception e) {
					System.out.println ("Ha ocurrido un error.\nNo se utilizara ningun servidor de copia de seguridad");
			}
				System.out.print ("Ingresar nombre de archivo log: ");
				arg_logfile = scan.nextLine();
		}

		servidor_archivos = new ServidorArchivos (arg_puerto, arg_logfile, backup_host);
		
	}
	
	public ServidorArchivos (int puerto, String archivoLog, String hostBackup) {
		this.puerto = puerto;
		this.inicializarLog (archivoLog);
		if (hostBackup != null) {
			this.iniciarWatcher (hostBackup);
		}
		try {
			this.inicializarSocket (puerto);
			this.escuchar();
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}

	}

	private void iniciarWatcher (String hostBackup) {
		this.watcher = new WatcherArchivo (DIRECTORIO_ARCHIVOS, null, hostBackup, PUERTO_REPLICA);
		this.watcherThread = new Thread (watcher);
		this.watcherThread.start ();
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
			this.servidor = new ServerSocket(this.puerto);
		} catch (IOException e) {
			throw new RuntimeException("No se puede abrir el puerto especificado", e);
		}
	}

	private void escuchar() {
		while (!this.terminar){
			Socket socket_cliente = null;
			HiloCliente hilo_cliente = null;
			try {
				socket_cliente = this.servidor.accept();
				hilo_cliente = new HiloCliente (socket_cliente, this);
				this.clientes.add(hilo_cliente);
				hilo_cliente.start();
			} catch (Exception e) {
					System.err.println("Error intentando aceptar conexion entrante");
					System.err.println(e.getMessage());
					this.terminar = true;
			}
		}
	}


	public synchronized void log(String mensaje) {
		this.streamLog.println (this.ahora() + mensaje);
	}

	private String ahora () {
		return ServidorArchivos.dateFormat.format(new Date());
	}
}

