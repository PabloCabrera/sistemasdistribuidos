package ar.edu.unlu.pcabrera.remotecontrol;

import java.util.GregorianCalendar;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class GraphicTVDisplay extends JFrame implements TVDisplay {
	private static final int TV_WIDTH = 640;
	private static final int TV_HEIGHT = 480;
	private static final int TV_BORDER = 64;
	private static final int TV_BORDER_ARC = 96;
	private static final int TV_LED_RADIUS = 12;
	private static final long TV_OSD_TIMEOUT = 3000;
	private static final int TV_OSD_FONT_SIZE = 36;
	private static final Color TV_OSD_CHANNEL_COLOR = Color.GREEN;
	private static final Color TV_OSD_VOLUME_COLOR = Color.RED;
	private static final Color TV_LED_ON_COLOR = Color.GREEN;
	private static final Color TV_LED_OFF_COLOR = Color.RED;
	private static final Color TV_SCREEN_ON_COLOR = Color.GRAY.darker().darker();
	private static final Color TV_SCREEN_OFF_COLOR = Color.GRAY.darker().darker().darker().darker();
	private static final Shape TV_SCREEN_RECTANGLE = new RoundRectangle2D.Float (TV_BORDER, TV_BORDER, TV_WIDTH -2*TV_BORDER, TV_HEIGHT-2*TV_BORDER, TV_BORDER_ARC, 2*TV_BORDER_ARC);
	private static final Font TV_OSD_FONT = new Font ("Monospaced", Font.BOLD, TV_OSD_FONT_SIZE);
;

	private String channel;
	private int volume;
	private boolean power = false;
	private boolean volumeShow = false;
	private boolean muted = false;

	public GraphicTVDisplay() {

		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		this.setSize (TV_WIDTH, TV_HEIGHT);
		this.show ();
		this.repaint();
		
	};

	public void setPower (boolean power) {
		if (power) {
			this.power = true;
		} else {
			this.power = false;
		}
		this.repaint ();
	}

	public void setChannel (int channel) {
		this.channel = ""+channel;
		this.volumeShow = false;
		this.repaint ();
	}

	public void setVolume (int volume) {
		this.volume = volume;
		this.volumeShow = true;
		this.repaint ();
	}

	public void setMuted (boolean muted) {
		this.muted = muted;
		this.volumeShow = !muted;
		this.repaint ();
	}

	public void writeChannel (String str) {
		this.channel = str;
		repaint ();
	}

	public void paint (Graphics graphics) {
		Graphics2D g2d;
		long now;

		now = (new GregorianCalendar()).getTimeInMillis();
		g2d = (Graphics2D) graphics;
		g2d.setFont (TV_OSD_FONT);

		/* Background */
		g2d.setColor (Color.BLACK.brighter());
		g2d.fillRect (0, 0, TV_WIDTH, TV_HEIGHT);

		/* Screen*/
		g2d.setColor (this.power? TV_SCREEN_ON_COLOR: TV_SCREEN_OFF_COLOR);
		g2d.fill (TV_SCREEN_RECTANGLE);

		/* ON/OFF led */
		g2d.setColor (this.power? TV_LED_ON_COLOR: TV_LED_OFF_COLOR);
		g2d.fillArc (TV_WIDTH - (2*TV_BORDER), TV_HEIGHT - (TV_BORDER/2), TV_LED_RADIUS, TV_LED_RADIUS, 0, 360);


		/* Channel OSD */
		if (this.power) {
			g2d.setColor (this.TV_OSD_CHANNEL_COLOR);
			g2d.drawString (this.channel, TV_WIDTH - (TV_BORDER+3*TV_OSD_FONT_SIZE) , 2*TV_BORDER);

			/* Volume OSD */
			if (this.volumeShow) {
				g2d.setColor (this.TV_OSD_VOLUME_COLOR);
				g2d.drawString ("VOLUME: " + this.volume, (TV_BORDER+TV_OSD_FONT_SIZE) , TV_HEIGHT - TV_BORDER - TV_OSD_FONT_SIZE);
			} else if (this.muted) {
				g2d.setColor (this.TV_OSD_VOLUME_COLOR);
				g2d.drawString ("MUTE", (TV_BORDER+TV_OSD_FONT_SIZE) , TV_HEIGHT - TV_BORDER - TV_OSD_FONT_SIZE);
			}
		}
		
	}

}
