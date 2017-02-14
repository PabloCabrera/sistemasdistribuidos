package tp2.balancecarga;

public class InfoServidor {
	
	protected String ip;
	protected Integer puerto;
	protected Integer nroConexionesAsignadas;
	
	public InfoServidor(String ip, Integer puertoEmisor) {
		this.ip=ip;
		this.puerto=puertoEmisor;
		this.nroConexionesAsignadas=0;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPuerto() {
		return puerto;
	}
	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}
	public Integer getNroConexionesAsignadas() {
		return nroConexionesAsignadas;
	}
	public void setNroConexionesAsignadas(Integer nroConexionesAsignadas) {
		this.nroConexionesAsignadas = nroConexionesAsignadas;
	}
	public void incrementarNroConexionesAsignadas(){
		this.nroConexionesAsignadas++;
	}
	public void decrementarNroConexionesAsignadas(){
		this.nroConexionesAsignadas--;
	}
	
	@Override
	public boolean equals(Object o){
		if(o.getClass()!=InfoServidor.class){
			return false;
		}else{
			InfoServidor is = (InfoServidor)o;
			if(is.getIp().equals(this.getIp()) && is.getPuerto().equals(this.getPuerto())){
				return true;
			}else{
				return false;
			}
		}
	}
	
	
}