package tp1.ej05;

import misc.Debugger;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

class WeatherServer extends UnicastRemoteObject implements WeatherProvider {
	private Registry registry;

	public WeatherServer (int port) throws RemoteException {
		super();
		this.registry = LocateRegistry.createRegistry (port);
		this.registry.rebind ("weather", this);
	}

	@Override
	public Weather getWeather () throws RemoteException{
		return new Weather();
	}

	public static void main (String[] args) {
		try {
			WeatherServer server = new WeatherServer (7697);
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}
}