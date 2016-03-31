package ar.edu.unlu.pcabrera.remotecontrol;

import java.util.GregorianCalendar;

class DVD implements RemoteControl {
	/* Configuracion */
	public static final int VOLUME_MIN = 0;
	public static final int VOLUME_MAX = 5;
	public static final int TRACK_MIN = 1;
	public static final int TRACK_MAX = 8;

	/* Modo */
	public static final byte MODE_OFF = 0; // Off
	public static final byte MODE_PLAYING = 1; // On, playing
	public static final byte MODE_PAUSED = 2; // On, paused
	public static final byte MODE_STOPPED = 3; // On, stopped
	public static final byte MODE_EJECT = 4; // On, tray out

	/* Estado */
	private int volume = 3;
	private int track = 1;
	private long playTime;
	private long pausedTime = 0;
	private byte mode  = MODE_OFF;
	private DVDDisplay display = null;
	private Thread notifier = null;

	public DVD() {};

	/* Funciones de conexion */
	public void setDisplay(DVDDisplay display) {
		this.display = display;
		this.notifier = new Thread (new DVDTimeNotifier (this, display));
		this.notifier.start();
	}

	/* Funciones internas */
	private void setMode (byte mode) {
		this.mode = mode;
	}

	private void notifyVolume () {
		/* OSD volumen */
		if (this.display != null) {
			this.display.notifyVolume (this.volume);
		}
	}

	private void notifyStatus () {
		if (this.display != null) {
			this.display.notifyStatus (this.mode);
		}
	}

	private void notifyTrack () {
		if (this.display != null) {
			this.display.notifyTrack (this.track);
		}
	}

	public long getPlayTime() {
		/* Returns the timestamp for start playing time */
		return this.playTime;
	}

	public long getPausedTime() {
		/* Returns the timestamp for start playing time */
		return this.pausedTime;
	}

	public byte getStatus() {
		/* Returns current status */
		return this.mode;
	}


	/* Control remoto */
	public void powerOnOff() {
		if (this.mode == MODE_OFF) {
			this.setMode (MODE_STOPPED);
		} else {
			this.setMode (MODE_OFF);
		}
		this.notifyStatus();
	}

	public void play() {
		long now = new GregorianCalendar().getTimeInMillis();

		if (this.mode == MODE_STOPPED) {
			this.playTime = now;
			this.mode = MODE_PLAYING;
			this.notifyStatus();
		} else if (this.mode == MODE_PAUSED) {
			this.playTime += (now - this.pausedTime); // Hack feo
			this.mode = MODE_PLAYING;
			this.notifyStatus();
		}
	}

	public void pause() {
		long now = new GregorianCalendar().getTimeInMillis();

		if (this.mode == MODE_PLAYING) {
			this.pausedTime = now;
			this.mode = MODE_PAUSED;
			this.notifyStatus();
		} else if (this.mode == MODE_PAUSED) {
			this.play();
		}
	}
	public void stop() {
		if (this.mode == MODE_PLAYING || this.mode == MODE_PAUSED) {
			this.setMode (MODE_STOPPED);
			this.notifyStatus ();
		}
	}
	public void trackNext() {
		if (this.mode != MODE_OFF && this.mode != MODE_EJECT && this.track < TRACK_MAX) {
			this.track++;
			this.notifyTrack ();
			this.setMode(MODE_STOPPED);
			this.play();
		}
	}
	public void trackPrevious() {
		if (this.mode != MODE_OFF && this.mode != MODE_EJECT && this.track > TRACK_MIN) {
			this.track--;
			this.notifyTrack ();
			this.setMode(MODE_STOPPED);
			this.play();
		}
	}

	public void eject() {
		if (this.mode == MODE_EJECT) {
			this.setMode(MODE_STOPPED);
			this.notifyStatus();
		} else if (this.mode != MODE_OFF) {
			this.setMode(MODE_EJECT);
			this.notifyStatus();
		}
	}


	public void volumeUp() {
		if (this.mode != MODE_OFF && this.volume < VOLUME_MAX) {
			this.volume++;
			this.notifyVolume();
		}
	}

	public void volumeDown() {
		if (this.mode != MODE_OFF && this.volume > VOLUME_MIN) {
			this.volume--;
			this.notifyVolume();
		}
	}

	public void mute() {
		if (this.mode != MODE_OFF) {
			this.volume = 0;
			this.notifyVolume();
		}
	}

	/* Estos no hacen nada */
	public void rec() {}
	public void number0() {}
	public void number1() {}
	public void number2() {}
	public void number3() {}
	public void number4() {}
	public void number5() {}
	public void number6() {}
	public void number7() {}
	public void number8() {}
	public void number9() {}
	public void channelNext() {}
	public void channelPrev() {}

}
