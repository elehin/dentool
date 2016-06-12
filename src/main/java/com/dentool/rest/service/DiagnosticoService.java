package com.dentool.rest.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.Diagnostico;
import com.dentool.model.Paciente;
import com.dentool.model.Pago;
import com.dentool.model.Tratamiento;

@Stateless
public class DiagnosticoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	public Diagnostico addDiagnostico(Diagnostico diagnostico) {
		Paciente p = entityManager.find(Paciente.class, diagnostico.getPaciente().getId());
		Tratamiento t = entityManager.find(Tratamiento.class, diagnostico.getTratamiento().getId());

		diagnostico.setTratamiento(t);
		diagnostico.setPrecio(t.getPrecio());
		diagnostico.setDiagnosticado(new Date(Calendar.getInstance().getTimeInMillis()));
		p.getDiagnosticos().add(diagnostico);

		return diagnostico;
	}

	public Diagnostico find(Long id) {
		Diagnostico d = entityManager.find(Diagnostico.class, id);
		return d;
	}

	public List<Diagnostico> getDiagnosticosByPaciente(Long pacienteId) {
		Paciente p = entityManager.find(Paciente.class, pacienteId);

		String query = "SELECT d FROM Diagnostico d WHERE d.paciente = :paciente "
				+ "ORDER BY d.finalizado, d.iniciado DESC, d.diagnosticado DESC, d.fechaInicio, d.fechaFin DESC";
		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = entityManager.createQuery(query).setParameter("paciente", p).getResultList();
		return lista;
	}

	public Diagnostico updateDiagnostico(Diagnostico d) {
		Diagnostico ld = find(d.getId());

		if (d.getPagado() > 0 && ld.getPagado() != d.getPagado()) {
			this.createPago(ld, d.getPagado() - ld.getPagado());
		}

		ld.update(d);

		if (ld.getPagado() == ld.getPrecio() && ld.getFechaInicio() == null) {
			ld.setFechaInicio(new Date(Calendar.getInstance().getTimeInMillis()));
		}

		if (ld.getPagado() == ld.getPrecio() && ld.getFechaFin() == null) {
			ld.setFechaFin(new Date(Calendar.getInstance().getTimeInMillis()));
		}

		if (ld.getFechaFin() != null && ld.getFechaInicio() == null) {
			ld.setFechaInicio(ld.getFechaFin());
		}

		return d;
	}

	public void delete(long id) {
		Diagnostico ld = entityManager.find(Diagnostico.class, id);
		ld.getPaciente().getDiagnosticos().remove(ld);
		logger.debug("######################################### Borrando diagnóstico " + ld.getId());
		entityManager.remove(ld);
	}

	private void createPago(Diagnostico d, float cantidad) {
		Pago p = new Pago();

		p.setDiagnosticoId(d.getId());
		p.setCantidad(cantidad);
		p.setFecha(new Date(Calendar.getInstance().getTimeInMillis()));

		this.entityManager.persist(p);
	}
}
