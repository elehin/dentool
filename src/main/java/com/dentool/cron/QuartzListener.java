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

		String schedule = "0 0 3 * * ? *";
		// String schedule = "0/30 * * * * ? *";

		try {
			Scheduler scheduler = factory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(MostUsedTratamientosJob.class).build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Tratamientos más usados")
					.withSchedule(CronScheduleBuilder.cronSchedule(schedule)).startNow().build();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (Exception e) {
			ctx.log("There was an error scheduling the job.", e);
		}
	}

}
