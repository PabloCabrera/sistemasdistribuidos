package tp2.backuparchivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServidorBackup implements Runnable {
	
	protected ServerSocket server;
	protected Socket so;
	protected Integer puerto;
	
	protected String carpeta;	//carpeta en donde se guardan los archivos que recibo
	protected boolean funcionando=false;
	protected Integer tiempoEspera=3000;
	protected String prefijo="backup-";	//prefijo para el nombre del nuevo archivo, si queda con el mismo nombre se hace un loop
	
	public static void main(String[] args) {
		//metodo para probar nomas
		ServidorBackup s= new ServidorBackup(4444,"backup");
		Thread t= new Thread(s);
		t.start();
	}
	
	public ServidorBackup(){
		
	}
	
	public ServidorBackup(Integer puerto,String carpeta){
		this.puerto=puerto;
		this.carpeta=carpeta;
	}
	
	@Override
	public void run() {
		funcionando=true;
		try {
			server=new ServerSocket(this.puerto);
		} catch (IOException e) {
			System.out.println("Error del tipo: ");
			e.printStackTrace();
			funcionando=false;
		}
		
		while(funcionando){
			try {
				System.out.println("esperando conexiones, estoy en: "+this.server.getInetAddress());
				this.so=server.accept();
				this.recibirArchivo();
			} catch (IOException e) {
				System.out.println("error dentro del funcionando: ");
				e.printStackTrace();
			}
		}
	}

	protected boolean recibirArchivo(){
		InputStream in = null;
	    OutputStream out = null;
	    String nombreArchivo=null;
		try {
            in = so.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
            return false;
        }
		
		//leo 4 bytes, q es un integer, que me dice cuantos bytes leer ahora, que va a ser el nombre del archivo
		byte[] bytes = new byte[Integer.BYTES];
		int count;
		try {
			in.read(bytes);									//leo los 4 bytes
			ByteBuffer bb = ByteBuffer.wrap(bytes);			//transformo a entero
			count = bb.getInt();
			bytes = new byte[count];						//creo el arreglo con esa cantidad de bytes
			in.read(bytes);									//leo esa cantidad de bytes
			//System.out.println("el nombre del archivo en bytes es: "+bytes);
			nombreArchivo = new String (bytes); //la paso a String y es el nombre del archivo que voy a recibir
			//System.out.println("el nombre del archivo es: "+nombreArchivo);
		} catch (IOException e1) {
			System.out.println("no pude recibir el tamanio o nombre del archivo");
			e1.printStackTrace();
			return false;
		}
		nombreArchivo = carpeta+"/"+this.prefijo+nombreArchivo;
		File archivo= new File(nombreArchivo);	//chequeo si existe el archivo, sino lo creo
		if(!archivo.exists()){
			try {
				archivo.createNewFile();
			} catch (IOException e) {
				System.out.println("no pude crear el archivo");
				e.printStackTrace();
				return false;
			}
		}
		
        try {
            out = new FileOutputStream(nombreArchivo);	//pongo el archivo como destino
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
            return false;
        }
		
        bytes = new byte[16*1024];				//redimensiono el arreglo a un tamano alto
        try {
			while ((count = in.read(bytes)) > 0) {		//leo bytes del Stream del socket, se guardan en la variable bytes
			    out.write(bytes, 0, count);				//guardo esos bytes en el archivo
			}
			out.close();								//cierro todo
	        in.close();
	        so.close();
	        System.out.println("hice backup en el archivo: "+nombreArchivo);
		} catch (IOException e) {
			System.out.println("no pude leer del socket y escribir en el archivo: ");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
