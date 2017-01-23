package tp1.ej08;

import misc.Debugger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class Client {
	private Registry registry;
	private String serverName = "localhost";
	private int port = 9808;
	private TaskExecutor server;

	public Client () {
		this.connect ();
	}

	public Object executeTaskOnServer (Task task) throws RemoteException {
		return this.server.executeTask (task);
	}

	private boolean connect () {
		System.out.println ("Conectandose a " + this.serverName + " en el puerto " + this.port);
		try {
			this.registry = LocateRegistry.getRegistry(this.serverName, this.port);
			this.server = (TaskExecutor)(registry.lookup("taskExecutor"));
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return false;
		}
		return true;
	}

	public static void main (String[] args) {
		try {
			Client client = new Client();
			PiCalculator calculator = new PiCalculator ();
			for (int precision = 2; precision < 12; precision++) {
				calculator.setPrecision (precision);
				Double result = (Double) client.executeTaskOnServer (calculator);
				System.out.println ("Numero PI con " + precision + " decimales: " + result);
			}
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}
}