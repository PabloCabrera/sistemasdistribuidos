package ar.edu.unlu.pcabrera.remotecontrol;

public class TestGraphicDVD {
	public static void main (String[] args) {
		DVD dvd = null;
		GraphicControl control = null;
		DVDDisplay display = null;

		try {

			dvd = new DVD();
			display = new GraphicDVDDisplay();
			control = new GraphicControl();

			control.setControllable(dvd);
			dvd.setDisplay(display);
			control.start();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
