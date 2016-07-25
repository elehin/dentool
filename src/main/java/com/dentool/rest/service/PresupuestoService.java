package com.dentool.rest.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Presupuesto;
import com.dentool.rest.service.itext.PresupuestoPdfCreator;

@Stateless
public class PresupuestoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private DiagnosticoService diagnosticoService;

	// @Inject
	// private PacienteService pacienteService;

	public Presupuesto create(Presupuesto presupuesto) {

		logger.debug("PresupuestoService.create()");

		presupuesto.setFecha(new Date(Calendar.getInstance().getTimeInMillis()));
		presupuesto.setPrecio(this.diagnosticoService.getPrecioDiagnosticos(presupuesto.getDiagnosticos()));

		this.entityManager.persist(presupuesto);

		PresupuestoPdfCreator pdfCreator = new PresupuestoPdfCreator();
		String fileName = pdfCreator.createPresupuestoPdf(presupuesto);

		presupuesto.setFileName(fileName);

		// ------ Añade una nota en el paciente diciendo que se da presupuesto
		// -------
		// Paciente p = this.pacienteService.find(presupuesto.getPacienteId());
		//
		// p.setNotas(p.getNotas() + "\n" + Utils.getCurrentFormattedDate() + ":
		// Creado presupuesto por "
		// + presupuesto.getPrecio() + " €");
		// this.pacienteService.updatePaciente(p);

		return presupuesto;
	}

	public List<Presupuesto> getPresupuestosByPaciente(long pacienteId) {
		String query = "SELECT p FROM Presupuesto p WHERE p.pacienteId = :pacienteId ORDER BY p.fecha DESC";
		@SuppressWarnings("unchecked")
		List<Presupuesto> lista = this.entityManager.createQuery(query).setParameter("pacienteId", pacienteId)
				.getResultList();
		return lista;
	}

	public Presupuesto getPresupuesto(long id) {
		return this.entityManager.find(Presupuesto.class, id);
	}

}
