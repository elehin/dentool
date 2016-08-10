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

import com.dentool.rest.service.DatosComercialesService;

public class DatosComercialesJob implements Job {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Ejecutándose actualización de DatosComerciales: " + Calendar.getInstance().getTime().toString());
		Context context = null;
		DatosComercialesService datosComercialesService = null;
		try {
			context = new InitialContext();

			datosComercialesService = (DatosComercialesService) context
					.lookup("java:global/ROOT/DatosComercialesService");

		} catch (NamingException e) {
			try {
				context = new InitialContext();

				datosComercialesService = (DatosComercialesService) context
						.lookup("java:global/dentool/DatosComercialesService");
			} catch (NamingException e1) {
				throw new JobExecutionException("NamingException al acceder a DatosComercialesService");
			}

		}
		if (datosComercialesService == null) {
			throw new JobExecutionException("datosComercialesService es null");
		}
		datosComercialesService.executeReport();

	}
}
