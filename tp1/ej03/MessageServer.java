package tp1.ej03;

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

	private void close() {
		for (ServerConnection connection: this.connections) {
			connection.close();
		}
		this.connections.clear();
	}

	public static void main (String[] args) {
		MessageServer server = new MessageServer(8932);
		
	}

}
