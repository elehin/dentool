package com.dentool.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.dentool.exception.NoCitaFoundException;
import com.dentool.filter.Secured;
import com.dentool.model.MiniCalendario;
import com.dentool.model.entities.Cita;
import com.dentool.rest.service.CitaService;

@Path("/cita")
public class CitaRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private CitaService citaService;

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupCitaById(@PathParam("id") long id) {
		Cita cita = citaService.find(id);
		if (cita == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(cita).build();
	}

	@GET
	@Secured
	@Path("/fecha/{fecha}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupCitasByFecha(@PathParam("fecha") Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);

		List<Cita> lista = citaService.getCitas(cal);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}
	
	@GET
	@Secured
	@Path("/fechaString/{fecha}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupCitasByStringFecha(@PathParam("fecha") String fecha) {
		Date d;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
		} catch (ParseException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return this.lookupCitasByFecha(d);
	}

	@GET
	@Secured
	@Path("/siguientes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSiguientesCitas() {

		List<Cita> lista = citaService.getSiguientesCitas();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/minicalendario/{fecha}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMiniCalendario(@PathParam("fecha") Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		MiniCalendario mc = citaService.getMiniCalendario(cal);
		if (mc == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(mc).build();
	}

	// public Response lookupCitasByFecha(@PathParam("fecha") String fecha) {
	// Calendar cal = Calendar.getInstance();
	// cal.set(Calendar.DATE, Integer.parseInt(fecha.substring(0,
	// fecha.indexOf('-'))));
	// cal.set(Calendar.MONTH,
	// Integer.parseInt(fecha.substring(fecha.indexOf('-') + 1,
	// fecha.lastIndexOf('-'))) - 1);
	// cal.set(Calendar.YEAR,
	// Integer.parseInt(fecha.substring(fecha.lastIndexOf('-') + 1,
	// fecha.length())));
	//
	// // System.out.println(cal.getTime());
	//
	// List<Cita> lista = citaService.getCitas(cal);
	// if (lista == null) {
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	// return Response.ok(lista).build();
	// }

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Cita c) {
		this.citaService.create(c);
		return Response.created(UriBuilder.fromResource(CitaRestService.class).path(String.valueOf(c.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Cita c) {
		this.citaService.update(c);
		return Response.created(UriBuilder.fromResource(CitaRestService.class).path(String.valueOf(c.getId())).build())
				.build();
	}

	@DELETE
	@Secured
	@Path("/{id}")
	public void delete(@PathParam("id") long id) {
		try {
			this.citaService.delete(id);

			Response.ok().build();

		} catch (NoCitaFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}
