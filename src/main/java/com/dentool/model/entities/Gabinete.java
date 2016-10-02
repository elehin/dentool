package com.dentool.model.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Gabinete {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String nombre;
	private String especialidad;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "lunesMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal lunesMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "lunesTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal lunesTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "martesMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal martesMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "martesTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal martesTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "miercolesMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal miercolesMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "miercolesTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal miercolesTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "juevesMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal juevesMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "juevesTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal juevesTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "viernesMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal viernesMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "viernesTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal viernesTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "sabadoMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal sabadoMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "sabadoTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal sabadoTarde;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "domingoMorning", referencedColumnName = "id")
	// @JsonIgnore
	private Personal domingoMorning;

	@ManyToOne(optional = true, cascade = CascadeType.MERGE)
	@JoinColumn(name = "domingoTarde", referencedColumnName = "id")
	// @JsonIgnore
	private Personal domingoTarde;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public Personal getLunesMorning() {
		return lunesMorning;
	}

	public void setLunesMorning(Personal lunesMorning) {
		this.lunesMorning = lunesMorning;
	}

	public Personal getLunesTarde() {
		return lunesTarde;
	}

	public void setLunesTarde(Personal lunesTarde) {
		this.lunesTarde = lunesTarde;
	}

	public Personal getMartesMorning() {
		return martesMorning;
	}

	public void setMartesMorning(Personal martesMorning) {
		this.martesMorning = martesMorning;
	}

	public Personal getMartesTarde() {
		return martesTarde;
	}

	public void setMartesTarde(Personal martesTarde) {
		this.martesTarde = martesTarde;
	}

	public Personal getMiercolesMorning() {
		return miercolesMorning;
	}

	public void setMiercolesMorning(Personal miercolesMorning) {
		this.miercolesMorning = miercolesMorning;
	}

	public Personal getMiercolesTarde() {
		return miercolesTarde;
	}

	public void setMiercolesTarde(Personal miercolesTarde) {
		this.miercolesTarde = miercolesTarde;
	}

	public Personal getJuevesMorning() {
		return juevesMorning;
	}

	public void setJuevesMorning(Personal juevesMorning) {
		this.juevesMorning = juevesMorning;
	}

	public Personal getJuevesTarde() {
		return juevesTarde;
	}

	public void setJuevesTarde(Personal juevesTarde) {
		this.juevesTarde = juevesTarde;
	}

	public Personal getViernesMorning() {
		return viernesMorning;
	}

	public void setViernesMorning(Personal viernesMorning) {
		this.viernesMorning = viernesMorning;
	}

	public Personal getViernesTarde() {
		return viernesTarde;
	}

	public void setViernesTarde(Personal viernesTarde) {
		this.viernesTarde = viernesTarde;
	}

	public Personal getSabadoMorning() {
		return sabadoMorning;
	}

	public void setSabadoMorning(Personal sabadoMorning) {
		this.sabadoMorning = sabadoMorning;
	}

	public Personal getSabadoTarde() {
		return sabadoTarde;
	}

	public void setSabadoTarde(Personal sabadoTarde) {
		this.sabadoTarde = sabadoTarde;
	}

	public void update(Gabinete origen) {
		this.setNombre(origen.getNombre());
		this.setEspecialidad(origen.getEspecialidad());
		this.setLunesMorning(origen.getLunesMorning());
		this.setLunesTarde(origen.getLunesTarde());
		this.setMartesMorning(origen.getMartesMorning());
		this.setMiercolesMorning(origen.getMiercolesMorning());
		this.setMiercolesTarde(origen.getMiercolesTarde());
		this.setJuevesMorning(origen.getJuevesMorning());
		this.setJuevesTarde(origen.getJuevesTarde());
		this.setViernesMorning(origen.getViernesMorning());
		this.setViernesTarde(origen.getViernesTarde());
		this.setSabadoMorning(origen.getSabadoMorning());
		this.setSabadoTarde(origen.getSabadoTarde());
		this.setDomingoMorning(origen.getDomingoMorning());
		this.setDomingoTarde(origen.getDomingoTarde());
	}

	public Personal getDomingoMorning() {
		return domingoMorning;
	}

	public void setDomingoMorning(Personal domingoMorning) {
		this.domingoMorning = domingoMorning;
	}

	public Personal getDomingoTarde() {
		return domingoTarde;
	}

	public void setDomingoTarde(Personal domingoTarde) {
		this.domingoTarde = domingoTarde;
	}
}
