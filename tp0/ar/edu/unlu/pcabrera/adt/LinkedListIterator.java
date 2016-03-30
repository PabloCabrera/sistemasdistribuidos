package ar.edu.unlu.pcabrera.adt;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class LinkedListIterator implements Iterator <Object> {
	private LinkedList list = null;
	private ListItem currentItem = null;
	private int index = 0;
	private boolean removed = false;
	
	public LinkedListIterator (LinkedList list) {
		this.list = list;
	}

	public boolean hasNext () {

		if (this.currentItem == null) {
			return !this.list.isEmpty ();
		} else {
			return (this.currentItem.getNextItem () != null);
		}
	}

	public Object next () throws NoSuchElementException {
		this.index++;
		this.removed = false;
		this.currentItem = this.currentItem.getNextItem ();

		if (currentItem == null) {
			throw new NoSuchElementException ();
		} else {
			return this.currentItem.getData ();
		}
	}

	public void remove () throws IllegalStateException {
		if (this.currentItem == null || this.removed) {
			throw new IllegalStateException ();
		} else {
			this.removed = true;
			this.list.remove(this.index);
		}
	}

}
