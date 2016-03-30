package ar.edu.unlu.pcabrera.fraccion;

import java.util.Scanner;

public class ClienteConsola {

	public static void main (String[] args) {
		Fraccion a, b, c;
		Scanner escaner;
		String entrada;
		boolean salir = false;

		a = new Fraccion ();
		b = new Fraccion ();
		escaner = new Scanner (System.in);
	
		while (!salir) {
			ClienteConsola.mostrarMenu(a, b);
			entrada = escaner.nextLine();

			if (entrada.equals ("1")) {
				a = ingresarFraccion (escaner);
			} else if (entrada.equals ("2")) {
				b = ingresarFraccion (escaner);
			} else if (entrada.equals ("3")) {
				System.out.println(a + " + " + b + " = " +a.sumar(b));
				System.out.println("Presione ENTER para continuar");
				escaner.nextLine ();
			} else if (entrada.equals ("4")) {
				System.out.println(a + " - " + b + " = " +a.restar(b));
				System.out.println("Presione ENTER para continuar");
				escaner.nextLine ();
			} else if (entrada.equals ("0")) {
				salir = true;
			}
		}

	}

	public static void mostrarMenu (Fraccion a, Fraccion b) {
		System.out.print ("\033[H\033[2J");
		System.out.println ();
		System.out.println ("Valor actual de A: " + a);
		System.out.println ("Valor actual de B: " + b);
		System.out.println ("");
		System.out.println ("1. Ingresar fraccion A");
		System.out.println ("2. Ingresar fraccion B");
		System.out.println ("3. A + B");
		System.out.println ("4. A - B");
		System.out.println ("0. Salir");
		System.out.println ("");
	}

	public static Fraccion ingresarFraccion (Scanner escaner) {
		int numerador, denominador;
		String entrada;
		Fraccion fraccion;

		fraccion = new Fraccion();

		try {
			System.out.print ("Numerador: ");
			numerador = Integer.parseInt (escaner.nextLine());
			
			System.out.print ("Denominador: ");
			denominador = Integer.parseInt (escaner.nextLine());

			fraccion = new Fraccion (numerador, denominador);
		} catch (NumberFormatException exception) {
			System.out.println ("ERROR: Se ha introducido un numero invalido.");
			System.out.println("Presione ENTER para volver al menu");
			escaner.nextLine ();
		} catch (DenominadorCero exception) {
			System.out.println ("ERROR: Se ha introducido una fraccion con denominador cero.");
			System.out.println("Presione ENTER para volver al menu");
			escaner.nextLine ();
		}

		return fraccion;
	}


}
