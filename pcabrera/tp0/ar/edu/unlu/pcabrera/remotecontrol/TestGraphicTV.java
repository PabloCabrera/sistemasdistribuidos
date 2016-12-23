package ar.edu.unlu.pcabrera.remotecontrol;

public class TestGraphicTV {
	public static void main (String[] args) {
		TV tv = null;
		GraphicControl control = null;
		TVDisplay display = null;

		try {

			tv = new TV();
			display = new GraphicTVDisplay();
			control = new GraphicControl();

			control.setControllable(tv);
			tv.setDisplay(display);
			control.start();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
