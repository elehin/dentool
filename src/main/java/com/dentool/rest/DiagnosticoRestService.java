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

import com.dentool.model.Diagnostico;
import com.dentool.rest.service.DiagnosticoService;

@Path("/diagnostico")
public class DiagnosticoRestService {

	@Inject
	private DiagnosticoService diagnosticoService;

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiagnosticoById(@PathParam("id") long id) {
		Diagnostico diagnostico = diagnosticoService.find(id);
		if (diagnostico == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(diagnostico).build();
	}

	@GET
	@Path("/paciente/{paciente}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiagnosticosByPaciente(@PathParam("paciente") long id) {
		List<Diagnostico> lista = diagnosticoService.getDiagnosticosByPaciente(id);
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
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addDiagnostico(Diagnostico d) {
		System.out.println(d);
		diagnosticoService.addDiagnostico(d);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDiagnostico(Diagnostico d) {
		diagnosticoService.updateDiagnostico(d);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}
}
