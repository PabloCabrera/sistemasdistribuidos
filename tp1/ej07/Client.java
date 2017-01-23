package tp1.ej07;

import misc.Debugger;
import java.util.List;
import java.util.Scanner;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class Client {
	private String serverName = "localhost";
	private int port = 8935;
	private Scanner scanner;
	private Registry registry;
	private MessageService server;
	private boolean mustExit = false;

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
			this.registry = LocateRegistry.getRegistry(this.serverName, this.port);
			this.server = (MessageService)(registry.lookup("messageService"));
		} catch (Exception exception) {
			Debugger.debugException (exception);
			return false;
		}
		return true;
	}

	private void mainMenu () {
		try {
			while (! this.mustExit) {
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

	private void readMessages () {
		String recipient = this.askRecipient ();

		try {
			List<Message> messages = this.server.readMessages (recipient);
			for (Message message : messages) {
				displayMessage (message);
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
		this.server.sendMessage (message);
	}

	private void exit () {
		this.mustExit = true;
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
