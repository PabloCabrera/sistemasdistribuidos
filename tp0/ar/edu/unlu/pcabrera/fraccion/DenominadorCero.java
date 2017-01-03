package ar.edu.unlu.pcabrera.fraccion;
class DenominadorCero extends Exception {
	public DenominadorCero () {
		super("Fracción no válida, denominador=0");
	}
}
