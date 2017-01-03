package tp1.ej03;

import java.io.Serializable;

public class DownloadRequest implements Serializable {
	private static final long serialVersionUID = 1483421079L;

	private String recipient;

	public DownloadRequest (String recipient) {
		this.recipient = recipient;
	}

	public String getRecipient () {
		return this.recipient;
	}
}