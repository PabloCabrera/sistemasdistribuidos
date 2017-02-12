package tp1.ej12;

import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Download {
	private URL url;
	private WgetOptions options;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private PrintStream printer;
	private String redirect = null;
	

	public Download (URL url, WgetOptions options) {
		this.url = url;
		this.options = options;
	}

	public boolean connect() {
		int puerto;
		String servidor;

		if (this.options.proxyServer == null) {
			servidor = this.url.getHost();
			puerto = this.url.getPort();

			if (puerto == -1 && this.url.getProtocol().equals("http")) {
				puerto = 80;
			}
		} else {
			servidor = options.proxyServer;
			puerto = options.puertoProxy;
			System.out.println ("Utilizando el servidor proxy "+ servidor + ":"+puerto);
			options.log ("Utilizando el servidor proxy "+ servidor + ":"+puerto);
		}

		try {
			assert (puerto > 0);
			assert (servidor != null);

			this.socket = new Socket (servidor, puerto);
			this.input = socket.getInputStream ();
			this.output = socket.getOutputStream ();
			this.printer = new PrintStream (this.output);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean sendHeaders () {
		String enviar ="";
		try {
			if(options.proxyServer == null) {
				enviar=("GET "+ this.url.getFile() +" HTTP/1.1\r\n");
			} else {
				enviar=("GET "+ this.url +" HTTP/1.1\r\n");
			}
			this.printer.print(enviar);
			this.options.log("-> "+enviar);
			if (options.proxyServer == null) {
				enviar=("Host: "+this.url.getHost()+"\r\n");
				this.printer.print(enviar);
				this.options.log("-> "+enviar);
			}
			enviar=("Connection: close\r\n\r\n");
			this.printer.print(enviar);
			this.options.log("-> "+enviar);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String getLine() {
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

	public int receiveHeaders() {
		boolean fin_header = false;
		String line;
		String str_response_code;
		int response_code = 200;

		while (!fin_header) {
			try {
				line = this.getLine();
				if(line != null && line.length() > 0 ) {
					if (line.matches ("^HTTP/.*")) {
						str_response_code = line.replaceFirst("^[^ ]* ", "");
						str_response_code = str_response_code.replaceFirst(" .*$", "");
						response_code = Integer.parseInt(str_response_code);
					} else if (line.matches ("^Location: .*$")) {
						this.redirect = line.replaceFirst ("Location: ", "");
					}
					options.log ("<- " + line);
				} else {
					fin_header = true;
				}
			} catch (Exception e) {
				return -1;
			}
		}
		return response_code;
	}

	public boolean receiveRecurso(OutputStream guardar) {
		boolean cerrar = false;
		String line;
		byte[] buffer = new byte[2049];
		int leidos = 0;

		while (!cerrar) {
			try {
				leidos = this.input.read(buffer, 0, 2048);

				if (leidos == -1) {
					cerrar= true;
				} else if (leidos == 0) {
					System.err.println("..Esperando datos..");
				} else {
					guardar.write(buffer, 0, leidos);
				}
			} catch (IOException e) {
				System.err.println("Ha ocurrido un error de I/O");
				cerrar = true;
			}
		}
		try {
			this.socket.close();
		} catch (Exception e) {
			System.err.println("No se pudo cerrar el socket");
		}
		return true;
	}

	public String getRedirect () {
		return this.redirect;
	}

}
