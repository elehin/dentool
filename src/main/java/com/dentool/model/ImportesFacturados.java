package com.dentool.model;

import com.dentool.utils.Utils;

public class ImportesFacturados {
	private float mes;
	private float trimestre;
	private float year;
	private float mesAnterior;
	private String stringMesAnterior;
	private String stringMesCurso;

	public float getMes() {
		return mes;
	}

	public void setMes(float mes) {
		this.mes = mes;
	}

	public float getTrimestre() {
		return trimestre;
	}

	public void setTrimestre(float trimestre) {
		this.trimestre = trimestre;
	}

	public float getYear() {
		return year;
	}

	public void setYear(float year) {
		this.year = year;
	}

	public float getMesAnterior() {
		return mesAnterior;
	}

	public void setMesAnterior(float mesAnterior) {
		this.mesAnterior = mesAnterior;
	}

	public String getStringMesAnterior() {
		return Utils.capitalize(this.stringMesAnterior);
	}

	public void setStringMesAnterior(String stringMesAnterior) {
		this.stringMesAnterior = stringMesAnterior;
	}

	public String getStringMesCurso() {
		return Utils.capitalize(this.stringMesCurso);
	}

	public void setStringMesCurso(String stringMesCurso) {
		this.stringMesCurso = stringMesCurso;
	}

}
