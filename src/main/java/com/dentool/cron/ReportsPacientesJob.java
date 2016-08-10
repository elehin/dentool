package com.dentool.cron;

import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.rest.service.PacienteService;

public class ReportsPacientesJob implements Job {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Ejecutándose actualización de ReportDatosPacientes: " + Calendar.getInstance().getTime().toString());
		Context context = null;
		PacienteService pacienteService = null;
		try {
			context = new InitialContext();

			pacienteService = (PacienteService) context.lookup("java:global/ROOT/PacienteService");

		} catch (NamingException e) {
			try {
				context = new InitialContext();

				pacienteService = (PacienteService) context.lookup("java:global/dentool/PacienteService");
			} catch (NamingException e1) {
				throw new JobExecutionException("NamingException al acceder a PacienteService");
			}

		}
		if (pacienteService == null) {
			throw new JobExecutionException("pacienteService es null");
		}
		pacienteService.executeAltasReport();

	}
}
