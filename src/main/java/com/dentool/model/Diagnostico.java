package com.dentool.model;

import java.sql.Date;

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

	@ManyToOne(optional = false)
	@JoinColumn(name = "paciente", referencedColumnName = "id")
	@JsonBackReference
	private Paciente paciente;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Tratamiento tratamiento;
	private boolean iniciado;
	private boolean finalizado;
	private Date diagnosticado;
	private Date fechaFin;
	private Date fechaInicio;
	private float precio;
	private float pagado;

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
		setPrecio(origen.getPrecio());
		setIniciado(origen.isIniciado());
		setFinalizado(origen.isFinalizado());
		setDiagnosticado(origen.getDiagnosticado());
		setFechaFin(origen.getFechaFin());
		setFechaInicio(origen.getFechaInicio());
		setPrecio(origen.getPrecio());
		setPagado(origen.getPagado());
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
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
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

}
