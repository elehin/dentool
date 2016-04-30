package com.dentool.rest.service;

import java.util.List;

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

	public List<Paciente> findByApellido(String apellido) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.apellido LIKE :apellido")
				.setParameter("apellido", "%" + apellido + "%").getResultList();
		return lista;

	}

	public List<Paciente> findByTelefono(String telefono) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.telefono LIKE :telefono")
				.setParameter("telefono", telefono).getResultList();
		return lista;
	}
}
