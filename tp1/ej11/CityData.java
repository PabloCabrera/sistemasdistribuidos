package tp1.ej11;

import java.util.HashMap;

public class CityData {
	private static CityData instance;
	private HashMap <String, Integer> timezones;
	
	private CityData () {
		this.timezones = new HashMap <String, Integer> ();
		this.addCity ("HAWAII", -10);
		this.addCity ("HONOLULU", -10);
		this.addCity ("ALASKA", -9);
		this.addCity ("LOS ANGELES", -8);
		this.addCity ("LAS VEGAS", -8);
		this.addCity ("MEXICO", -6);
		this.addCity ("ONTARIO", -6);
		this.addCity ("BOGOTA", -5);
		this.addCity ("MIAMI", -5);
		this.addCity ("NEW YORK", -5);
		this.addCity ("WASHINGTON", -5);
		this.addCity ("CARACAS", -4);
		this.addCity ("BRAZILIA", -3);
		this.addCity ("MONTEVIDEO", -3);
		this.addCity ("RIO DE JANEIRO", -3);
		this.addCity ("BUENOS AIRES", -3);
		this.addCity ("LUJAN", -3);
		this.addCity ("CORDOBA", -3);
		this.addCity ("MENDOZA", -3);
		this.addCity ("MADRID", 1);
		this.addCity ("AMSTERDAM", 1);
		this.addCity ("BERLIN", 1);
		this.addCity ("BUDAPEST", 1);
		this.addCity ("PARIS", 1);
		this.addCity ("ROMA", 1);
		this.addCity ("VIENNA", 1);
		this.addCity ("ATENAS", 2);
		this.addCity ("CAIRO", 2);
		this.addCity ("BAGDAD", 3);
		this.addCity ("MOSCU", 3);
		this.addCity ("KABUL", 4);
		this.addCity ("CALCUTA", 5);
		this.addCity ("BANKOK", 7);
		this.addCity ("BEIJING", 8);
		this.addCity ("HONG KONG", 8);
		this.addCity ("OSAKA", 9);
		this.addCity ("TOKIO", 9);
		this.addCity ("SYDNEY", 10);
	}

	private void addCity (String name, int hour) {
		this.timezones.put (name, Integer.valueOf(hour));
	}
	
	public static Integer getTimeZone (String city) {
		CityData.createSingletonInstance ();
		
		return CityData.instance.timezones.get (city.toUpperCase());
	}

	private static void createSingletonInstance () {
		if (CityData.instance == null) {
			CityData.instance = new CityData ();
		}
	}
	
}
