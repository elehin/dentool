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
import com.dentool.model.entities.Usuario;
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

		entityManager.persist(usuario);
		return usuario;
	}

	public Usuario find(Credenciales credenciales) {
		String query = "SELECT u FROM Usuario u WHERE u.username = :username AND u.activo = :activo";

		String password = Utils.md5Hash(credenciales.getPassword());

		Usuario u = null;
		try {
			u = (Usuario) entityManager.createQuery(query).setParameter("username", credenciales.getUsername())
					.setParameter("activo", true).getSingleResult();
			if (u == null || !u.getPassword().equals(password)) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return u;
	}

	public Usuario find(long id) {
		return this.entityManager.find(Usuario.class, id);
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

	public Usuario updateUsuario(Usuario u) {
		Usuario lu = entityManager.find(Usuario.class, u.getId());

		if (u.getPassword() != null && !"".equals(u.getPassword())) {
			u.setPassword(Utils.md5Hash(u.getPassword()));
		}
		lu.update(u);

		return lu;
	}
}
