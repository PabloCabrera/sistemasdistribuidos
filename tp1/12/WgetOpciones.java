import java.util.List;
import java.util.ArrayList;
import java.net.URL;

public class WgetOpciones {
	public String servidorProxy = null;
	public int puertoProxy = 8080;
	public String archivoLista = null;
	public String archivoLog = null;
	public boolean continuarIncompleta = false;
	public String usuarioHttp = null;
	public String passwordHttp = null;
	public int reintentos = 0;
	public String directorio = null;
	public boolean conservarFecha = false;
	public List<URL> urls;

	public WgetOpciones () {
		this.urls = new ArrayList<URL>();
	}
}
