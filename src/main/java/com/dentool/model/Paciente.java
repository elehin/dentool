package com.dentool.model;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.dentool.utils.Utils;

@Entity
public class Paciente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	private String apellidos;
	private String telefono;
	private String direccion;
	private String notas;
	private Date fechaNacimiento;
	private String dni;
	private String nameNormalized;
	private String apellidosNormalized;
	private boolean alergico = false;
	private Date alta;
	private Date lastChange;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
		this.setNameNormalized();
	}

	@Override
	public String toString() {
		return "Paciente: id " + this.id + " Nombre: " + this.name;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos.trim();
		this.setApellidosNormalized();
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono.replace(" ", "");
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getNameNormalized() {
		return nameNormalized;
	}

	private void setNameNormalized() {
		this.nameNormalized = Utils.removeTildes(this.name.toLowerCase());
	}

	public String getApellidosNormalized() {
		return apellidosNormalized;
	}

	private void setApellidosNormalized() {
		this.apellidosNormalized = Utils.removeTildes(this.apellidos.toLowerCase());
	}

	public void update(Paciente origen) {
		setName(origen.getName());
		setApellidos(origen.getApellidos());
		setTelefono(origen.getTelefono());
		setDireccion(origen.getDireccion());
		setNotas(origen.getNotas());
		setFechaNacimiento(origen.getFechaNacimiento());
		setAlergico(origen.isAlergico());
		setDni(origen.getDni());
		setAlta(origen.getAlta());
		setLastChange(new Date(Calendar.getInstance().getTimeInMillis()));
	}

	public boolean isAlergico() {
		return alergico;
	}

	public void setAlergico(boolean alergico) {
		this.alergico = alergico;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public Date getAlta() {
		return alta;
	}

	public void setAlta(Date alta) {
		this.alta = alta;
	}

	public Date getLastChange() {
		return lastChange;
	}

	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}
}
