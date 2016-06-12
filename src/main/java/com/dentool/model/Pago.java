package com.dentool.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pago {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long diagnosticoId;
	private float cantidad;
	private Date fecha;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getCantidad() {
		return cantidad;
	}

	public void setCantidad(float cantidad) {
		this.cantidad = cantidad;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public long getDiagnosticoId() {
		return diagnosticoId;
	}

	public void setDiagnosticoId(long diagnosticoId) {
		this.diagnosticoId = diagnosticoId;
	}

}
