package tp1.ej04;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1483420687L;
	private String recipient;
	private String text;
	private int id;

	public Message (String recipient, String text) {
		this.recipient = recipient;
		this.text = text;
	}

	public int getId () {
		return this.id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getRecipient () {
		return this.recipient;
	}

	public String getText () {
		return this.text;
	}
}
