package com.dentool.rest.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Parametro;
import com.dentool.service.ParametroService;
import com.dentool.utils.Utils;

@Stateless
public class PacienteService {

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private ParametroService parametroService;

	public Paciente create(Paciente paciente) {
		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		paciente.setAlta(now);
		paciente.setLastChangeTs(now);
		entityManager.persist(paciente);

		if (paciente.isPacienteAnteriorADentool()) {
			Parametro p = this.parametroService.getParametro(Parametro.PACIENTES_ANTERIORES_DENTOOL);
			if (p != null) {
				int pacientesAntiguos = Integer.parseInt(p.getValue());
				pacientesAntiguos--;
				p.setValue(String.valueOf(pacientesAntiguos));
			}
		}

		return paciente;
	}

	public Paciente find(long id) {
		Paciente p = entityManager.find(Paciente.class, id);
		return p;
	}

	public List<Paciente> findLastModified() {
		String query = "SELECT p FROM Paciente p ORDER BY p.lastChangeTs DESC, p.id DESC";
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery(query).setMaxResults(10).getResultList();

		return lista;
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
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.telefono = :telefono")
				.setParameter("telefono", telefono).getResultList();
		return lista;
	}

	public List<Paciente> findByDni(String dni) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.dni = :dni")
				.setParameter("dni", dni).getResultList();
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

	public void deletePaciente(long id) {
		Paciente p = find(id);
		if (p != null) {
			entityManager.remove(p);
		}
	}
}
