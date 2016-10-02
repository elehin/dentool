package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Gabinete;

@Stateless
public class GabineteService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Gabinete create(Gabinete gabinete) {
		entityManager.persist(gabinete);
		return gabinete;
	}

	public Gabinete find(long id) {
		return this.entityManager.find(Gabinete.class, id);
	}

	public List<Gabinete> getGabinetes() {
		String query = "SELECT g FROM Gabinete g";
		@SuppressWarnings("unchecked")
		List<Gabinete> lista = entityManager.createQuery(query).getResultList();

		return lista;
	}

	public Gabinete updateGabinete(Gabinete g) {
		Gabinete lg = entityManager.find(Gabinete.class, g.getId());

		lg.update(g);

		return lg;
	}

	public void delete(long id) {
		Gabinete g = this.find(id);
		this.entityManager.remove(g);
	}

}
