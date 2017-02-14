package tp2.sobel;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.awt.image.BufferedImage;

public interface RemoteSobel extends Remote {
	/* Estados */
	public static final int NO_INICIADO = 0;
	public static final int TRABAJANDO = 1;
	public static final int COMPLETO = 2;
	public static final int FALLO = -1;
	public static final int DESCONECTADO = -2;

	/* Metodos */
	public SerializableImage sobel (SerializableImage image) throws RemoteException, Exception;
	public int getEstado () throws RemoteException;
}
