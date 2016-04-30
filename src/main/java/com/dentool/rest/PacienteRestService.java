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

import com.dentool.model.Paciente;
import com.dentool.rest.service.PacienteService;

@Path("/service")
public class PacienteRestService {

	@Inject
	private PacienteService pacienteService;

	@POST
	@Consumes("application/json")
	public Response create(Paciente paciente) {
		pacienteService.create(paciente);
		return Response.created(
				UriBuilder.fromResource(PacienteRestService.class).path(String.valueOf(paciente.getId())).build())
				.build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Paciente lookupPacienteById(@PathParam("id") long id) {
		Paciente paciente = pacienteService.find(id);
		if (paciente == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return paciente;
	}

	@GET
	@Path("/apellido/{apellido}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Paciente> lookupPacienteByApellido(@PathParam("apellido") String apellido) {
		List<Paciente> lista = pacienteService.findByApellido(apellido);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return lista;
	}

	@GET
	@Path("/telefono/{telefono}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Paciente> lookupPacienteByTelefono(@PathParam("telefono") String telefono) {
		List<Paciente> lista = pacienteService.findByTelefono(telefono);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return lista;
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

}
