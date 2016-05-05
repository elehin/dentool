package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.Paciente;
import com.dentool.utils.Utils;

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
		apellido = Utils.removeTildes(apellido.toLowerCase());
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager
				.createQuery("SELECT p FROM Paciente p WHERE p.apellidosNormalized LIKE :apellido")
				.setParameter("apellido", "%" + apellido + "%").getResultList();
		return lista;

	}

	public List<Paciente> findByName(String name) {
		name = Utils.removeTildes(name.toLowerCase());
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.nameNormalized LIKE :name")
				.setParameter("name", "%" + name + "%").getResultList();
		return lista;
	}

	public List<Paciente> findByTelefono(String telefono) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.telefono LIKE :telefono")
				.setParameter("telefono", "%" + telefono + "%").getResultList();
		return lista;
	}

	public List<Paciente> findByFullName(String name, String apellido) {
		name = Utils.removeTildes(name.toLowerCase());
		apellido = Utils.removeTildes(apellido.toLowerCase());
		String query = "SELECT p FROM Paciente p WHERE p.nameNormalized LIKE :name AND p.apellidosNormalized LIKE :apellido";
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery(query).setParameter("name", "%" + name + "%")
				.setParameter("apellido", "%" + apellido + "%").getResultList();
		return lista;
	}

	public Paciente updatePaciente(Paciente p) {
		Paciente lp = find(p.getId());

		lp.update(p);

		return p;
	}

	public Paciente deletePaciente(long id) {
		Paciente p = find(id);
		if (p == null) {
			return null;
		}
		entityManager.remove(p);
		return p;
	}
}
