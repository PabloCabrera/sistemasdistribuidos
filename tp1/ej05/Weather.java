package tp1.ej05;

import java.util.Random;
import java.util.Calendar;
import java.io.Serializable;

class Weather implements Serializable {
	public static long SerialUID = 1483988005L;

	private String location;
	private float temperature;
	private float humidity;
	private Random random;
	
	public Weather () {
		this.random = new Random (Calendar.getInstance().getTimeInMillis());
		this.location = this.generateLocation();
		this.temperature = this.generateTemperature ();
		this.humidity = this.generateHumidity ();
	}

	public String getLocation () {
		return this.location;
	}

	public float getTemperature () {
		return this.temperature;
	}

	public float getHumidity () {
		return this.humidity;
	}

	private String generateLocation () {
		return "Buenos Aires, Argentina";
	}

	private float generateTemperature () {
		int dayOfYear = Calendar.getInstance().get (Calendar.DAY_OF_YEAR);
		return this.temperatureForDay (dayOfYear) + (-6+12*(this.random.nextFloat()));
	}

	private float temperatureForDay (int dayOfYear) {
		return (float) (17.d *  (1.d + Math.sin (2*(dayOfYear + 80) * Math.PI / 365.d)));
	}

	private float generateHumidity () {
		return 30 + 69 * this.random.nextFloat();
	}
}
