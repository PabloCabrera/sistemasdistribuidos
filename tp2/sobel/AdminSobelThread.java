package tp2.sobel;

import java.awt.image.BufferedImage;

class AdminSobelThread extends Thread {
	private PedazoImagen pedazo = null;
	private BufferedImage imagen = null;
	private BufferedImage img_sobel = null;
	private RemoteSobel worker = null;
	private AdminSobel maestro = null;
	
	public AdminSobelThread(PedazoImagen pedazo, RemoteSobel worker, AdminSobel maestro) {
		this.pedazo = pedazo;
		this.imagen = pedazo.getBuffer();
		this.worker = worker;
		this.maestro = maestro;
	}

	public PedazoImagen getPedazo() {
		return this.pedazo;
	}
	
	@Override
	public void run (){
		boolean salir = false;
		int estadoWorker;
		SerializableImage serImagen, serSobel;
		while (!salir) {
			try {
			 estadoWorker = this.worker.getEstado();
			 switch (estadoWorker) {
			 	case RemoteSobel.COMPLETO:
			 	case RemoteSobel.NO_INICIADO:
			 		serImagen = new SerializableImage (this.imagen);
			 		serSobel = this.worker.sobel(serImagen);
			 		this.img_sobel = serSobel.getImage();
			 		this.pedazo.setBuffer (img_sobel);
			 		this.pedazo.setProcesado(true);
			 		maestro.notificar (RemoteSobel.COMPLETO, this);
			 		salir=true;
			 		break;
			 	case RemoteSobel.TRABAJANDO:
			 		Thread.sleep(1);
			 		break;
			 	case RemoteSobel.FALLO:
			 		maestro.notificar (RemoteSobel.FALLO, this);
			 		salir = true;
			 		break;
			 }
			} catch (Exception e) {
			 maestro.notificar (RemoteSobel.DESCONECTADO, this);
			 System.err.println("Error en AdminSobelThread: "+e.getMessage());
			}
		}
	}
}
