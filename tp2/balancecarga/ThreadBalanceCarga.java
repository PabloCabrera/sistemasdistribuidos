package tp2.balancecarga;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadBalanceCarga extends Hilo {
	
	protected Integer id;
	protected Socket socketServidor;
	protected String ipServidor;
	protected Integer puertoServidor;
	protected ObjectOutputStream enviarAServidor;
	protected ObjectInputStream leerDeServidor;
	protected boolean conectadoServidor;
	
	public ThreadBalanceCarga(Socket so, Integer nrohilo,String ipServidor, Integer puerto) {
		super(so);
		this.id=nrohilo;
		this.ipServidor=ipServidor.trim();
		this.puertoServidor=puerto;
		this.conectadoServidor=false;
	}
	
	public boolean ConectarseAServidor(){
		try {
			this.socketServidor=new Socket(ipServidor,puertoServidor);
			this.enviarAServidor=new ObjectOutputStream( this.socketServidor.getOutputStream() );
			this.leerDeServidor=new ObjectInputStream( this.socketServidor.getInputStream() );
			this.conectadoServidor=true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void run(){
		if(this.conectadoServidor){
			try {
				//este hilo se encarga de leer lo que manda el cliente y mandarselo al servidor
				Thread lectura = new Thread(new ThreadLecturaEnvio(this.id,this.inStrm,this.enviarAServidor));	
				lectura.start();
				//este hilo hace al reves, lo que le envia el servidor se lo envia al cliente
				Thread escritura = new Thread(new ThreadLecturaEnvio(this.id,this.leerDeServidor,this.outStrm));
				//espero que lo hilos terminen, terminan cuando se corta la comunicacion
				escritura.start();
				lectura.join();
				escritura.join();
			} catch (Exception e) {
				//TODO borra excepcion una vez q ande todo bien
				//e.printStackTrace();
			}
		}
	}
	
}