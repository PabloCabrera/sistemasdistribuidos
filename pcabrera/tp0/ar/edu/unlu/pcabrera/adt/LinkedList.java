package ar.edu.unlu.pcabrera.adt;

import java.util.Iterator;

public class LinkedList implements Iterable {
	private ListItem first = null;

	public LinkedList () {
	}

	public LinkedList (Object head) {
		this.first = new ListItem (head);
	}

	public void append (Object item) {
		if (this.first == null) {
			this.first = new ListItem (item);
		} else {
			this.first.append(item);
		}
	}

	public ListItem getItem (int index) throws IndexOutOfBoundsException {
		if (index < 1 || this.first == null) {
			throw new IndexOutOfBoundsException ();
		} else {
			return this.first.getItem (index -1);
		}
	}

	public Object get (int index) throws IndexOutOfBoundsException {
		ListItem item;
		item = this.getItem (index);
		return item.getData();
	}

	public boolean isEmpty () {
		return (this.first == null);
	}

	public void insert  (Object item, int position) throws IndexOutOfBoundsException {
		if (position == 0 && this.first == null) {
			this.append (item);
		} else if (position < 0 || this.first == null) {
			throw new IndexOutOfBoundsException ();
		} else {
			this.first.insert (item, position);
		}
	}

	public void remove (int position) throws IndexOutOfBoundsException {
		ListItem next, previous;
		if (position < 1 || this.first == null) {
			throw new IndexOutOfBoundsException ();
		} else if (position == 1) {
			this.first = this.getItem (2);
		} else {
			previous = this.getItem (position -1);
			next = this.getItem (position +1);
			previous.setNextItem (next);
		}
	}

	public Iterator <Object> iterator () {
		return new LinkedListIterator (this);
	}

}
