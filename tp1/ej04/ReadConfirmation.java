package tp1.ej04;

import java.io.Serializable;

class ReadConfirmation implements Serializable {
	public static long SerialUID = 1483489673L;
	private String recipient;
	private int idMessage;

	public ReadConfirmation (Message message) {
		this.recipient = message.getRecipient ();
		this.idMessage = message.getId ();
	}

	public String getRecipient () {
		return this.recipient;
	}

	public int getIdMessage () {
		return this.idMessage;
	}

}
