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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.Pago;
import com.dentool.rest.service.PagoService;

@Path("/pago")
public class PagoRestService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private PagoService pagoService;

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPagoById(@PathParam("id") long id) {
		Pago pago = pagoService.find(id);
		if (pago == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(pago).build();
	}

	@GET
	@Path("/diagnostico/{diagnostico}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiagnosticosByPaciente(@PathParam("diagnostico") long diagnosticoId) {
		List<Pago> lista = pagoService.getPagosByDiagnostico(diagnosticoId);
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

	@PUT
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Pago p) {
		logger.debug("diagnosticoId: " + p.getDiagnosticoId() + "###################################");
		this.pagoService.create(p);
		return Response.created(UriBuilder.fromResource(PagoRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Pago p) {
		this.pagoService.update(p);
		return Response.created(UriBuilder.fromResource(PagoRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@Path("/delete/{id}")
	@DELETE
	public void delete(@PathParam("id") long id) {
		this.pagoService.delete(id);
	}
}
