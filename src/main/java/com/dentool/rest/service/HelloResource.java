package com.dentool.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/helloService")
public class HelloResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/ping")
	public String ping() {
		return "Hello World!";
	}

}
