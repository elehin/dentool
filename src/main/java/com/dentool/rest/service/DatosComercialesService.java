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

		datosComerciales.setClientesDiagnosticados(Integer.parseInt(resultDiagnosticado[0].toString()));
		datosComerciales.setDiagnosticado(Float.valueOf(resultDiagnosticado[1].toString()));

		Object[] resultPresupuestado = (Object[]) this.entityManager.createQuery(qPacientesPresupuestado)
				.setParameter("fechaInicio", cal.getTime()).setParameter("iniciado", false).getSingleResult();

		datosComerciales.setClientesPresupuestados(Integer.parseInt(resultPresupuestado[0].toString()));
		datosComerciales.setPresupuestado(Float.valueOf(resultPresupuestado[1].toString()));

		Object[] resultIniciado = (Object[]) this.entityManager.createQuery(qPacientesIniciados)
				.setParameter("fechaInicio", cal.getTime()).setParameter("iniciado", true).getSingleResult();

		datosComerciales.setClientesIniciadosSinPagar(Integer.parseInt(resultIniciado[0].toString()));
		datosComerciales.setIniciadoSinPagar(Float.valueOf(resultIniciado[1].toString()));

		Object[] resultTratados = (Object[]) this.entityManager.createQuery(qPacientesTratados)
				.setParameter("fechaInicio", cal.getTime()).getSingleResult();

		datosComerciales.setClientesTratados(Integer.parseInt(resultTratados[0].toString()));
		datosComerciales.setIngresos(Float.valueOf(resultTratados[1].toString()));

		entityManager.persist(datosComerciales);

	}

	public List<DatosComerciales> getDatosComerciales() {
		@SuppressWarnings("unchecked")
		List<DatosComerciales> lista = entityManager
				.createQuery("SELECT d FROM DatosComerciales d ORDER BY d.fecha DESC, d.id DESC").setMaxResults(1).getResultList();
		return lista;
	}
}
