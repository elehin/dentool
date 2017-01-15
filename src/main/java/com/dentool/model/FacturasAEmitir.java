package com.dentool.model;

import java.sql.Date;
import java.util.List;

public class FacturasAEmitir {

	private List<Long> pacientes;
	private Date fechaFactura;
	private String timezone;

	public Date getFechaFactura() {
		return fechaFactura;
	}

	public void setFechaFactura(Date fechaFactura) {
		this.fechaFactura = fechaFactura;
	}

	public List<Long> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Long> pacientes) {
		this.pacientes = pacientes;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
