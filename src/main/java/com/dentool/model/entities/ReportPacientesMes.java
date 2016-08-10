package com.dentool.model.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ReportPacientesMes {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private int altas;
	@Temporal(TemporalType.DATE)
	private Date fecha;
	private int pacientesTratados;

	public ReportPacientesMes() {
		this.altas = 0;
		this.setPacientesTratados(0);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAltas() {
		return altas;
	}

	public void setAltas(int altas) {
		this.altas = altas;
	}

	public int getPacientesTratados() {
		return pacientesTratados;
	}

	public void setPacientesTratados(int pacientesTratados) {
		this.pacientesTratados = pacientesTratados;
	}

}
