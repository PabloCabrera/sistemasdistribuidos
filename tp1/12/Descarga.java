import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Descarga {
	private URL url;
	private WgetOpciones opciones;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private BufferedReader reader;
	private PrintStream printer;
	private String redireccion = null;
	//static private Pattern regexResultado = new Pattern.compile("^HTTP[^ ]* (\\d{3}).*");
	

	public Descarga (URL url, WgetOpciones opciones) {
		this.url = url;
		this.opciones = opciones;
	}

	public boolean conectar() {
		int puerto;
		String servidor;

		puerto = this.url.getPort();

		if (puerto == -1) {
			if (this.url.getProtocol().equals("https")) {
				puerto = 443;
			} if (this.url.getProtocol().equals("http")) {
				puerto = 80;
			}
		}
		
		servidor = this.url.getHost();

		try {
			assert (puerto > 0);
			assert (servidor != null);

			this.socket = new Socket (servidor, puerto);
			this.input = socket.getInputStream ();
			this.output = socket.getOutputStream ();
			this.reader  = new BufferedReader (new InputStreamReader (this.input));
			this.printer = new PrintStream (this.output);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean enviarHeaders () {
		try {
			this.printer.println("GET "+ this.url.getFile() +" HTTP/1.1");
			this.printer.println("Host: "+this.url.getHost());
			this.printer.println("Connection: close");
			this.printer.println();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public int recibirHeaders() {
		boolean fin_header = false;
		String linea;
		String str_codigo_respuesta;
		int codigo_respuesta = 200;

		while (!fin_header) {
			try {
				linea = this.reader.readLine();
				if(linea != null && linea.length() > 0 ) {
					if (linea.matches ("^HTTP/.*")) {
						str_codigo_respuesta = linea.replaceFirst("^[^ ]* ", "");
						str_codigo_respuesta = str_codigo_respuesta.replaceFirst(" .*$", "");
						codigo_respuesta = Integer.parseInt(str_codigo_respuesta);
					} else if (linea.matches ("^Location: .*$")) {
						this.redireccion = linea.replaceFirst ("Location: ", "");
					}
					//System.out.println("# " +linea);
				} else {
					fin_header = true;
				}
			} catch (Exception e) {
				return -1;
			}
		}
		return codigo_respuesta;
	}

	public boolean recibirRecurso(OutputStream guardar) {
		boolean cerrar = false;
		String linea;
		byte[] buffer = new byte[2048];
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
			guardar.flush();
			this.socket.close();
		} catch (Exception e) {
			System.err.println("No se pudo cerrar el socket");
		}
		return true;
	}

	public String getRedireccion () {
		return this.redireccion;
	}

}
