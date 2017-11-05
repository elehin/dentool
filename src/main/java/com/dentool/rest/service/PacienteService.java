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

import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Pago;
import com.dentool.model.entities.Parametro;
import com.dentool.model.entities.ReportPacientesMes;
import com.dentool.service.ParametroService;
import com.dentool.utils.Utils;

@Stateless
public class PacienteService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private ParametroService parametroService;

	public Paciente create(Paciente paciente) {
		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		paciente.setAlta(now);
		paciente.setLastChangeTs(now);
		entityManager.persist(paciente);

		if (paciente.isPacienteAnteriorADentool()) {
			Parametro p = this.parametroService.getParametro(Parametro.PACIENTES_ANTERIORES_DENTOOL);
			if (p != null) {
				int pacientesAntiguos = Integer.parseInt(p.getValor());
				pacientesAntiguos--;
				p.setValor(String.valueOf(pacientesAntiguos));
			}
		}

		return paciente;
	}

	public Paciente find(long id) {
		Paciente p = entityManager.find(Paciente.class, id);
		return p;
	}

	public List<Paciente> findLastModified() {
		String query = "SELECT p FROM Paciente p ORDER BY p.lastChangeTs DESC, p.id DESC";
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery(query).setMaxResults(10).getResultList();

		return lista;
	}

	public List<Paciente> findByApellido(String apellido) {
		apellido = Utils.removeTildes(apellido.toLowerCase());
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager
				.createQuery("SELECT p FROM Paciente p WHERE p.apellidosNormalized LIKE :apellido")
				.setParameter("apellido", "%" + apellido + "%").getResultList();
		return lista;

	}

	public List<Paciente> findByName(String name) {
		name = Utils.removeTildes(name.toLowerCase());
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.nameNormalized LIKE :name")
				.setParameter("name", "%" + name + "%").getResultList();
		return lista;
	}

	public List<Paciente> findByTelefono(String telefono) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.telefono = :telefono")
				.setParameter("telefono", telefono).getResultList();
		return lista;
	}

	public List<Paciente> findByDni(String dni) {
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery("SELECT p FROM Paciente p WHERE p.dni = :dni")
				.setParameter("dni", dni).getResultList();
		return lista;
	}

	public List<Paciente> findByFullName(String name, String apellido) {
		name = Utils.removeTildes(name.toLowerCase());
		apellido = Utils.removeTildes(apellido.toLowerCase());
		String query = "SELECT p FROM Paciente p WHERE p.nameNormalized LIKE :name AND p.apellidosNormalized LIKE :apellido";
		@SuppressWarnings("unchecked")
		List<Paciente> lista = entityManager.createQuery(query).setParameter("name", "%" + name + "%")
				.setParameter("apellido", "%" + apellido + "%").getResultList();
		return lista;
	}

	public Paciente updatePaciente(Paciente p) {
		Paciente lp = find(p.getId());

		lp.update(p);

		return p;
	}

	public void deletePaciente(long id) {
		Paciente p = find(id);
		if (p != null) {
			entityManager.remove(p);
		}
	}

	public void executeReportsPacientesMes() {

		List<ReportPacientesMes> backup = this.clearReportsPacientesMes();

		try {

			String queryAltas = "SELECT COUNT(p) FROM Paciente p WHERE p.alta BETWEEN :desde AND :hasta";
			String queryPacientesTratados = "SELECT COUNT(DISTINCT d.paciente) FROM Diagnostico d WHERE d.fechaInicio BETWEEN :desde AND :hasta";

			Calendar desde = Calendar.getInstance();
			Calendar hasta = Calendar.getInstance();

			desde.set(Calendar.DATE, desde.getActualMinimum(Calendar.DATE));
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));

			int i = 0;
			boolean isPersistible = false;

			do {
				i++;
				isPersistible = false;

				Object resultAltas = this.entityManager.createQuery(queryAltas).setParameter("desde", desde.getTime())
						.setParameter("hasta", hasta.getTime()).getSingleResult();

				Object resultPacientesTratados = this.entityManager.createQuery(queryPacientesTratados)
						.setParameter("desde", desde.getTime()).setParameter("hasta", hasta.getTime())
						.getSingleResult();

				ReportPacientesMes am = new ReportPacientesMes();

				if (resultAltas != null) {
					int altas = Integer.parseInt(resultAltas.toString());

					am.setFecha(hasta.getTime());
					am.setAltas(altas);
					isPersistible = true;
				}

				if (resultPacientesTratados != null) {
					int pacientesTratados = Integer.parseInt(resultPacientesTratados.toString());
					am.setFecha(hasta.getTime());
					am.setPacientesTratados(pacientesTratados);
					isPersistible = true;
				}

				if (isPersistible) {
					this.entityManager.persist(am);
				}

				desde.add(Calendar.MONTH, -1);
				hasta.add(Calendar.MONTH, -1);
				hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));

			} while (i < 24);
		} catch (Exception e) {
			logger.error(
					"#####################################################################################################");
			logger.error(
					"Error al ejecutar la actualización en PacienteService.executeAltasReport(). Se ejecuta restore.");
			logger.error(e.getMessage());
			for (ReportPacientesMes am : backup) {
				entityManager.merge(am);
			}

		}
	}

	public List<ReportPacientesMes> clearReportsPacientesMes() {
		@SuppressWarnings("unchecked")
		List<ReportPacientesMes> antiguos = entityManager.createQuery("SELECT r FROM ReportPacientesMes r")
				.getResultList();
		for (ReportPacientesMes r : antiguos) {
			entityManager.detach(r);
		}
		entityManager.createQuery("DELETE FROM ReportPacientesMes r").executeUpdate();
		return antiguos;
	}

	public List<ReportPacientesMes> getReportsPacientesMes() {
		@SuppressWarnings("unchecked")
		List<ReportPacientesMes> lista = entityManager.createQuery("SELECT r FROM ReportPacientesMes r")
				.getResultList();

		return lista;
	}

	public List<Paciente> getPacientesConPagosNoFacturados() {

		// select p.id, p.name, p.apellidos, d.id, pago.cantidad, pago.factura
		// from Paciente p join diagnostico d on d.paciente = p.id
		// join pago pago on pago.diagnosticoId = d.id where d.variasfacturas =
		// true and pago.factura is null;

		String queryDiagnosticos = "SELECT d.id FROM Diagnostico d WHERE d.variasFacturas = :variasFacturas";
		@SuppressWarnings("unchecked")
		List<Long> diagnosticos = this.entityManager.createQuery(queryDiagnosticos).setParameter("variasFacturas", true)
				.getResultList();

		String queryPagos = "SELECT p FROM Pago p WHERE p.diagnosticoId IN :diagnosticos AND p.factura IS EMPTY";
		@SuppressWarnings("unchecked")
		List<Pago> pagos = this.entityManager.createQuery(queryPagos).setParameter("diagnosticos", diagnosticos)
				.getResultList();

		List<Long> diagnosticoIds = new ArrayList<Long>();
		for (Pago pago : pagos) {
			diagnosticoIds.add(pago.getDiagnosticoId());
		}

		String queryPacientes = "SELECT p FROM Diagnostico d JOIN d.paciente AS p WHERE d.id in :diagnosticoIds";
		@SuppressWarnings("unchecked")
		List<Paciente> pacientes = this.entityManager.createQuery(queryPacientes)
				.setParameter("diagnosticoIds", diagnosticoIds).getResultList();

		return pacientes;
	}

	public boolean hayPacientesConPagosNoFacturados() {
		String queryDiagnosticos = "SELECT d.id FROM Diagnostico d WHERE d.variasFacturas = :variasFacturas";
		@SuppressWarnings("unchecked")
		List<Long> diagnosticos = this.entityManager.createQuery(queryDiagnosticos).setParameter("variasFacturas", true)
				.getResultList();

		if (diagnosticos == null || diagnosticos.isEmpty()) {
			return false;
		}

		String queryPagos = "SELECT p FROM Pago p WHERE p.diagnosticoId IN :diagnosticos AND p.factura IS EMPTY";
		@SuppressWarnings("unchecked")
		List<Pago> pagos = this.entityManager.createQuery(queryPagos).setParameter("diagnosticos", diagnosticos)
				.getResultList();

		return pagos != null && !pagos.isEmpty();
	}
}
