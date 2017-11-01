package com.dentool.rest.service.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dentool.model.entities.Factura;
import com.dentool.utils.Utils;

public class GeneradorInformeFacturacion {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String path;

	public GeneradorInformeFacturacion() {
		this.checkFilePath();
	}

	// TODO cambiar métodos deprecados de poi
	public File creaInforme(String titulo, String name, List<Factura> facturas) {
		String filename = this.path + name + ".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Clínicas OSLO");
		sheet.setDisplayGridlines(false);

		HSSFCellStyle currencyStyle = workbook.createCellStyle();
		HSSFDataFormat df = workbook.createDataFormat();
		currencyStyle.setDataFormat(df.getFormat("#,##0.00 €"));
		currencyStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		currencyStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		currencyStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		currencyStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		HSSFCellStyle defaultStyle = workbook.createCellStyle();
		defaultStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		defaultStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		defaultStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		defaultStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		HSSFFont headerFont = workbook.createFont();
		headerFont.setColor(HSSFColor.WHITE.index);

		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setFont(headerFont);

		HSSFFont logoFont = workbook.createFont();
		logoFont.setColor(HSSFColor.WHITE.index);
		logoFont.setFontHeightInPoints((short) 16);
		logoFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		HSSFCellStyle logoStyle = workbook.createCellStyle();
		logoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		logoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		logoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		logoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		logoStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		logoStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		logoStyle.setFont(logoFont);

		HSSFRow rowTitle = sheet.createRow((short) 1);
		Cell cellLogo = rowTitle.createCell(1, Cell.CELL_TYPE_STRING);
		cellLogo.setCellValue("Clínicas OSLO");
		cellLogo.setCellStyle(logoStyle);

		HSSFRow rowSubTitle = sheet.createRow((short) 3);
		rowSubTitle.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(titulo);

		List<String> headerStrings = new ArrayList<String>();
		headerStrings.add(0, "");
		headerStrings.add(1, "Número Factura");
		headerStrings.add(2, "Paciente");
		headerStrings.add(3, "NIF");
		headerStrings.add(4, "Fecha");
		headerStrings.add(5, "Importe");

		HSSFRow rowhead = sheet.createRow((short) 5);
		for (int i = 1; i <= 5; i++) {
			HSSFCell cell = rowhead.createCell(i, Cell.CELL_TYPE_STRING);
			cell.setCellValue(headerStrings.get(i));
			cell.setCellStyle(headerStyle);
			CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);
		}

		short rowNum = 6;
		for (Factura factura : facturas) {
			if (factura.getFileName() != null && !"".equals(factura.getFileName())) {
				HSSFRow row = sheet.createRow(rowNum);

				HSSFCell c1 = row.createCell(1, Cell.CELL_TYPE_STRING);
				c1.setCellValue(factura.getNumero());
				c1.setCellStyle(defaultStyle);

				HSSFCell c2 = row.createCell(2, Cell.CELL_TYPE_STRING);
				c2.setCellValue(factura.getNombreFactura());
				c2.setCellStyle(defaultStyle);

				HSSFCell c3 = row.createCell(3, Cell.CELL_TYPE_STRING);
				c3.setCellValue(factura.getNifFactura());
				c3.setCellStyle(defaultStyle);

				HSSFCell c4 = row.createCell(4, Cell.CELL_TYPE_STRING);
				Calendar cal = Calendar.getInstance();
				cal.setTime(factura.getFecha());
				c4.setCellValue(
						cal.get(Calendar.DATE) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
				c4.setCellStyle(defaultStyle);

				HSSFCell c5 = row.createCell(5);
				c5.setCellValue(factura.getImporte());
				c5.setCellStyle(currencyStyle);

				rowNum++;
			}
		}

		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}

		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filename);

			workbook.write(fileOut);
			fileOut.close();

			File f = new File(filename);
			return f;

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	private void checkFilePath() {
		this.path = Utils.getFileStoragePath() + "excel/";
		logger.info(this.path);
	}
}
