package tp2.relojes;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ThreadHoraWeb extends ThreadHoraLocal {

	public ThreadHoraWeb(Socket so) {
		super(so);
	}
	
	@Override
	protected Date conseguirHora(){
		SimpleDateFormat formato_hora = new SimpleDateFormat("hh:mm:ss a");
		Date horaRespuesta = null;
		//String hora="Hora Web: no pude recuperar la hora";
		
		//pagina que voy a pedir
		String nueva_url= "http://24timezones.com/world_directory/current_buenos_aires_time.php";
		try {
			URL obj = new URL(nueva_url);
			obj.openConnection();
			Document document = Jsoup.connect(nueva_url).get();
			//con esto obtengo el HTML 
			//puedo buscar datos en el HTML como si fuera JQUERY, busco mediante los id que tenia la pagina
			String horaWeb = document.select("#currentTime").text();
			System.out.println ("Hora recibida de web: " + horaWeb);
			//recorto los datos para tener la hora
			horaWeb = horaWeb.substring(0, 11);
			horaRespuesta = formato_hora.parse (horaWeb);
		} catch (Exception e) {
			System.out.println("Hora Web: no pude recuperar la hora");
			e.printStackTrace();
		}
		return horaRespuesta;
	}
	
}
