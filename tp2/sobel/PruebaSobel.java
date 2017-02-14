package tp2.sobel;

import java.util.Date;
import java.io.File;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class PruebaSobel extends JFrame {
	public static void main (String[] args) {
		PruebaSobel ventana = new PruebaSobel();
	}

	private Image imagen = null;

	public PruebaSobel() {
		super("Prueba Sobel");
		this.setSize(1920, 1080);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.show();
		try {
			BufferedImage img_in = ImageIO.read (new File("sobel/imagen_prueba2.jpg"));
			this.setImg(img_in);
			Date antes = new Date();
			WorkerSobel worker = new WorkerSobel();
			Date despues = new Date();
			System.out.println("Se ha completado el trabajo en "+ (despues.getTime() - antes.getTime()) +" milisegundos");
			SerializableImage img_serial_out = worker.sobel(new SerializableImage(img_in));
			BufferedImage img_out = img_serial_out.getImage();
			this.setImg(img_out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint (Graphics g) {
		if (this.imagen != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage (this.imagen, 0, 0, null);
		}
	}

	public void setImg(Image img){
		this.imagen=img;
		this.repaint();
	}
}
