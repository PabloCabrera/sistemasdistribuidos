package tp2.banco;

import java.io.Serializable;

public class MensajeBanco implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected Integer monto;
	protected Operacion operacion;
	protected String error;
	
	public MensajeBanco(int id, int monto, Operacion operacion) {
		this.id=id;
		this.monto=monto;
		this.operacion=operacion;
	}

	public MensajeBanco() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public Operacion getOperacion() {
		return operacion;
	}

	public void setOperacion(Operacion operacion) {
		this.operacion = operacion;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	
	
}
