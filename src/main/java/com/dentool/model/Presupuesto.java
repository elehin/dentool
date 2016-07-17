package com.dentool.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Presupuesto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long pacienteId;
	@Temporal(TemporalType.DATE)
	private Date fecha;
	private String fileName;
	private float precio;

	@ManyToMany(targetEntity = Diagnostico.class, fetch = FetchType.EAGER)
	private List<Diagnostico> diagnosticos;

	public List<Diagnostico> getDiagnosticos() {
		return diagnosticos;
	}

	public void setDiagnosticos(List<Diagnostico> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void update(Presupuesto origen) {
	}

	public long getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(long pacienteId) {
		this.pacienteId = pacienteId;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

}
