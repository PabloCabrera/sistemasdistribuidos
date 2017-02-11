package tp1.ej06;

import misc.Debugger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

class SumClient {
	private Registry registry;
	private SumProvider provider;

	public SumClient (String server, int port) {
		this.connect (server, port);
	}

	private boolean connect (String server, int port) {
		try {
			this.registry = LocateRegistry.getRegistry(server, port);
			this.provider = (SumProvider)(registry.lookup("vectorSum"));
		} catch (RemoteException exception) {
			System.err.println ("No se puede conectar al servidor " + server + " en el puerto "+port);
			return false;
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return false;
		}
		return true;
	}

	public boolean isConnected() {
		return (this.provider != null);
	}

	public void displaySum(int[] vect1, int[] vect2) throws RemoteException {
		System.out.print (this.vectorToString(vect1));
		System.out.print (" + ");
		System.out.print (this.vectorToString(vect2));
		System.out.print (" = ");
		System.out.println  (this.vectorToString(this.sum(vect1, vect2)));
	}

	private static String vectorToString (int[] vector) {
		String out = "[";
		for (int index = 0; index < vector.length -1; index++) {
			out += vector[index];
			out += ", ";
		}
		out += vector[vector.length-1] + "]";
		return out;
	}

	public int[] sum (int[] vect1, int[] vect2) throws RemoteException {
		return this.provider.sum (vect1, vect2);
	}

	public static void main (String[] args) {
		SumClient client = new SumClient ("localhost", 5106);
		if (client.isConnected()) {
			int[] v1 = {10, 12, 14, 16};
			int[] v2 = {4, 7 , 2, 8};
			try {
				System.out.println("Valor actual de v1: "+SumClient.vectorToString (v1));
				client.displaySum (v1, v2);
				System.out.println("Valor actual de v1: "+SumClient.vectorToString (v1));
			} catch (Exception exception) {
				Debugger.debugException (exception);
			}
		}
	}
}
