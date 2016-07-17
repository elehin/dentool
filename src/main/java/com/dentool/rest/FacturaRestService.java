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
import com.dentool.model.Factura;
import com.dentool.rest.service.FacturaService;

@Path("/factura")
public class FacturaRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private FacturaService facturaService;

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFacturaById(@PathParam("id") long id) {
		Factura factura = this.facturaService.getFactura(id);
		if (factura == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(factura).build();
	}

	@GET
	@Secured
	@Path("/paciente/{pacienteId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFacturasByPaciente(@PathParam("pacienteId") long pacienteId) {
		List<Factura> lista = this.facturaService.getFacturasByPaciente(pacienteId);
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Factura f) {
		Factura factura = this.facturaService.create(f);

		return Response.ok(factura).build();
	}

	@PUT
	@Secured
	@Path("/createandprint")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response createAndPrint(Factura factura) {
		Factura f = this.facturaService.create(factura);

		File file = new File(f.getFileName());
		ResponseBuilder response = Response.ok((Object) file);

		String fileName = f.getFileName().substring(f.getFileName().lastIndexOf('/') + 1, f.getFileName().length());

		response.header("Content-Disposition", "attachment; filename=" + fileName);

		return response.build();
	}

	@GET
	@Secured
	@Path("/pdf/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	public Response getPdf(@PathParam("id") long id) {

		Factura f = this.facturaService.getFactura(id);

		if (f == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		File file = new File(f.getFileName());

		ResponseBuilder response = Response.ok((Object) file);
		String fileName = f.getFileName().substring(f.getFileName().lastIndexOf('/') + 1, f.getFileName().length());
		response.header("Content-Disposition", "attachment; filename=" + fileName);
		return response.build();
	}

	// @GET
	// @Secured
	// @Path("/pdf/{id}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces("application/pdf")
	// public Response getPdf(@PathParam("id") long id) {
	//
	// Presupuesto p = this.presupuestoService.getPresupuesto(id);
	//
	// if (p == null) {
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	//
	// File file = new File(p.getFileName());
	//
	// ResponseBuilder response = Response.ok((Object) file);
	// String fileName =
	// p.getFileName().substring(p.getFileName().lastIndexOf('/') + 1,
	// p.getFileName().length());
	// response.header("Content-Disposition", "attachment; filename=" +
	// fileName);
	// return response.build();
	// }

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
