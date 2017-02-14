package tp2.sobel;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Graphics2D;
import java.rmi.Remote; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;

/* Responsabilidades de la clase:
	cargar una porcion de imagen
	aplicar el filtro sobel sobmre la porcion de imagen
	devolver el resultado
*/

public class WorkerSobel extends UnicastRemoteObject implements RemoteSobel{

	private final static float[][] kernelX = {
		{-1f, 0f, 1f}, 
		{-2f, 0f, 2f}, 
		{-1f, 0f, 1f}
	};


	private final static float[][] kernelY = {
		{-1f, -2f, -1f}, 
		{0f,  0f,  0f}, 
		{1f,  2f,  1f}
	};

	private BufferedImage fuente = null;
	private BufferedImage fuente_gris = null;
	private BufferedImage procesada = null;
	private Integer estado = RemoteSobel.NO_INICIADO; // 0=no_iniciado, 1=trabajando, 2=completo, -1=fallo

	public WorkerSobel () throws RemoteException {
	}

	@Override
	public SerializableImage sobel (SerializableImage entrada) throws RemoteException, Exception {
		if (this.estado != RemoteSobel.TRABAJANDO) {
			synchronized (this.estado) {
				this.estado = 1;
			}
			this.fuente = entrada.getImage();
			this.fuente_gris =new BufferedImage (this.fuente.getWidth(), this.fuente.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g2d = (Graphics2D) this.fuente_gris.getGraphics();
			g2d.drawImage(fuente, 0, 0, null);
			this.procesada = new BufferedImage (this.fuente.getWidth(), this.fuente.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

			this.sobel_real (this.fuente_gris, this.procesada);

			synchronized (this.estado) {
				this.estado=2;
			}
			SerializableImage salida = new SerializableImage(this.procesada);
			return salida;
		} else {
			throw new Exception ("Worker ocupado");
			//return null;
		}
	}

	@Override
	public int getEstado() throws RemoteException {
		// Esto sirve para saber si el cliente se cayo
		synchronized (this.estado) {
			return this.estado;
		}
	}

	private void sobel_real(BufferedImage img, BufferedImage out) { 

		int w = img.getWidth();
		int h = img.getHeight();
		float pixel_x, pixel_y;
		Raster rin = img.getData();
		WritableRaster rout = out.getRaster();

		 for (int x=1; x < w-2; x++) {
			for (int y=1; y < h-2; y++) {
				pixel_x = 1/6 *
					(kernelX[0][0] * rin.getSample(x-1,y-1,0)) + (kernelX[0][1] * rin.getSample(x,y-1,0)) + (kernelX[0][2] * rin.getSample(x+1,y-1,0)) +
					(kernelX[1][0] * rin.getSample(x-1,y,  0)) + (kernelX[1][1] * rin.getSample(x,y,  0)) + (kernelX[1][2] * rin.getSample(x+1,y  ,0)) +
					(kernelX[2][0] * rin.getSample(x-1,y+1,0)) + (kernelX[2][1] * rin.getSample(x,y+1,0)) + (kernelX[2][2] * rin.getSample(x+1,y+1,0));

				pixel_y = 1/6 * 
					(kernelY[0][0] * rin.getSample(x-1,y-1,0)) + (kernelY[0][1] * rin.getSample(x,y-1,0)) + (kernelY[0][2] * rin.getSample(x+1,y-1,0)) +
					(kernelY[1][0] * rin.getSample(x-1,y  ,0)) + (kernelY[1][1] * rin.getSample(x,y  ,0)) + (kernelY[1][2] * rin.getSample(x+1,y  ,0)) +
					(kernelY[2][0] * rin.getSample(x-1,y+1,0)) + (kernelY[2][1] * rin.getSample(x,y+1,0)) + (kernelX[2][2] * rin.getSample(x+1,y+1,0));

				int val = (int)Math.sqrt((pixel_x * pixel_x) + (pixel_y * pixel_y));

				if(val < 0) {
				   val = 0;
				}

				if(val > 255) {
					  val = 255;
				}

				rout.setSample(x, y, 0, val);

			}
		}
	}
}
