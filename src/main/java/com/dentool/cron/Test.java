package com.dentool.cron;

import java.util.Calendar;

import com.dentool.model.Paciente;
import com.dentool.rest.service.PacienteService;

public class Test {

	public void test() {
		PacienteService service = new PacienteService();

		Paciente p = new Paciente();
		p.setName("Prueba");
		Calendar c = Calendar.getInstance();
		p.setApellidos(c.toString());

		service.create(p);
	}

}
