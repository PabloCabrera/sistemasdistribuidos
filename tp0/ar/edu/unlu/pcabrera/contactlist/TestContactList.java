package ar.edu.unlu.pcabrera.contactlist;

import java.util.Iterator;

class TestContactList {
	public static void main (String[] args) {
		/* Simple example for testing contact sorting and listing */

		ContactList clist;
		Contact contact;
		Iterator iter;

		/* Create contact list and add some contacts */
		clist = new ContactList();
		clist.addContact (new Contact ("Paula", "023424285"));
		clist.addContact (new Contact ("Ignacio", "03354289"));
		clist.addContact (new Contact ("Sofia", "3535073"));
		clist.addContact (new Contact ("Julian", "129490"));
		clist.addContact (new Contact ("Camila", "0344581"));
		clist.addContact (new Contact ("Mariano", "9938202"));
		clist.addContact (new Contact ("Melina", "857492"));
		clist.addContact (new Contact ("Jorge", "0058332859"));

		/* Show contact list */
		/* We can use "for" because ContactList implements Iterable interface */
		for (Object element: clist) {
			contact = (Contact) element;
			System.out.println ("NOMBRE: " + contact.getName() +"	TELEFONO: " + contact.getPhone());
		}
	}
}
