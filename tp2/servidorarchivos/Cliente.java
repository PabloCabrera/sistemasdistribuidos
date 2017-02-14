package tp2.servidorarchivos;

import java.util.Scanner;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class Cliente {
	Socket socket = null;
	OutputStream out;
	PrintStream printer;
	InputStream in;


	public Cliente (String servidor, int puerto) throws UnknownHostException, IOException {
		this.socket = new Socket(servidor, puerto);
		out = socket.getOutputStream();
		in = socket.getInputStream();
		printer = new PrintStream (out);
	}

	public void interactivo() {
		Scanner s= new Scanner(System.in);
		String msj, respuesta;
		boolean continuar=true;

		if (this.socket == null) {
			System.out.println("No se ha conectado a un servidor");
			continuar = false;
		} else {
			System.out.println("Comandos:");
			System.out.println("\tget nombre_archivo : Descargar archivo del servidor");
			System.out.println("\tput nombre_archivo : Subir archivo al servidor");
			System.out.println("\tdir: Mostrar archivos en servidor");
			System.out.println("\tdel nombre_archivo: Eliminar archivo del servidor");
			System.out.println("\tquit: Salir");
			System.out.println("");
		}

		while(continuar){
			try {
				System.out.print("> ");
				msj=s.nextLine();
				if(msj.matches("(?i)^put .*$")){
					this.put(msj);
					respuesta = "";
				} else {
					this.printer.println(msj);
					this.printer.flush();
					respuesta = this.getLinea();
				
 					if(msj.matches("(?i)quit")){
						continuar=false;
					}
				}
				if (respuesta.startsWith("MSG ")) {
					System.out.println(respuesta.substring(4));
				} else if (respuesta.startsWith("FILE ")) {
					System.out.println("Se recibira un archivo");
					this.recibirArchivo(respuesta);
				} else if (respuesta.startsWith("ERROR")) {
					System.out.println("Se ha producido un error");
				} else if (respuesta.startsWith("OK")) {
					System.out.println("OK");
				}
			} catch (Exception e) {
				System.out.println("ERROR");
				continuar=false;
				e.printStackTrace();
			}
		}
	}

	private void recibirArchivo(String header) {
		long tamanio, leido_total, max_leer;
		int leido_buffer;
		String nombre;
		FileOutputStream guardar;
		byte[] buffer = new byte[2048];

		tamanio = Long.parseLong(header.substring(5).replaceAll(" .*", ""));
		nombre = header.substring(5).replaceFirst("[^ ]* ", "");
		System.out.println("TRANSFIRIENDO <<"+nombre+">> ("+tamanio+" Bytes)");
		try {
			guardar = new FileOutputStream(nombre);
			leido_total = 0l;
			leido_buffer = 0;
			while (leido_total < tamanio) {
				max_leer = tamanio - leido_total;
				if(max_leer >= buffer.length) {
					max_leer = buffer.length;
				}
				leido_buffer = this.in.read(buffer, 0, (int)max_leer);
				guardar.write(buffer, 0, leido_buffer);
				System.out.print("*");
				leido_total += leido_buffer;
			}
			guardar.close();
		System.out.println("\nTRANSFERENCIA COMPLETA <<"+nombre+">> ("+tamanio+" Bytes)");
		} catch (Exception e) {
			System.out.println("Se ha producido un error: "+e.getMessage());
		}
	}

	private void put (String comando) {
		String nombre = comando.substring(4);
		File file = new File (nombre);

		if (file.exists()){
			if(!file.isDirectory()) {
				try {
					long tamanio = file.length();
					this.printer.println("PUT "+tamanio+" "+file.getName());
					this.enviarArchivo(file);
				} catch (Exception e) {
					System.out.println("Se ha producido un error: "+e.getMessage());
				}
			} else {
				System.out.println("No se puede enviar el archivo "+nombre+", es un directorio.");
			}
		} else {
			System.out.println("El archivo "+nombre+" no existe.");
		}
		
	}

	private void enviarArchivo (File file) {
		// Esto se ejecuta despues de enviar la cabecera
		try {
			boolean cerrar = false;
			int leidos = 0;
			byte[] buffer = new byte[2048];
			FileInputStream finput = new FileInputStream (file);
			System.out.println("TRANSFIRIENDO ARCHIVO <<"+file.getName()+">> ("+file.length()+" Bytes)");

			while (!cerrar) {
				try {
					leidos = finput.read(buffer, 0, 2048);

					if (leidos == -1) {
						cerrar= true;
					} else if (leidos > 0) {
						this.out.write(buffer, 0, leidos);
						System.out.print("+");
					}
				} catch (IOException e) {
					System.out.println("\nSe ha producido un error: "+e.getMessage());
					cerrar = true;
				}
			}
			System.out.println("\nTRANSFERENCIA COMPLETA <<"+file.getName()+">> ("+file.length()+" Bytes)");

		} catch (Exception e) {
			this.printer.println("Se ha producido un error: "+e.getMessage());
		}
		
	}


	private String getLinea() {
		int leido=0;
		ByteArrayOutputStream barray = new ByteArrayOutputStream();
		boolean continuar = true;

		while (continuar) {
			try {
				leido = this.in.read();
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

	public static void main(String args[]) {
		String servidor=null;
		int puerto=-1;
		Scanner sc;
		Cliente cl;

		if (args.length == 2) {
		} else {
			sc = new Scanner(System.in);
			System.out.print("Conectarse al servidor: ");
			servidor = sc.nextLine();
			if (servidor.length() == 0) {
				System.out.println("Warning: No se ha especificado un servidor. Se utilizara localhost"); 
			}
			System.out.print("Puerto: ");
			try {
				puerto = Integer.parseInt(sc.nextLine());
			} catch (Exception e) {
				System.out.println("Warning: Numero de puerto no valido. Se utilizara el puerto predeterminado 9019");
				puerto = 9019;
			}

			if (puerto > 0) {
				try {
					cl = new Cliente(servidor, puerto);
					cl.interactivo();
				} catch (Exception e) {
					System.out.println("Error: No se puede conectar al servidor");
				}
			}
		}
	}

}
