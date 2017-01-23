package tp1.ej08;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskExecutor extends Remote {
	public Object executeTask (Task task) throws RemoteException;
}
