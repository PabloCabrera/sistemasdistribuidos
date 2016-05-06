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
		URL urlRedireccion;
		String strRedireccion;

		if (!url.getProtocol().equals("http")) {
			System.err.println ("Protocolo no soportado " +url.getProtocol());
			return false;
		}

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
				System.err.print (codigo+ ": ");
			if (codigo >= 500) {
				System.err.println ("Error interno del servidor");
				return false;
			} else if (codigo == 404) {
				System.err.println ("No encontrado");
				return false;
			} else if (codigo == 403) {
				System.err.println ("Prohibido");
				return false;
			} else if (codigo == 400) {
				System.err.println ("Peticion no valida");
				return false;
			} else if (codigo > 400) {
				System.err.println ("La peticion no puede ser satisfecha o es inconrrecta");
				return false;
			} else if (codigo >= 300) {
				strRedireccion = descarga.getRedireccion();
				System.err.println ("Redirigiendo a "+strRedireccion);
				urlRedireccion = new URL(strRedireccion);
				return Wget.descargar (urlRedireccion, opciones);
			} else {
				System.err.println ("OK");
			}

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
		} catch (MalformedURLException e) {
			System.err.println ("No se puede redirigir correctamente. URL no valida");
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

		if (urlString.matches("^[\\da-zA-Z]+://[^/]*$")) {
			// Si no tiene una barra al final, agregarla
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

