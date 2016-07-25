package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Diagnostico;
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
}
