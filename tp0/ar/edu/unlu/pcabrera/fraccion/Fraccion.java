package ar.edu.unlu.pcabrera.fraccion;
import java.lang.Math;

public class Fraccion {
	private int numerador = 1;
	private int denominador = 1;

	public Fraccion () {
	/* Constructor predeterminado. Devuelve una fraccion de valor 1 */
	}

	public Fraccion (int numerador, int denominador) throws DenominadorCero {
	/* Constructor especifiando numerador y denominador. */
		if (denominador == 0) {
			throw new DenominadorCero();
		}
		this.numerador = numerador;
		this.denominador = denominador;
		this.simplificar();
	}

	public int getNumerador () {
	/* Devuelve el numerador de la fraccion actual simplificada */
		return this.numerador;
	}

	public int getDenominador () {
	/* Devuelve el denominador de la fraccion actual simplificada */
		return this.denominador;
	}

	public void setValor (int numerador, int denominador) {
	/* Cambiar el valor de la fraccion */
		this.numerador = numerador;
		this.denominador = denominador;
		this.simplificar();
	}

	/* NOTA: Se han eliminado los metodos setNumerador() y setDenominador() para evitar que la simplificacion automatica produzca resultados inesperados */

	@Override
	public String toString() {
	/* Devuelve una representacion de la fraccion como un String */
		return this.numerador + "/" + this.denominador;
	}

	private int mcd () {
	/* Devuelve el Maximo Comun Divisor entre el numerador y el denominador de la funcion. Uso interno. */
		return Fraccion.mcd (this.numerador, this.denominador);
	}

	public static int mcd (int num1, int num2) {
		/* Devuelve el Maximo Comun Divisor entre dos numeros. */
		/* Implementado mediante el algoritmo de Euclides recursivo */
		int mayor, menor, cociente, resto;
		mayor = Math.max (Math.abs(num1), Math.abs(num2));
		menor = Math.min (Math.abs(num1), Math.abs(num2));
		cociente = mayor/menor;
		resto = mayor % menor;
		if (resto == 0) {
			return menor;
		} else {
			return Fraccion.mcd(menor, resto);
		}
	}

	private void simplificar () {
		/* Simplifica la fraccion. Uso interno. */
		int mcd = this.mcd ();

		if (mcd != 1) {
			this.numerador /= mcd;
			this.denominador /= mcd;
		}

		this.normalizarSigno();
	}

	private void normalizarSigno () {
	/* Fuerza que el denominador sea positivo. Uso interno. */
		if (this.denominador < 0) {
			this.denominador = -this.denominador;
			this.numerador = -this.numerador;
		}
	}

	public Fraccion sumar (Fraccion otra) {
	/* Devuelve el resultado de sumar otra fraccion a la actual */
		int nnum, nden;
		Fraccion fraccion = null;

		try {
			nnum = (this.numerador * otra.getDenominador()) + (otra.getNumerador() * this.denominador);
			nden = this.denominador * otra.getDenominador();
			fraccion = new Fraccion (nnum, nden);
		} catch (DenominadorCero e) {
			/* Esto no deberia suceder */
		}
		return fraccion;
	}

	public Fraccion restar (Fraccion otra) {
	/* Devuelve el resultado de restar otra fraccion a la actual */
		return this.sumar (otra.opuesta());
	}

	public Fraccion opuesta () {
	/* Devuelve la fraccion opuesta a la actual */
		Fraccion fraccion = null;
		try {
			fraccion = new Fraccion (-this.numerador, this.denominador);
		} catch (DenominadorCero e) {
			/* Esto no deberia suceder */
		}
		return fraccion;
	}

	public int compareTo (Fraccion otra) {
	/* Devuelve un entero 1, 0 o -1 segun esta fraccion sea mayor, igual o menor a otra */
		float otraEquivNomin;

		otraEquivNomin = (float) otra.getNumerador () * (float) this.getDenominador () / (float) otra.getDenominador ();
		if (otraEquivNomin == this.numerador) {
			return 0;
		} else if (otraEquivNomin < this.numerador) {
			return 1;
		} else {
			return -1;
		}

	}

}
