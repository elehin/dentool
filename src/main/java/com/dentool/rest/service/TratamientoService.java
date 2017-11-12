package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Tratamiento;
import com.dentool.utils.Utils;

@Stateless
public class TratamientoService {

	@PersistenceContext
	private EntityManager entityManager;

	public Tratamiento create(Tratamiento tratamiento) {
		entityManager.persist(tratamiento);
		return tratamiento;
	}

	public Tratamiento find(long id) {
		Tratamiento t = entityManager.find(Tratamiento.class, id);
		return t;
	}

	public List<Tratamiento> findAll() {
		@SuppressWarnings("unchecked")
		List<Tratamiento> lista = entityManager.createQuery("SELECT t FROM Tratamiento t").getResultList();
		return lista;

	}

	public List<Tratamiento> findByNombre(String nombre) {
		nombre = Utils.removeTildes(nombre.toLowerCase());
		@SuppressWarnings("unchecked")
		List<Tratamiento> lista = entityManager
				.createQuery("SELECT t FROM Tratamiento t WHERE t.nombreNormalized LIKE :nombre")
				.setParameter("nombre", "%" + nombre + "%").getResultList();
		return lista;

	}

	public Tratamiento updateTratamiento(Tratamiento t) {
		Tratamiento lt = find(t.getId());

		lt.update(t);

		return t;
	}

	public void deleteTratamiento(long id) {
		Tratamiento t = find(id);
		if (t != null) {
			entityManager.remove(t);
		}
	}

	public List<Tratamiento> getTratamientos(List<Long> ids) {
		@SuppressWarnings("unchecked")
		List<Tratamiento> lista = entityManager.createQuery("SELECT t FROM Tratamiento t WHERE t.id IN :ids")
				.setParameter("ids", ids).getResultList();
		return lista;
	}
}
