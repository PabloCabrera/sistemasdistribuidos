package tp2.servidorarchivos;

import java.net.Socket;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

class HiloCliente extends Thread {

	ServidorArchivos servidor;
	Socket socket = null;
	InputStream input = null;
	OutputStream output = null;
	PrintStream printer = null;

	public HiloCliente (Socket socket, ServidorArchivos servidor) {
		this.servidor = servidor;
		this.socket = socket;
		
		try {
			this.input  = socket.getInputStream();
			this.output = socket.getOutputStream();
			this.printer = new PrintStream (output);
		} catch (IOException e) {
			System.err.println("Error en conexion con cliente");
		}
	}

	@Override
	public void run() {
		boolean cerrar = false;
		String linea = null;
		String dir = null;
		File fdir = null;
		String resultado = null;

		this.servidor.log ("CONEXION ABIERTA: " + this.socket.getRemoteSocketAddress());

		while ( (!this.socket.isInputShutdown()) && (!cerrar)) {
			try {
				linea = this.getLinea();

				if(linea != null) {

					if (linea.matches ("(?i)^put .*$")) {
						// Subir archivo
						this.put(linea);
					} else if (linea.matches ("(?i)^del .*$")) {
						// Eliminar archivo
						this.del(linea.replaceFirst("del ", ""));
					} else if (linea.matches ("(?i)^get .*$")) {
						// Recuperar archivo
						this.get(linea.replaceFirst("get ", ""));
					} else if (linea.matches ("(?i)^dir.*$")) {
						// Mostrar listado de directorio
						this.dir();
					} else if (linea.matches ("(?i)^quit.*$")) {
						// Terminar
						this.printer.println("MSG Cerrando conexion");
						cerrar = true;
					} else {
						// Mostrar cartel de comando desconocido
						this.printer.println("MSG Comando desconocido");
					}
				} else {
					cerrar = true;
				}
			} catch (Exception e) {
				System.out.println("Excepcion: " + e.getMessage());
				cerrar = true;
			}
		}

		this.servidor.log("CLOSED " + this.socket.getRemoteSocketAddress());
	}

	private void put (String header) {
		this.servidor.log(header+"\t"+ this.socket.getRemoteSocketAddress());
		this.recibirArchivo(header);
	}

	private void get (String filename) {
		this.servidor.log("GET "+filename+"\t"+ this.socket.getRemoteSocketAddress());
		File f = new File (ServidorArchivos.DIRECTORIO_ARCHIVOS + filename);
		if (f.exists()) {
			if(!f.isDirectory()) {
				this.enviarArchivo(f);
			} else {
				this.printer.println("MSG No se puede descargar "+filename+", es un directorio");
			}
		} else {
			this.printer.println("MSG No existe el archivo "+filename);
		}
	}

	private void dir () {
		File f = new File(ServidorArchivos.DIRECTORIO_ARCHIVOS + ".");
		String dirs = "";
		for(String filename: f.list()){
			dirs += filename + "\t";
		}
		this.servidor.log("DIR"+"\t"+ this.socket.getRemoteSocketAddress());
		this.printer.println("MSG "+dirs);
	}

	private void del (String filename) {
		this.servidor.log("DEL "+filename+"\t"+ this.socket.getRemoteSocketAddress());
		boolean borrado = false;
		try {
			File f = new File (ServidorArchivos.DIRECTORIO_ARCHIVOS + filename);
			borrado = f.delete();
		} catch (SecurityException e) {
			this.servidor.log ("ERROR DEL " + filename+"\t"+ this.socket.getRemoteSocketAddress());
		}
		if (borrado) {
			this.printer.println("OK");
		} else {
			this.printer.println("ERROR");
		}
	}

	private void enviarArchivo (File file) {
		try {
			boolean cerrar = false;
			int leidos = 0;
			byte[] buffer = new byte[2048];
			FileInputStream finput = new FileInputStream (file);
			this.printer.println("FILE "+file.length()+" "+file.getName());

			while (!cerrar) {
				try {
					leidos = finput.read(buffer, 0, 2048);

					if (leidos == -1) {
						cerrar= true;
					} else if (leidos == 0) {
						this.sleep(1);
					} else {
						this.output.write(buffer, 0, leidos);
					}
				} catch (IOException e) {
					this.servidor.log("ERROR GET " + file.getName()+"\t"+ this.socket.getRemoteSocketAddress());
					cerrar = true;
				}
			}

		} catch (Exception e) {
			this.printer.println("ERROR");
		}
		
	}

	private void recibirArchivo(String header) {
		long tamanio, leido_total, max_leer;
		int leido_buffer;
		String nombre;
		FileOutputStream guardar;
		byte[] buffer = new byte[2048];

		tamanio = Long.parseLong(header.substring(4).replaceAll(" .*", ""));
		nombre = header.substring(4).replaceFirst("[^ ]* ", "");
		this.servidor.log("UPLOAD START "+tamanio+" "+nombre+"\t"+ this.socket.getRemoteSocketAddress());
		try {
			guardar = new FileOutputStream(ServidorArchivos.DIRECTORIO_ARCHIVOS+nombre);
			leido_total = 0l;
			leido_buffer = 0;
			while (leido_total < tamanio) {
				max_leer = tamanio - leido_total;
				if(max_leer >= buffer.length) {
					max_leer = buffer.length;
				}
				leido_buffer = this.input.read(buffer, 0, (int)max_leer);
				guardar.write(buffer, 0, leido_buffer);
				leido_total += leido_buffer;
			}
			guardar.close();
			this.servidor.log("UPLOAD COMPLETE "+tamanio+" "+nombre+"\t"+ this.socket.getRemoteSocketAddress());
		} catch (Exception e) {
			this.servidor.log("ERROR UPLOAD "+tamanio+" "+nombre+"\t"+ this.socket.getRemoteSocketAddress());
		}
	}

	private String getLinea() {
		int leido=0;
		ByteArrayOutputStream barray = new ByteArrayOutputStream();
		boolean continuar = true;

		while (continuar) {
			try {
				leido = this.input.read();
				if (leido == '\r') {
				//hacer nada
				} else if (leido == '\n') {
					continuar = false;
				} else {
					barray.write(leido);
				}
			} catch (Exception e) {
				continuar = false;
			}
		}
		return barray.toString();
	}
}
