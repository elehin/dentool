package com.dentool.rest.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.DiagnosticosNoFacturado;
import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Pago;
import com.dentool.model.entities.Tratamiento;

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
		diagnostico.setDescuento((1 - diagnostico.getPrecio() / t.getPrecio()) * 100);
		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		diagnostico.setDiagnosticado(now);
		diagnostico.setLastChangeTs(now);
		p.getDiagnosticos().add(diagnostico);
		p.setLastChangeTs(now);

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

	public List<Diagnostico> getDiagnosticosNoFacturadosByPaciente(Long pacienteId) {
		Paciente p = entityManager.find(Paciente.class, pacienteId);

		return this.getDiagnosticosNoFacturadosByPaciente(p);
	}

	public List<Diagnostico> getDiagnosticosNoFacturadosByPaciente(Paciente paciente) {
		String query = "SELECT d FROM Diagnostico d WHERE d.paciente = :paciente AND d.finalizado = :finalizado AND d.factura IS EMPTY "
				+ "AND d.variasFacturas = :variasFacturas "
				+ "ORDER BY d.finalizado, d.iniciado DESC, d.diagnosticado DESC, d.fechaInicio, d.fechaFin DESC";
		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = entityManager.createQuery(query).setParameter("paciente", paciente)
				.setParameter("finalizado", true).setParameter("variasFacturas", false).getResultList();
		return lista;
	}

	public List<Diagnostico> getDiagnosticosNotStartedByPaciente(Long pacienteId) {
		Paciente p = entityManager.find(Paciente.class, pacienteId);

		String query = "SELECT d FROM Diagnostico d WHERE d.paciente = :paciente AND d.iniciado = :iniciado "
				+ "ORDER BY d.finalizado, d.iniciado DESC, d.diagnosticado DESC, d.fechaInicio, d.fechaFin DESC";
		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = entityManager.createQuery(query).setParameter("paciente", p)
				.setParameter("iniciado", false).getResultList();
		return lista;
	}

	public List<Diagnostico> getDiagnosticos(List<Long> ids) {
		String query = "SELECT d FROM Diagnostico d WHERE d.id IN :ides";

		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = this.entityManager.createQuery(query).setParameter("ides", ids).getResultList();

		return lista;
	}

	public Diagnostico updateDiagnostico(Diagnostico d) {
		Diagnostico ld = find(d.getId());

		// Comprueba si hay nuevo pago
		if (d.getPagado() > 0 && ld.getPagado() != d.getPagado()) {
			// Se crea el pago al haber diferencias entre el precio anterior y
			// el nuevo
			this.createPago(ld, d.getPagado() - ld.getPagado());
		}

		ld.update(d);

		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		// Si está pagado y sin fecha de inicio se actualiza la fecha de inicio
		// a now()
		if (ld.getPagado() == ld.getPrecio() && ld.getFechaInicio() == null) {
			ld.setFechaInicio(now);
		}

		// Si está pagado y no está finalizado se actualiza la fecha de fin a
		// now()
		if (ld.getPagado() == ld.getPrecio() && ld.getFechaFin() == null) {
			ld.setFechaFin(now);
		}

		// Si tiene fecha de fin y no tiene fecha de inicio se actualiza la
		// fecha de inicio = a fecha de fin
		if (ld.getFechaFin() != null && ld.getFechaInicio() == null) {
			ld.setFechaInicio(ld.getFechaFin());
		}

		// Se actualiza el descuento aplicado
		ld.setDescuento((1 - ld.getPrecio() / ld.getTratamiento().getPrecio()) * 100);

		// Se actualiza la fecha de última modificación
		ld.setLastChangeTs(now);

		Paciente p = this.entityManager.find(Paciente.class, ld.getPaciente().getId());
		p.setLastChangeTs(now);

		return d;
	}

	public void delete(long id) {
		Diagnostico ld = entityManager.find(Diagnostico.class, id);

		// Se actualiza la fecha de modificación del paciente
		Paciente p = this.entityManager.find(Paciente.class, ld.getPaciente().getId());
		p.setLastChangeTs(new Date(ld.getLastChange().getTime()));

		ld.getPaciente().getDiagnosticos().remove(ld);
		logger.debug("######################################### Borrando diagnóstico " + ld.getId());
		entityManager.remove(ld);
	}

	private void createPago(Diagnostico d, float cantidad) {
		Pago p = new Pago();

		p.setDiagnosticoId(d.getId());
		p.setCantidad(cantidad);
		p.setFecha(new Date(Calendar.getInstance().getTimeInMillis()));

		Paciente paciente = this.entityManager.find(Paciente.class, d.getPaciente().getId());
		if (paciente.getSaldo() > 0) {
			if (paciente.getSaldo() - cantidad < 0) {
				paciente.setSaldo(0);
			} else {
				paciente.setSaldo(paciente.getSaldo() - cantidad);
			}
		}

		d.setLastChangeTs(new Date(Calendar.getInstance().getTimeInMillis()));
		paciente.setLastChangeTs(new Date(Calendar.getInstance().getTimeInMillis()));

		this.entityManager.persist(p);
	}

	public float getPagosPendientes(Paciente p) {
		return getPagosPendientes(p.getId());
	}

	public float getPagosPendientes(long pacienteId) {
		Paciente p = this.entityManager.find(Paciente.class, pacienteId);
		String query = "SELECT sum(d.precio)-sum(d.pagado) FROM Diagnostico d WHERE d.finalizado = :finalizado AND d.precio > d.pagado AND d.paciente = :paciente";
		Object r = this.entityManager.createQuery(query).setParameter("finalizado", true).setParameter("paciente", p)
				.getSingleResult();

		if (r != null) {
			double result = (double) r;
			float resultFloat = Float.parseFloat(Double.toString(result));
			return resultFloat;
		}
		return 0;

	}

	public float getPrecioDiagnosticos(List<Diagnostico> diagnosticos) {
		String query = "SELECT sum(d.precio) FROM Diagnostico d WHERE d IN :diagnosticos";
		Object o = this.entityManager.createQuery(query).setParameter("diagnosticos", diagnosticos).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			return resultFloat;
		}
		return 0f;
	}

	public List<DiagnosticosNoFacturado> getDiagnosticosNoFacturados() {
		String query = "SELECT p.id AS pacienteId, p.name AS name, p.apellidos AS apellidos, p.dni AS dni, sum(d.pagado) AS importe, "
				+ "min(d.fechaFin) AS fecha " + "FROM Diagnostico d JOIN d.paciente as p "
				+ "WHERE d.finalizado = true AND d.factura IS EMPTY AND d.pagado = d.precio AND d.variasFacturas = :variasFacturas "
				+ "AND p.name IS NOT NULL AND (p.apellidos IS NOT NULL AND p.apellidos <> '') "
				+ "AND (p.dni IS NOT NULL AND p.dni <> '') GROUP BY p.id, p.name, p.apellidos, p.dni "
				+ "ORDER BY fecha";
		@SuppressWarnings("unchecked")
		List<Object[]> lista = this.entityManager.createQuery(query).setParameter("variasFacturas", false)
				.getResultList();

		List<DiagnosticosNoFacturado> returnList = new ArrayList<DiagnosticosNoFacturado>();
		for (Object[] o : lista) {
			DiagnosticosNoFacturado dn = new DiagnosticosNoFacturado();
			dn.setPacienteId(Long.valueOf(String.valueOf(o[0])));
			dn.setName(String.valueOf(o[1]));
			dn.setApellidos(String.valueOf(o[2]));
			dn.setDni(String.valueOf(o[3]));
			dn.setImporte(Float.valueOf(String.valueOf(o[4])));
			dn.setFecha((Date) o[5]);
			returnList.add(dn);
		}

		return returnList;
	}

	public List<DiagnosticosNoFacturado> getDiagnosticosNoFacturables() {
		String query = "SELECT p.id AS pacienteId, p.name AS name, p.apellidos AS apellidos, p.dni AS dni, sum(d.pagado) AS importe"
				+ " FROM Diagnostico d JOIN d.paciente as p "
				+ "WHERE d.finalizado = true AND d.factura IS EMPTY AND d.pagado = d.precio  "
				+ "AND (p.name IS NULL OR p.name = '' OR p.apellidos IS NULL OR p.apellidos = '' "
				+ "OR p.dni IS NULL OR p.dni = '') GROUP BY p.id, p.name, p.apellidos, p.dni";
		@SuppressWarnings("unchecked")
		List<Object[]> lista = this.entityManager.createQuery(query).getResultList();

		List<DiagnosticosNoFacturado> returnList = new ArrayList<DiagnosticosNoFacturado>();
		for (Object[] o : lista) {
			DiagnosticosNoFacturado dn = new DiagnosticosNoFacturado();
			dn.setPacienteId(Long.valueOf(String.valueOf(o[0])));
			dn.setName(String.valueOf(o[1]));
			dn.setApellidos(String.valueOf(o[2]));
			dn.setDni(String.valueOf(o[3]));
			dn.setImporte(Float.valueOf(String.valueOf(o[4])));
			returnList.add(dn);
		}

		return returnList;
	}

	public List<Diagnostico> aplicaDescuentoPorcentual(List<Diagnostico> diagnosticos, float descuento) {
		List<Long> ids = new ArrayList<Long>();
		for (Diagnostico d : diagnosticos) {
			ids.add(d.getId());
		}

		String query = "SELECT d FROM Diagnostico d WHERE d.id IN :lista";
		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = entityManager.createQuery(query).setParameter("lista", ids).getResultList();

		for (Diagnostico d : lista) {
			d.setPrecio(d.getPrecio() - (d.getPrecio() * descuento / 100));
			d.setDescuento((1 - d.getPrecio() / d.getTratamiento().getPrecio()) * 100);
		}

		if (lista.isEmpty()) {
			return null;
		} else {
			return getDiagnosticosNotStartedByPaciente(lista.get(0).getPaciente().getId());
		}
	}

}
