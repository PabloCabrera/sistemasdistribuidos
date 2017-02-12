package tp1.ej12;

import java.net.URL;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WgetOptions {
	public String proxyServer = null;
	public int puertoProxy = 8080;
	public PrintStream archivoLog = null;
	public boolean continuarIncompleta = false;
	public String usuarioHttp = null;
	public String passwordHttp = null;
	public int reintentos = 0;
	public String directorio = null;
	public boolean conservarFecha = false;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS ");

	public WgetOptions () {
	}

	public void log(String texto) {
		if (this.archivoLog != null) {
			this.archivoLog.println (ahora() +" " + texto);
		}
	}

	public static String ahora() {
		return dateFormat.format(new Date());
	}
}
