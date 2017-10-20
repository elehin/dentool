package com.dentool.rest.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.PagoEager;
import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.IngresosMes;
import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Pago;

@Stateless
public class PagoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Pago create(Pago pago) {
		this.entityManager.persist(pago);

		this.actualizarPagos(pago.getDiagnosticoId());

		this.actualizaSaldo(pago.getDiagnosticoId(), pago);

		return pago;
	}

	private void actualizarPagos(long diagnosticoId) {
		String query = "SELECT sum(p.cantidad) FROM Pago p WHERE p.diagnosticoId = :diagnosticoId";
		Object o = this.entityManager.createQuery(query).setParameter("diagnosticoId", diagnosticoId).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));

			this.entityManager.find(Diagnostico.class, diagnosticoId).setPagado(resultFloat);
		} else {
			this.entityManager.find(Diagnostico.class, diagnosticoId).setPagado(0);
		}
	}

	private void actualizaSaldo(long diagnosticoId, Pago pago) {
		Diagnostico d = entityManager.find(Diagnostico.class, diagnosticoId);
		Paciente paciente = this.entityManager.find(Paciente.class, d.getPaciente().getId());

		if (paciente.getSaldo() > 0) {
			if (paciente.getSaldo() - pago.getCantidad() < 0) {
				paciente.setSaldo(0);
			} else {
				paciente.setSaldo(paciente.getSaldo() - pago.getCantidad());
			}
		}
	}

	public Pago find(Long id) {
		Pago p = entityManager.find(Pago.class, id);
		return p;
	}

	public List<Pago> getPagosByDiagnostico(Long diagnosticoId) {
		String query = "SELECT p FROM Pago p WHERE p.diagnosticoId = :diagnosticoId " + "ORDER BY p.fecha";
		@SuppressWarnings("unchecked")
		List<Pago> lista = entityManager.createQuery(query).setParameter("diagnosticoId", diagnosticoId)
				.getResultList();
		return lista;
	}

	public Pago update(Pago nuevo) {
		Pago persistido = this.entityManager.find(Pago.class, nuevo.getId());

		if (nuevo.getCantidad() != 0) {
			persistido.setCantidad(nuevo.getCantidad());
		}
		if (nuevo.getFecha() != null) {
			persistido.setFecha(nuevo.getFecha());
		}

		return persistido;
	}

	public void delete(long id) {
		Pago p = entityManager.find(Pago.class, id);
		long diagnosticoId = p.getDiagnosticoId();

		logger.debug("######################################### Borrando pago " + p.getId());
		this.entityManager.remove(p);

		this.actualizarPagos(diagnosticoId);
	}

	public void enviaPagoASaldo(long id) {
		Pago p = entityManager.find(Pago.class, id);
		long diagnosticoId = p.getDiagnosticoId();

		Diagnostico d = entityManager.find(Diagnostico.class, p.getDiagnosticoId());
		d.getPaciente().setSaldo(d.getPaciente().getSaldo() + p.getCantidad());

		logger.debug("######################################### Borrando pago " + p.getId());
		this.entityManager.remove(p);

		this.actualizarPagos(diagnosticoId);
		if (d.getPagado() == 0) {
			d.setPrecio(0f);
		}
	}

	public void executeIngresosReport() {

		List<IngresosMes> backup = this.clearDatosIngresos();

		try {

			String query = "SELECT SUM(p.cantidad) FROM Pago p WHERE p.fecha BETWEEN :desde AND :hasta";

			Calendar desde = Calendar.getInstance();
			Calendar hasta = Calendar.getInstance();

			desde.set(Calendar.DATE, desde.getActualMinimum(Calendar.DATE));
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));

			int i = 0;

			do {
				i++;
				Object result = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
						.setParameter("hasta", hasta.getTime()).getSingleResult();

				if (result == null) {
					desde.add(Calendar.MONTH, -1);
					hasta.add(Calendar.MONTH, -1);
					hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));
					continue;
				}
				float ingresos = Float.valueOf(result.toString());
				IngresosMes im = new IngresosMes();
				im.setFecha(hasta.getTime());
				im.setIngresos(ingresos);

				desde.add(Calendar.MONTH, -1);
				hasta.add(Calendar.MONTH, -1);
				hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));

				this.entityManager.persist(im);

			} while (i < 24);
		} catch (Exception e) {
			logger.error(
					"#####################################################################################################");
			logger.error(
					"Error al ejecutar la actualización en PagoService.executeIngresosReport(). Se ejecuta restore.");
			logger.error(e.getMessage());
			for (IngresosMes im : backup) {
				entityManager.merge(im);
			}

		}
	}

	public List<IngresosMes> clearDatosIngresos() {
		@SuppressWarnings("unchecked")
		List<IngresosMes> antiguos = entityManager.createQuery("SELECT im FROM IngresosMes im").getResultList();
		for (IngresosMes im : antiguos) {
			entityManager.detach(im);
		}
		entityManager.createQuery("DELETE FROM IngresosMes im").executeUpdate();
		return antiguos;
	}

	public List<IngresosMes> getReportIngresos() {
		@SuppressWarnings("unchecked")
		List<IngresosMes> lista = entityManager.createQuery("SELECT im FROM IngresosMes im").getResultList();

		return lista;
	}

	public List<PagoEager> getPagosNoFacturadosByPaciente(Long pacienteId) {
		Paciente p = entityManager.find(Paciente.class, pacienteId);

		// String query = "SELECT d FROM Diagnostico d WHERE d.paciente =
		// :paciente AND d.pagado > 0 and d.pagado < d.precio "
		// + "AND d.factura IS EMPTY ORDER BY d.finalizado, d.iniciado DESC,
		// d.diagnosticado DESC, d.fechaInicio, d.fechaFin DESC";
		String query = "SELECT d FROM Diagnostico d WHERE d.paciente = :paciente AND d.pagado > 0 "
				+ "AND d.factura IS EMPTY ORDER BY d.finalizado, d.iniciado DESC, d.diagnosticado DESC, d.fechaInicio, d.fechaFin DESC";
		@SuppressWarnings("unchecked")
		List<Diagnostico> diagnosticos = entityManager.createQuery(query).setParameter("paciente", p).getResultList();

		List<Long> ids = new ArrayList<Long>();
		for (Diagnostico d : diagnosticos) {
			ids.add(d.getId());
		}

		String queryPagos = "SELECT p FROM Pago p WHERE p.diagnosticoId IN :diagnosticos AND p.factura IS EMPTY";
		@SuppressWarnings("unchecked")
		List<Pago> lista = this.entityManager.createQuery(queryPagos).setParameter("diagnosticos", ids).getResultList();

		List<PagoEager> pagosEager = new ArrayList<PagoEager>();
		for (Pago pago : lista) {
			PagoEager pe = new PagoEager();
			pe.setPago(pago);

			pe.setDiagnostico(this.entityManager.find(Diagnostico.class, pago.getDiagnosticoId()));

			pagosEager.add(pe);
		}

		return pagosEager;
	}

	public Set<Pago> getPagos(List<Long> ids) {
		String query = "SELECT p FROM Pago p WHERE p.id IN :ides";

		@SuppressWarnings("unchecked")
		Set<Pago> lista = new HashSet<Pago>(
				this.entityManager.createQuery(query).setParameter("ides", ids).getResultList());

		return lista;
	}

	public float getPrecioPagos(Set<Pago> pagos) {
		String query = "SELECT sum(p.cantidad) FROM Pago p WHERE p IN :pagos";
		Object o = this.entityManager.createQuery(query).setParameter("pagos", pagos).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			return resultFloat;
		}
		return 0f;
	}

	public List<Pago> getPagosByDiagnostico(long diagnosticoId) {
		String query = "SELECT p FROM Pago p WHERE p.diagnosticoId = :diagnostico";
		@SuppressWarnings("unchecked")
		List<Pago> lista = this.entityManager.createQuery(query).setParameter("diagnostico", diagnosticoId)
				.getResultList();

		return lista;
	}
}
