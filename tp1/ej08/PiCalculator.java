package tp1.ej08;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PiCalculator implements Task, Serializable {
	private static final long serialVersionUID = 1485211690L;

	private int precision = 2;
	private double result;

	@Override
	public Object execute () {

		double mult = Math.pow (10, this.precision);
		this.result = Math.round (Math.PI * mult) / mult;
		
		System.out.println("Calculando PI: "+this.result);
		return new Double (this.result);
	}

	public void setPrecision (int decimal) {
		this.precision = decimal;
	}
}
