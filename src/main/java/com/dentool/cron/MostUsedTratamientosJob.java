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

import com.dentool.service.MostUsedTratamientosService;

public class MostUsedTratamientosJob implements Job {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info(
				"Ejecutándose actualización de MostUsedTratamientos: " + Calendar.getInstance().getTime().toString());
		Context context = null;
		MostUsedTratamientosService mostUsedTratamientosService = null;
		try {
			context = new InitialContext();

			mostUsedTratamientosService = (MostUsedTratamientosService) context
					.lookup("java:global/ROOT/MostUsedTratamientosService");

		} catch (NamingException e) {
			try {
				context = new InitialContext();

				mostUsedTratamientosService = (MostUsedTratamientosService) context
						.lookup("java:global/dentool/MostUsedTratamientosService");
			} catch (NamingException e1) {
				throw new JobExecutionException("NamingException al acceder a MostUsedTratamientosService");
			}

		}
		if (mostUsedTratamientosService == null) {
			throw new JobExecutionException("mostUsedTratamientosService es null");
		}
		mostUsedTratamientosService.executeReport();

	}
}
