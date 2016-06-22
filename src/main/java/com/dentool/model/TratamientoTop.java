package com.dentool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.dentool.utils.Utils;

@Entity
public class TratamientoTop {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String nombre;
	private float precio;
	private String nombreNormalized;
	private int count;
	private long tratamiento;

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

	public void copy(Tratamiento origen) {
		setNombre(origen.getNombre());
		setPrecio(origen.getPrecio());
		setTratamiento(origen.getId());
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getTratamiento() {
		return tratamiento;
	}

	public void setTratamiento(long tratamiento) {
		this.tratamiento = tratamiento;
	}

}