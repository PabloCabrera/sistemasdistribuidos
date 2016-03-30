package ar.edu.unlu.pcabrera.remotecontrol;

class TV implements RemoteControl {
	/* Configuracion */
	private static int VOLUME_MIN = 0;
	private static int VOLUME_MAX = 100;
	private static int CHANNEL_MIN = 1;
	private static int CHANNEL_MAX = 99;

	/* Modo */
	private static byte MODE_OFF = 0; //Apagado
	private static byte MODE_CLEAN = 1; //Encendido, normal
	private static byte MODE_CHANNEL = 2; //Escribiendo canal

	/* Estado */
	private int channel = 1;
	private int volume = 20;
	private boolean muted = false;
	private byte mode  = MODE_OFF;
	private int tmpNumber = 0;
	private TVDisplay display = null;

	public TV() {};

	/* Funciones de conexion */
	public void setDisplay(TVDisplay display) {
		this.display = display;
	}

	/* Funciones internas */
	private void setMode (byte mode) {
		this.mode = mode;
	}

	private void setChannel (int channel) {
		if (channel < CHANNEL_MIN) {
			this.channel = CHANNEL_MAX;
		} else if (channel > CHANNEL_MAX) {
			this.channel = CHANNEL_MIN;
		} else {
			this.channel = channel;
		}
		this.setMode (MODE_CLEAN);
		this.notifyChannel();
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

	private void notifyChannel() {
		/* OSD numero canal */
		if (this.display != null) {
			this.display.setChannel (this.channel);
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

	private void writeChannel() {
		if (this.display != null) {
			this.display.writeChannel (this.tmpNumber + "_");
		}
	}


	/* Control remoto*/
	public void powerOnOff() {
		if (this.mode == MODE_OFF) {
			this.setMode (MODE_CLEAN);
		} else {
			this.setMode (MODE_OFF);
		}
		this.notifyChannel();
		this.notifyPower();
	}

	public void channelNext() {
		 if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.setChannel(this.channel +1);
		}
	}

	public void channelPrev() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.setChannel (this.channel -1);
		}
	}

	public void volumeUp() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.mode = MODE_CLEAN;
			this.muted = false;
			if (this.volume < VOLUME_MAX) {
				this.volume++;
			}
			this.notifyMuted();
			this.notifyVolume();
		}
	}

	public void volumeDown() {
		if (this.mode == MODE_CLEAN || this.mode == MODE_CHANNEL) {
			this.mode = MODE_CLEAN;
			this.muted = false;
			if (this.volume > VOLUME_MIN) {
				this.volume--;
			}
			this.notifyMuted();
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

	public void number0() {
		this.inputNumber (0);
	}

	public void number1() {
		this.inputNumber (1);
	}

	public void number2() {
		this.inputNumber (2);
	}

	public void number3() {
		this.inputNumber (3);
	}

	public void number4() {
		this.inputNumber (4);
	}

	public void number5() {
		this.inputNumber (5);
	}

	public void number6() {
		this.inputNumber (6);
	}

	public void number7() {
		this.inputNumber (7);
	}

	public void number8() {
		this.inputNumber (8);
	}

	public void number9() {
		this.inputNumber (9);
	}

	/* Estos no hacen nada */
	public void play() {}
	public void pause() {}
	public void stop() {}
	public void rec() {}
	public void trackNext() {}
	public void trackPrevious() {}
	public void eject() {}
}
