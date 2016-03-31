package ar.edu.unlu.pcabrera.contactlist;

import ar.edu.unlu.pcabrera.adt.LinkedList;
import java.util.Iterator;

public class ContactList extends LinkedList {

	public void addContact (Contact newContact) {
		/* Add a contact to the list while keeping the list alphabetically sorted. */
		Iterator iter;
		Contact currentContact;
		Object element;
		String newName, currentName;
		boolean cont = true; //continue searching?
		int pos = 0;

		iter = this.iterator ();
		while (iter.hasNext() && cont) {
			element = iter.next();
			if (element instanceof Contact) {
				currentContact = (Contact) element;
				currentName = currentContact.getName();
				newName = newContact.getName();

				if (currentName.toLowerCase().compareTo(newName.toLowerCase()) > 0) {
					cont = false;
				} else {
					pos++;
				}
				
			} else {
				pos++;
			}
		}
		this.insert (newContact, pos);
	
	}



}
