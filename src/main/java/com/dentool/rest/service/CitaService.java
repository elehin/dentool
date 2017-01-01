package com.dentool.rest.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.exception.NoCitaFoundException;
import com.dentool.model.MiniCalendario;
import com.dentool.model.entities.Cita;
import com.dentool.utils.Utils;

@Stateless
public class CitaService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Cita create(Cita cita) {
		this.entityManager.persist(cita);

		return cita;
	}

	public Cita find(long id) {
		return this.entityManager.find(Cita.class, id);
	}

	public List<Cita> getCitas(Calendar date) {
		String query = "SELECT c FROM Cita c WHERE c.inicio BETWEEN :desde AND :hasta ORDER BY c.inicio";

		Calendar desde = Calendar.getInstance();
		desde.setTime(date.getTime());
		desde.set(Calendar.HOUR_OF_DAY, desde.getActualMinimum(Calendar.HOUR_OF_DAY));
		desde.set(Calendar.MINUTE, desde.getActualMinimum(Calendar.MINUTE));

		Calendar hasta = Calendar.getInstance();
		hasta.setTime(date.getTime());
		hasta.set(Calendar.HOUR_OF_DAY, desde.getActualMaximum(Calendar.HOUR_OF_DAY));
		hasta.set(Calendar.MINUTE, desde.getActualMaximum(Calendar.MINUTE));

		@SuppressWarnings("unchecked")
		List<Cita> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();

		return lista;
	}

	public Cita update(Cita cita) {
		Cita c = this.entityManager.merge(cita);
		return c;
	}

	public void delete(Long id) throws NoCitaFoundException {
		Cita c = this.find(id);

		if (c == null) {
			throw new NoCitaFoundException();
		}

		this.entityManager.remove(c);
	}

	// @SuppressWarnings("unchecked")
	// public List<Cita> getSiguientesCitas() {
	// // Crea desde para el inicio día actual y hasta para final del día
	// // actual
	// Calendar desde = Calendar.getInstance();
	// desde.set(Calendar.HOUR_OF_DAY,
	// desde.getActualMinimum(Calendar.HOUR_OF_DAY));
	// desde.set(Calendar.MINUTE, desde.getActualMinimum(Calendar.MINUTE));
	// desde.set(Calendar.SECOND, desde.getActualMinimum(Calendar.SECOND));
	// desde.set(Calendar.MILLISECOND,
	// desde.getActualMinimum(Calendar.MILLISECOND));
	//
	// Calendar hasta = Calendar.getInstance();
	// hasta.set(Calendar.HOUR_OF_DAY,
	// desde.getActualMaximum(Calendar.HOUR_OF_DAY));
	// hasta.set(Calendar.MINUTE, desde.getActualMaximum(Calendar.MINUTE));
	// hasta.set(Calendar.SECOND, desde.getActualMaximum(Calendar.SECOND));
	// hasta.set(Calendar.MILLISECOND,
	// desde.getActualMaximum(Calendar.MILLISECOND));
	//
	// String query = "SELECT c FROM Cita c WHERE c.inicio BETWEEN :desde AND
	// :hasta ORDER BY c.inicio";
	// List<Cita> lista = null;
	// int i = 0;
	//
	// // Mira día a día durante 30 días hasta que encuentra citas
	// // Si no hay citas en los siguientes 30 días no mostrará nada
	// do {
	// lista = this.entityManager.createQuery(query).setParameter("desde",
	// desde.getTime())
	// .setParameter("hasta", hasta.getTime()).getResultList();
	//
	// desde.add(Calendar.DATE, 1);
	// hasta.add(Calendar.DATE, 1);
	// i++;
	// } while ((lista == null || (lista != null && lista.size() == 0)) && i <
	// 30);
	//
	// return lista;
	//
	// }

	@SuppressWarnings("unchecked")
	public List<Cita> getSiguientesCitas() {
		// Crea desde para el inicio día actual y hasta para 30 días después
		Calendar desde = Calendar.getInstance();
		Utils.setInicioDia(desde);

		Calendar hasta = Calendar.getInstance();
		Utils.setFinDia(hasta);
		hasta.add(Calendar.DATE, 30);

		// Busca la primera cita en los próximos 30 días
		String queryFecha = "SELECT c FROM Cita c WHERE c.inicio BETWEEN :desde AND :hasta ORDER BY c.inicio";
		Cita c = (Cita) this.entityManager.createQuery(queryFecha).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).setMaxResults(1).getSingleResult();

		List<Cita> lista = null;

		// Busca todas las citas de la fecha de la primera cita encontrada
		if (c != null) {
			desde.setTime(c.getInicio());
			Utils.setInicioDia(desde);
			hasta.setTime(c.getInicio());
			Utils.setFinDia(hasta);
			lista = this.entityManager.createQuery(queryFecha).setParameter("desde", desde.getTime())
					.setParameter("hasta", hasta.getTime()).getResultList();
		}

		return lista;

	}

	public MiniCalendario getMiniCalendario(Calendar fecha) {
		String query = "SELECT c FROM Cita c WHERE c.inicio BETWEEN :desde AND :hasta ORDER BY c.inicio";

		Calendar desde = Calendar.getInstance();
		desde.setTime(fecha.getTime());
		desde.set(Calendar.DAY_OF_MONTH, desde.getActualMinimum(Calendar.DAY_OF_MONTH));
		desde.set(Calendar.HOUR_OF_DAY, desde.getActualMinimum(Calendar.HOUR_OF_DAY));
		desde.set(Calendar.MINUTE, desde.getActualMinimum(Calendar.MINUTE));

		Calendar hasta = Calendar.getInstance();
		hasta.setTime(fecha.getTime());
		hasta.set(Calendar.DAY_OF_MONTH, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
		hasta.set(Calendar.HOUR_OF_DAY, desde.getActualMaximum(Calendar.HOUR_OF_DAY));
		hasta.set(Calendar.MINUTE, desde.getActualMaximum(Calendar.MINUTE));

		@SuppressWarnings("unchecked")
		List<Cita> citas = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();

		MiniCalendario mc = new MiniCalendario();
		mc.setFecha(fecha);
		mc.setPrimerDiaMes((short) desde.get(Calendar.DAY_OF_WEEK));
		mc.setUltimoDiaMes((short) hasta.get(Calendar.DAY_OF_MONTH));
		mc.setDiasCita(new ArrayList<Short>());

		Calendar aux = Calendar.getInstance();
		for (Cita c : citas) {
			aux.setTime(c.getInicio());
			short dia = (short) aux.get(Calendar.DAY_OF_MONTH);

			if (mc.getDiasCita().indexOf(dia) == -1) {
				mc.getDiasCita().add(dia);
			}
		}

		return mc;
	}
}
