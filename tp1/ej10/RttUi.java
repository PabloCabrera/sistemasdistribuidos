package tp1.ej10;

import java.util.Scanner;

public class RttUi {
	private TransportProtocol protocol = TransportProtocol.TCP;
	private String proxy = null;
	private int numHits = 5;
	private boolean mustExit = false;
	private Scanner scanner = null;
	private RttCalculator calculator;

	public static void main (String args[]) {
		RttUi ui = new RttUi ();
	}

	public RttUi () {
		this.calculator = new RttCalculator ();
		this.scanner = new Scanner (System.in);
		while (!mustExit) {
			this.showMenu();
		}
	}

	private void showMenu () {
		System.out.println (
			"Calculador de Round Trip Time\n"+
			"Ingrese un numero para acceder a una funcion\n"+
			"\n"+
			"1. Calcular RTT a un host\n"+
			"2. Calcular RTT a una lista de hosts\n"+
			"3. Configurar proxy (actual: "+this.proxyInfo()+")\n"+
			"4. Cambiar protocolo (actual: "+this.protocolInfo()+")\n"+
			"5. Cambiar numero de hits (actual: "+this.hitsInfo()+")\n"+
			"\n"+
			"0. Salir\n"+
			"\n"
		);
		switch (this.getMenuOption ()) {
			case 1:
				this.showHostRttInput();
				break;
			case 2:
				this.showHostListRttInput();
				break;
			case 3:
				this.showConfigProxyInput();
				break;
			case 4:
				this.showConfigProtocolInput();
				break;
			case 5:
				this.showConfigHitsInput();
				break;
			case 0:
				this.mustExit = true;
				break;
			default:
				this.showInvalidKeyPressed();
				this.showMenu();
		}
	}

	private void showInvalidKeyPressed () {
		System.out.println ("Ha introducido una opcion no valida");
	}

	private int getMenuOption () {
		String input = this.scanner.nextLine();
		int numeric = -1;
		try {
			numeric = Integer.parseInt (input);
		} catch (NumberFormatException e) {
		}
		return numeric;
	}

	private void showHostRttInput () {
		System.out.println ("Escriba el nombre de host o direccion IP");
		String host = this.scanner.nextLine();
		this.calculateRttHost (host);
	}

	private void calculateRttHost (String host) {
		this.calculator.setProxy (this.proxy);
		this.calculator.setProtocol (this.protocol);
		this.calculator.setNumHits (this.numHits);
		long rtt = this.calculator.calculateRtt (host);
		System.out.println ("Round Trip Time: " + rtt + "\n");
		this.pressKeyToContinue ();
	}

	private void pressKeyToContinue () {
		System.out.println("Presione ENTER para continuar");
		try {
			System.in.read();
		} catch (Exception e) {
		}
	}


	private void showHostListRttInput () {
		System.out.println ("Escriba el nombre de archivo de la lista de hosts");
		String filename = this.scanner.nextLine();
		this.calculateRttList (filename);
	}

	private void calculateRttList (String filename) {
		this.calculator.setProxy (this.proxy);
		this.calculator.setProtocol (this.protocol);
		this.calculator.calculateRttList (filename);
	}

	private void showConfigProxyInput () {
		System.out.println ("Escriba el nombre de host y puerto del servidor proxy.\n Ejemplo: proxy.unlu.edu.ar:8080");
		String host = this.scanner.nextLine();
		if (host.length() > 0) {
			this.proxy = host;
		} else {
			this.proxy = null;
		}
	}

	private void showConfigProtocolInput () {
		System.out.println (
			"Ingrese un numero para seleccionar una opcion\n" +
			"1. TCP (HTTP)\n"+
			"2. UDP\n"
		);
		switch (this.getMenuOption ()) {
			case 1:
				this.protocol = TransportProtocol.TCP;
				break;
			case 2:
				this.protocol = TransportProtocol.UDP;
				break;
			default:
				this.showInvalidKeyPressed ();
				this.showConfigProtocolInput ();
		}
	}

	private void showConfigHitsInput () {
		System.out.println ("Ingrese la cantidad de hits:");
		String readed = this.scanner.nextLine();
		int hits;
		try {
			hits = Integer.parseInt (readed);
			this.numHits = hits;
		} catch (Exception e) {
			System.err.println ("Formato de numero incorrecto.\n");
			this.showConfigHitsInput();
		}
	}

	private String proxyInfo () {
		if (this.proxy != null){
			return this.proxy;
		} else {
			return "Sin Proxy";
		}
	}

	private String protocolInfo () {
		return this.protocol.name();
	}

	private int hitsInfo () {
		return this.numHits;
	}
}
