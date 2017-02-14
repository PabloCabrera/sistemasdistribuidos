package tp2.otros;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import tp2.banco.ThreadDeposito;

public abstract class Servidor implements Runnable {
	
	protected Integer puerto;
	protected ServerSocket serversocket;
	protected ArrayList<Runnable> threads;
	protected String nombre;
	protected boolean esperarConexiones;
	protected Integer tiempoEspera=1000;
	
	public Servidor() throws IOException{
		this(4500);
	}
	
	public Servidor(Integer puerto) throws IOException{
		this.puerto=puerto;
		this.esperarConexiones=true;
		this.threads = new ArrayList<Runnable>();
		this.serversocket = new ServerSocket(this.puerto);
		this.serversocket.setSoTimeout(this.tiempoEspera);
	}
	
	@Override
	public void run() {
		this.levantarServicio();
	}
	
	protected boolean levantarServicio(){
		System.out.println(this.nombre+": LEVANTANDO SERVICIO");
		try {
			System.out.println(this.nombre+" esperando conexiones en: "+ InetAddress.getLocalHost());
		} catch (UnknownHostException e1) {
			System.out.println(this.nombre+" esperando conexiones en: "+ this.serversocket.getInetAddress());
		}
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
	
	protected void recibiConexion(Socket so){
		System.out.println("+ "+this.nombre+": recibi una conexion desde "+so.getInetAddress());
		Thread t= new Thread(new ThreadDeposito(so));
		t.start();
		this.threads.add(t);
	}
	
	public Integer getPuerto() {
		return puerto;
	}

	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}

	public ServerSocket getServersocket() {
		return serversocket;
	}

	public void setServersocket(ServerSocket serversocket) {
		this.serversocket = serversocket;
	}

	public ArrayList<Runnable> getThreads() {
		return threads;
	}

	public void setThreads(ArrayList<Runnable> threads) {
		this.threads = threads;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getTiempoEspera() {
		return tiempoEspera;
	}

	public void setTiempoEspera(Integer tiempoEspera) {
		this.tiempoEspera = tiempoEspera;
	}
	
	public boolean isEsperarConexiones() {
		return esperarConexiones;
	}

	public void setEsperarConexiones(boolean esperarConexiones) {
		this.esperarConexiones = esperarConexiones;
	}
	
}
