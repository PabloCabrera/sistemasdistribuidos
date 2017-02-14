package tp2.relojes;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import tp2.balancecarga.Hilo;

public class ThreadHoraLocal extends Hilo {
	
	protected Socket s;
	
	public ThreadHoraLocal(Socket socket){
		super(socket);
	}
	
	@Override
	public void run() {
		boolean enviando=true;
		while(enviando){
			try {
				this.inStrm.readObject();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("ThreadHora: se desconecto el cliente");
				enviando=false;
			}
			if(enviando){
				try {
					String fecha = this.conseguirHora();
					//System.out.println("envio la hora:" +fecha);
					this.outStrm.writeObject(fecha);
				} catch (IOException e) {
					System.out.println("ThreadHora: no pude enviar la hora");
					enviando=false;
				}	
			}			
		}
	}
	
	@SuppressWarnings("deprecation")
	protected String conseguirHora(){
		String hora=null;
		
		Date fechaYHora = new Date();
		hora="Hora Local: "+ fechaYHora.getHours() +":"+fechaYHora.getMinutes()+":" +fechaYHora.getSeconds();
		return hora;
	}
	
}