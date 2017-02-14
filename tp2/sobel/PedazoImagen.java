package tp2.sobel;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class PedazoImagen {
	private int originalWidth, originalHeight;
	private int offsetX, offsetY;
	private int width, height;
	private BufferedImage buffer;
	private boolean procesado = false;

	public PedazoImagen 
	(
		int originalWidth, int originalHeight,
		int offsetX,int offsetY,
		int width, int height,
		int imageType
	) {
		this.originalWidth = originalWidth;
		this.originalHeight = originalHeight;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		buffer = new BufferedImage(width, height, imageType);
	}

	public int getOriginalWidth() {
		return originalWidth;
	}

	public void setOriginalWidth(int originalWidth) {
		this.originalWidth = originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public void setOriginalHeight(int originalHeight) {
		this.originalHeight = originalHeight;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public BufferedImage getBuffer() {
		return buffer;
	}

	public void setBuffer(BufferedImage buffer) {
		this.buffer = buffer;
	}

	public void setProcesado (boolean p) {
		this.procesado = p;
	}

	public boolean getProcesado () {
		return this.procesado;
	}

	public void pegarEn(BufferedImage lienzo) {
		Graphics2D g2d = (Graphics2D) lienzo.getGraphics();
		g2d.drawImage (buffer, this.offsetX, this.offsetY, null);
	}
}
