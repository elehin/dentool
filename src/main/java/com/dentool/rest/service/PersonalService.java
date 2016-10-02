package com.dentool.rest.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.Personal;

@Stateless
public class PersonalService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Personal create(Personal personal) {
		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		personal.setFechaAlta(now);
		personal.setActivo(true);

		entityManager.persist(personal);
		return personal;
	}

	public Personal find(long id) {
		return this.entityManager.find(Personal.class, id);
	}

	public List<Personal> getPersonal() {
		String query = "SELECT p FROM Personal p ORDER BY p.fechaAlta DESC, p.id DESC";
		@SuppressWarnings("unchecked")
		List<Personal> lista = entityManager.createQuery(query).getResultList();

		return lista;
	}

	public Personal updatePersonal(Personal p) {
		Personal lp = entityManager.find(Personal.class, p.getId());

		lp.update(p);

		return lp;
	}

	public Personal setBaja(long id) {
		Personal lp = entityManager.find(Personal.class, id);
		lp.setFechaBaja(Calendar.getInstance().getTime());
		lp.setActivo(false);

		return lp;
	}

	public Personal setActivo(long id) {
		Personal lp = entityManager.find(Personal.class, id);
		lp.setFechaBaja(null);
		lp.setActivo(true);

		return lp;
	}
}
