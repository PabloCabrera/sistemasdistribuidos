package tp1.ej07;

import misc.Debugger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class MessageServer extends UnicastRemoteObject implements MessageService {
	private Map <String, List<Message>> messages;
	private int port;
	private Registry registry;

	public MessageServer (int port) throws Exception {
		super();
		this.messages = new HashMap <String, List <Message>> ();
		this.start (port);
	}

	private void start (int port) throws RemoteException {
		this.registry = LocateRegistry.createRegistry (port);
		this.registry.rebind ("messageService", this);
		System.out.println ("Se ha iniciado el servidor en el puerto " + port);
	}

	public void displayText (String text) {
		System.out.println (text);
	}

	@Override
	public List <Message> readMessages (String recipient) throws RemoteException {
		Debugger.write (recipient + " ha solicitado leer sus mensajes.");
		List <Message> messageList = this.getMessagesForRecipient (recipient);

		Debugger.write ("(" + messageList.size () + ") mensajes");
		return messageList;

	}

	@Override
	public void sendMessage (Message message) throws RemoteException {
		String recipient = message.getRecipient ();
		this.createMessageQueueIfNotExists (recipient);

		Debugger.write ("Enviar mensaje a " + recipient);
		List <Message> messageQueue = this.messages.get (recipient);
		messageQueue.add (message);
	}

	private void createMessageQueueIfNotExists (String recipient) {
		if (! this.messages.containsKey (recipient)) {
			this.messages.put (recipient, new ArrayList ());
		}
	}

	public List <Message> getMessagesForRecipient (String recipient) {
		this.createMessageQueueIfNotExists (recipient);
		return this.messages.get (recipient);
	}

	public static void main (String[] args) {
		try {
			MessageServer server = new MessageServer(8935);
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
		
	}

}
