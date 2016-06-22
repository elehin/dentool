package com.dentool.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dentool.filter.Secured;
import com.dentool.model.TratamientoTop;
import com.dentool.rest.service.MostUsedTratamientosService;

@Path("/tratamientoTop")
public class MostUsedTratamientosRestService {

	@Inject
	private MostUsedTratamientosService mostUsedTratamientosService;

	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll() {
		List<TratamientoTop> lista = mostUsedTratamientosService.getTratamientosTop();
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

}
