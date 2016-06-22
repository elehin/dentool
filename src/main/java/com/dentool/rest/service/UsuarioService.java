package com.dentool.rest.service;

import java.security.Key;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.DatatypeConverter;

import com.dentool.filter.KeyStoreService;
import com.dentool.model.Credenciales;
import com.dentool.model.Usuario;
import com.dentool.utils.Utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Stateless
public class UsuarioService {

	// private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private KeyStoreService keyStoreService;

	public Usuario create(Usuario usuario) {
		Date now = new Date(Calendar.getInstance().getTimeInMillis());
		usuario.setFechaAlta(now);
		usuario.setPassword(Utils.md5Hash(usuario.getPassword()));
		usuario.setActivo(true);
		/*
		 * usuario.setToken(this.issueToken());
		 * 
		 * Calendar c = Calendar.getInstance(); c.add(Calendar.DATE, 3); Date
		 * expirationDate = new Date(c.getTimeInMillis());
		 * 
		 * usuario.setTokenExpirationDate(expirationDate);
		 */

		entityManager.persist(usuario);
		return usuario;
	}

	public Usuario find(Credenciales credenciales) {
		String query = "SELECT u FROM Usuario u WHERE u.username = :username";

		String password = Utils.md5Hash(credenciales.getPassword());

		Usuario u = null;
		try {
			u = (Usuario) entityManager.createQuery(query).setParameter("username", credenciales.getUsername())
					.getSingleResult();
			if (!u.getPassword().equals(password)) {
				return null;
			}
			/*
			 * if (u.getToken() == null) { u.setToken(this.issueToken());
			 * 
			 * Calendar c = Calendar.getInstance(); c.add(Calendar.DATE, 3);
			 * Date expirationDate = new Date(c.getTimeInMillis());
			 * 
			 * u.setTokenExpirationDate(expirationDate); } else if
			 * (Calendar.getInstance().getTime().after(u.getTokenExpirationDate(
			 * ))) { u.setToken(this.issueToken());
			 * 
			 * Calendar c = Calendar.getInstance(); c.add(Calendar.DATE, 3);
			 * Date expirationDate = new Date(c.getTimeInMillis());
			 * 
			 * u.setTokenExpirationDate(expirationDate); }
			 */
		} catch (Exception e) {
			return null;
		}
		return u;
	}

	/**
	 * 
	 * @param credenciales
	 *            Usuario y contraseña a autenticar
	 * @return Un String que contiene un token JWT en caso de que se autentique
	 *         y null en caso contrario
	 */
	public String login(Credenciales credenciales) {
		Usuario u = this.find(credenciales);
		long ttlMillis = 86400000; // un día en milisegundos
		String issuer = "dentool";
		String encodedKey = this.keyStoreService.getEncodedKey();

		if (u != null) {

			// The JWT signature algorithm we will be using to sign the token
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);

			// We will sign our JWT with our ApiKey secret
			byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(encodedKey);
			Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

			// Let's set the JWT Claims
			JwtBuilder builder = Jwts.builder().setId(String.valueOf(u.getId())).setIssuedAt(now)
					.setSubject(u.getUsername()).setIssuer(issuer).signWith(signatureAlgorithm, signingKey);

			// if it has been specified, let's add the expiration
			if (ttlMillis >= 0) {
				long expMillis = nowMillis + ttlMillis;
				Date exp = new Date(expMillis);
				builder.setExpiration(exp);
			}

			// Builds the JWT and serializes it to a compact, URL-safe string
			return builder.compact();

		} else {
			return null;
		}
	}

	public List<Usuario> findLastCreated() {
		String query = "SELECT u FROM Usuario u ORDER BY u.fechaAlta DESC, u.id DESC";
		@SuppressWarnings("unchecked")
		List<Usuario> lista = entityManager.createQuery(query).setMaxResults(10).getResultList();

		return lista;
	}

	/*
	 * public Usuario validaToken(String token) { String query =
	 * "SELECT u FROM Usuario u WHERE u.token = :token"; Usuario u = null; try {
	 * u = (Usuario) this.entityManager.createQuery(query).setParameter("token",
	 * token).getSingleResult(); } catch (Exception e) { return null; } if
	 * (Calendar.getInstance().getTime().after(u.getTokenExpirationDate())) {
	 * return null; } else { return u; } }
	 */

	public Usuario updateUsuario(Usuario u) {
		Usuario lu = entityManager.find(Usuario.class, u.getId());

		lu.setPassword(Utils.md5Hash(u.getPassword()));
		lu.setActivo(u.isActivo());

		return lu;
	}

	// private String issueToken() {
	// SecureRandom random = new SecureRandom();
	// byte bytes[] = new byte[40];
	// random.nextBytes(bytes);
	// String token = bytes.toString();
	// return token;
	// }
}
