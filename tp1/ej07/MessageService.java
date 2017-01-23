package tp1.ej07;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageService extends Remote {
	public List <Message> readMessages (String recipient) throws RemoteException;
	public void sendMessage (Message message) throws RemoteException;
}
