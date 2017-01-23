package tp1.ej07;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1483420687L;
	private String recipient;
	private String text;

	public Message (String recipient, String text) {
		this.recipient = recipient;
		this.text = text;
	}

	public String getRecipient () {
		return this.recipient;
	}

	public String getText () {
		return this.text;
	}
}
