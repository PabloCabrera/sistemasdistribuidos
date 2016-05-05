import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Wget {
	
	private static boolean descargar (URL url, WgetOpciones opciones) {
		boolean estado = true;
		int codigo;
		String nombreGuardar = null;
		OutputStream streamGuardar = null;

		try {

			Descarga descarga = new Descarga (url,opciones);

			System.out.println ("Conectando con "+url);
			estado = descarga.conectar();
			assert (estado) : "No se pudo conectar";

			System.out.println ("Enviando peticion...");
			estado = descarga.enviarHeaders();
			assert (estado) : "No se pudo enviar peticion";

			System.out.println ("Recibiendo cabeceras...");
			codigo = descarga.recibirHeaders();
			assert (codigo >= 200) : ("Codigo de respuesta: "+codigo);

			System.out.println ("Recibiendo datos...");
			nombreGuardar = url.toString().replaceAll(".*/", "");
			if (nombreGuardar.length() == 0) {
				nombreGuardar = "index.html";
			}
			System.out.println ("Se guardara en el archivo " + nombreGuardar);
			streamGuardar = new FileOutputStream (nombreGuardar);
			estado = descarga.recibirRecurso(streamGuardar);
			assert (estado) : "No se recibio datos";

		} catch (AssertionError e) {
			System.err.println ("assert error: " + e.getMessage());
			return false;
		} catch (FileNotFoundException|SecurityException e) {
			System.err.println ("No se puede crear el archivo "+ nombreGuardar+": " + e.getMessage());
			return false;
		}
		
		return true;
	}

	
	public static void main (String[] args) {
		URL url = null;

		if (args.length == 0) {
			mostrarAyuda();
		} else {

			try {
				url = parseUrl (args[0]);
				descargar (url, null);
			} catch (MalformedURLException e) {
				System.err.println ("URL invalida");
			}
		}

	}

	public static URL parseUrl (String urlString) throws MalformedURLException {
		URL url = null;
		if (urlString.matches("^/+.*$")) {
			// Borrar los slashes del comenzo
			urlString = urlString.replaceFirst("/+", "");
		}

		if (!urlString.matches("^[\\da-zA-Z]+://.*$")) {
			// Agregar el protocolo si no esta puesto
			urlString = "http://" + urlString;
		}

		if (urlString.matches("^[\\da-zA-Z]://[^/]*$")) {
			urlString = urlString + "/";
		}

		try {
			url = new URL (urlString);
		} catch (MalformedURLException e) {
			url = new URL (urlString);
		}

		return url;
	}

	public static void mostrarAyuda() {
		System.err.println("Wget: Falta URL");
		System.err.println("Uso: java Wget [opciones] URL");
	}
}

