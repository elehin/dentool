package com.dentool.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.dentool.filter.Secured;
import com.dentool.model.entities.Fichero;
import com.dentool.rest.service.FicheroService;

@Path("/fichero")
public class FicheroRestService {

	@Inject
	private FicheroService ficheroService;

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(MultipartFormDataInput input) {

		// check if all form parameters are provided
		if (input == null)
			return Response.status(400).entity("Invalid form data").build();

		List<Fichero> lista = null;
		try {
			lista = ficheroService.create(input);
		} catch (IOException e) {
			return Response.status(500).entity("Cannot save file").build();
		}

		return Response.ok(lista).build();

	}

	@GET
	@Secured
	@Path("/paciente/{pacienteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFicherosByPaciente(@PathParam("pacienteId") long pacienteId) {
		List<Fichero> lista = this.ficheroService.getFicherosByPaciente(pacienteId);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFile(@PathParam("id") long id) {

		Fichero f = this.ficheroService.getFichero(id);

		if (f == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return Response.ok(f).build();
	}

	@GET
	@Secured
	@Path("/file/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getFileToDownload(@PathParam("id") long id) {

		Fichero f = this.ficheroService.getFichero(id);

		if (f == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		File file = new File(f.getFullPathName());

		ResponseBuilder response = Response.ok((Object) file);
		// String fileName = f.getFileName().substring(f.getFileName().lastIndexOf('/')
		// + 1, f.getFileName().length());
		response.header("Content-Disposition", "attachment; filename=" + f.getFileName());
		return response.build();
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

}
