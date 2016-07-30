package com.dentool.filter;

import java.util.Calendar;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	// @Inject
	// private UsuarioService usuarioService;

	@Inject
	private KeyStoreService keyStoreService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @see ContainerRequestFilter#filter(ContainerRequestContext)
	 */
	public void filter(ContainerRequestContext requestContext) throws java.io.IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		logger.info(requestContext.getUriInfo().getAbsolutePath().getPath());

		try {
			// Check if the HTTP Authorization header is present and formatted
			// correctly
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				// throw new NotAuthorizedException("Authorization header must
				// be
				// provided");
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			} else {

				// Extract the token from the HTTP Authorization header
				String token = authorizationHeader.substring("Bearer".length()).trim();

				// Validate the token
				this.validateToken(token);
			}
		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

		
	}

	private void validateToken(String jwt) throws Exception {
		String encodedKey = this.keyStoreService.getEncodedKey();

		// This line will throw an exception if it is not a signed JWS (as
		// expected)
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(encodedKey)).parseClaimsJws(jwt)
				.getBody();

		if (Calendar.getInstance().getTime().after(claims.getExpiration())) {
			throw new Exception();
		}
		// System.out.println("ID: " + claims.getId());
		// System.out.println("Subject: " + claims.getSubject());
		// System.out.println("Issuer: " + claims.getIssuer());
		// System.out.println("Expiration: " + claims.getExpiration());
	}

}
