package tp2.banco;

import java.util.Scanner;
import java.io.IOException;

import tp2.backuparchivos.WatcherArchivo;

public class MainServidoresBanco {
	
	private  ServidorDeposito sd;
	private  ServidorExtraccion se;
	private  WatcherArchivo backupServidor;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		MainServidoresBanco main= new MainServidoresBanco();
	}
	
	public MainServidoresBanco(){
		ServidorConsultas sc;
		try {
			sc = new ServidorConsultas();
			new Thread(sc).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			sd = new ServidorDeposito();
			new Thread(sd).start();
			Thread.sleep(1000);		//le doy tiempo al primer metodo para crear el archivo si no existe y inicializarlo
			
			se = new ServidorExtraccion();
			new Thread(se).start();
			
			String[] archivos={"Cuentas.bin"};		//este es el archivo que se va a hacer backup
											//los parametros son: 1-nombre de la carpeta en la que estan los archivos a observar
											//2- arreglo con los nombres de los archivos a observar
											//3- ip del servidor al q le manda los archivos
											//4 - puerto en el q escucha el servidor
			backupServidor = new WatcherArchivo("cuentas",archivos,"localhost",4444);
			new Thread(backupServidor).start();
			
			Thread.sleep(250);		//lo hago dormir un cachito asi quedan bien los println
			Scanner s= new Scanner(System.in);
			System.out.println("ingrese algo para bajar los servidores");
			s.nextLine();
			if(sd!=null){
				sd.setEsperarConexiones(false);
			}
			if(se!=null){
				se.setEsperarConexiones(false);
			}
			if(backupServidor!=null){
				backupServidor.setFuncionando(false);
			}
			s.close();
		} catch (Exception e) {
			//si algo falla bajo todos los servidores
			if(sd!=null){
				sd.setEsperarConexiones(false);
			}
			if(se!=null){
				se.setEsperarConexiones(false);
			}
			if(backupServidor!=null){
				backupServidor.setFuncionando(false);
			}
		}
		
	}
	
}
