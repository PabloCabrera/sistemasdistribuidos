package misc;

public class Debugger {
	private static boolean showExceptionMessage = true;
	private static boolean showExceptionStackTrace = true;

	public static void debugException (Exception exception) {
		if(Debugger.showExceptionMessage) {
			System.err.println ("[DEBUG] Exception Message=" + exception.getMessage ());
		}
		if(Debugger.showExceptionStackTrace) {
			exception.printStackTrace ();
		}
	}

	public static void write (String message) {
		System.out.println ("[DEBUG] " + message);
	}
}
