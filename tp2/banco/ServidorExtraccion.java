package tp2.banco;

import java.io.IOException;
import java.net.Socket;

public class ServidorExtraccion extends ServidorDeposito {
	
	public ServidorExtraccion() throws IOException {
		this(5000);
		this.nombre="Servidor Extracciones";
	}

	public static void main(String[] args) {
		try {
			ServidorExtraccion sd= new ServidorExtraccion();
			Thread t= new Thread(sd);
			t.start();
		} catch (IOException e) {
			System.out.println("ERROR: ");
			e.printStackTrace();
		}
	}
	
	public ServidorExtraccion(Integer puerto) throws IOException{
		super(puerto);
	}
	
	@Override
	protected void recibiConexion(Socket so){
		System.out.println("+ "+this.nombre+": recibi una conexion desde "+so.getInetAddress());
		Thread t= new Thread(new ThreadExtraccion(so));
		t.start();
		this.threads.add(t);
	}
}
