package com.dentool.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HelloResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String ping(){
		return "Hello World!";
	}
	
}
