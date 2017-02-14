package tp2.relojes;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ThreadHoraWeb extends ThreadHoraLocal {

	public ThreadHoraWeb(Socket so) {
		super(so);
	}
	
	@Override
	protected String conseguirHora(){
		String hora="Hora Web: no pude recuperar la hora";
		
		//pagina que voy a pedir
		String nueva_url= "http://24timezones.com/world_directory/current_buenos_aires_time.php";
		try {
			URL obj = new URL(nueva_url);
			obj.openConnection();
			Document document = Jsoup.connect(nueva_url).get();
			//con esto obtengo el HTML 
			//puedo buscar datos en el HTML como si fuera JQUERY, busco mediante los id que tenia la pagina
			String horaWeb = document.select("#currentTime").text();
			//recorto los datos para tener la hora
			hora ="Hora Web: "+ horaWeb.substring(0, 9);
		} catch (IOException e) {
			System.out.println("Hora Web: no pude recuperar la hora");
			e.printStackTrace();
		}
		return hora;
	}
	
}
