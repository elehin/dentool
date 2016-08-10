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

import com.dentool.rest.service.PagoService;

public class ReportIngresosJob implements Job {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Ejecutándose actualización de ReportIngresos: " + Calendar.getInstance().getTime().toString());
		Context context = null;
		PagoService pagoService = null;
		try {
			context = new InitialContext();

			pagoService = (PagoService) context.lookup("java:global/ROOT/PagoService");

		} catch (NamingException e) {
			try {
				context = new InitialContext();

				pagoService = (PagoService) context.lookup("java:global/dentool/PagoService");
			} catch (NamingException e1) {
				throw new JobExecutionException("NamingException al acceder a PagoService");
			}

		}
		if (pagoService == null) {
			throw new JobExecutionException("pagoService es null");
		}
		pagoService.executeIngresosReport();

	}
}
