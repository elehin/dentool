package com.dentool.cron;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

@WebListener
public class QuartzListener extends QuartzInitializerListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		ServletContext ctx = sce.getServletContext();
		StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);

		String scheduleTratamientosTop = "0 0 21 * * ? *";
		String scheduleDatosComerciales = "0 10 21 * * ? *";
		String scheduleReportIngresos = "0 20 21 * * ? *";
		String scheduleReportPacientes = "0 25 21 * * ? *";

		// --------------- Schedules para pruebas ------------------------------
		// String scheduleTratamientosTop = "0/30 * * * * ? *";
		// String scheduleDatosComerciales = "10/40 * * * * ? *";
		// String scheduleReportIngresos = "20/49 * * * * ? *";
		// String scheduleReportPacientes = "25/55 * * * * ? *";

		try {
			Scheduler scheduler = factory.getScheduler();

			JobDetail jobTratamientosTop = JobBuilder.newJob(MostUsedTratamientosJob.class).build();
			Trigger triggerTratamientosTop = TriggerBuilder.newTrigger().withIdentity("Tratamientos más usados")
					.withSchedule(CronScheduleBuilder.cronSchedule(scheduleTratamientosTop)).startNow().build();
			scheduler.scheduleJob(jobTratamientosTop, triggerTratamientosTop);

			JobDetail jobDatosComerciales = JobBuilder.newJob(DatosComercialesJob.class).build();
			Trigger triggerDatosComerciales = TriggerBuilder.newTrigger().withIdentity("Datos comerciales")
					.withSchedule(CronScheduleBuilder.cronSchedule(scheduleDatosComerciales)).startNow().build();
			scheduler.scheduleJob(jobDatosComerciales, triggerDatosComerciales);

			JobDetail jobReportIngresos = JobBuilder.newJob(ReportIngresosJob.class).build();
			Trigger triggerReportIngresos = TriggerBuilder.newTrigger().withIdentity("Report Ingresos mensuales")
					.withSchedule(CronScheduleBuilder.cronSchedule(scheduleReportIngresos)).startNow().build();
			scheduler.scheduleJob(jobReportIngresos, triggerReportIngresos);

			JobDetail jobReportPacientes = JobBuilder.newJob(ReportsPacientesJob.class).build();
			Trigger triggerReportPacientes = TriggerBuilder.newTrigger().withIdentity("Report mensuales Pacientes")
					.withSchedule(CronScheduleBuilder.cronSchedule(scheduleReportPacientes)).startNow().build();
			scheduler.scheduleJob(jobReportPacientes, triggerReportPacientes);

			scheduler.start();
		} catch (Exception e) {
			ctx.log("There was an error scheduling the jobs.", e);
		}
	}

}
