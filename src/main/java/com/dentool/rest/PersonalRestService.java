package com.dentool.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import com.dentool.model.entities.Personal;
import com.dentool.rest.service.PersonalService;

@Path("/personal")
public class PersonalRestService {

	@Inject
	private PersonalService personalService;

	@PUT
	@Secured
	@Consumes("application/json")
	public Response create(Personal personal) {
		this.personalService.create(personal);
		return Response.created(
				UriBuilder.fromResource(PersonalRestService.class).path(String.valueOf(personal.getId())).build())
				.build();
	}

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPersonalById(@PathParam("id") long id) {
		Personal personal = this.personalService.find(id);
		if (personal == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(personal).build();
	}

	@GET
	@Secured
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupAllPersonal() {
		List<Personal> lista = this.personalService.getPersonal();

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
	public Response updatePersonal(Personal p) {
		this.personalService.updatePersonal(p);
		return Response
				.created(UriBuilder.fromResource(PersonalRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/activar")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response activarPersonal(Personal p) {
		this.personalService.setActivo(p.getId());
		return Response
				.created(UriBuilder.fromResource(PersonalRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/baja")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bajaPersonal(Personal p) {
		this.personalService.setBaja(p.getId());
		return Response
				.created(UriBuilder.fromResource(PersonalRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}
}
