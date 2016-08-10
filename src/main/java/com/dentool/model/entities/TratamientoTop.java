package com.dentool.model.entities;

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
	private int countLastMonth;
	private float facturadoLastYear;
	private float facturadoLastMonth;
	private float porcentajeLastYear;
	private int totalLastYear;
	private float totalFacturadoLastYear;
	private float porcentajeFacturacionLastYear;

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

	public float getFacturadoLastMonth() {
		return facturadoLastMonth;
	}

	public void setFacturadoLastMonth(float facturadoLastMonth) {
		this.facturadoLastMonth = facturadoLastMonth;
	}

	public int getCountLastMonth() {
		return countLastMonth;
	}

	public void setCountLastMonth(int countLastMonth) {
		this.countLastMonth = countLastMonth;
	}

	public float getFacturadoLastYear() {
		return facturadoLastYear;
	}

	public void setFacturadoLastYear(float facturadoLastYear) {
		this.facturadoLastYear = facturadoLastYear;
	}

	public float getPorcentajeLastYear() {
		return porcentajeLastYear;
	}

	public void setPorcentajeLastYear(float porcentajeLastYear) {
		this.porcentajeLastYear = porcentajeLastYear;
	}

	public int getTotalLastYear() {
		return totalLastYear;
	}

	public void setTotalLastYear(int totalLastYear) {
		this.totalLastYear = totalLastYear;
	}

	public float getTotalFacturadoLastYear() {
		return totalFacturadoLastYear;
	}

	public void setTotalFacturadoLastYear(float totalFacturadoLastYear) {
		this.totalFacturadoLastYear = totalFacturadoLastYear;
	}

	public float getPorcentajeFacturacionLastYear() {
		return porcentajeFacturacionLastYear;
	}

	public void setPorcentajeFacturacionLastYear(float porcentajeFacturacionLastYear) {
		this.porcentajeFacturacionLastYear = porcentajeFacturacionLastYear;
	}

}
