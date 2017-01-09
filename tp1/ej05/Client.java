package tp1.ej05;

import misc.Debugger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

class Client {
	private WeatherProvider provider = null;
	private Registry registry;

	public Client (String server, int port) {
		this.connect (server, port);
	}

	public boolean connect (String server, int port) {
		try {
			this.registry = LocateRegistry.getRegistry(server, port);
			provider = (WeatherProvider)(registry.lookup("weather"));
		} catch (RemoteException exception) {
			System.err.println ("No se puede conectar al servidor " + server + " en el puerto "+port);
			return false;
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return false;
		}
		return true;
	}

	public boolean isConnected () {
		return this.provider != null;
	}

	public void displayWeather() {
		try {
			Weather weather = this.provider.getWeather();
			this.printWeather (weather);
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}

	public void printWeather (Weather weather) {
		System.out.println ("Tiempo en " + weather.getLocation());
		System.out.println ("Temperatura: " + String.format ("%.1f", weather.getTemperature()) + "º C");
		System.out.println ("Humedad: " + String.format ("%.1f", weather.getHumidity()) + "%");
	}

	public static void main (String[] args) {
		Client client = new Client ("localhost", 7697);
		if (client.isConnected()) {
			client.displayWeather ();
		}
	}
}
