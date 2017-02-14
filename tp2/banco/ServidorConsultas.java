package tp2.banco;

import java.io.IOException;
import java.net.Socket;

import tp2.backuparchivos.ServidorBackup;

public class ServidorConsultas extends ServidorDeposito {
	
	protected ServidorBackup serverBackup;
	
	public ServidorConsultas() throws IOException {
		this(5500);
	}
	
	public ServidorConsultas(Integer puerto) throws IOException {
		super(5500);
		this.nombre="Servidor Consultas";
		this.pathArchivo="backup/backup-Cuentas.bin";
	}
	
	public static void main(String[] args) {
		//metodo para probar
		ServidorConsultas sc;
		try {
			sc = new ServidorConsultas();
			new Thread(sc).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void recibiConexion(Socket so){
		System.out.println("+ "+this.nombre+": recibi una conexion desde "+so.getInetAddress());
		Thread t= new Thread(new ThreadConsulta(so));
		t.start();
		this.threads.add(t);
	}
	
	@Override		
	protected void chequearArchivo() {
		//metodo que cheque si esta el archivo e informa
		if(ManejadorArchivo.chequearExistencia(pathArchivo) ){
			System.out.println(this.nombre+" archivo "+this.pathArchivo+" encontrado.");
		}else{
			System.out.println(this.nombre+" archivo "+this.pathArchivo+"NO encontrado!!!!");
			System.out.println(this.nombre+" cuando el servidor reciba el archivo podra hacer consultas");
		}
		System.out.println(this.nombre+" iniciando servicio de backup!");
		this.serverBackup= new ServidorBackup(4444,"backup");
		new Thread(this.serverBackup).start();
		System.out.println(this.nombre+" servicio de backup iniciado!");
	}
	
}
