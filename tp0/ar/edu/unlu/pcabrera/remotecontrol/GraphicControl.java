package ar.edu.unlu.pcabrera.remotecontrol;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GraphicControl implements ActionListener, WindowListener {
	RemoteControl controllable = null;
	JFrame mainWindow = null;

	String[][] buttonLayout = {
		{"E",  null, "On"},
		{null, null, null},
		{"1",  "2",   "3"},
		{"4",  "5",   "6"},
		{"7",  "8",   "9"},
		{null, "0",  null},
		{null, null, null},
		{null, "^",  null},
		{"-",  "M",   "+"},
		{null, "v",  null},
		{null, null, null},
		{"[]", ">",  "||"},
		{"|<", "o",  ">|"}

	};

	public GraphicControl(){
		int row=0, col=0;
		String label;
		JButton button;

		this.mainWindow = new JFrame("Control remoto");
		this.mainWindow.addWindowListener(this);
		this.mainWindow.setLayout(new GridLayout (13,3));

		for (row=0; row < 13; row++) {
			for (col=0; col < 3; col++) {
				label = this.buttonLayout[row][col];
				if (label == null) {
					this.mainWindow.getContentPane().add(new JLabel(" "));
				} else {
					button = new JButton(label);
					button.addActionListener(this);
					this.mainWindow.getContentPane().add(button);
				}
			}
		}

		this.mainWindow.pack();
		this.mainWindow.show();
	}

	public void setControllable(RemoteControl controllable) {
		this.controllable = controllable;
	}

	public void start() throws Exception {
		boolean quit = false;
		int input;

		if (this.controllable == null) {
			System.out.println ("El control remoto esta desconectado");
			quit = true;
		}
	}

	public void actionPerformed (ActionEvent event) {
		Object src;
		JButton button;
		String label;
	
		src = event.getSource();
		if (src instanceof JButton) {
			button = (JButton) event.getSource();
			label = button.getText();
	
			switch (label) {
				case "E":
					this.controllable.eject();
					break;
				case "On":
					this.controllable.powerOnOff();
					break;
				case "1":
					this.controllable.number1();
					break;
				case "2":
					this.controllable.number2();
						break;
				case "3":
					this.controllable.number3();
					break;
				case "4":
					this.controllable.number4();
					break;
				case "5":
					this.controllable.number5();
					break;
				case "6":
					this.controllable.number6();
					break;
				case "7":
					this.controllable.number7();
					break;
				case "8":
					this.controllable.number8();
					break;
				case "9":
					this.controllable.number9();
					break;
				case "R":
					this.controllable.rec();
					break;
				case "0":
					this.controllable.number0();
					break;
				case "M":
					this.controllable.mute();
					break;
				case "^":
					this.controllable.channelNext();
					break;
				case "v":
					this.controllable.channelPrev();
					break;
				case "-":
					this.controllable.volumeDown();
					break;
				case "+":
					this.controllable.volumeUp();
					break;
				case "[]":
					this.controllable.stop();
					break;
				case ">":
					this.controllable.play();
					break;
				case "||":
					this.controllable.pause();
					break;
				case "|<":
					this.controllable.trackPrevious();
					break;
				case ">|":
					this.controllable.trackNext();
					break;
			}
		}
	}

	public void windowClosing (WindowEvent e) {
		System.exit(0);
	}
	public void windowActivated (WindowEvent e) {}
	public void windowClosed (WindowEvent e) {}
	public void windowDeactivated (WindowEvent e) {}
	public void windowDeiconified (WindowEvent e) {}
	public void windowIconified (WindowEvent e) {}
	public void windowOpened (WindowEvent e) {}
}

