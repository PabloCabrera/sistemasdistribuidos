package tp2.backuparchivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

//clase que vigila una carpeta y los archivos que tiene, si estos son modificados, creados o borrados
//se los manda a un servidor.
//se le debe pasar el nombre de la carpeta (con path)
//un arreglo con los nombres de los archivos a copiar al servidor. SI ES NULO SE COPIAN TODOS LOS ARCHIVOS DE LA CARPETA
// se le debe pasar los datos del servidor, ip y puerto

public class WatcherArchivo implements Runnable {
	
	//atributos que necesito que me pasen
	protected String carpeta;	//carpeta a vigilar
	protected String[] archivos;	//archivos a vigilar de esa carpeta, el archivo que se modifica no esa aca no le doy bola
	protected String  ipServidor;	//servidor al cual mandarle el archivo nuevo-modificado
	protected Integer puertoServidor;
	protected boolean copiarCualquierCosa;	//si recibo null en archivos, hago backup de cualquier cosa q haya en la carpeta
	
	//atributos para el funcionamiento propio
	protected boolean funcionando;	//si esta andando el servicio
	protected WatchService watcher;
	protected WatchKey watchKey;
	protected Path myDir;
	protected boolean bloqueado;	//si el proceso esta bloqueado esperando el primer evento
	protected Socket socket; 
	protected OutputStream outStream;
	
	public static void main(String[] args) {
    	//para probar el ejercisio2y3
    	String[] archivos= {"Cuentas.txt"};
    	WatcherArchivo wa= new WatcherArchivo("cuentas",archivos,"localhost",4444);
    	Thread hilo = new Thread(wa);
    	hilo.start();
    }
	
	public WatcherArchivo(){
		//contructor vacio
	}
	
	public WatcherArchivo(String carpeta,String[] archivos, String ipServidor,Integer puertoServidor){
		this.carpeta=carpeta;
		this.archivos= archivos;
		if(archivos==null){
			this.copiarCualquierCosa=true;
		}
		this.ipServidor = ipServidor;
		this.puertoServidor=puertoServidor;
		this.funcionando=false;
	}
    
    @Override
	public void run() {
		//separe todo en metodos para si hay q hacer modificaciones sea facil reutilizar codigo
		if(this.inicializarWatcher()){
			this.recibirEventos();
			this.cerrarWatchers();
		}
	}
    
    protected boolean inicializarWatcher(){
    	myDir = Paths.get(carpeta);
		try {
			//obtengo un observador para esa carpeta
			watcher = myDir.getFileSystem().newWatchService();
			//lo registro y dico que eventos tome
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
		     		   StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			System.out.println("+WatcherArchivo - esperando eventos: ");
			this.bloqueado=true;
			watchKey = watcher.take();	//SE BLOQUE ACA HASTA Q HAY UN EVENTO AUNQUE SEA
			this.bloqueado=false;
		} catch (IOException e) {
			System.out.println("+WatcherArchivo - ERROR: INICIALIZAR WATCHER: ");
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			System.out.println("+WatcherArchivo - ERROR: INICIALIZAR WATCHER: ");
			e.printStackTrace();
			return false;
		}
		return true;
    }
    
    protected boolean recibirEventos(){
    	this.funcionando=true;
    	while(this.funcionando){
			//mientras este funcionando reviso si tengo eventos
			List<WatchEvent<?>> events = watchKey.pollEvents();
			//por cada evento hago algo
			for (WatchEvent event : events) {
				boolean esArchivoACopiar=false;
				int i=0;
				//me fijo si el archivo es un archivo
				if(this.copiarCualquierCosa){	//si copio cualquier cosa no entro al while 
					esArchivoACopiar=true;
				}
				while(!esArchivoACopiar && i<this.archivos.length){	//while para verificar si copio el archivo o no
					if(event.context().toString().contains(this.archivos[i])){
						esArchivoACopiar=true;
					}
				}
				if(esArchivoACopiar){
					if( !this.conectarse() ){	//si no me pude conectar al servidor termino la ejecucion
						this.funcionando=false;
						return false;
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("+WatcherArchivo - Created: " + event.context().toString());
						this.enviarArchivo(event.context().toString());
	                }
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						//por ahora no hago nada con esto
						System.out.println("+WatcherArchivo - Delete: " + event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("+WatcherArchivo - Modify: " + event.context().toString());
						this.enviarArchivo(event.context().toString());
					}
				}
			}
		}
    	this.funcionando=false;
    	System.out.println("+WatcherArchivo - BAJANDO EL SERVICIO");
    	return true;
    }
    
    protected boolean enviarArchivo(String nombreArchivo){
    	File archivo = new File(carpeta+"/"+nombreArchivo);
        byte[] bytes = new byte[16 * 1024];
        InputStream in=null;
        try {
			in = new FileInputStream(archivo);
		} catch (FileNotFoundException e) {
			System.out.println("+WatcherArchivo - error de archivo no encontrado en EnviarArchivo: "+this.carpeta+"/"+nombreArchivo);
			e.printStackTrace();
			return false;
		}
        //aca le envio un Integer con el tamanio del nombre del archivo (cantidad de caracteres *2)
        Integer tamanioNombreArchivo = nombreArchivo.getBytes().length;
        bytes = ByteBuffer.allocate(4).putInt(tamanioNombreArchivo).array();
        try {
			this.outStream.write(bytes, 0, Integer.BYTES);
			//le envio el nombre del archivo
			bytes = nombreArchivo.getBytes();
			System.out.println("+WatcherArchivo - le envio el nombre de archivo: "+nombreArchivo);
		    this.outStream.write(bytes, 0, bytes.length);
		} catch (IOException e1) {
			System.out.println("+WatcherArchivo - no pude enviar el tamanio o nombre del archivo: ");
			e1.printStackTrace();
			return false;
		}  
        
        int count;
        try {
			while ((count = in.read(bytes)) > 0) {
			    this.outStream.write(bytes, 0, count);
			}
			this.outStream.close();
	        in.close();
	        socket.close();
		} catch (IOException e) {
			System.out.println("+WatcherArchivo - Error en EnviarArchivo: ");
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    protected boolean conectarse() {
		
    	try {
			this.socket= new Socket(this.ipServidor,this.puertoServidor);
			this.outStream = socket.getOutputStream();
		} catch (UnknownHostException e) {
			System.out.println("+WatcherArchivo - no pude encontrar el servidor, finalizo");
			return true;
		} catch (IOException e) {
			System.out.println("+WatcherArchivo - error al conectarse al servidor de backup");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void cerrarWatchers(){
    	try {
    		if(watchKey!=null){
    			watchKey.cancel();
    		}
			if(watcher!=null){
				watcher.close();
			}
		} catch (IOException e) {
			System.out.println("+WatcherArchivo - Error al cerrar los watcher: ");
			e.printStackTrace();
		}
    }
    
    public String getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(String carpeta) {
		this.carpeta = carpeta;
	}

	public String[] getArchivos() {
		return archivos;
	}

	public void setArchivos(String[] archivos) {
		this.archivos = archivos;
	}

	public String getIpServidor() {
		return ipServidor;
	}

	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

	public Integer getPuertoServidor() {
		return puertoServidor;
	}

	public void setPuertoServidor(Integer puertoServidor) {
		this.puertoServidor = puertoServidor;
	}

	public boolean isFuncionando() {
		return funcionando;
	}

	public void setFuncionando(boolean funcionando) {
		this.funcionando = funcionando;
	}

	
}