package tp2.relojes;

import java.io.IOException;

import tp2.balancecarga.ServidorBalanceCarga;

public class MainServidores {

	public static void main(String[] args) {
		//se crea el balanceador de carga,y 2 servidores, el primero saca la fecha mediante Date
		// el segundo de la pagina web (de a momentos daba mal la hora la pagina)
		try {
			ServidorBalanceCarga sbc = new ServidorBalanceCarga();
			new Thread(sbc).start();
			Thread.sleep(100);
			ServidorHora sh1=new ServidorHora(5555,true);	//uso Date
			new Thread(sh1).start();
			Thread.sleep(100);
			ServidorHora sh2=new ServidorHora(5000,false);	//usa web
			new Thread(sh2).start();
			//pueden crear clientes, estos se conectan directo con el balanceador de carga que estan en el puerto 6000
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
