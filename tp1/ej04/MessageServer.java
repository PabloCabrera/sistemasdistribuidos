package tp1.ej04;

import misc.Debugger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageServer {
	private int port;
	private ServerSocket serverSocket;
	private List <ServerConnection> connections;
	private Map <String, List<Message>> messages;
	private int lastMessageId = 1;

	public MessageServer (int port) {
		this.port = port;
		this.connections = new ArrayList <ServerConnection> ();
		this.messages = new HashMap <String, List <Message>> ();
		this.start ();
	}

	private void start() {
		try {
			this.openServerSocket();
			this.handleClientRequests();
		} catch (Exception exception) {
			Debugger.debugException (exception);
			this.close();
		}
	}

	private void openServerSocket() throws Exception {
		this.serverSocket = new ServerSocket (this.port);
		this.displayText ("Se ha iniciado el servidor de mensajes en el puerto "+port);
	}

	private void handleClientRequests() throws Exception {
		Socket socket;

		while (! this.serverSocket.isClosed ()) {
			socket = this.serverSocket.accept ();
			this.openServerConnection (socket);
		}
	}

	private void openServerConnection (Socket socket) {
		ServerConnection connection = new ServerConnection (this, socket);
		this.connections.add (connection);
		try {
			connection.start();
			this.displayText ("Se ha conectado un cliente desde " + socket.getRemoteSocketAddress ());
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}

	public void displayText (String text) {
		System.out.println (text);
	}

	public void sendMessage (Message message) {
		String recipient = message.getRecipient ();
		this.createMessageQueueIfNotExists (recipient);

		Debugger.write ("Enviar mensaje a " + recipient);
		message.setId (this.getNextMessageId());
		List <Message> messageQueue = this.messages.get (recipient);
		messageQueue.add (message);
	}

	private int getNextMessageId() {
		return ++this.lastMessageId;
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

	public void deleteReadedMessage (ReadConfirmation confirmation) {
		List <Message> recipientList = this.messages.get (confirmation.getRecipient ());
		if (recipientList != null) {
			this.deleteMessageFromList (recipientList, confirmation.getIdMessage ());
		}
	}

	public void deleteMessageFromList (List <Message> list, int idMessage) {
		Message message;
		int index = 0;
		while (index < list.size ()) {
			message = list.get (index);
			if (message.getId () == idMessage) {
				list.remove (message);
				Debugger.write ("Se ha eliminado el mensaje con ID " + idMessage);
			}
			index++;
		}
	}

	private void close() {
		for (ServerConnection connection: this.connections) {
			connection.close();
		}
		this.connections.clear();
	}

	public static void main (String[] args) {
		MessageServer server = new MessageServer(8933);
		
	}

}
