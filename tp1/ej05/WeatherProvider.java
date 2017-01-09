package tp1.ej05;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface WeatherProvider extends java.rmi.Remote {
	public Weather getWeather () throws RemoteException;
}
