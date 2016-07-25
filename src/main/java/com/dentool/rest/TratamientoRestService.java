package com.dentool.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.dentool.filter.Secured;
import com.dentool.model.entities.Tratamiento;
import com.dentool.rest.service.TratamientoService;

@Path("/tratamiento")
public class TratamientoRestService {

	@Inject
	private TratamientoService tratamientoService;

	@POST
	@Secured
	@Consumes("application/json")
	public Response create(Tratamiento tratamiento) {
		tratamientoService.create(tratamiento);
		return Response.created(
				UriBuilder.fromResource(TratamientoRestService.class).path(String.valueOf(tratamiento.getId())).build())
				.build();
	}

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupTratamientoById(@PathParam("id") long id) {
		Tratamiento tratamiento = tratamientoService.find(id);
		if (tratamiento == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(tratamiento).build();
	}

	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll() {
		List<Tratamiento> lista = tratamientoService.findAll();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/nombre/{nombre}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupTratamientoByName(@PathParam("nombre") String nombre) {
		List<Tratamiento> lista = tratamientoService.findByNombre(nombre);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

	@POST
	@Secured
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTratamiento(Tratamiento t) {
		tratamientoService.updateTratamiento(t);
		return Response
				.created(UriBuilder.fromResource(TratamientoRestService.class).path(String.valueOf(t.getId())).build())
				.build();
	}
}
