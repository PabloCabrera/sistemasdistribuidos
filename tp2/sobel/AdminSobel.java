package tp2.sobel;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/* Responsabilidades de la clase:
	cargar una imagen
	dividirla en N partes 
	enviar las partes N WorkerSobel para que las procesen
	recibir los resultados y guardarlos temporalmente
	unir los resultados
	mostrar el resultado final en pantalla y/o permitir guardarlo en un archivo
	mostrar datos de performance
*/

public class AdminSobel extends JFrame{
	protected final static int NUM_DIVISIONES = 4;
	protected final static String HOSTLIST_FILE = "ej_4/hostlist.txt";
	protected final static String INPUT_FILE = "ej_4/imagen_prueba2.jpg";

	protected BufferedImage imagen_fuente = null;
	protected BufferedImage imagen_procesada = null;
	protected BufferedImage imagen_mostrar = null;
	protected ArrayList<RemoteSobel> workers;
	protected ArrayList<Thread> hilos;
	protected ArrayList<PedazoImagen> pedazos = null;
	protected int completados = 0;
	protected int total = NUM_DIVISIONES;
	protected int ultimo_worker_asignado = -1;
	protected Date tiempo_inicio =null;
	protected Date tiempo_fin =null;
	
	public static void main(String args[]){
		AdminSobel admin = new AdminSobel();
		admin.cargarImagen (new File (INPUT_FILE));
		admin.iniciarProceso();
		
	}

	public AdminSobel() {
		super("Sobel Distribuido");
		this.workers = new ArrayList<RemoteSobel>();
		this.hilos = new ArrayList<Thread>();
		this.conectarAWorkers();

		this.setSize(1920, 1080);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.show();
	}

