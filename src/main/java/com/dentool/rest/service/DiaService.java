package com.dentool.rest.service;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Dia;

@Stateless
public class DiaService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Dia create(Dia dia) {
		this.entityManager.persist(dia);

		return dia;
	}

	public Dia find(long id) {
		return this.entityManager.find(Dia.class, id);
	}

	public Dia update(Dia dia) {
		Dia d = this.entityManager.merge(dia);
		return d;
	}

	public void delete(Long id) {
		Dia d = this.find(id);

		this.entityManager.remove(d);
	}

	public Dia getDia(Calendar fecha) {
		String queryFecha = "SELECT d FROM Dia d WHERE d.fecha = :fecha";
		try {
			Dia d = (Dia) this.entityManager.createQuery(queryFecha).setParameter("fecha", fecha.getTime())
					.setMaxResults(1).getSingleResult();
			return d;
		} catch (NoResultException e) {
			return null;
		}

	}

}
