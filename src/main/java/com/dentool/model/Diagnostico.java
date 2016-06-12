package com.dentool.model;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Diagnostico {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "paciente", referencedColumnName = "id")
	@JsonBackReference
	private Paciente paciente;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.MERGE)
	private Tratamiento tratamiento;

	private boolean iniciado;
	private boolean finalizado;
	private Date diagnosticado;
	private Date fechaFin;
	private Date fechaInicio;
	private float precio;
	private float pagado;
	private short pieza;

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public float getPagado() {
		return pagado;
	}

	public void setPagado(float pagado) {
		this.pagado = pagado;
	}

	public void update(Diagnostico origen) {
		if (origen.getPrecio() != 0) {
			this.setPrecio(origen.getPrecio());
		}
		if (origen.getDiagnosticado() != null) {
			this.setDiagnosticado(origen.getDiagnosticado());
		}
		if (origen.getFechaFin() != null) {
			this.setFechaFin(origen.getFechaFin());
		}
		if (origen.getFechaInicio() != null) {
			this.setFechaInicio(origen.getFechaInicio());
		}
		if (origen.getPrecio() != 0) {
			this.setPrecio(origen.getPrecio());
		}
		if (origen.getPagado() != 0) {
			this.setPagado(origen.getPagado());
		}
		if (origen.getPieza() != 0) {
			this.setPieza(origen.getPieza());
		}
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isIniciado() {
		return iniciado;
	}

	public void setIniciado(boolean iniciado) {
		this.iniciado = iniciado;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public Date getDiagnosticado() {
		return diagnosticado;
	}

	public void setDiagnosticado(Date diagnosticado) {
		this.diagnosticado = diagnosticado;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
		setFinalizado(this.fechaFin != null);
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
		setIniciado(this.fechaInicio != null);
	}

	public Tratamiento getTratamiento() {
		return tratamiento;
	}

	public void setTratamiento(Tratamiento tratamiento) {
		this.tratamiento = tratamiento;
	}

	@Override
	public String toString() {
		String s = "Diagnostico: {\"id\" : \"" + this.id + "\", \"tratamiento\" : \"" + this.tratamiento + "\"}";
		return s;
	}

	public short getPieza() {
		return pieza;
	}

	public void setPieza(short pieza) {
		this.pieza = pieza;
	}

}
