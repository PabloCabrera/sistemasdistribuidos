package ar.edu.unlu.pcabrera.remotecontrol;

public class TextTVDisplay implements TVDisplay {
	private TV tv = null;

	public TextTVDisplay() {
		System.out.println ("TVDisplay en modo texto activada.");
		System.out.println ("El televisor esta apagado.");
	};

	public void setPower (boolean power) {
		if (power) {
			System.out.println ("Encendiendo TV");
		} else {
			System.out.println ("Apagando TV");
		}
	}

	public void setChannel (int channel) {
		System.out.println ("Canal: " + channel);
	}

	public void setVolume (int volume) {
		System.out.println ("Volumen: " + volume);
	}

	public void setMuted (boolean muted) {
		if (muted) {
			System.out.println ("MUTE ");
		} else {
			System.out.println ("UNMUTED");
		}
	}

	public void writeChannel (String str) {
		System.out.println ("Canal: " + str);
	}
}
