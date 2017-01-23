package tp1.ej08;

import misc.Debugger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class TaskServer extends UnicastRemoteObject implements TaskExecutor {
	private Registry registry;
	private int port = 9808;

	public TaskServer () throws RemoteException {
		super ();
		this.registry = LocateRegistry.createRegistry (this.port);
		this.registry.rebind ("taskExecutor", this);
		System.out.println ("Se ha iniciado el servidor en el puerto " + port);
	}

	@Override
	public Object executeTask (Task task) throws RemoteException {
		return task.execute ();
	}

	public static void main (String[] args) {
		try {
			TaskServer server = new TaskServer ();
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}

}
