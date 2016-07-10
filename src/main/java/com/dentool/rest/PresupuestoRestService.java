package com.dentool.rest;

import java.io.File;
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

import com.dentool.filter.Secured;
import com.dentool.model.Presupuesto;
import com.dentool.rest.service.PresupuestoService;

@Path("/presupuesto")
public class PresupuestoRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private PresupuestoService presupuestoService;
	//
	// @GET
	// @Secured
	// @Path("/{id:[0-9][0-9]*}")
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response lookupPagoById(@PathParam("id") long id) {
	// Pago pago = pagoService.find(id);
	// if (pago == null) {
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	// return Response.ok(pago).build();
	// }

	@GET
	@Secured
	@Path("/paciente/{pacienteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiagnosticosByPaciente(@PathParam("pacienteId") long pacienteId) {
		List<Presupuesto> lista = this.presupuestoService.getPresupuestosByPaciente(pacienteId);
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response create(Presupuesto p) {
		this.presupuestoService.create(p);

		File file = new File(p.getFileName());
		ResponseBuilder response = Response.ok((Object) file);

		String fileName = p.getFileName().substring(p.getFileName().lastIndexOf('/') + 1, p.getFileName().length());

		response.header("Content-Disposition", "attachment; filename=" + fileName);

		return response.build();
	}

	@GET
	@Secured
	@Path("/pdf/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response getPdf(@PathParam("id") long id) {

		Presupuesto p = this.presupuestoService.getPresupuesto(id);

		if (p == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		File file = new File(p.getFileName());

		ResponseBuilder response = Response.ok((Object) file);
		String fileName = p.getFileName().substring(p.getFileName().lastIndexOf('/') + 1, p.getFileName().length());
		response.header("Content-Disposition", "attachment; filename=" + fileName);
		return response.build();
	}

	// @POST
	// @Secured
	// @Path("/update")
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response update(Pago p) {
	// this.pagoService.update(p);
	// return Response
	// .created(UriBuilder.fromResource(PresupuestoRestService.class).path(String.valueOf(p.getId())).build())
	// .build();
	// }
	//
	// @DELETE
	// @Secured
	// @Path("/delete/{id}")
	// public void delete(@PathParam("id") long id) {
	// this.pagoService.delete(id);
	// }
}
