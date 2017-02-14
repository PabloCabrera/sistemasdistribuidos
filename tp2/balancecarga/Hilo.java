package tp2.balancecarga;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Hilo implements Runnable {
	
	protected Socket s;
	protected ObjectOutputStream outStrm;
	protected ObjectInputStream inStrm;
	
	public Hilo(Socket socket){
		this.s=socket;
		try {
			this.inStrm=new ObjectInputStream( this.s.getInputStream() );
			this.outStrm=new ObjectOutputStream( this.s.getOutputStream() );
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
	
	public Hilo(){
	}
	
	@Override
	public void run() {
		
	}
	
	protected void cerrarConexion() {
		try {
			if(this.inStrm!=null){
				this.inStrm.close();
			}
			if(this.outStrm!=null){
				this.outStrm.close();
			}
			if(this.s!=null){
				this.s.close();
			}
		} catch (IOException e) {
			//no me interesan los errores q haya aca
		}
	}
	
	//metodo para que este hilo libere los recursos y el garbage colector lo elimine(en caso de que no lo inicialize)
	public void anular(){
		this.s=null;
		this.inStrm=null;
		this.outStrm=null;
	}
}
