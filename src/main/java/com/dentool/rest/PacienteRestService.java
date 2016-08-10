package com.dentool.rest;

import java.util.ArrayList;
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

import com.dentool.filter.Secured;
import com.dentool.model.PacienteLazy;
import com.dentool.model.entities.ReportPacientesMes;
import com.dentool.model.entities.Paciente;
import com.dentool.rest.service.PacienteService;

@Path("/paciente")
public class PacienteRestService {

	@Inject
	private PacienteService pacienteService;

	@POST
	@Secured
	@Consumes("application/json")
	public Response create(Paciente paciente) {
		pacienteService.create(paciente);
		return Response.created(
				UriBuilder.fromResource(PacienteRestService.class).path(String.valueOf(paciente.getId())).build())
				.build();
	}

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteById(@PathParam("id") long id) {
		Paciente paciente = pacienteService.find(id);
		if (paciente == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(paciente).build();
	}

	@GET
	@Secured
	@Path("/apellido/{apellido}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByApellido(@PathParam("apellido") String apellido) {
		List<Paciente> lista = pacienteService.findByApellido(apellido);
		List<Paciente> listaNombres = this.pacienteService.findByName(apellido);

		if (lista == null && listaNombres == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		if (lista != null && listaNombres != null) {
			lista.addAll(listaNombres);
		} else if (lista == null) {
			lista = listaNombres;
		}

		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/telefono/{telefono}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByTelefono(@PathParam("telefono") String telefono) {
		List<Paciente> lista = pacienteService.findByTelefono(telefono);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/dni/{dni}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByDni(@PathParam("dni") String dni) {
		List<Paciente> lista = pacienteService.findByDni(dni);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/nombre/{nombre}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByName(@PathParam("nombre") String nombre) {
		List<Paciente> lista = pacienteService.findByName(nombre);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/nombre/{nombre}/apellido/{apellido}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByFullName(@PathParam("nombre") String name, @PathParam("apellido") String apellido) {
		List<Paciente> lista = pacienteService.findByFullName(name, apellido);
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/lastChanges")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupPacienteByLastChange() {
		List<Paciente> lista = pacienteService.findLastModified();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		List<PacienteLazy> listaLazy = new ArrayList<PacienteLazy>();
		short orden = 0;
		for (Paciente p : lista) {
			PacienteLazy pl = new PacienteLazy(p);
			orden++;
			pl.setOrden(orden);
			listaLazy.add(new PacienteLazy(p));
		}

		return Response.ok(listaLazy).build();
	}

	@GET
	@Secured
	@Path("/datosMensuales")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatosAltasMes() {
		List<ReportPacientesMes> lista = pacienteService.getDatosAltasMes();
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
	public Response updatePaciente(Paciente p) {
		pacienteService.updatePaciente(p);
		return Response
				.created(UriBuilder.fromResource(PacienteRestService.class).path(String.valueOf(p.getId())).build())
				.build();
	}
}
