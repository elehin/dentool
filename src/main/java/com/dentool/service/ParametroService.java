package com.dentool.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Parametro;

@Stateless
public class ParametroService {

	@PersistenceContext
	private EntityManager entityManager;

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Parametro getParametro(String key) {
		String query = "SELECT p FROM Parametro p where p.key = :key";
		@SuppressWarnings("rawtypes")
		List l = this.entityManager.createQuery(query).setParameter("key", key).getResultList();
		Parametro p = null;
		if (!l.isEmpty()) {
			p = (Parametro) l.get(0);
		}
		return p;
	}

}
