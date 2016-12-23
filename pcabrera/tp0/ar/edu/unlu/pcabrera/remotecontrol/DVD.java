package ar.edu.unlu.pcabrera.remotecontrol;

import java.util.GregorianCalendar;

class DVD implements RemoteControl {

	/* Config */
	public static final int VOLUME_MIN = 0;
	public static final int VOLUME_MAX = 5;
	public static final int TRACK_MIN = 1;
	public static final int TRACK_MAX = 8;

	/* Mode */
	public static final byte MODE_OFF = 0; // Off
	public static final byte MODE_PLAYING = 1; // On, playing
	public static final byte MODE_PAUSED = 2; // On, paused
	public static final byte MODE_STOPPED = 3; // On, stopped
	public static final byte MODE_EJECT = 4; // On, tray out

	/* DVD status */
	private int volume = 3;
	private int track = 1;
	private long playTime;
	private long pausedTime = 0;
	private byte mode  = MODE_OFF;

	/* Display */
	private DVDDisplay display = null;

	/* Playing time notifier thread */
	private Thread notifier = null;

	public DVD () {
		/* Constructor */
	};

	public void setDisplay (DVDDisplay display) {
		/* Connect DVD to a DVD Display */
		this.display = display;
		this.notifier = new Thread (new DVDTimeNotifier (this, display));
		this.notifier.start();
	}

	private void setMode (byte mode) {
		/* Set mode (playing/paused/stopped/eject/off) */
		this.mode = mode;
	}

	public long getPlayTime() {
		/* Returns the timestamp for the starting playing time */
		return this.playTime;
	}

	public long getPausedTime() {
		/* Returns the timestamp for the last paused time */
		return this.pausedTime;
	}

	public byte getStatus() {
		/* Returns current status */
		return this.mode;
	}

	private void notifyVolume () {
		/* Notify current volume to display */
		if (this.display != null) {
			this.display.notifyVolume (this.volume);
		}
	}

	private void notifyStatus () {
		/* Notify current status to display */
		if (this.display != null) {
			this.display.notifyStatus (this.mode);
		}
	}

	private void notifyTrack () {
		/* Notify current track to display */
		if (this.display != null) {
			this.display.notifyTrack (this.track);
		}
	}



	/* Implementing RemoteControl interface */

	public void powerOnOff() {
		/* Turn ON/OFF and notify to display */
		if (this.mode == MODE_OFF) {
			this.setMode (MODE_STOPPED);
		} else {
			this.setMode (MODE_OFF);
		}
		this.notifyStatus();
	}

	public void play() {
		/* Play track and notify to display */
		long now = new GregorianCalendar().getTimeInMillis();

		if (this.mode == MODE_STOPPED) {
			this.playTime = now;
			this.mode = MODE_PLAYING;
			this.notifyStatus();
		} else if (this.mode == MODE_PAUSED) {
			/* after unpause, playTime will contain a fake value to force the following: */
			/* now - playTime = track_elapsed_time */
			this.playTime += (now - this.pausedTime);
			this.mode = MODE_PLAYING;
			this.notifyStatus();
		}
	}

	public void pause() {
		/* Pause/Unpause playing track and notify to display */
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
		/* Stop playing track and notify to display */
		if (this.mode == MODE_PLAYING || this.mode == MODE_PAUSED) {
			this.setMode (MODE_STOPPED);
			this.notifyStatus ();
		}
	}
	public void trackNext() {
		/* Jump to next track (if exists) and notify to display */
		if (this.mode != MODE_OFF && this.mode != MODE_EJECT && this.track < TRACK_MAX) {
			this.track++;
			this.notifyTrack ();
			this.setMode(MODE_STOPPED);
			this.play();
		}
	}
	public void trackPrevious() {
		/* Jump to previous track (if exists) and notify to display */
		if (this.mode != MODE_OFF && this.mode != MODE_EJECT && this.track > TRACK_MIN) {
			this.track--;
			this.notifyTrack ();
			this.setMode(MODE_STOPPED);
			this.play();
		}
	}

	public void eject() {
		/* Eject or close tray and notify to display */
		if (this.mode == MODE_EJECT) {
			this.setMode(MODE_STOPPED);
			this.notifyStatus();
		} else if (this.mode != MODE_OFF) {
			this.setMode(MODE_EJECT);
			this.notifyStatus();
		}
	}


	public void volumeUp() {
		/* Increase volume and notify to display */
		if (this.mode != MODE_OFF && this.volume < VOLUME_MAX) {
			this.volume++;
			this.notifyVolume();
		}
	}

	public void volumeDown() {
		/* Decrease volume and notify to display */
		if (this.mode != MODE_OFF && this.volume > VOLUME_MIN) {
			this.volume--;
			this.notifyVolume();
		}
	}

	public void mute() {
		/* Set volume to zero and notify to display */
		if (this.mode != MODE_OFF) {
			this.volume = 0;
			this.notifyVolume();
		}
	}

	/* Useless buttons */
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
