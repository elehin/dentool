package com.dentool.rest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.ImportesFacturados;
import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.Factura;
import com.dentool.model.entities.Paciente;
import com.dentool.model.entities.Pago;
import com.dentool.rest.service.itext.FacturaPdfCreator;
import com.dentool.rest.service.poi.GeneradorInformeFacturacion;
import com.dentool.utils.Utils;

@Stateless
public class FacturaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String path;

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private DiagnosticoService diagnosticoService;

	@Inject
	private PacienteService pacienteService;

	@Inject
	private PagoService pagoService;

	public FacturaService() {
		this.checkFilePath();
	}

	public Factura create(Factura factura) {

		logger.debug("FacturaService.create()");

		// ------ Si viene nombre en la petición se emite la factura a ese
		// nombre.
		// ------ En caso contrario se usan los datos del paciente.
		Paciente p = null;

		if (factura.getNombreFactura() == null || "".equals(factura.getNombreFactura())) {
			p = this.pacienteService.find(factura.getPacienteId());
			factura.setNombreFactura(p.getName() + " " + p.getApellidos());
		}

		// ------ Si viene nif en la petición se emite la factura a ese nif.
		// ------ En caso contrario se usan los datos del paciente.
		if (factura.getNifFactura() == null || "".equals(factura.getNifFactura())) {
			if (p == null) {
				p = this.pacienteService.find(factura.getPacienteId());
			}
			factura.setNifFactura(p.getDni());
		}

		// ------ Si viene fecha en la petición se emite la factura a ese nif.
		// ------ En caso contrario se pone la fecha actual.
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
		if (factura.getFecha() == null) {
			factura.setFecha(new Date(calendar.getTimeInMillis()));
		} else {
			calendar.setTimeInMillis(factura.getFecha().getTime());
			factura.setFecha(calendar.getTime());
		}
		factura.setCreada(new Date(Calendar.getInstance().getTimeInMillis()));

		// Comprueba si se van a facturar diagnósticos pagados o pagos parciales
		if (factura.getDiagnosticos() != null && !factura.getDiagnosticos().isEmpty()) {

			// Extrae los Ids de los diagnosticos los guarda en una Lista<Long>
			// para uso posterior en la recuperación de los diagnósticos de BBDD
			List<Long> diagnosticosIds = new ArrayList<Long>();
			for (Diagnostico d : factura.getDiagnosticos()) {
				diagnosticosIds.add(d.getId());
			}
			// Recupera los diagnósticos de la bbdd para que no estén detached.
			factura.setDiagnosticos(this.diagnosticoService.getDiagnosticos(diagnosticosIds));
			factura.setImporte(this.diagnosticoService.getPrecioDiagnosticos(factura.getDiagnosticos()));

			for (Diagnostico d : factura.getDiagnosticos()) {
				d.setFactura(factura);
			}
		} else if (factura.getPagos() != null && !factura.getPagos().isEmpty()) {

			// ------- Recupera los pagos de la bbdd para que no estén detached
			List<Long> pagosIds = new ArrayList<Long>();
			for (Pago pago : factura.getPagos()) {
				pagosIds.add(pago.getId());
			}
			factura.setPagos(this.pagoService.getPagos(pagosIds));
			factura.setImporte(this.pagoService.getPrecioPagos(factura.getPagos()));

			for (Pago pago : factura.getPagos()) {
				pago.setFactura(factura);
			}

			// // Marca los diagnosticos como variasFacturas = true
			// List<Long> diagnosticosIds = new ArrayList<Long>();
			// for (Pago pago : factura.getPagos()) {
			// diagnosticosIds.add(pago.getDiagnosticoId());
			// }
			// // Recupera los diagnósticos de la bbdd para que no estén
			// detached.
			// factura.setDiagnosticos(this.diagnosticoService.getDiagnosticos(diagnosticosIds));
			// for (Diagnostico d : factura.getDiagnosticos()) {
			// d.setVariasFacturas(true);
			// }
		}

		Factura f = this.entityManager.merge(factura);

		if (f.getPagos() != null && !f.getPagos().isEmpty()) {
			// Marca los diagnosticos como variasFacturas = true
			List<Long> diagnosticosIds = new ArrayList<Long>();
			for (Pago pago : f.getPagos()) {
				diagnosticosIds.add(pago.getDiagnosticoId());
			}
			// Recupera los diagnósticos de la bbdd para que no estén detached.
			f.setDiagnosticos(this.diagnosticoService.getDiagnosticos(diagnosticosIds));
			for (Diagnostico d : f.getDiagnosticos()) {
				d.setVariasFacturas(true);
			}
		}

		this.entityManager.flush();

		f.setNumero(getNumeroFactura(f));

		// ------ Se crea el pdf de la factura -------
		FacturaPdfCreator pdfCreator = new FacturaPdfCreator();
		String fileName = pdfCreator.createFacturaPdf(f);

		f.setFileName(fileName);

		// ------ Añade una nota en el paciente diciendo que se emite factura
		// -------
		// p.setNotas(p.getNotas() + "\n" + Utils.getCurrentFormattedDate() + ":
		// Emitida factura número " + f.getNumero());
		// this.pacienteService.updatePaciente(p);

		return f;
	}

	private synchronized String getNumeroFactura(Factura f) {

		String query = "SELECT f FROM Factura f WHERE f.fecha BETWEEN :desde AND :hasta AND f.numero IS NOT NULL ORDER BY f.numero DESC";

		Calendar desde = Calendar.getInstance();
		desde.setTime(f.getFecha());
		desde.set(Calendar.MONTH, 0);
		desde.set(Calendar.DATE, 1);
		Utils.setInicioDia(desde);

		Calendar hasta = Calendar.getInstance();
		hasta.setTime(f.getFecha());
		hasta.set(Calendar.MONTH, 11);
		hasta.set(Calendar.DATE, 31);
		Utils.setFinDia(hasta);

		Factura anterior = null;
		String numeroSiguiente = String.valueOf(desde.get(Calendar.YEAR)) + "/";

		try {
			anterior = (Factura) this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
					.setParameter("hasta", hasta.getTime()).setMaxResults(1).getSingleResult();

			if (anterior != null) {
				String secuencialSiguente = anterior.getNumero().substring(5, anterior.getNumero().length());
				int secSiguiente = Integer.parseInt(secuencialSiguente) + 1;
				numeroSiguiente += String.format("%05d", secSiguiente);
			} else {
				numeroSiguiente += String.format("%05d", 1);
			}
		} catch (NoResultException e) {
			numeroSiguiente += String.format("%05d", 1);
		}

		return numeroSiguiente;

	}

	// private String getNumeroFactura(Factura f) {
	//
	// long previousId = f.getId() - 1L;
	//
	// Factura anterior = null;
	// while (anterior == null && previousId > 0L) {
	// anterior = this.entityManager.find(Factura.class, previousId);
	// previousId--;
	// }
	//
	// int previousYear = Integer.valueOf(anterior.getNumero().substring(0, 4));
	//
	// Calendar cal = Calendar.getInstance();
	//
	// String numeroSiguiente = "";
	// String secuencialSiguente = anterior.getNumero().substring(5,
	// anterior.getNumero().length());
	// int secSiguiente = Integer.parseInt(secuencialSiguente) + 1;
	//
	// if (cal.get(Calendar.YEAR) == previousYear) {
	// numeroSiguiente = cal.get(Calendar.YEAR) + "/" + String.format("%05d",
	// secSiguiente);
	// } else {
	// numeroSiguiente = cal.get(Calendar.YEAR) + "/" + String.format("%05d",
	// 1);
	// }
	//
	// return numeroSiguiente;
	//
	// }

	public int emitirFacturas(List<Long> pacientes, Date fechaFactura) {
		int facturas = 0;
		Calendar cal = Calendar.getInstance();

		for (long id : pacientes) {
			Factura f = new Factura();

			Paciente p = this.pacienteService.find(id);
			f.setPacienteId(p.getId());
			f.setNombreFactura(p.getName() + " " + p.getApellidos());
			f.setCreada(new Date(Calendar.getInstance().getTimeInMillis()));
			f.setNifFactura(p.getDni());
			f.setDiagnosticos(this.diagnosticoService.getDiagnosticosNoFacturadosByPaciente(p));
			if (f.getDiagnosticos() == null) {
				continue;
			}
			f.setImporte(this.diagnosticoService.getPrecioDiagnosticos(f.getDiagnosticos()));

			if (fechaFactura == null) {
				// TODO incluir TimeZone
				f.setFecha(new Date(cal.getTimeInMillis()));
			} else {
				f.setFecha(fechaFactura);
			}

			for (Diagnostico d : f.getDiagnosticos()) {
				d.setFactura(f);
			}

			Factura factura = this.entityManager.merge(f);
			this.entityManager.flush();

			factura.setNumero(this.getNumeroFactura(factura));

			// ------ Se crea el pdf de la factura
			// -------
			FacturaPdfCreator pdfCreator = new FacturaPdfCreator();
			String fileName = pdfCreator.createFacturaPdf(factura);

			factura.setFileName(fileName);
			facturas++;
		}
		return facturas;
	}

	public List<Factura> getFacturasByPaciente(long pacienteId) {
		String query = "SELECT f FROM Factura f WHERE f.pacienteId = :pacienteId ORDER BY f.creada DESC";
		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("pacienteId", pacienteId)
				.getResultList();
		return lista;
	}

	public Factura getFactura(long id) {
		return this.entityManager.find(Factura.class, id);
	}

	public List<Factura> getFacturasTrimestre(int mes, int year) {
		String query = "SELECT f FROM Factura f WHERE f.fecha between :desde AND :hasta ORDER BY f.creada DESC";

		Calendar desde = Calendar.getInstance();
		Calendar hasta = Calendar.getInstance();

		desde.set(Calendar.YEAR, year);
		hasta.set(Calendar.YEAR, year);
		this.getMesesDelTrimestre(mes, desde, hasta);

		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();
		return lista;
	}

	private void getMesesDelTrimestre(int mes, Calendar desde, Calendar hasta) {
		int mesDesde = 0;
		int mesHasta = 2;

		switch (mes) {
		case 0:
		case 1:
		case 2:
			mesDesde = 0;
			mesHasta = 2;
			break;
		case 3:
		case 4:
		case 5:
			mesDesde = 3;
			mesHasta = 5;
			break;
		case 6:
		case 7:
		case 8:
			mesDesde = 6;
			mesHasta = 8;
			break;
		case 9:
		case 10:
		case 11:
			mesDesde = 9;
			mesHasta = 11;
			break;

		}

		desde.set(Calendar.MONTH, mesDesde);
		desde.set(Calendar.DATE, 1);
		hasta.set(Calendar.MONTH, mesHasta);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));
	}

	public File getZipFacturasTrimestre(int mes, int year) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		GeneradorInformeFacturacion gif = new GeneradorInformeFacturacion();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.YEAR, year);
		List<Factura> facturas = this.getFacturasTrimestre(mes, year);

		String q = String.valueOf((mes + 3) / 3).substring(0, 1);
		String zipFileName = "zip_Q" + q + "_" + cal.getTimeInMillis() + ".zip";

		try {
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);

			for (Factura f : facturas) {
				if (f.getFileName() != null && !"".equals(f.getFileName())) {
					File file = new File(f.getFileName());
					this.addToZipFile(file, zos);
				}
			}

			Calendar desde = Calendar.getInstance();
			Calendar hasta = Calendar.getInstance();
			this.getMesesDelTrimestre(mes, desde, hasta);

			String informeFileName = "informe_trimestre_"
					+ desde.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES")) + "_"
					+ hasta.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES"));
			String titulo = "Resumen facturas "
					+ desde.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES")) + "-"
					+ hasta.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES"));

			File informe = gif.creaInforme(titulo, informeFileName, facturas);
			this.addToZipFile(informe, zos);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				zos.close();
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		File zipFile = new File(zipFileName);
		return zipFile;
	}

	public List<Factura> getFacturasMes(int mes, int year) {
		String query = "SELECT f FROM Factura f WHERE f.fecha between :desde AND :hasta ORDER BY f.creada DESC";

		Calendar desde = Calendar.getInstance();
		Calendar hasta = Calendar.getInstance();

		desde.set(Calendar.MONTH, mes);
		desde.set(Calendar.DATE, 1);
		desde.set(Calendar.YEAR, year);
		hasta.set(Calendar.MONTH, mes);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));
		hasta.set(Calendar.YEAR, year);

		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();
		return lista;
	}

	public File getZipFacturasMes(int mes, int year) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		GeneradorInformeFacturacion gif = new GeneradorInformeFacturacion();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.YEAR, year);
		List<Factura> facturas = this.getFacturasMes(mes, year);

		String zipFileName = "zip_mes_"
				+ cal.getDisplayName(Calendar.MONTH, Calendar.SHORT_FORMAT, new Locale("es", "ES")) + "_"
				+ cal.getTimeInMillis() + ".zip";

		try {
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);

			for (Factura f : facturas) {
				if (f.getFileName() != null && !"".equals(f.getFileName())) {
					File file = new File(f.getFileName());
					this.addToZipFile(file, zos);
				}
			}

			String informeFileName = "informe_mes_"
					+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES"));
			String titulo = "Resumen facturas "
					+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("es", "ES"));

			File informe = gif.creaInforme(titulo, informeFileName, facturas);
			this.addToZipFile(informe, zos);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				zos.close();
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		File zipFile = new File(zipFileName);
		return zipFile;
	}

	public List<Factura> getFacturasYear(int year) {
		String query = "SELECT f FROM Factura f WHERE f.fecha between :desde AND :hasta ORDER BY f.creada DESC";

		Calendar desde = Calendar.getInstance();
		Calendar hasta = Calendar.getInstance();

		desde.set(Calendar.MONTH, 0);
		desde.set(Calendar.DATE, 1);
		hasta.set(Calendar.MONTH, 11);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));
		desde.set(Calendar.YEAR, year);
		hasta.set(Calendar.YEAR, year);

		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();
		return lista;
	}

	public File getZipFacturasYear(int year) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		GeneradorInformeFacturacion gif = new GeneradorInformeFacturacion();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		List<Factura> facturas = this.getFacturasYear(year);

		String zipFileName = "zip_año_" + cal.get(Calendar.YEAR) + "_" + cal.getTimeInMillis() + ".zip";

		try {
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);

			for (Factura f : facturas) {
				if (f.getFileName() != null && !"".equals(f.getFileName())) {
					File file = new File(f.getFileName());
					this.addToZipFile(file, zos);
				}
				String informeFileName = "informe_año_" + cal.get(Calendar.YEAR);
				String titulo = "Resumen facturas " + cal.get(Calendar.YEAR);

				File informe = gif.creaInforme(titulo, informeFileName, facturas);
				this.addToZipFile(informe, zos);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				zos.close();
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		File zipFile = new File(zipFileName);
		return zipFile;
	}

	private void addToZipFile(File file, ZipOutputStream zos) {

		FileInputStream fis = null;
		try {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

			fis = new FileInputStream(file);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				zos.closeEntry();
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		}
	}

	public List<Factura> getLastFacturas() {

		String query = "SELECT f FROM Factura f ORDER BY f.fecha DESC";
		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setMaxResults(200).getResultList();

		return lista;
	}

	public ImportesFacturados getImportesFacturados() {
		String query = "SELECT sum(f.importe) FROM Factura f WHERE f.fecha between :desde AND :hasta";

		ImportesFacturados ifs = new ImportesFacturados();

		// Cálculo del importe facturado en el mes en curso
		Calendar desde = Calendar.getInstance();
		desde.set(Calendar.DATE, 1);
		Calendar hasta = Calendar.getInstance();
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));

		Object o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		double result;
		float resultFloat;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
			ifs.setMes(resultFloat);
			ifs.setStringMesCurso(desde.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES")));
		}

		// Cálculo del importe facturado en el mes anterior
		desde.add(Calendar.MONTH, -1);
		hasta.add(Calendar.MONTH, -1);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		result = 0;
		resultFloat = 0;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
		}
		ifs.setMesAnterior(resultFloat);
		ifs.setStringMesAnterior(desde.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES")));

		// Cálculo del importe facturado en el trimestre en curso
		desde = Calendar.getInstance();
		desde.set(Calendar.DATE, 1);
		hasta = Calendar.getInstance();
		switch (Math.floorDiv(Calendar.getInstance().get(Calendar.MONTH), 3)) {
		case 0:
			desde.set(Calendar.MONTH, 0);
			hasta.set(Calendar.MONTH, 2);
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		case 1:
			desde.set(Calendar.MONTH, 3);
			hasta.set(Calendar.MONTH, 5);
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		case 2:
			desde.set(Calendar.MONTH, 6);
			hasta.set(Calendar.MONTH, 8);
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		case 3:
			desde.set(Calendar.MONTH, 9);
			hasta.set(Calendar.MONTH, 11);
			hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		}

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		result = 0;
		resultFloat = 0;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
		}
		ifs.setTrimestre(resultFloat);

		// Cálculo del importe facturado en el trimestre anterior
		desde.add(Calendar.MONTH, -3);
		hasta.add(Calendar.MONTH, -3);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		result = 0;
		resultFloat = 0;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
		}
		ifs.setTrimestreAnterior(resultFloat);

		// Cálculo del importe facturado en el año en curso
		desde = Calendar.getInstance();
		hasta = Calendar.getInstance();

		desde.set(Calendar.MONTH, 0);
		hasta.set(Calendar.MONTH, 11);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		result = 0;
		resultFloat = 0;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
		}
		ifs.setYear(resultFloat);

		// Cálculo del importe facturado en el año anterior
		desde.add(Calendar.YEAR, -1);
		hasta.add(Calendar.YEAR, -1);

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		result = 0;
		resultFloat = 0;
		if (o != null) {
			result = (double) o;
			resultFloat = Float.parseFloat(Double.toString(result));
		}
		ifs.setYearAnterior(resultFloat);

		return ifs;
	}

	private void checkFilePath() {
		this.path = System.getenv("OPENSHIFT_DATA_DIR");
		if (this.path == null) {
			logger.info("Ejecución en entorno no OpenShift, se crearán los ficheros en ruta absoluta.");
			this.path = "C:/Users/Vane/Documents/";
		} else {
			path += "facturas/";
		}

		logger.info(this.path);
	}
}
