package tp1.ej04;

import misc.Debugger;
import java.util.List;
import java.net.Socket;
import java.net.SocketException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.EOFException;

class ServerConnection extends Thread {
	private MessageServer server;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	public ServerConnection (MessageServer server, Socket socket) {
		try {
			this.server = server;
			this.socket = socket;
			this.input = new ObjectInputStream (socket.getInputStream ());
			this.output = new ObjectOutputStream (socket.getOutputStream ());
		} catch (Exception exception) {
			Debugger.debugException (exception);
			this.close ();
		}
	}

	@Override
	public void run () {
		boolean connectionClosed = this.socket.isClosed();
		Object recieved;

		while (!connectionClosed) {
			try {
				recieved = this.input.readObject();
				this.processRecieved (recieved);
			} catch (SocketException exception) {
				this.server.displayText ("Se ha perdido la conexion con el cliente " + this.socket.getRemoteSocketAddress ());
				this.close ();
			} catch (EOFException exception) {
				this.server.displayText ("Cliente desconectado " + this.socket.getRemoteSocketAddress ());
				this.close ();
			} catch (Exception exception) {
				Debugger.debugException (exception);
				this.close ();
			}
			connectionClosed = this.socket.isClosed();
		}
	}

	private void processRecieved (Object recieved) {
		if (recieved instanceof Message) {
			this.server.sendMessage ((Message) recieved);
		} else if (recieved instanceof DownloadRequest) {
			this.readMessages(((DownloadRequest) recieved).getRecipient ());
		} else if (recieved instanceof ReadConfirmation) {
			this.server.deleteReadedMessage ((ReadConfirmation) recieved);
		}
	}

	private void readMessages (String recipient) {
		Debugger.write (recipient + " ha solicitado leer sus mensajes.");
		List <Message> messageList = this.server.getMessagesForRecipient (recipient);

		Debugger.write ("(" + messageList.size () + ") mensajes");
		for (Message message: messageList) {
			this.deliverMessage (message);
		}
		this.sendEndOfQueue ();
	}

	private void deliverMessage (Message message) {
		try {
			this.output.writeObject (message);
			this.output.flush ();
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}

	private void sendEndOfQueue () {
	try {
			this.output.writeObject (new EndOfQueue ());
			this.output.flush ();
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}
	}

	public void close () {
		this.closeInput();
		this.closeOutput();
		this.closeSocket();
	}

	private void closeInput () {
		try {
			this.input.close ();
		} catch (Exception e) {}
	}

	private void closeOutput () {
		try {
			this.output.close ();
		} catch (Exception e) {}
	}

	private void closeSocket () {
		try {
			this.socket.close ();
		} catch (Exception e) {}
	}
}
