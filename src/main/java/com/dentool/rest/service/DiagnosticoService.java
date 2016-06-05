package com.dentool.rest.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.dentool.model.Diagnostico;
import com.dentool.model.Paciente;
import com.dentool.model.Tratamiento;

@Stateless
public class DiagnosticoService {

	@PersistenceContext
	private EntityManager entityManager;

	public Diagnostico addDiagnostico(Diagnostico diagnostico) {
		Paciente p = entityManager.find(Paciente.class, diagnostico.getPaciente().getId());
		Tratamiento t = entityManager.find(Tratamiento.class, diagnostico.getTratamiento().getId());
		diagnostico.setTratamiento(t);
		// if (diagnostico.getPrecio() == 0) {
		diagnostico.setPrecio(t.getPrecio());
		// }
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
				+ "ORDER BY d.finalizado DESC, d.iniciado, d.fechaInicio, d,fechaFin DESC";
		@SuppressWarnings("unchecked")
		List<Diagnostico> lista = entityManager.createQuery(query).setParameter("paciente", p).getResultList();
		return lista;
	}

	public Diagnostico updateDiagnostico(Diagnostico d) {
		Diagnostico ld = find(d.getId());

		ld.update(d);

		return d;
	}
}
