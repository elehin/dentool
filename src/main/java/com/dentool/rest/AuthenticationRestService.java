package com.dentool.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import com.dentool.model.Credenciales;
import com.dentool.model.LoginResponse;
import com.dentool.model.entities.Usuario;
import com.dentool.rest.service.UsuarioService;

@Path("/authentication")
public class AuthenticationRestService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private UsuarioService usuarioService;

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response authenticateUser(Credenciales credenciales) {

		try {

			// Authenticate the user using the credentials provided
			LoginResponse loginResponse = authenticate(credenciales);

			// Return the token on the response
			return Response.ok(loginResponse).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

	}

	private LoginResponse authenticate(Credenciales credenciales) throws Exception {
		String token = usuarioService.login(credenciales);
		if (token == null) {
			throw new Exception();
		}
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(token);
		return loginResponse;
	}

	@POST
	@Secured
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateUser(Usuario usuario) {

		Usuario u = this.usuarioService.updateUsuario(usuario);
		if (u == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return Response.ok(u).build();
	}

	@GET
	@Secured
	@Path("/lastUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupUsuarioByLastChange() {
		List<Usuario> lista = usuarioService.findLastCreated();
		if (lista == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return Response.ok(lista).build();
	}

	@GET
	@Secured
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response lookupUsuarioById(@PathParam("id") long id) {
		Usuario usuario = usuarioService.find(id);
		if (usuario == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return Response.ok(usuario).build();
	}

	// @PUT
	// @Secured
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response createUser(Credenciales c) {
	// Usuario u = new Usuario(c);
	// u = usuarioService.create(u);
	// return Response.created(
	// UriBuilder.fromResource(AuthenticationRestService.class).path(String.valueOf(u.getUsername())).build())
	// .build();
	// }

	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(Usuario u) {
		u = usuarioService.create(u);
		return Response.created(
				UriBuilder.fromResource(AuthenticationRestService.class).path(String.valueOf(u.getUsername())).build())
				.build();
	}
}
