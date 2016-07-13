package com.dentool.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.Tratamiento;
import com.dentool.model.TratamientoTop;

@Stateless
public class MostUsedTratamientosService {

	@PersistenceContext
	private EntityManager entityManager;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void executeReport() {
		List<TratamientoTop> backup = clearResults();

		try {
			String query = "SELECT d.tratamiento.id, count(d) AS total FROM Diagnostico d GROUP BY d.tratamiento.id ORDER BY total DESC";

			@SuppressWarnings("unchecked")
			List<Object[]> lista = entityManager.createQuery(query).getResultList();

			int i = 1;
			for (Object[] o : lista) {
				if (i > 5) {
					break;
				}
				TratamientoTop topt = new TratamientoTop();
				Tratamiento t = entityManager.find(Tratamiento.class, Long.valueOf(o[0].toString()));

				topt.copy(t);
				if (o[1] != null) {
					topt.setCount(Integer.valueOf(o[1].toString()));
				} else {
					topt.setCount(0);
				}

				entityManager.persist(topt);
				i++;

			}
		} catch (Exception e) {
			logger.error(
					"#####################################################################################################");
			logger.error(
					"Error al ejecutar la actualización de MostUsedTratamientosService.executeReport(). Se ejecuta restore.");
			for (TratamientoTop t : backup) {
				entityManager.merge(t);
			}

		}
	}

	public List<TratamientoTop> clearResults() {
		@SuppressWarnings("unchecked")
		List<TratamientoTop> antiguos = entityManager.createQuery("SELECT t FROM TratamientoTop t").getResultList();
		for (TratamientoTop t : antiguos) {
			entityManager.detach(t);
		}
		entityManager.createQuery("DELETE FROM TratamientoTop t").executeUpdate();
		return antiguos;
	}

	public List<TratamientoTop> getTratamientosTop() {
		@SuppressWarnings("unchecked")
		List<TratamientoTop> lista = entityManager.createQuery("SELECT t FROM TratamientoTop t").getResultList();
		return lista;
	}
}
