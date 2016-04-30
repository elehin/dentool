package com.dentool.rest.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.Paciente;

@Stateless
public class PacienteService {

	@PersistenceContext
	private EntityManager entityManager;

	public Paciente create(Paciente paciente) {
		entityManager.persist(paciente);
		return paciente;
	}

	public Paciente find(long id) {
		Paciente p = entityManager.find(Paciente.class, id);
		return p;
	}
}
