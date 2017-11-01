package com.dentool.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.dentool.utils.Utils;

@Entity
public class Tratamiento {

	@Id
	@SequenceGenerator(name = "tratamiento_id_seq", sequenceName = "tratamiento_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tratamiento_id_seq")

	private long id;

	private String nombre;
	private float precio;
	private String nombreNormalized;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
		setNombreNormalized();
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public String getNombreNormalized() {
		return nombreNormalized;
	}

	private void setNombreNormalized() {
		this.nombreNormalized = Utils.removeTildes(this.nombre.toLowerCase());
	}

	public void update(Tratamiento origen) {
		setNombre(origen.getNombre());
		setPrecio(origen.getPrecio());
	}

}
