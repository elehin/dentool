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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.ImportesFacturados;
import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.Factura;
import com.dentool.model.entities.Paciente;
import com.dentool.rest.service.itext.FacturaPdfCreator;

@Stateless
public class FacturaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private DiagnosticoService diagnosticoService;

	@Inject
	private PacienteService pacienteService;

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

		if (factura.getFecha() == null) {
			factura.setFecha(new Date(Calendar.getInstance().getTimeInMillis()));
		}
		factura.setCreada(new Date(Calendar.getInstance().getTimeInMillis()));

		// ------- Recupera los diagnósticos de la bbdd para que no estén
		// detacched -------
		List<Long> diagnosticosIds = new ArrayList<Long>();
		for (Diagnostico d : factura.getDiagnosticos()) {
			diagnosticosIds.add(d.getId());
		}
		factura.setDiagnosticos(this.diagnosticoService.getDiagnosticos(diagnosticosIds));
		factura.setImporte(this.diagnosticoService.getPrecioDiagnosticos(factura.getDiagnosticos()));

		for (Diagnostico d : factura.getDiagnosticos()) {
			d.setFactura(factura);
		}
		Factura f = this.entityManager.merge(factura);
		this.entityManager.flush();

		if (factura.getNumero() == null) {
			Calendar cal = Calendar.getInstance();
			f.setNumero(cal.get(Calendar.YEAR) + "/" + String.format("%05d", f.getId()));
		}

		// ------ Se crea el pdf de la factura
		// -------
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

	public int emitirFacturas(List<Long> pacientes) {
		int facturas = 0;
		Calendar cal = Calendar.getInstance();

		for (long id : pacientes) {
			Factura f = new Factura();

			Paciente p = this.pacienteService.find(id);
			f.setPacienteId(p.getId());
			f.setNombreFactura(p.getName() + " " + p.getApellidos());
			f.setFecha(new Date(cal.getTimeInMillis()));
			f.setCreada(new Date(Calendar.getInstance().getTimeInMillis()));
			f.setNifFactura(p.getDni());
			f.setDiagnosticos(this.diagnosticoService.getDiagnosticosNoFacturadosByPaciente(p.getId()));
			f.setImporte(this.diagnosticoService.getPrecioDiagnosticos(f.getDiagnosticos()));
			for (Diagnostico d : f.getDiagnosticos()) {
				d.setFactura(f);
			}

			Factura factura = this.entityManager.merge(f);
			this.entityManager.flush();

			factura.setNumero(cal.get(Calendar.YEAR) + "/" + String.format("%05d", factura.getId()));

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

	public List<Factura> getFacturasTrimestre(int mes) {
		String query = "SELECT f FROM Factura f WHERE f.fecha between :desde AND :hasta ORDER BY f.creada DESC";

		Calendar desde = Calendar.getInstance();
		Calendar hasta = Calendar.getInstance();

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

		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();
		return lista;
	}

	public File getZipFacturasTrimestre(int mes) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes);
		List<Factura> facturas = this.getFacturasTrimestre(mes);

		String zipFileName = "zip_mes_trimestre_"
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

	public List<Factura> getFacturasMes(int mes) {
		String query = "SELECT f FROM Factura f WHERE f.fecha between :desde AND :hasta ORDER BY f.creada DESC";

		Calendar desde = Calendar.getInstance();
		Calendar hasta = Calendar.getInstance();

		desde.set(Calendar.MONTH, mes);
		desde.set(Calendar.DATE, 1);
		hasta.set(Calendar.MONTH, mes);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DATE));

		@SuppressWarnings("unchecked")
		List<Factura> lista = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getResultList();
		return lista;
	}

	public File getZipFacturasMes(int mes) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes);
		List<Factura> facturas = this.getFacturasMes(mes);

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

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		List<Factura> facturas = this.getFacturasYear(year);

		String zipFileName = "zip_año_"
				+ cal.getDisplayName(Calendar.YEAR, Calendar.SHORT_FORMAT, new Locale("es", "ES")) + "_"
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
			fis = new FileInputStream(file);

			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

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
				fis.close();
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

		Object o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			ifs.setMes(resultFloat);
			ifs.setStringMesCurso(desde.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES")));
		}

		// Cálculo del importe facturado en el mes anterior
		desde.add(Calendar.MONTH, -1);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));
		hasta.add(Calendar.MONTH, -1);

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			ifs.setMesAnterior(resultFloat);
			ifs.setStringMesAnterior(desde.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES")));
		}

		// Cálculo del importe facturado en el trimestre en curso
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
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			ifs.setTrimestre(resultFloat);
		}

		// Cálculo del importe facturado en el año en curso
		desde.set(Calendar.MONTH, 0);
		hasta.set(Calendar.MONTH, 11);
		hasta.set(Calendar.DATE, hasta.getActualMaximum(Calendar.DAY_OF_MONTH));

		o = this.entityManager.createQuery(query).setParameter("desde", desde.getTime())
				.setParameter("hasta", hasta.getTime()).getSingleResult();
		if (o != null) {
			double result = (double) o;
			float resultFloat = Float.parseFloat(Double.toString(result));
			ifs.setYear(resultFloat);
		}

		return ifs;
	}

}
