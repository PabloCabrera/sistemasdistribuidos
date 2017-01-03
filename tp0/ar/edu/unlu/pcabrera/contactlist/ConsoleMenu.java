package ar.edu.unlu.pcabrera.contactlist;

import java.util.Scanner;
import ar.edu.unlu.pcabrera.contactlist.ContactList;
import ar.edu.unlu.pcabrera.contactlist.Contact;

class ConsoleMenu {
	public static void main (String[] args) {
		boolean quit = false;
		ContactList clist;
		Scanner scanner;
		String input;

		scanner = new Scanner (System.in);
		clist = new ContactList ();

		/* Loop: show main menu and parse input */
		while (!quit) {
			ConsoleMenu.printMenu();
			input = scanner.nextLine();
			if (input.equals ("1")) {
				ConsoleMenu.showContactList (clist, scanner);
			} else if (input.equals ("2")) {
				ConsoleMenu.inputContact(clist, scanner);
			} else if (input.equals ("3")) {
				ConsoleMenu.searchContactByName(clist, scanner);
			} else if (input.equals ("4")) {
				ConsoleMenu.searchContactByPhone(clist, scanner);
			} else if (input.equals ("0")) {
				quit = true;
			}
		}
	}

	private static void printMenu () {
		/* Show menu */
		System.out.print ("\033[H\033[2J");
		System.out.println ("1. Mostrar lista de contactos ");
		System.out.println ("2. Agregar contacto");
		System.out.println ("3. Buscar contacto por nombre");
		System.out.println ("4. Buscar contacto por telefono");
		System.out.println ("0. Salir");
		System.out.println ("");
	}

	private static void showContactList (ContactList clist, Scanner scanner) {
		/* Show contact list */

		Contact contact;

		System.out.print ("\033[H\033[2J");
		System.out.println ("LISTA DE CONTACTOS");
		for (Object element: clist) {
			contact = (Contact) element;
			System.out.print ("NOMBRE: " + contact.getName ());
			System.out.println ("  TELFONO: " + contact.getPhone ());
		}

		System.out.println ("Presione ENTER para volver al menu");
		scanner.nextLine ();
	}

	private static void inputContact (ContactList clist, Scanner scanner) {
		/* Show new contact dialog */

		String name, phone;
		Contact contact = null;

			System.out.print ("\033[H\033[2J");
			System.out.println ("AGREGAR CONTACTO");
			System.out.print ("NOMBRE: ");
			name = scanner.nextLine();

			System.out.print ("TELEFONO: ");
			phone = scanner.nextLine();

			contact = new Contact (name, phone);
			clist.addContact (contact);
	}
	
	private static void searchContactByName (ContactList clist, Scanner scanner) {
		/* Show search contact by name dialog */

		String input, needle, haystack;
		Contact contact;

		System.out.print ("\033[H\033[2J");
		System.out.println ("BUSCAR CONTACTO");
		System.out.print ("NOMBRE: ");
		input = scanner.nextLine();
		System.out.println ();
		System.out.println ("RESULTADOS:");
		for (Object element: clist) {
			contact = (Contact) element;
			haystack = contact.getName().toLowerCase();
			needle = input.toLowerCase();
			if (haystack.contains (needle)) {
				System.out.print ("NOMBRE: " + contact.getName());
				System.out.println ("  TELEFONO: " + contact.getPhone());
			}
		}
		System.out.println ();
		System.out.println ("Presione ENTER para continuar");
		scanner.nextLine();
	}

	private static void searchContactByPhone (ContactList clist, Scanner scanner) {
		/* Show search contact by name dialog */

		String input, needle, haystack;
		Contact contact;

		System.out.print ("\033[H\033[2J");
		System.out.println ("BUSCAR CONTACTO");
		System.out.print ("NUMERO: ");
		input = scanner.nextLine();
		System.out.println ();
		System.out.println ("RESULTADOS:");
		for (Object element: clist) {
			contact = (Contact) element;
			haystack = contact.getPhone();
			needle = input.toLowerCase();
			if (haystack.contains (needle)) {
				System.out.print ("NOMBRE: " + contact.getName());
				System.out.println ("  TELEFONO: " + contact.getPhone());
			}
		}
		System.out.println ();
		System.out.println ("Presione ENTER para continuar");
		scanner.nextLine();
	}
}
