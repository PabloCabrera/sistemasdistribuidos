package tp2.banco;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ServidorDeposito implements Runnable {
	
	protected Integer puerto;
	protected ServerSocket serversocket;
	protected ArrayList<Runnable> threads;
	protected String nombre;
	protected String pathArchivo= ManejadorArchivo.pathEJ2y3;
	
	protected boolean esperarConexiones;
	
	public static void main(String[] args) {
		try {
			ServidorDeposito sd= new ServidorDeposito();
			Thread t= new Thread(sd);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServidorDeposito() throws IOException{
		this(4500);
		this.nombre="Servidor Deposito";
	}
	
	public ServidorDeposito(Integer puerto) throws IOException{
		this.puerto=puerto;
		this.esperarConexiones=true;
		this.threads = new ArrayList<Runnable>();
		this.serversocket = new ServerSocket(this.puerto);
		this.serversocket.setSoTimeout(1000);
		chequearArchivo();
	}
	
	protected void chequearArchivo() {
		//metodo que busca si existe el archivo, si existe no hace nada, sino lo crea e inicializa
		if(!ManejadorArchivo.chequearExistencia(pathArchivo) ){
			ManejadorArchivo.crearArchivo(pathArchivo);
			ManejadorArchivo.iniciarArchivoDirecto(pathArchivo);
		}
	}

	protected boolean levantarServicio(){
		System.out.println(this.nombre+": LEVANTANDO SERVICIO");
		System.out.println(this.nombre+" esperando conexiones en: "+this.serversocket.getLocalSocketAddress());
		while(this.esperarConexiones){
			try {
				Socket so=this.serversocket.accept();
				this.recibiConexion(so);
			}catch(SocketTimeoutException e){
				//si sale por el timeout no pasa nada
			}catch (IOException e) {
				System.out.println(this.nombre+" ERROR: ");
				e.printStackTrace();
				System.out.println(this.nombre+" FIN DEL SERVIDOR! ");
				this.esperarConexiones=false;
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		levantarServicio();
	}
	
	protected void recibiConexion(Socket so){
		System.out.println("+ "+this.nombre+": recibi una conexion desde "+so.getInetAddress());
		Thread t= new Thread(new ThreadDeposito(so));
		t.start();
		this.threads.add(t);
	}

	public boolean isEsperarConexiones() {
		return esperarConexiones;
	}

	public void setEsperarConexiones(boolean esperarConexiones) {
		this.esperarConexiones = esperarConexiones;
	}
}
