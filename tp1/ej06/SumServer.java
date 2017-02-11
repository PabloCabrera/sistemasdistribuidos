package tp1.ej06;

import misc.Debugger;
import java.util.Random;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

class SumServer extends UnicastRemoteObject implements SumProvider { 
	private Registry registry;

	public SumServer (int port) throws RemoteException {
		super();
		this.registry = LocateRegistry.createRegistry (port);
		this.registry.rebind ("vectorSum", this);
		System.out.println ("Se ha iniciado el servidor en el puerto " + port);
	}

	public int[] sum (int[] vect1, int[] vect2) throws RemoteException {
		int length = Math.min (vect1.length, vect2.length);
		int[] result = new int[length];
		this.modify (vect2);
		for (int index = 0; index < length; index++) {
			result[index] = vect1[index] + vect2[index];
		}
		return result;
	}

	private void modify (int[] vector) {
		vector[0]=8934;
	}

	public static void main (String[] args) {
		try {
			SumServer server = new SumServer (5106);
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}
}