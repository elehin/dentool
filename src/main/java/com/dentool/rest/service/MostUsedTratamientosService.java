package com.dentool.rest.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Tratamiento;
import com.dentool.model.entities.TratamientoTop;

@Stateless
public class MostUsedTratamientosService {

	@PersistenceContext
	private EntityManager entityManager;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void executeReport() {
		List<TratamientoTop> backup = clearResults();

		try {
			String query = "SELECT d.tratamiento.id, count(d), sum(d.precio) AS total FROM Diagnostico d "
					+ "WHERE d.fechaInicio > :fecha " + " GROUP BY d.tratamiento.id ORDER BY total DESC";
			String queryMes = "SELECT d.tratamiento.id, count(d), sum(d.precio) AS total FROM Diagnostico d "
					+ " WHERE d.tratamiento.id = :idTratamiento AND d.fechaInicio > :fechaInicio "
					+ "GROUP BY d.tratamiento.id ORDER BY total DESC";

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);

			String queryTotal = "SELECT d FROM Diagnostico d WHERE d.fechaInicio > :fechaInicio";
			int diagsLastYear = this.entityManager.createQuery(queryTotal).setParameter("fechaInicio", cal.getTime())
					.getResultList().size();

			String queryFacturacion = "SELECT sum(d.precio) FROM Diagnostico d WHERE d.fechaInicio > :fechaInicio";
			double doubleFacturacionLastYear = (double) this.entityManager.createQuery(queryFacturacion)
					.setParameter("fechaInicio", cal.getTime()).getSingleResult();
			float facturacionLastYear = Float.valueOf(Double.toString(doubleFacturacionLastYear));

			@SuppressWarnings("unchecked")
			List<Object[]> lista = entityManager.createQuery(query).setParameter("fecha", cal.getTime())
					.getResultList();

			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.MONTH, -1);

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
				if (o[2] != null) {
					topt.setFacturadoLastYear(Float.parseFloat(o[2].toString()));
				} else {
					topt.setFacturadoLastYear(0f);
				}

				Object[] oMes = (Object[]) this.entityManager.createQuery(queryMes)
						.setParameter("idTratamiento", t.getId()).setParameter("fechaInicio", cal.getTime())
						.getSingleResult();
				topt.setCountLastMonth(Integer.valueOf(oMes[1].toString()));
				topt.setFacturadoLastMonth(Float.valueOf(oMes[2].toString()));

				topt.setTotalLastYear(diagsLastYear);
				float porcentaje = (float) topt.getCount() / diagsLastYear * 100;
				topt.setPorcentajeLastYear(porcentaje);

				topt.setTotalFacturadoLastYear(facturacionLastYear);
				float porcentajeFacturacion = (float) topt.getFacturadoLastYear() / topt.getTotalFacturadoLastYear()
						* 100;
				topt.setPorcentajeFacturacionLastYear(porcentajeFacturacion);

				entityManager.persist(topt);
				i++;

			}
		} catch (Exception e) {
			logger.error(
					"#####################################################################################################");
			logger.error(
					"Error al ejecutar la actualización de MostUsedTratamientosService.executeReport(). Se ejecuta restore.");
			logger.error(e.getMessage());
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
		List<TratamientoTop> lista = entityManager.createQuery("SELECT t FROM TratamientoTop t ORDER BY t.count DESC")
				.getResultList();
		return lista;
	}
}