	public void conectarAWorkers() {
		// El archivo hostlists tiene la lista de servidores
		try {
			Scanner fscan = new Scanner (new File (HOSTLIST_FILE));
			String linea, host, regname;
			int spc;


			while (fscan.hasNextLine()) {
				linea = fscan.nextLine();
				if (linea.indexOf("#") != 0) {
					spc = linea.indexOf(" ");
					if (spc >0 && spc < linea.length() -1) {
						host=linea.substring(0, spc);
						regname=linea.substring(spc+1);
						System.out.println("Conectar al recurso "+regname+" del servidor "+host);
						RemoteSobel worker = this.conectarWorker(host, regname);
						if (worker != null) {
							this.workers.add(worker);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println ("No se ha encontrado el archivo de listado de hosts "+HOSTLIST_FILE);
		}
	}

	private RemoteSobel conectarWorker (String hostname, String regname) {
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			RemoteSobel worker = (RemoteSobel) registry.lookup(regname);
			return worker;
		} catch (Exception e) {
			System.out.println("No se pudo conectar al recurso "+regname+" del servidor "+hostname);
			return null;
		}
	}

	public boolean cargarImagen (File archivo) {
		try {
			this.imagen_fuente = ImageIO.read(archivo);
			this.imagen_mostrar = this.imagen_fuente;
			this.repaint();
			this.imagen_procesada = new BufferedImage(this.imagen_fuente.getWidth(), this.imagen_fuente.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			this.pedazos = this.dividir (NUM_DIVISIONES, this.imagen_fuente); 
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void iniciarProceso () {
		System.out.println("La imagen se ha divido en "+this.pedazos.size()+" pedazos");
		this.tiempo_inicio = new Date();
		for (PedazoImagen pedazo: this.pedazos) {
			if (!pedazo.getProcesado()) {
				RemoteSobel worker = this.getWorkerDisponible();
				if (worker == null) {
					throw new RuntimeException ("No hay workers disponibles para esta tarea. ABORTADO.");
				}
				AdminSobelThread hilo = new AdminSobelThread (pedazo, worker, this);
				this.hilos.add (hilo);
				hilo.start();
			}
		}
	}

	public RemoteSobel getWorkerDisponible() {
		RemoteSobel worker = null;
		int cuenta = 0;
		int estadoWorker;
		while (worker == null && cuenta <= 1+3*this.workers.size()) {
			try {
				ultimo_worker_asignado++;
				estadoWorker = this.workers.get(ultimo_worker_asignado).getEstado();
				if (estadoWorker == RemoteSobel.NO_INICIADO || estadoWorker == RemoteSobel.COMPLETO) {
					worker = this.workers.get(ultimo_worker_asignado);
				}
			} catch (Exception e) {
				System.err.println ("Error buscando worker asignable "+e.getMessage());
			}
			cuenta ++;
		}
		return worker;
	}

	public void mostrarResultado () {
		for (PedazoImagen pedazo: this.pedazos) {
			pedazo.pegarEn (this.imagen_procesada);
		}
		this.imagen_mostrar = imagen_procesada;
		this.repaint();
	}

	/* Dada una imagen, devuelve una lista de pedazos de esa imagen */
	private ArrayList<PedazoImagen> dividir (int partes, BufferedImage imagen) {
		ArrayList<PedazoImagen> pedazos = new ArrayList<PedazoImagen>();
		List<Rectangle> rectangulos = this.recortes (partes, imagen);
		for (Rectangle rect: rectangulos) {
			BufferedImage buf_pedazo = imagen.getSubimage((int)rect.getX(),(int) rect.getY(),(int) rect.getWidth(),(int) rect.getHeight());
			PedazoImagen pedazo = new PedazoImagen(imagen.getWidth(), imagen.getHeight(), (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(), imagen.getType());
			pedazo.setBuffer(buf_pedazo);
			pedazos.add(pedazo);
		}
		return pedazos;
	}

	private List<Rectangle> recortes (int partes, BufferedImage imagen) {
		Rectangle rect = new Rectangle(imagen.getWidth(), imagen.getHeight());
		return this.recortes (partes, rect);
	}

	private List<Rectangle> recortes (int partes, Rectangle rectangle) {
		List<Rectangle> rects = new ArrayList<Rectangle>();
			// Hacer cuadraditos
			if (partes == 1) {
				rects.add (rectangle);
				return rects;
			} else if (partes == 4) {
				int midX =(int) (rectangle.getWidth() / 2);
				int midY =(int) rectangle.getHeight() / 2;
				boolean xPar = (rectangle.getWidth()%2 == 0);
				boolean yPar = (rectangle.getHeight()%2 == 0);
				Rectangle r1, r2, r3, r4;
				r1 = new Rectangle (midX+1, midY+1);
				r2 = new Rectangle (
					midX, //offset X
					0, //offset Y
					xPar? midX: midX+1, //width
					midY //height
				);
				r3 = new Rectangle (
					0, //offset X
					midY, //offset Y
					midX, //width
					yPar? midY: midY+1 //height
				);
				r4 = new Rectangle (
					midX, //offset X
					midY, //offset Y
					xPar? midX: midX+1, //width
					yPar? midY: midY+1 //height
				);
				rects.add (r1);
				rects.add (r2);
				rects.add (r3);
				rects.add (r4);
			} else {
				throw new RuntimeException ("Solo esta soportado dividir la imagen en 4 partes");
			}
		return rects;
	}

	public void notificar (int estado, AdminSobelThread hilo) {
		switch (estado) {
			case RemoteSobel.COMPLETO:
				System.out.println ("Uno de los procesos distribuidos completo la tarea");
				this.completados++;
				if (this.completados == total) {
					this.tiempo_fin = new Date();
					long transcurrido = tiempo_fin.getTime()-tiempo_inicio.getTime();
					System.out.println("Se ha completado el trabajo en "+transcurrido+" milisegundos");
					this.mostrarResultado();
				}
				break;
			case RemoteSobel.FALLO:
				System.err.println ("Uno de los procesos distribuidos fallo");
				break;
			case RemoteSobel.DESCONECTADO:
				System.err.println ("Se perdio la conexion con uno de los proceso distribuidos");
				break;
		}
	}

	@Override
	public void paint (Graphics g) {
		if (this.imagen_mostrar != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage (this.imagen_mostrar, 0, 0, null);
		}
	}
	
}
