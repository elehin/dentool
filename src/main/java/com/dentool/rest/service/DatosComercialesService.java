package com.dentool.rest.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.entities.DatosComerciales;

@Stateless
public class DatosComercialesService {

	@PersistenceContext
	private EntityManager entityManager;

	public void executeReport() {

		String qPacientesDiagnosticados = "SELECT COUNT(DISTINCT d.paciente), SUM(d.precio) FROM Diagnostico d "
				+ "WHERE d.diagnosticado > :fechaInicio AND  d.presupuestos IS EMPTY AND d.iniciado = :iniciado";
		String qPacientesPresupuestado = "SELECT COUNT(DISTINCT d.paciente), SUM(d.precio) FROM Diagnostico d "
				+ "WHERE d.presupuestos IS NOT EMPTY AND d.diagnosticado > :fechaInicio AND d.iniciado = :iniciado";
		String qPacientesIniciados = "SELECT COUNT(DISTINCT d.paciente), SUM(d.precio) FROM Diagnostico d "
				+ "WHERE d.diagnosticado > :fechaInicio AND d.iniciado = :iniciado AND d.precio > d.pagado";
		String qPacientesTratados = "SELECT COUNT(DISTINCT d.paciente), SUM(d.pagado) FROM Diagnostico d "
				+ "WHERE d.diagnosticado > :fechaInicio AND d.precio <= d.pagado";
		DatosComerciales datosComerciales = new DatosComerciales();

		Calendar cal = Calendar.getInstance();

		datosComerciales.setFecha(cal.getTime());

		cal.add(Calendar.YEAR, -1);
		cal.add(Calendar.DATE, -1);

		Object[] resultDiagnosticado = (Object[]) this.entityManager.createQuery(qPacientesDiagnosticados)
				.setParameter("fechaInicio", cal.getTime()).setParameter("iniciado", false).getSingleResult();

		if (resultDiagnosticado[0] != null) {
			datosComerciales.setClientesDiagnosticados(Integer.parseInt(resultDiagnosticado[0].toString()));
		} else {
			datosComerciales.setClientesDiagnosticados(0);
		}
		if (resultDiagnosticado[1] != null) {
			datosComerciales.setDiagnosticado(Float.valueOf(resultDiagnosticado[1].toString()));
		} else {
			datosComerciales.setDiagnosticado(0f);
		}

		Object[] resultPresupuestado = (Object[]) this.entityManager.createQuery(qPacientesPresupuestado)
				.setParameter("fechaInicio", cal.getTime()).setParameter("iniciado", false).getSingleResult();

		if (resultPresupuestado[0] != null) {
			datosComerciales.setClientesPresupuestados(Integer.parseInt(resultPresupuestado[0].toString()));
		} else {
			datosComerciales.setClientesPresupuestados(0);
		}
		if (resultPresupuestado[1] != null) {
			datosComerciales.setPresupuestado(Float.valueOf(resultPresupuestado[1].toString()));
		} else {
			datosComerciales.setPresupuestado(0f);
		}

		Object[] resultIniciado = (Object[]) this.entityManager.createQuery(qPacientesIniciados)
				.setParameter("fechaInicio", cal.getTime()).setParameter("iniciado", true).getSingleResult();

		if (resultIniciado[0] != null) {
			datosComerciales.setClientesIniciadosSinPagar(Integer.parseInt(resultIniciado[0].toString()));
		} else {
			datosComerciales.setClientesIniciadosSinPagar(0);
		}
		if (resultIniciado[1] != null) {
			datosComerciales.setIniciadoSinPagar(Float.valueOf(resultIniciado[1].toString()));
		} else {
			datosComerciales.setIniciadoSinPagar(0f);
		}

		Object[] resultTratados = (Object[]) this.entityManager.createQuery(qPacientesTratados)
				.setParameter("fechaInicio", cal.getTime()).getSingleResult();

		if (resultTratados[0] != null) {
			datosComerciales.setClientesTratados(Integer.parseInt(resultTratados[0].toString()));
		} else {
			datosComerciales.setClientesTratados(0);
		}
		if (resultTratados[1] != null) {
			datosComerciales.setIngresos(Float.valueOf(resultTratados[1].toString()));
		} else {
			datosComerciales.setIngresos(0f);
		}

		entityManager.persist(datosComerciales);

	}

	public List<DatosComerciales> getDatosComerciales() {
		@SuppressWarnings("unchecked")
		List<DatosComerciales> lista = entityManager
				.createQuery("SELECT d FROM DatosComerciales d ORDER BY d.fecha DESC, d.id DESC").setMaxResults(1)
				.getResultList();
		return lista;
	}
}
