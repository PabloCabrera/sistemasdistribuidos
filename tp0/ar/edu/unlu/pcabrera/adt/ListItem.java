package ar.edu.unlu.pcabrera.adt;

//Class is not public. Only accesible from same package
class ListItem {
	protected Object data = null; // Stored element
	//private ListItem previous = null; // Previous item in double-linked list
	private ListItem next = null; // Next element in either single or double linked list

	public ListItem () {
		/* Create empty ListItem */
	}

	public ListItem (Object data) {
		/* Create ListItem storing data */
		this.data = data;
	}

	public void append (Object element) {
		/* Append element at the end of the list */
		if (this.next == null) {
			this.next = new ListItem (element);
		} else {
			this.next.append(element);
		}
	}

	public ListItem getItem (int index) {
		/* Get an item  */
		if (index == 0) {
			return this;
		} else if (this.next != null) {
			return this.next.getItem (index -1);
		} else {
			return null;
		}
	}

	public void insert (Object item, int position) throws IndexOutOfBoundsException {
		ListItem nlitem;
		if (position == 0) {
			nlitem = new ListItem (this.data);
			nlitem.setNextItem (this.next);
			this.data = item;
			this.next = nlitem;
		} else if (this.next == null) {
			this.append (item);
		} else if (position < 0) {
			throw new IndexOutOfBoundsException ();
		} else {
			this.next.insert (item, position -1);
		}
	}

	public Object getData () {
		return this.data;
	}

	public void setData (Object data) {
		this.data = data;
	}


	public ListItem getNextItem () {
		return this.next;
	}

	public void setNextItem (ListItem item) {
		this.next = item;
	}

}

