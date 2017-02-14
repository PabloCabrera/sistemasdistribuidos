package tp2.balancecarga;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ThreadLecturaEnvio implements Runnable {
	protected Integer id;
	protected ObjectOutputStream enviar;
	protected ObjectInputStream leer;
	
	public ThreadLecturaEnvio(Integer id,ObjectInputStream lectura, ObjectOutputStream escritura) {
		this.id=id;
		this.leer=lectura;
		this.enviar=escritura;
	}

	//metodo que lo unico q hace es leer de un stream y mandar al otro.
	//si hay error el hilo termina
	@Override
	public void run() {
		boolean error=false;
		while(!error){
			try {
				Object o=leer.readObject();
				//System.out.println(this.id+"- paso por el balanceador: "+o);
				enviar.writeObject(o);
			} catch (ClassNotFoundException | IOException e) {
				error=true;
				System.out.println(this.id+"- se han desconectado!");
				//e.printStackTrace();
			}
		}
	}

}
