package com.dentool.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Parametro {
	@Id
	private long id;
	@Column(unique = true)
	private String clave;
	private String valor;

	public final static String PACIENTES_ANTERIORES_DENTOOL = "pacientesAnterioresADentool";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

}
