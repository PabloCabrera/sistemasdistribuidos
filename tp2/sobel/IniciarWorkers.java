package tp2.sobel;

import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

// Esta clase inicia workers con nombres Sobel1, Sobel2 ...
public class IniciarWorkers {
	private static final int NUM_WORKERS = 4;

	public static void main(String args[]) {
		try {
			RemoteSobel[] workers = new RemoteSobel[NUM_WORKERS];
			for (int index=0; index<NUM_WORKERS; index++) {
				workers[index] = new WorkerSobel();
			}

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			for (int index=0; index<NUM_WORKERS; index++) {
				String nombre = "Sobel"+(index+1);
				registry.bind(nombre, (RemoteSobel) workers[index]);
				System.out.println("Se ha iniciado el worker "+nombre);
			}

			System.out.println(" ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
