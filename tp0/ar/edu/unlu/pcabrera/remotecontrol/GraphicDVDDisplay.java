package ar.edu.unlu.pcabrera.remotecontrol;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class GraphicDVDDisplay extends JFrame implements DVDDisplay {
	private static final int DVD_WIDTH = 640;
	private static final int DVD_HEIGHT = 128;
	private static final int DVD_STATUS_DISPLAY_OFFSET = 32;
	private static final int DVD_STATUS_DISPLAY_WIDTH = 128;
	private static final int DVD_TIME_DISPLAY_OFFSET = 192;
	private static final int DVD_TIME_DISPLAY_WIDTH = 192;
	private static final int DVD_TRACK_DISPLAY_OFFSET = 416;
	private static final int DVD_TRACK_DISPLAY_WIDTH = 64;
	private static final int DVD_VOLUME_DISPLAY_OFFSET = 512;
	private static final int DVD_VOLUME_DISPLAY_WIDTH = 96;
	private static final int DVD_BORDER = 32;
	private static final int DVD_LED_RADIUS = 12;
	private static final int DVD_LCD_FONT_SIZE = 24;
	private static final Color DVD_LED_ON_COLOR = Color.GREEN;
	private static final Color DVD_LED_OFF_COLOR = Color.RED;
	private static final Color DVD_LCD_FONT_COLOR = Color.GREEN;
	private static final Color DVD_LCD_OFF_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DVD_LCD_ON_BACKGROUND_COLOR = Color.BLACK;
	private static final Color DVD_BACKGROUND_COLOR = Color.GRAY;
	private static final Font DVD_LCD_FONT = new Font ("Monospaced", Font.BOLD, DVD_LCD_FONT_SIZE);
;

	private int volume = 3;
	private int status = DVD.MODE_STOPPED;
	private boolean power = false;
	private String time = "00:00:00";
	private int track = 1;

	public GraphicDVDDisplay () {
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		this.setSize (DVD_WIDTH, DVD_HEIGHT);
		this.show ();
		this.repaint();
		
	}

	public void notifyStatus (byte status) {
		this.status = status;
		this.power = !(status == DVD.MODE_OFF);
		this.repaint ();
	}

	public void notifyVolume (int volume) {
		this.volume = volume;
		this.repaint ();
	}

	public void notifyTime (long millisecs) {
		long hour, minute, second;

		hour = millisecs/360000;
		minute = (millisecs % 360000) / 60000;
		second = (millisecs % 60000) / 1000;
		this.time = hour + ":" + minute + ":" + second;
		this.repaint();
	}

	public void notifyTray (boolean inside) {
		this.status = inside? DVD.MODE_STOPPED: DVD.MODE_EJECT;
	}

	public void notifyTrack (int number) {
		this.track = number;
	}
	
	public void paint (Graphics graphics) {
		Graphics2D g2d;

		g2d = (Graphics2D) graphics;
		g2d.setFont (DVD_LCD_FONT);

		/* Background */
		g2d.setColor (DVD_BACKGROUND_COLOR);
		g2d.fillRect (0, 0, DVD_WIDTH, DVD_HEIGHT);

		/* ON/OFF led */
		g2d.setColor (this.power? DVD_LED_ON_COLOR: DVD_LED_OFF_COLOR);
		g2d.fillArc (DVD_WIDTH - DVD_BORDER, DVD_HEIGHT/2, DVD_LED_RADIUS, DVD_LED_RADIUS, 0, 360);

		/* LCD display backgrounds */
		g2d.setColor (this.power? DVD_LCD_ON_BACKGROUND_COLOR: DVD_LCD_OFF_BACKGROUND_COLOR);
		g2d.fillRect (DVD_STATUS_DISPLAY_OFFSET, DVD_BORDER, DVD_STATUS_DISPLAY_WIDTH, DVD_HEIGHT-2*DVD_BORDER);
		g2d.fillRect (DVD_TIME_DISPLAY_OFFSET, DVD_BORDER, DVD_TIME_DISPLAY_WIDTH, DVD_HEIGHT-2*DVD_BORDER);
		g2d.fillRect (DVD_TRACK_DISPLAY_OFFSET, DVD_BORDER, DVD_TRACK_DISPLAY_WIDTH, DVD_HEIGHT-2*DVD_BORDER);
		g2d.fillRect (DVD_VOLUME_DISPLAY_OFFSET, DVD_BORDER, DVD_VOLUME_DISPLAY_WIDTH, DVD_HEIGHT-2*DVD_BORDER);

		/* LCD display text */
		if (this.power) {
			g2d.setColor (DVD_LCD_FONT_COLOR);
			g2d.setFont (DVD_LCD_FONT);
			g2d.drawString (this.getStatusString (), DVD_STATUS_DISPLAY_OFFSET+DVD_BORDER/2, DVD_HEIGHT-(3*DVD_BORDER/2));
			g2d.drawString (this.time, DVD_TIME_DISPLAY_OFFSET+DVD_BORDER/2, DVD_HEIGHT-(3*DVD_BORDER/2));
			g2d.drawString (this.track + "/" + DVD.TRACK_MAX, DVD_TRACK_DISPLAY_OFFSET+DVD_BORDER/2, DVD_HEIGHT-(3*DVD_BORDER/2));
			g2d.drawString (this.getVolumeString(), DVD_VOLUME_DISPLAY_OFFSET+DVD_BORDER/2, DVD_HEIGHT-(3*DVD_BORDER/2));
		}
 
	}

	private String getStatusString () {
		switch (this.status) {
			case DVD.MODE_PLAYING:
				return "PLAY";
			case DVD.MODE_PAUSED:
				return "PAUSE";
			case DVD.MODE_STOPPED:
				return "STOP";
			case DVD.MODE_EJECT:
				return "EJECT";
			default:
				return "";
		}
	}

	private String getVolumeString () {
		switch (this.volume) {
			case 1:
				return "_";
			case 2:
				return "__";
			case 3:
				return "__-";
			case 4:
				return "__--";
			case 5:
				return "__--^";
			default:
				return "";
		}
	}
}
