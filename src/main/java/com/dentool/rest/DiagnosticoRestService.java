package com.dentool.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.dentool.model.Descuento;
import com.dentool.model.DescuentosAEliminar;
import com.dentool.model.DiagnosticoSet;
import com.dentool.model.DiagnosticosNoFacturado;
import com.dentool.model.entities.Diagnostico;
import com.dentool.rest.service.DiagnosticoService;

@Path("/diagnostico")
public class DiagnosticoRestService {

	@Inject
	private DiagnosticoService diagnosticoService;

	@GET
	@Secured
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
	@Secured
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
	@Secured
	@Path("/notStarted/paciente/{paciente}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupDiagnosticosNotStartedByPaciente(@PathParam("paciente") long id) {
		List<Diagnostico> lista = diagnosticoService.getDiagnosticosNotStartedByPaciente(id);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/noFacturados/paciente/{paciente}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiagnosticosNoFacturadosByPaciente(@PathParam("paciente") long id) {
		List<Diagnostico> lista = diagnosticoService.getDiagnosticosNoFacturadosByPaciente(id);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/noFacturados")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiagnosticosNoFacturados() {
		List<DiagnosticosNoFacturado> lista = diagnosticoService.getDiagnosticosNoFacturados();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/noFacturables")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiagnosticosNoFacturables() {
		List<DiagnosticosNoFacturado> lista = diagnosticoService.getDiagnosticosNoFacturables();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/pagosPendientes/{paciente}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPagosPendientes(@PathParam("paciente") long id) {
		float pagosPendientes = diagnosticoService.getPagosPendientes(id);
		return Response.ok(pagosPendientes).build();
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_HTML)
	public String ping() {
		return "Up & running";
	}

	@POST
	@Secured
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addDiagnostico(Diagnostico d) {
		diagnosticoService.addDiagnostico(d);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/addMultiple")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDiagnosticoMultiple(DiagnosticoSet set) {
		List<Diagnostico> lista = diagnosticoService.addDiagnostico(set);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@POST
	@Secured
	@Path("/aplicaDescuento")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response aplicaDescuento(Descuento descuento) {
		List<Diagnostico> lista = diagnosticoService.aplicaDescuentoPorcentual(descuento.getDiagnosticos(),
				descuento.getDescuento());
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@POST
	@Secured
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDiagnostico(Diagnostico d) {
		diagnosticoService.updateDiagnostico(d);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/resetFechaInicio/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetFechaInicio(@PathParam("id") Long id) {
		Diagnostico d = diagnosticoService.resetFechaInicio(id);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/resetFechaFin/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetFechaFin(@PathParam("id") Long id) {
		Diagnostico d = diagnosticoService.resetFechaFin(id);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@POST
	@Secured
	@Path("/archiva/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiva(@PathParam("id") Long id) {
		Diagnostico d = diagnosticoService.archiva(id);
		return Response
				.created(UriBuilder.fromResource(DiagnosticoRestService.class).path(String.valueOf(d.getId())).build())
				.build();
	}

	@DELETE
	@Secured
	@Path("/delete/{id}")
	public void delete(@PathParam("id") long id) {
		try {
			diagnosticoService.delete(id);
		} catch (EJBTransactionRolledbackException e) {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			System.out.println(e.getClass());
		}
	}

	@POST
	@Secured
	@Path("/resetDescuentos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetDescuentos(List<Diagnostico> diagnosticos) {
		List<Diagnostico> lista = diagnosticoService.eliminaDescuento(diagnosticos);
		return Response.ok(lista).build();
	}

	@POST
	@Secured
	@Path("/resetDescuentosById")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetDescuentosById(DescuentosAEliminar descuentos) {
		List<Diagnostico> diagnosticos = new ArrayList<Diagnostico>();
		for (Long id : descuentos.getDiagnosticosIds()) {
			Diagnostico d = new Diagnostico();
			d.setId(id);
			diagnosticos.add(d);
		}

		List<Diagnostico> lista = diagnosticoService.eliminaDescuento(diagnosticos);
		return Response.ok(lista).build();
	}
}
