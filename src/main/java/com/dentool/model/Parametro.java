package com.dentool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Parametro {
	@Id
	private long id;
	@Column(unique = true)
	private String key;
	private String value;

	public final static String PACIENTES_ANTERIORES_DENTOOL = "pacientesAnterioresADentool";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
