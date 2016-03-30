package ar.edu.unlu.pcabrera.remotecontrol

class DVD implements RemoteControl {
	/* Configuracion */
	private static final int VOLUME_MIN = 0;
	private static final int VOLUME_MAX = 100;

	/* Modo */
	private static byte MODE_OFF = 0; // Off
	private static byte MODE_PLAYING = 1; // On, playing
	private static byte MODE_PAUSED = 2; // On, paused
	private static byte MODE_STOPPED = 3; // On, stopped

	/* Estado */
	private int volume = 20;
	private boolean muted = false;
	private long playTime;
	private long pausedTime = 0;
	private byte mode  = MODE_OFF;
	private DVDDisplay display = null;

	public DVD() {};

	/* Funciones de conexion */
	public void setDisplay(DVDDisplay display) {
		this.display = display;
	}

	/* Funciones internas */
	private void setMode (byte mode) {
		this.mode = mode;
	}

	private void inputNumber (int number) {
		if (this.mode == MODE_CLEAN) {
			this.setMode (MODE_CHANNEL);
			this.tmpNumber = number;
			this.writeChannel();
		} else if (this.mode == MODE_CHANNEL) {
			this.tmpNumber = this.tmpNumber*10 + number;
			this.setChannel(this.tmpNumber);
		}
	}

	private void notifyPower() {
		if (this.display != null) {
			this.display.setPower (this.mode != MODE_OFF);
		}
	}

	private void notifyVolume() {
		/* OSD volumen */
		if (this.display != null) {
			this.display.setVolume (this.volume);
		}
	}

	private void notifyMuted() {
		/* OSD mute */
		if (this.display != null) {
			this.display.setMuted (this.muted);
		}
	}


	/* Control remoto */
	public void powerOnOff() {
		if (this.mode == MODE_OFF) {
			this.setMode (MODE_STOPPED);
		} else {
			this.setMode (MODE_OFF);
		}
		this.notifyPower();
	}

	public void play() {
		if (this.mode == MODE_STOPPED) {
			this.pausedTime = 0;
			this.playTime = new Calendar().getTimeInMilis();
			this.mode = MODE_PLAYING;
			this.notifyPlay();
		}
	}

	public void pause() {}
	public void stop() {}
	public void rec() {}
	public void trackNext() {}
	public void trackPrevious() {}
	public void eject() {}


	public void volumeUp() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.mode = MODE_CLEAN;
			if (this.volume < VOLUME_MAX) {
				this.volume++;
			}
			this.notifyVolume();
		}
	}

	public void volumeDown() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.mode = MODE_CLEAN;
			if (this.volume > VOLUME_MIN) {
				this.volume--;
			}
			this.notifyVolume();
		}
	}

	public void mute() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.mode = MODE_CLEAN;
			this.muted = !this.muted;
			this.notifyMuted();
		}
	}

	/* Estos no hacen nada */
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
