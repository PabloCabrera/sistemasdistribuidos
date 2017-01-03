package tp1.ej03;

import misc.Debugger;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class Client {
	private String serverName = "localhost";
	private int port = 8932;
	private Scanner scanner;
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public Client () {
		this.scanner = new Scanner (System.in);

		if (this.connect ()) {
			this.mainMenu();
		} else {
			this.exit ();
		}
	}

	private boolean connect () {
		System.out.println ("Conectandose a " + this.serverName + " en el puerto " + this.port);
		try {
			this.socket = new Socket (this.serverName, this.port);
			this.output = new ObjectOutputStream (socket.getOutputStream ());
			this.input = new ObjectInputStream (socket.getInputStream ());
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return false;
		}
		return true;
	}

	private void mainMenu () {
		try {
			while (! this.socket.isClosed ()) {
				this.writeMenuText ();
				this.processMenuEntry ();
			}
		} catch (Exception exception) {
			Debugger.debugException (exception);
			this.exit ();
		}
	}

	private void writeMenuText() {
		System.out.print ("\n\n\n\n\n\n\n");
		System.out.println ("Escriba un numero para acceder a una funcion");
		System.out.println ("");
		System.out.println ("1. Leer mensajes");
		System.out.println ("2. Enviar mensaje");
		System.out.println ("");
		System.out.println ("3. Salir");
		System.out.print ("\n\n\n\n\n\n\n");
	}

	private void processMenuEntry () throws Exception {
		switch (this.scanner.nextLine ()) {
			case "1" :
				this.readMessages ();
				break;
			case "2" :
				this.writeMessage ();
				break;
			case "3":
				this.exit ();
				break;
		}
	}

	private void readMessages () throws Exception {
		String recipient = askRecipient ();
		DownloadRequest request = new DownloadRequest (recipient);
		this.output.writeObject (request);
		this.recieveMessages ();
	}

	private void recieveMessages () {
		Object recieved;
		boolean endOfQueue = false;

		try {
			while (!endOfQueue) {
				recieved = this.input.readObject();
				if (recieved instanceof Message) {
					this.displayMessage ((Message) recieved);
				} else if (recieved instanceof EndOfQueue) {
					endOfQueue = true;
				}
			}
		} catch (Exception exception ) {
			Debugger.debugException (exception);
			this.exit ();
		}
	}

	private void displayMessage (Message message) {
		System.out.println ("/===========================================\\");
		System.out.println ("Mensaje para: " + message.getRecipient());
		System.out.println ("=============================================");
		System.out.println (message.getText ());
		System.out.println ("=============================================");
		System.out.println ("Presione ENTER para continuar");
		System.out.println ("\\===========================================/");
		this.scanner.nextLine();
	}

	private void writeMessage () throws Exception {
		String recipient = askRecipient ();
		String text = askMessageText ();
		Message message = new Message (recipient, text);
		this.output.writeObject (message);
	}

	private void exit () {
		try {
			if (! this.socket.isClosed ()) {
				this.socket.close();
			}
		} catch (Exception exception) {
		}
	}

	private String askRecipient () {
		System.out.print ("Escriba el nombre del destinatario: ");
		return scanner.nextLine();
	}

	private String askMessageText () {
		String text = "";
		String line;
		int previousWhite = 0;

		System.out.println ("Escriba el cuerpo del mensaje.");
		System.out.println ("Deje 2 lineas en blanco para terminar.");
		System.out.println ("");

		try {
			while (previousWhite < 2) {
				line = scanner.nextLine();
				text = text + line + "\n";
				if (line.length () == 0) {
					previousWhite++;
				} else {
					previousWhite = 0;
				}
			}
		} catch (Exception exception) {
			Debugger.debugException (exception);
		}

		return text;
	}

	public static void main (String args[]) {
		Client client = new Client();
	}
}
