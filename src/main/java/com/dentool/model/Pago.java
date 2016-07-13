package com.dentool.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Pago {

	@Id
	@SequenceGenerator(name = "pago_id_seq", sequenceName = "pago_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pago_id_seq")
	// @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long diagnosticoId;
	private float cantidad;
	@Temporal(TemporalType.DATE)
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
