package ar.edu.unlu.pcabrera.remotecontrol;

import java.util.GregorianCalendar;

public class DVDTimeNotifier implements Runnable {
	DVD dvd;
	DVDDisplay display;

	public DVDTimeNotifier (DVD dvd, DVDDisplay display) {
		this.dvd = dvd;
		this.display = display;
	}

	public void run () {
		byte status;
		long playTime, pausedTime, now, offset;

		try {
			while (true) {
				status = dvd.getStatus ();

				if (status == DVD.MODE_PLAYING) {
					playTime = dvd.getPlayTime();
					now = (new GregorianCalendar ()).getTimeInMillis ();
					offset = now - playTime;
					display.notifyTime (offset);
				} else if (status == DVD.MODE_PAUSED) {
					playTime = dvd.getPlayTime ();
					pausedTime = dvd.getPausedTime ();
					offset = pausedTime - playTime;
					display.notifyTime (offset);
				} else {
					display.notifyTime (0);
				}
				Thread.sleep (1000);
			}
		} catch (Exception e) {
			// Do nothing
		}
	}
}
