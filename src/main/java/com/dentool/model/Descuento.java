package com.dentool.model;

import java.util.List;

import com.dentool.model.entities.Diagnostico;

public class Descuento {

	private List<Diagnostico> diagnosticos;
	private float descuento;

	public List<Diagnostico> getDiagnosticos() {
		return diagnosticos;
	}

	public void setDiagnosticos(List<Diagnostico> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}

	public float getDescuento() {
		return descuento;
	}

	public void setDescuento(float descuento) {
		this.descuento = descuento;
	}

}
