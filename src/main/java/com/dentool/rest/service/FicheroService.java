package com.dentool.rest.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Fichero;
import com.dentool.utils.Utils;

@Stateless
public class FicheroService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String path;

	@PersistenceContext
	private EntityManager entityManager;

	public FicheroService() {
		this.path = Utils.getFileStoragePath() + "ficheros/";
	}

	public List<Fichero> create(MultipartFormDataInput input) throws IOException {

		String fileName = "";
		String fullPathName = "";
		long pacienteId = 0L;
		boolean fileCreated = false;
		Fichero f = null;
		List<Fichero> lista = new ArrayList<Fichero>();

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		List<InputPart> pacienteParts = uploadForm.get("pacienteId");
		for (InputPart inputPart : pacienteParts) {
			pacienteId = inputPart.getBody(Long.class, null);
		}

		List<InputPart> inputParts = uploadForm.get("inputUploadFile");

		for (InputPart inputPart : inputParts) {

			try {

				fileCreated = false;

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fullPathName = this.path + pacienteId + "_" + fileName;

				writeFile(bytes, fullPathName);
				fileCreated = true;

			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}

			if (fileCreated) {
				f = new Fichero();
				f.setFileName(fileName);
				f.setFullPathName(fullPathName);
				f.setUploadedDate(Calendar.getInstance().getTime());
				f.setPacienteId(pacienteId);

				entityManager.persist(f);

				lista.add(f);
			}
		}

		return lista;
	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	// get uploaded filename, is there a easy way in RESTEasy?
	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}

	public List<Fichero> getFicherosByPaciente(long pacienteId) {
		String query = "SELECT f FROM Fichero f WHERE f.pacienteId = :pacienteId ORDER BY f.uploadedDate DESC";
		@SuppressWarnings("unchecked")
		List<Fichero> lista = this.entityManager.createQuery(query).setParameter("pacienteId", pacienteId)
				.getResultList();
		return lista;
	}

	public Fichero getFichero(long id) {
		return this.entityManager.find(Fichero.class, id);
	}

}
