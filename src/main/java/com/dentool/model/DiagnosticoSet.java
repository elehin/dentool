package com.dentool.model;

import java.util.List;

import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Tratamiento;

public class DiagnosticoSet {

	private Paciente paciente;
	private Tratamiento tratamiento;
	private boolean iniciado;
	private boolean finalizado;
	private List<Short> pieza;

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Tratamiento getTratamiento() {
		return tratamiento;
	}

	public void setTratamiento(Tratamiento tratamiento) {
		this.tratamiento = tratamiento;
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

	public List<Short> getPieza() {
		return pieza;
	}

	public void setPieza(List<Short> pieza) {
		this.pieza = pieza;
	}

}
