package com.dentool.model.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Personal {

	@Id
	@SequenceGenerator(name = "personal_id_seq", sequenceName = "personal_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personal_id_seq")
	private long id;

	@Temporal(TemporalType.DATE)
	private Date fechaAlta;
	@Temporal(TemporalType.DATE)
	private Date fechaBaja;
	private boolean activo;
	private String nombre;
	private String apellidos;
	private String puesto;

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Personal() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public void update(Personal origen) {
		this.setFechaAlta(origen.getFechaAlta());
		this.setFechaBaja(origen.getFechaBaja());
		this.setActivo(origen.isActivo());
		this.setNombre(origen.getNombre());
		this.setApellidos(origen.getApellidos());
		this.setPuesto(origen.getPuesto());
	}
}
