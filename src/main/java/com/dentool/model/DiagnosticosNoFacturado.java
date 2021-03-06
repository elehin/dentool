package com.dentool.model;

import java.util.Date;

public class DiagnosticosNoFacturado {

	private long pacienteId;
	private String name;
	private String apellidos;
	private String dni;
	private float importe;
	private Date fecha;

	public long getPacienteId() {
		return pacienteId;
	}

	public void setPacienteId(long pacienteId) {
		this.pacienteId = pacienteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public float getImporte() {
		return importe;
	}

	public void setImporte(float importe) {
		this.importe = importe;
	}

	@Override
	public String toString() {
		return "DiagnosticoNoFacturado pacienteId : \"" + this.pacienteId + "\"";
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
