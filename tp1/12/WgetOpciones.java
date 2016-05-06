import java.net.URL;
import java.io.PrintStream;

public class WgetOpciones {
	public String servidorProxy = null;
	public int puertoProxy = 8080;
	public PrintStream archivoLog = null;
	public boolean continuarIncompleta = false;
	public String usuarioHttp = null;
	public String passwordHttp = null;
	public int reintentos = 0;
	public String directorio = null;
	public boolean conservarFecha = false;

	public WgetOpciones () {
	}
}
