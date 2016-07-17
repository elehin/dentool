package com.dentool.rest.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.Diagnostico;
import com.dentool.model.Factura;
import com.dentool.model.Paciente;
import com.dentool.rest.service.itext.FacturaPdfCreator;
import com.dentool.utils.Utils;

@Stateless
public class FacturaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private DiagnosticoService diagnosticoService;

	@Inject
	private PacienteService pacienteService;

	public Factura create(Factura factura) {

		logger.debug("FacturaService.create()");

		// ------ Si viene nombre en la petición se emite la factura a ese
		// nombre.
		// ------ En caso contrario se usan los datos del paciente.
		Paciente p = this.pacienteService.find(factura.getPacienteId());

		if (factura.getNombreFactura() == null) {
			factura.setNombreFactura(p.getName() + " " + p.getApellidos());
		}

		// ------ Si viene nif en la petición se emite la factura a ese nif.
		// ------ En caso contrario se usan los datos del paciente.
		if (factura.getNifFactura() == null) {
			factura.setNifFactura(p.getDni());
		}

		if (factura.getFecha() == null) {
			factura.setFecha(new Date(Calendar.getInstance().getTimeInMillis()));
		}
		factura.setCreada(new Date(Calendar.getInstance().getTimeInMillis()));

		// ------- Recupera los diagnósticos de la bbdd para que no estén
		// detacched -------
		List<Long> diagnosticosIds = new ArrayList<Long>();
		for (Diagnostico d : factura.getDiagnosticos()) {
			diagnosticosIds.add(d.getId());
		}
		factura.setDiagnosticos(this.diagnosticoService.getDiagnosticos(diagnosticosIds));
		factura.setImporte(this.diagnosticoService.getPrecioDiagnosticos(factura.getDiagnosticos()));

		for (Diagnostico d : factura.getDiagnosticos()) {
			d.setFactura(factura);
		}
		Factura f = this.entityManager.merge(factura);
		this.entityManager.flush();

		if (factura.getNumero() == null) {
			Calendar cal = Calendar.getInstance();
			f.setNumero(cal.get(Calendar.YEAR) + "/" + String.format("%05d", f.getId()));
		}

		// ------ Se crea el pdf de la factura
		// -------
		FacturaPdfCreator pdfCreator = new FacturaPdfCreator();
		String fileName = pdfCreator.createFacturaPdf(f);

		f.setFileName(fileName);

		// ------ Añade una nota en el paciente diciendo que se emite factura
		// -------
		p.setNotas(p.getNotas() + "\n" + Utils.getCurrentFormattedDate() + ": Emitida factura número " + f.getNumero());
		this.pacienteService.updatePaciente(p);

		return f;
	}

	public List<Factura> getFacturasByPaciente(long pacienteId) {
		String query = "SELECT f FROM Factura f WHERE f.paciente.id = :pacienteId ORDER BY f.creada DESC";
		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("pacienteId", pacienteId)
				.getResultList();
		return lista;
	}

	public Factura getFactura(long id) {
		return this.entityManager.find(Factura.class, id);
	}

}
