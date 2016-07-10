package com.dentool.model;

import java.util.Date;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dentool.utils.Utils;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Paciente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "paciente", fetch = FetchType.EAGER)
	@JsonManagedReference
	private Collection<Diagnostico> diagnosticos;

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
	private boolean enfermoGrave = false;
	private Date alta;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastChangeTs;
	private Date lastChange;

	private float saldo = 0;

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
		this.setName(origen.getName());
		this.setApellidos(origen.getApellidos());
		this.setTelefono(origen.getTelefono());
		this.setDireccion(origen.getDireccion());
		this.setNotas(origen.getNotas());
		this.setFechaNacimiento(origen.getFechaNacimiento());
		this.setAlergico(origen.isAlergico());
		this.setDni(origen.getDni());
		this.setAlta(origen.getAlta());
		this.setLastChangeTs(new Date(Calendar.getInstance().getTimeInMillis()));
		this.setDiagnosticos(origen.getDiagnosticos());
		this.setEnfermoGrave(origen.isEnfermoGrave());

		if (origen.getSaldo() > this.getSaldo()) {
			String n = "";
			String fecha = Calendar.getInstance().get(Calendar.DATE) + "-" + Calendar.getInstance().get(Calendar.MONTH)
					+ "-" + Calendar.getInstance().get(Calendar.YEAR);
			if (!"".equals(this.getNotas())) {
				n = this.getNotas() + "\n";
				n += fecha + ": Depositado saldo por " + (origen.getSaldo() - this.getSaldo()) + " €";
			}
			this.setNotas(n);
		}
		this.setSaldo(origen.getSaldo());
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

	public Collection<Diagnostico> getDiagnosticos() {
		return diagnosticos;
	}

	public void setDiagnosticos(Collection<Diagnostico> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}

	public boolean isEnfermoGrave() {
		return enfermoGrave;
	}

	public void setEnfermoGrave(boolean enfermoGrave) {
		this.enfermoGrave = enfermoGrave;
	}

	public float getSaldo() {
		return saldo;
	}

	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}

	public Date getLastChangeTs() {
		return lastChangeTs;
	}

	public void setLastChangeTs(Date lastChangeTs) {
		this.lastChangeTs = lastChangeTs;
		this.setLastChange(lastChangeTs);
	}
}
