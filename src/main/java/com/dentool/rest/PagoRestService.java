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
import com.dentool.model.PagoEager;
import com.dentool.model.entities.Pago;
import com.dentool.rest.service.PagoService;

@Path("/pago")
public class PagoRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private PagoService pagoService;

	@GET
	@Secured
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
	@Secured
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
	@Secured
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Pago p) {
		this.pagoService.create(p);
		return Response.created(UriBuilder.fromResource(PagoRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Pago p) {
		this.pagoService.update(p);
		return Response.created(UriBuilder.fromResource(PagoRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/enviaPagoASaldo/{id}")
	public Response enviaPagoASaldo(@PathParam("id") long id) {
		this.pagoService.enviaPagoASaldo(id);
		return Response.ok().build();
	}

	@DELETE
	@Secured
	@Path("/delete/{id}")
	public void delete(@PathParam("id") long id) {
		this.pagoService.delete(id);
	}

	@GET
	@Secured
	@Path("/noFacturados/paciente/{paciente}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPagosNoFacturadosByPaciente(@PathParam("paciente") long id) {
		List<PagoEager> lista = pagoService.getPagosNoFacturadosByPaciente(id);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}
}
