package ar.edu.unlu.pcabrera.remotecontrol
public class TestGraphicTV {
	public static void main (String[] args) {
		TV tv = null;
		GraphicControl control = null;
		TVDisplay display = null;

		try {

			tv = new TV();
			control = new GraphicControl();
			display = new TextTVDisplay();

			control.setControllable(tv);
			tv.setDisplay(display);
			control.start();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
