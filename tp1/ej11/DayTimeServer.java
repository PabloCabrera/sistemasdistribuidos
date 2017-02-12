package tp1.ej11;

import java.util.Date;
import java.text.SimpleDateFormat;

public abstract class DayTimeServer extends Thread {
	private final static int PORT = 9013;

	public static void main (String[] args) {
		UdpServer udpServer = new UdpServer (PORT);
		udpServer.start();
	}

	public String getDayTime(String city) {
		Integer timezone = CityData.getTimeZone (city);
		if (timezone != null) {
			long current = System.currentTimeMillis();
			long localTime = current + ((timezone.longValue()*60l)*60l*1000l);
			Date date = new Date(localTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formatedDate = formatter.format (date);
			String response = city + ": " + formatedDate + " GMT" + ((timezone.intValue()<0)?"":"+") + timezone;
			return response;
		}
		return "Zona horaria desconocida para la ciudad "+city;
	}
}