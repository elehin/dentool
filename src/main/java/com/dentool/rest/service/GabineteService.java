package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Gabinete;
import com.dentool.model.entities.Personal;

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
		if (lg.getLunesMorning() != null) {
			lg.setLunesMorning(this.entityManager.find(Personal.class, lg.getLunesMorning().getId()));
		} else {
			lg.setLunesMorning(null);
		}
		if (lg.getMartesMorning() != null) {
			lg.setMartesMorning(this.entityManager.find(Personal.class, lg.getMartesMorning().getId()));
		} else {
			lg.setMartesMorning(null);
		}
		if (lg.getMiercolesMorning() != null) {
			lg.setMiercolesMorning(this.entityManager.find(Personal.class, lg.getMiercolesMorning().getId()));
		} else {
			lg.setMiercolesMorning(null);
		}
		if (lg.getJuevesMorning() != null) {
			lg.setJuevesMorning(this.entityManager.find(Personal.class, lg.getJuevesMorning().getId()));
		} else {
			lg.setJuevesMorning(null);
		}
		if (lg.getViernesMorning() != null) {
			lg.setViernesMorning(this.entityManager.find(Personal.class, lg.getViernesMorning().getId()));
		} else {
			lg.setViernesMorning(null);
		}
		if (lg.getSabadoMorning() != null) {
			lg.setSabadoMorning(this.entityManager.find(Personal.class, lg.getSabadoMorning().getId()));
		} else {
			lg.setSabadoMorning(null);
		}
		if (lg.getDomingoMorning() != null) {
			lg.setDomingoMorning(this.entityManager.find(Personal.class, lg.getDomingoMorning().getId()));
		} else {
			lg.setDomingoMorning(null);
		}

		if (lg.getLunesTarde() != null) {
			lg.setLunesTarde(this.entityManager.find(Personal.class, lg.getLunesTarde().getId()));
		} else {
			lg.setLunesTarde(null);
		}
		if (lg.getMartesTarde() != null) {
			lg.setMartesTarde(this.entityManager.find(Personal.class, lg.getMartesTarde().getId()));
		} else {
			lg.setMartesTarde(null);
		}
		if (lg.getMiercolesTarde() != null) {
			lg.setMiercolesTarde(this.entityManager.find(Personal.class, lg.getMiercolesTarde().getId()));
		} else {
			lg.setMiercolesTarde(null);
		}
		if (lg.getJuevesTarde() != null) {
			lg.setJuevesTarde(this.entityManager.find(Personal.class, lg.getJuevesTarde().getId()));
		} else {
			lg.setJuevesTarde(null);
		}
		if (lg.getViernesTarde() != null) {
			lg.setViernesTarde(this.entityManager.find(Personal.class, lg.getViernesTarde().getId()));
		} else {
			lg.setViernesTarde(null);
		}
		if (lg.getSabadoTarde() != null) {
			lg.setSabadoTarde(this.entityManager.find(Personal.class, lg.getSabadoTarde().getId()));
		} else {
			lg.setSabadoTarde(null);
		}
		if (lg.getDomingoTarde() != null) {
			lg.setDomingoTarde(this.entityManager.find(Personal.class, lg.getDomingoTarde().getId()));
		} else {
			lg.setDomingoTarde(null);
		}

		// this.entityManager.merge(lg);

		return lg;
	}

	public void delete(long id) {
		Gabinete g = this.find(id);
		this.entityManager.remove(g);
	}

}
