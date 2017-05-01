package com.dentool.rest;

import java.util.Calendar;
import java.util.Date;

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

import com.dentool.filter.Secured;
import com.dentool.model.entities.Dia;
import com.dentool.rest.service.DiaService;

@Path("/dia")
public class DiaRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private DiaService diaService;

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupCitaById(@PathParam("id") long id) {
		Dia dia = diaService.find(id);
		if (dia == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(dia).build();
	}

	@GET
	@Secured
	@Path("/fecha/{fecha}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiaByFecha(@PathParam("fecha") Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);

		Dia dia = diaService.getDia(cal);
		if (dia == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(dia).build();
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Dia d) {
		this.diaService.create(d);
		return Response.created(UriBuilder.fromResource(DiaRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Dia d) {
		this.diaService.update(d);
		return Response.created(UriBuilder.fromResource(DiaRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@DELETE
	@Secured
	@Path("/{id}")
	public void delete(@PathParam("id") long id) {
		this.diaService.delete(id);

		Response.ok().build();
	}
}
