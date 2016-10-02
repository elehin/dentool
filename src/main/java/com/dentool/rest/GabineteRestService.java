package com.dentool.rest;

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

import com.dentool.filter.Secured;
import com.dentool.model.entities.Gabinete;
import com.dentool.rest.service.GabineteService;

@Path("/gabinete")
public class GabineteRestService {

	@Inject
	private GabineteService gabineteService;

	@PUT
	@Secured
	@Consumes("application/json")
	public Response create(Gabinete gabinete) {
		this.gabineteService.create(gabinete);
		return Response.created(
				UriBuilder.fromResource(GabineteRestService.class).path(String.valueOf(gabinete.getId())).build())
				.build();
	}

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupGabineteById(@PathParam("id") long id) {
		Gabinete gabinete = this.gabineteService.find(id);
		if (gabinete == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(gabinete).build();
	}

	@GET
	@Secured
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupAllGabinetes() {
		List<Gabinete> lista = this.gabineteService.getGabinetes();

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
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateGabinete(Gabinete g) {
		this.gabineteService.updateGabinete(g);
		return Response
				.created(UriBuilder.fromResource(GabineteRestService.class).path(String.valueOf(g.getId())).build())
				.build();
	}

	@DELETE
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	public Response delete(@PathParam("id") long id) {
		this.gabineteService.delete(id);
		return Response.noContent().build();
	}
}
