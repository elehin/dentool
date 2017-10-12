package com.dentool.model.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Dia {

	@Id
	@SequenceGenerator(name = "dia_id_seq", sequenceName = "dia_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dia_id_seq")
	private long id;

	private boolean agendaDetalle;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isAgendaDetalle() {
		return agendaDetalle;
	}

	public void setAgendaDetalle(boolean agendaDetalle) {
		this.agendaDetalle = agendaDetalle;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Dia.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Dia other = (Dia) obj;
	
		return other.getFecha().equals(this.fecha);
	}
}
