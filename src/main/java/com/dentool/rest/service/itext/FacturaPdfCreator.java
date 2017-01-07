package com.dentool.rest.service.itext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Diagnostico;
import com.dentool.model.entities.Factura;
import com.dentool.model.entities.Pago;
import com.dentool.rest.service.DiagnosticoService;
import com.dentool.rest.service.PagoService;
import com.dentool.utils.Utils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FacturaPdfCreator {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String path;
	// private float precioTotal = 0f;
	private String fileName;

	private DiagnosticoService diagnosticoService;
	private PagoService pagoService;

	PdfWriter writer;

	public FacturaPdfCreator() {
		this.checkFilePath();

		Context context = null;
		try {
			context = new InitialContext();

			diagnosticoService = (DiagnosticoService) context.lookup("java:global/ROOT/DiagnosticoService");

		} catch (NamingException e) {
			logger.info("----- Ejecución en entorno no OpenShift, se usará \"java:global/dentool/\" ------");
			try {
				context = new InitialContext();

				diagnosticoService = (DiagnosticoService) context.lookup("java:global/dentool/DiagnosticoService");
			} catch (NamingException e1) {
				logger.error("PresupuestoPdfCreator() ---- Error al obtener stub para DiagnosticoService ----");
				logger.error(e.getMessage());
				e1.printStackTrace();
			}
		}
		try {
			context = new InitialContext();

			pagoService = (PagoService) context.lookup("java:global/ROOT/PagoService");

		} catch (NamingException e) {
			logger.info("----- Ejecución en entorno no OpenShift, se usará \"java:global/dentool/\" ------");
			try {
				context = new InitialContext();

				pagoService = (PagoService) context.lookup("java:global/dentool/PagoService");
			} catch (NamingException e1) {
				logger.error("PresupuestoPdfCreator() ---- Error al obtener stub para PagoService ----");
				logger.error(e.getMessage());
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Crea el documento pdf con los datos de la Factura que recibe.
	 * 
	 * @param factura
	 * @return Un String con el nombre del fichero que se crea
	 */
	public String createFacturaPdf(Factura factura) {
		// ------- Creación y configuración del documento -------
		Document document = this.prepareDocument(factura);

		// ------- Datos bajo el encabezado -------
		this.setDatosBajoHeader(document);

		try {
			// ------- Tabla datos Factura -------
			PdfPTable tablefactura = new PdfPTable(new float[] { 9f, 6f });
			tablefactura.setWidthPercentage(90);
			String lineFactura = "Número de factura:;\n     " + factura.getNumero() + ";Fecha:;\n      "
					+ Utils.formatAsFecha(factura.getFecha());
			this.processHeader(tablefactura, lineFactura, 40f);
			tablefactura.setSpacingBefore(5);

			document.add(tablefactura);
			// ------- ./ Tabla datos Factura -------

			// ------- Tabla Header -------
			// paciente = pacienteService.find(factura.getPacienteId());

			PdfPTable tableHeader = new PdfPTable(new float[] { 9f, 6f });
			tableHeader.setWidthPercentage(90);
			String line = "Paciente:;\n     " + factura.getNombreFactura() + "\n     NIF: " + factura.getNifFactura()
					+ ";Emisor:;\n      Clínicas OSLO, S.L.P.\n      CIF: B­85935443";

			this.processHeader(tableHeader, line, 55f);
			tableHeader.setSpacingBefore(5);

			document.add(tableHeader);

			// ------- ./ Tabla Header -------

			// ------- Tabla Body -------
			PdfPTable tableBody = new PdfPTable(new float[] { 1f, 10f, 2f, 3f });
			tableBody.setWidthPercentage(90);

			line = "#; Tratamiento; Pieza; Precio";
			this.processBody(tableBody, line, true, true);

			int rows = 0;
			int lineasFactura = 0;

			// No sé por qué no usa los diagnosticos de factura
			// Comento para ver si funciona bien con los de la factura
			// Nota: tras probarlo parece que va bien con los del objeto factura
			// List<Long> diagnosticosIds = new ArrayList<Long>();
			// for (Diagnostico d : factura.getDiagnosticos()) {
			// diagnosticosIds.add(d.getId());
			// }
			//
			// List<Diagnostico> diagnosticosList =
			// this.diagnosticoService.getDiagnosticos(diagnosticosIds);

			// Comprueba si tiene que facturar diagnosticos o pagos
			if (factura.getPagos() != null && !factura.getPagos().isEmpty()) {
				for (Pago p : factura.getPagos()) {
					rows++;
					Diagnostico d = this.diagnosticoService.find(p.getDiagnosticoId());

					while (rows > 22 && rows < 28) {
						rows++;
						line = " ; ; ; ";
						this.processBody(tableBody, line, false, false);
					}
					lineasFactura++;

					int ordinal = 1;
					for (Pago pago : this.pagoService.getPagosByDiagnostico(p.getDiagnosticoId())) {
						if (pago.compare(pago, p) > 0) {
							ordinal++;
						} else if (pago.compare(pago, p) == 0) {
							if (pago.getId() < p.getId()) {
								ordinal++;
							}
						}
					}
					String concepto = d.getTratamiento().getNombre() + " - Pago " + ordinal + "º - "
							+ Utils.formatAsPorcentaje(p.getCantidad() / d.getPrecio());

					line = lineasFactura + "; " + concepto + "; " + d.getPieza() + "; "
							+ Utils.formatAsCurrency(p.getCantidad());
					this.processBody(tableBody, line, false, true);
					// this.precioTotal += p.getCantidad();

				}
			} else if (factura.getDiagnosticos() != null && !factura.getDiagnosticos().isEmpty()) {
				for (Diagnostico d : factura.getDiagnosticos()) {
					rows++;

					while (rows > 22 && rows < 28) {
						rows++;
						line = " ; ; ; ";
						this.processBody(tableBody, line, false, false);
					}

					lineasFactura++;
					line = lineasFactura + "; " + d.getTratamiento().getNombre() + "; " + d.getPieza() + "; "
							+ Utils.formatAsCurrency(d.getPrecio());
					this.processBody(tableBody, line, false, true);
					// this.precioTotal += d.getPrecio();

				}
			}

			tableBody.setSpacingBefore(20f);

			document.add(tableBody);

			// ------- ./ Tabla Body -------

			// ------- Tabla Totalizador -------
			PdfPTable tableTotal = new PdfPTable(new float[] { 8f, 3f, 3f });
			tableTotal.setWidthPercentage(90);
			tableTotal.setSpacingBefore(20f);

			this.processTotalizer(tableTotal, Utils.formatAsCurrency(factura.getImporte()));

			document.add(tableTotal);

			// ------- ./ Tabla Totalizador -------

			// ------- Decoración -------
			if (lineasFactura <= 20 || rows > 25) {
				this.renderDecoration(document);
			}

			document.close();
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return this.fileName;
	}

	/**
	 * Prepara los datos a mostrar bajo el encabezado en la primera página
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void setDatosBajoHeader(Document document) {
		// ------- Datos bajo el encabezado -------
		try {
			Font fontSubHeader = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8.0f);
			fontSubHeader.setColor(BaseColor.GRAY);
			Paragraph datos = new Paragraph(
					"CIF: B­85935443\nC/. Oslo 41, local 3\n28922 Alcorcón – MADRID\nTeléfono: 91 689 00 70",
					fontSubHeader);
			document.add(datos);

			Font facturaLineFont = FontFactory.getFont(FontFactory.COURIER_BOLDOBLIQUE, 14.0f);
			Paragraph facturaLine = new Paragraph("Factura", facturaLineFont);
			facturaLine.setIndentationLeft(350.0f);
			document.add(facturaLine);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		// ------- ./ Datos bajo el encabezado -------
	}

	/**
	 * Crea el documento, establece márgenes y añade el evento para la creación
	 * del header y el footer
	 * 
	 * @param factura
	 * @return un Document
	 */
	private Document prepareDocument(Factura factura) {
		// ------- Creación y configuración del documento -------
		Document document = new Document();
		document.setMargins(20, 20, 70, 50);

		OutputStream fos;
		try {
			fos = new FileOutputStream(this.getFileName(factura));
			this.writer = PdfWriter.getInstance(document, fos);
		} catch (FileNotFoundException | DocumentException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
		}

		FooterNHeaderRenderer event = new FooterNHeaderRenderer();
		event.setPath(this.path);
		this.writer.setPageEvent(event);

		document.open();
		// ------- ./ Creación y configuración del documento -------

		return document;
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

	private String getFileName(Factura factura) {
		this.fileName = this.path + "factura_" + factura.getId() + "_" + Calendar.getInstance().getTimeInMillis()
				+ ".pdf";
		return this.fileName;
	}

	public void processHeader(PdfPTable table, String line, float height) {
		StringTokenizer tokenizer = new StringTokenizer(line, ";");

		// Create a PdfFont
		Font fontHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 10.0f);
		Font fontHeaderTitulo = FontFactory.getFont(FontFactory.COURIER, 8.0f);

		PdfPCell cell1 = new PdfPCell();
		cell1.setFixedHeight(height);
		cell1.setPaddingTop(-2);
		Chunk titular1 = new Chunk(tokenizer.nextToken(), fontHeaderTitulo);
		Chunk nombre = new Chunk(tokenizer.nextToken(), fontHeader);
		Paragraph p = new Paragraph();
		p.add(titular1);
		p.add(nombre);
		cell1.addElement(p);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell();
		cell2.setFixedHeight(height);
		cell2.setPaddingTop(-2);
		Chunk titular2 = new Chunk(tokenizer.nextToken(), fontHeaderTitulo);
		Chunk fecha = new Chunk(tokenizer.nextToken(), fontHeader);
		Paragraph p2 = new Paragraph();
		p2.add(titular2);
		p2.add(fecha);
		cell2.addElement(p2);
		table.addCell(cell2);
	}

	public void processBody(PdfPTable table, String line, boolean isHeader, boolean isBordered) {
		StringTokenizer tokenizer = new StringTokenizer(line, ";");

		Font fontBody = FontFactory.getFont(FontFactory.COURIER, 9.0f);
		Font fontHeaderBody = FontFactory.getFont(FontFactory.COURIER_BOLD, 8.0f);

		PdfPCell cell;
		int column = 0;

		while (tokenizer.hasMoreTokens()) {
			column++;
			if (isHeader) {

				cell = new PdfPCell();
				Paragraph p = new Paragraph(tokenizer.nextToken());
				p.setFont(fontHeaderBody);
				cell.setFixedHeight(20f);
				cell.setBorderColor(BaseColor.LIGHT_GRAY);
				cell.addElement(p);
				table.addCell(cell);

			} else {
				String text = tokenizer.nextToken();
				cell = new PdfPCell(new Phrase(text, fontBody));
				if (column == 3) {
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				} else if (column == 4) {
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setPaddingRight(10f);
				}
				if (!isBordered) {
					cell.setBorder(Rectangle.NO_BORDER);
				}
				cell.setFixedHeight(20f);
				cell.setBorderColor(BaseColor.LIGHT_GRAY);

				table.addCell(cell);
			}
		}
	}

	public void processTotalizer(PdfPTable table, String value) {
		Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14.0f);

		PdfPCell cell0 = new PdfPCell();
		cell0.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell0);

		PdfPCell cell1 = new PdfPCell(new Phrase("Total:", font));
		cell1.setFixedHeight(30f);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(value, font));
		cell2.setFixedHeight(30f);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		if (value.length() < 9) {
			cell2.setPaddingRight(10f);
		}
		table.addCell(cell2);

	}

	private void renderDecoration(Document doc) {

		// -------- Información adicional ---------
		try {
			Font fontInfo = FontFactory.getFont(FontFactory.COURIER, 12.0f);

			Phrase p1 = new Phrase("Factura exenta de I.V.A. según ley 37/92", fontInfo);
			Paragraph info = new Paragraph();
			info.add(p1);

			info.setAlignment(Element.ALIGN_RIGHT);
			info.setIndentationRight(30f);
			info.setSpacingBefore(30f);

			doc.add(info);
		} catch (DocumentException e) {
			logger.warn(e.getMessage());
		}

		// -------- ./ Información adicional ---------

		// -------- Decoración cúbica ---------
		PdfContentByte canvas = writer.getDirectContent();
		// state 1:
		canvas.setRGBColorFill(0xFF, 0x45, 0x00);
		// fill a rectangle in state 1
		canvas.rectangle(10, 60, 60, 60);
		canvas.fill();
		canvas.saveState();
		// state 2;
		canvas.setLineWidth(3);
		canvas.setRGBColorFill(0x8B, 0x00, 0x00);
		// fill and stroke a rectangle in state 2
		canvas.rectangle(40, 70, 60, 60);
		canvas.fillStroke();
		canvas.saveState();
		// state 3:
		canvas.concatCTM(1, 0, 0.1f, 1, 0, 0);
		canvas.setRGBColorStroke(0xFF, 0x45, 0x00);
		canvas.setRGBColorFill(0x20, 0x20, 0xFF);
		// fill and stroke a rectangle in state 3
		canvas.rectangle(70, 80, 60, 60);
		canvas.fillStroke();
		canvas.restoreState();
		// stroke a rectangle in state 2
		canvas.rectangle(100, 90, 60, 60);
		canvas.stroke();
		canvas.restoreState();
		// fill and stroke a rectangle in state 1
		canvas.setRGBColorFill(0x00, 0x00, 0xAA);
		canvas.rectangle(130, 100, 60, 60);
		canvas.fillStroke();
	}

	/*
	 * -------------------- Clase FooterNHeaderRenderer
	 * -------------------------------
	 */
	class FooterNHeaderRenderer extends PdfPageEventHelper {
		Font ffont = new Font(Font.FontFamily.COURIER, 8);
		private String path;

		public void setPath(String path) {
			this.path = path;
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			renderHeader(writer, document, cb);
			renderFooter(writer, document, cb);
		}

		private void renderHeader(PdfWriter writer, Document document, PdfContentByte cb) {

			// Add an image
			String logoName = "logo.jpg";
			Image logo = null;
			try {
				logo = Image.getInstance(this.path + logoName);
				logo.scaleToFit(184, 40);
				logo.setAbsolutePosition(document.leftMargin(), document.top() + 5);
				// document.add(logo);
				cb.addImage(logo, true);

				cb.setColorStroke(BaseColor.BLUE);
				cb.moveTo(document.leftMargin(), document.top());
				cb.lineTo(document.right() - document.rightMargin(), document.top());
				cb.closePathStroke();

			} catch (IOException | DocumentException e) {
				logger.error(e.getMessage());
			}
		}

		private void renderFooter(PdfWriter writer, Document document, PdfContentByte cb) {

			cb.setRGBColorFill(0x00, 0x00, 0x00);

			String pagoString = "El pago de los importes se realizará en el momento de la realización del tratamiento. ";
			Phrase pago = new Phrase(pagoString, ffont);

			Chunk dudas = new Chunk("Para cualquier aclaración no dude en llamar al teléfono ", ffont);
			Chunk telefono = new Chunk("91 689 00 70 ", ffont);
			Phrase llamar = new Phrase();
			llamar.add(dudas);
			llamar.add(telefono);
			Paragraph footer = new Paragraph();
			footer.add(llamar);

			ColumnText.showTextAligned(cb, Element.ALIGN_JUSTIFIED, pago, document.leftMargin(), document.bottom() - 20,
					0);
			ColumnText.showTextAligned(cb, Element.ALIGN_JUSTIFIED, footer, document.leftMargin(),
					document.bottom() - 40, 0);

			cb.setColorStroke(BaseColor.BLUE);
			cb.moveTo(document.leftMargin(), document.bottom() - 30);
			cb.lineTo(document.right() - document.rightMargin(), document.bottom() - 30);
			cb.closePathStroke();
		}
	}

}
