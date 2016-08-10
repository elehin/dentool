package com.dentool.model.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class DatosComerciales {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private int clientesTratados;
	private float ingresos;
	private int clientesDiagnosticados;
	private float diagnosticado;
	private int clientesPresupuestados;
	private float presupuestado;
	private int clientesIniciadosSinPagar;
	private float iniciadoSinPagar;
	@Temporal(TemporalType.DATE)
	private Date fecha;

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

	public int getClientesTratados() {
		return clientesTratados;
	}

	public void setClientesTratados(int clientesTratados) {
		this.clientesTratados = clientesTratados;
	}

	public float getIngresos() {
		return ingresos;
	}

	public void setIngresos(float ingresos) {
		this.ingresos = ingresos;
	}

	public int getClientesDiagnosticados() {
		return clientesDiagnosticados;
	}

	public void setClientesDiagnosticados(int clientesDiagnosticados) {
		this.clientesDiagnosticados = clientesDiagnosticados;
	}

	public float getDiagnosticado() {
		return diagnosticado;
	}

	public void setDiagnosticado(float diagnosticado) {
		this.diagnosticado = diagnosticado;
	}

	public int getClientesPresupuestados() {
		return clientesPresupuestados;
	}

	public void setClientesPresupuestados(int clientesPresupuestados) {
		this.clientesPresupuestados = clientesPresupuestados;
	}

	public float getPresupuestado() {
		return presupuestado;
	}

	public void setPresupuestado(float presupuestado) {
		this.presupuestado = presupuestado;
	}

	public int getClientesIniciadosSinPagar() {
		return clientesIniciadosSinPagar;
	}

	public void setClientesIniciadosSinPagar(int clientesIniciadosSinPagar) {
		this.clientesIniciadosSinPagar = clientesIniciadosSinPagar;
	}

	public float getIniciadoSinPagar() {
		return iniciadoSinPagar;
	}

	public void setIniciadoSinPagar(float iniciadoSinPagar) {
		this.iniciadoSinPagar = iniciadoSinPagar;
	}

}
