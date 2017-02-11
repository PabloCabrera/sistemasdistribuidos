package tp1.ej06;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SumProvider extends Remote {
	public int[] sum (int[] vect1, int[] vect2) throws RemoteException;
}
