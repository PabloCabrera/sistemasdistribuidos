package ar.edu.unlu.pcabrera.remotecontrol;

public class TextControl {
	RemoteControl controllable = null;

	public TextControl(){};

	public void setControllable(RemoteControl controllable) {
		this.controllable = controllable;
	}

	public void start() throws Exception {
		boolean quit = false;
		int input;

		if (this.controllable == null) {
			System.out.println ("El control remoto esta desconectado");
			quit = true;
		} else {
			System.out.println ("Se ha activado el control remoto");
			System.out.println ("SPACEBAR: Encender/Apagar");
			System.out.println ("w/s: Canal +-");
			System.out.println ("a/d: Volumen -+");
			System.out.println ("m: Mute");
			System.out.println ("0-9: Numeros");
			System.out.println ("q: Salir");
			System.out.println ();
		}

		while (!quit) {
			input = System.in.read();
			switch (input) {
				case 'q':
					quit = true;
					break;
				case '0':
					this.controllable.number0();
					break;
				case '1':
					this.controllable.number1();
					break;
				case '2':
					this.controllable.number2();
					break;
				case '3':
					this.controllable.number3();
					break;
				case '4':
					this.controllable.number4();
					break;
				case '5':
					this.controllable.number5();
					break;
				case '6':
					this.controllable.number6();
					break;
				case '7':
					this.controllable.number7();
					break;
				case '8':
					this.controllable.number8();
					break;
				case '9':
					this.controllable.number9();
					break;
				case 'w':
					this.controllable.channelNext();
					break;
				case 's':
					this.controllable.channelPrev();
					break;
				case 'a':
					this.controllable.volumeDown();
					break;
				case 'd':
					this.controllable.volumeUp();
					break;
				case ' ':
					this.controllable.powerOnOff();
					break;
				case 'm':
					this.controllable.mute();
					break;
			}
		}
	}
}

