package tp1.ej12;

import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Wget {


	private static boolean download (URL url, WgetOptions options) {
		boolean state = true;
		int code;
		String nameToSave = null;
		OutputStream streamToSave = null;
		URL urlRedirect;
		String strRedirect;

		if (!url.getProtocol().equals("http")) {
			System.err.println ("Protocolo no soportado " +url.getProtocol());
			return false;
		}

		try {

			Download download = new Download (url,options);

			System.out.println ("Conectando con "+url);
			options.log ("Conectando con "+url);
			state = download.connect();
			assert (state) : "No se pudo connect";

			System.out.println ("Enviando peticion...");
			options.log ("Enviando peticion a "+url);
			state = download.sendHeaders();
			assert (state) : "No se pudo enviar peticion";

			System.out.println ("Recibiendo cabeceras...");
			options.log ("Recibiendo cabeceras de "+url);

			code = download.receiveHeaders();
			System.out.print (code+ ": ");

			if (code >= 500) {
				System.err.println ("Error interno del servidor");
				return false;
			} else if (code == 404) {
				System.err.println ("No encontrado");
				return false;
			} else if (code == 403) {
				System.err.println ("Prohibido");
				return false;
			} else if (code == 400) {
				System.err.println ("Peticion no valida");
				return false;
			} else if (code > 400) {
				System.err.println ("La peticion no puede ser satisfecha o es inconrrecta");
				return false;
			} else if (code >= 300) {
				strRedirect = download.getRedirect();
				System.out.println ("Redirigiendo a "+strRedirect);
				urlRedirect = new URL(strRedirect);
				return Wget.download (urlRedirect, options);
			} else {
				System.out.println ("OK");
			}

			System.out.println ("Recibiendo datos...");
			nameToSave = url.toString().replaceAll(".*/", "");
			if (nameToSave.length() == 0) {
				nameToSave = url.toString().replaceAll("[:/]+", ".")+"html";
			}
			if (options.directorio != null) {
				nameToSave = options.directorio + "/" + nameToSave;
			}
			System.out.println ("Descargando en " + nameToSave);
			streamToSave = new FileOutputStream (nameToSave);
			state = download.receiveRecurso(streamToSave);
			streamToSave.flush();
			streamToSave.close();
			streamToSave = null;
			assert (state) : "No se recibio datos";

		} catch (AssertionError e) {
			System.err.println ("assert error: " + e.getMessage());
			return false;
		} catch (FileNotFoundException|SecurityException e) {
			System.err.println ("No se puede crear el archivo "+ nameToSave+": " + e.getMessage());
			return false;
		} catch (MalformedURLException e) {
			System.err.println ("No se puede redirigir correctamente. URL no valida");
		} catch (IOException e) {
			System.err.println ("Error de entrada/salida");
		}
		
		return true;
	}

	
	public static void main (String[] args) {
		WgetOptions options;
		List<URL> urls;

		if (args.length == 0) {
			showHelp();
		} else {
			urls = new ArrayList<URL>();
			options = new WgetOptions();

			Wget.parseArgs(args, options, urls);

			for (URL url: urls) {
				download (url, options);
			}
		}

	}

	public static WgetOptions parseArgs (String[] args, WgetOptions options, List<URL> urls) {
		int parseados = 0;
	
		while (parseados < args.length) {
			parseados += parseArg(args, parseados, options, urls);
		}

		return options;
	}

	public static int parseArg(String[] args, int offset, WgetOptions options, List<URL> urls) {
		int cantidad = 1;
		URL url;

		if (args[offset].charAt(0) == '-') {
			if (args[offset].equals("-n")) {
				options.conservarFecha = true;
			} else if (args[offset].equals("-c")) {
				options.continuarIncompleta = true;
			} else if (args[offset].equals("-i")) {
				if (args.length > offset +1) {
					BufferedReader archivoLista;
					String linea;
					URL urlLinea;

					cantidad++;
					try {
						archivoLista = new BufferedReader (new FileReader (args[offset+1]));
						while ((linea = archivoLista.readLine()) != null) {
							try {
								urlLinea = parseUrl (linea);
								urls.add(urlLinea);
							} catch (MalformedURLException e) {
								System.err.println("URL no valida "+ linea);
							}
						}
						archivoLista.close();
					} catch (FileNotFoundException e) {
						System.err.println("No se puede abrir el archivo de lista de downloads "+ args[offset+1]);
					} catch (IOException e) {
						System.err.println("Error de entrada/salida "+ args[offset+1]);
					}
				} else {
					System.err.println("La opcion -i requiere un argumento");
				}
			} else if (args[offset].equals("-o")) {
				if (args.length > offset +1) {
					cantidad++;
					try {
						OutputStream fos = new FileOutputStream (args[offset+1], true);
						options.archivoLog = new PrintStream (fos);
					} catch (FileNotFoundException e) {
						System.err.println("No se puede abrir el archivo de log "+ args[offset+1]);
					} catch (SecurityException e) {
						System.err.println("No se disponen de permisos suficientes para escribir el archivo de log "+ args[offset+1]);
					}
				} else {
					System.err.println("La opcion -o requiere un argumento");
				}
			} else if (args[offset].equals("-p")) {
				if (args.length > offset +1) {
					cantidad++;
					options.directorio = args[offset+1];
				} else {
					System.err.println("La opcion -http-user requiere un argumento");
				}
			} else if (args[offset].equals("-http-user")) {
				if (args.length > offset +1) {
					cantidad++;
					options.usuarioHttp = args[offset+1];
				} else {
					System.err.println("La opcion -http-user requiere un argumento");
				}
			} else if (args[offset].equals("-http-password")) {
				if (args.length > offset +1) {
					cantidad++;
					options.passwordHttp = args[offset+1];
				} else {
					System.err.println("La opcion -http-password requiere un argumento");
				}
			} else if (args[offset].equals("-t")) {
				if (args.length > offset +1) {
					cantidad++;
					try {
						options.reintentos = Integer.parseInt(args[offset+1]);
					} catch (NumberFormatException e) {
						System.err.println("El argumento de la opcion -t debe ser un numero");
					}
				} else {
					System.err.println("La opcion -t requiere un argumento");
				}
			} else if (args[offset].equals("-http-proxy")) {
				if (args.length > offset +1) {
					cantidad++;
					if (args[offset+1].indexOf(':') != -1) {
						options.proxyServer = args[offset+1].replaceFirst(":.*$", "");
						try {
							options.puertoProxy = Integer.parseInt (args[offset+1].replaceFirst("^.*:", ""));
						} catch (NumberFormatException e) {
							System.err.println("Puerto no valido para servidor proxy");
						}
					} else {
						options.proxyServer = args[offset+1];
					}
				} else {
					System.err.println("La opcion -http-proxy requiere un argumento");
				}
			} else {
				System.err.println ("Opcion no soportada "+ args[offset]);
			}
		} else {
			try {
				url = parseUrl(args[offset]);
				urls.add(url);
			} catch (MalformedURLException e) {
				System.err.println ("URL no valida "+ args[offset]);
			}
		}

		return cantidad;
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

	public static void showHelp() {
		System.err.println("No se han especifcado URLs para download");
		System.err.println("Uso: java Wget [options] URLs");
		System.err.println("Opciones:");
		System.err.println("\t -i archivo_lista");
		System.err.println("\t -o archivo_log");
		System.err.println("\t -p directorio_download");
		System.err.println("\t -http-proxy servidor:puerto");
	}

}

