package ar.edu.unlu.pcabrera.adt;

//Class is not public. Only accesible from same package
class ListItemDouble {
	private ListItemDouble previous = null;
	private Object data = null;
	private ListItemDouble next = null;

	public ListItemDouble () {
	}

	public ListItemDouble (Object data) {
		this.data = data;
	}

	public void append (Object item) {
		ListItemDouble neu;

		if (this.next == null) {
			neu = new ListItemDouble (item);
			this.next = neu;
			neu.setPreviousItem (this);
		} else {
			this.next.append(item);
		}
	}

	public ListItemDouble getItem (int index) {
		if (index == 0) {
			return this;
		} else if (this.next == null || index < 0) {
			return null;
		} else {
			return this.next.getItem (index -1);
		}
	}

	public Object getData () {
		return this.data;
	}

	public void insert (Object item, int position) throws IndexOutOfBoundsException {
		ListItemDouble nlitem;
		if (position == 0) {
			nlitem = new ListItemDouble (this.data);
			nlitem.setPreviousItem (this);
			nlitem.setNextItem (this.next);
			this.data = item;
			this.next = nlitem;
		} else if (this.next == null || position < 0) {
			throw new IndexOutOfBoundsException ();
		} else {
			this.next.insert (item, position -1);
		}
	}

	public ListItemDouble getNextItem () {
		return this.next;
	}

	public void setNextItem (ListItemDouble item) {
		this.next = item;
	}

	public ListItemDouble getPreviousItem () {
		return this.previous;
	}

	public void setPreviousItem (ListItemDouble item) {
		this.next = item;
	}
}

