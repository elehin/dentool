package com.dentool.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	/**
	 * Función que elimina acentos y caracteres especiales de una cadena de texto.
	 * 
	 * @param input
	 * @return cadena de texto limpia de acentos y caracteres especiales.
	 */
	public static String removeTildes(String input) {
		// Cadena de caracteres original a sustituir.
		String original = "áàäéèëíìïóòöúùuç";
		// Cadena de caracteres ASCII que reemplazarán los originales.
		String ascii = "aaaeeeiiiooouuuc";
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			// Reemplazamos los caracteres especiales.
			output = output.replace(original.charAt(i), ascii.charAt(i));
		} // for i
		return output;
	}

	// public static void copyPaciente(Paciente origen, Paciente destino) {
	// destino.setName(origen.getName());
	// destino.setApellidos(origen.getApellidos());
	// destino.setTelefono(origen.getTelefono());
	// destino.setDireccion(origen.getDireccion());
	// destino.setNotas(origen.getNotas());
	// destino.setFechaNacimiento(origen.getFechaNacimiento());
	// destino.setAlergico(origen.isAlergico());
	// }

	public static String md5Hash(String input) {
		byte[] bytesOfMessage;
		byte[] thedigest = null;
		try {
			bytesOfMessage = input.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			thedigest = md.digest(bytesOfMessage);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String result = new String(thedigest, StandardCharsets.UTF_8);
		return result;
	}

	public static String getCurrentFormattedDate() {

		Calendar cal = Calendar.getInstance();

		int dia = cal.get(Calendar.DATE);
		String sDia;
		if (dia < 10) {
			sDia = "0" + dia;
		} else {
			sDia = String.valueOf(dia);
		}

		int month = cal.get(Calendar.MONTH) + 1;
		String sMonth;
		if (month < 10) {
			sMonth = "0" + month;
		} else {
			sMonth = String.valueOf(month);
		}
		String year = String.valueOf(cal.get(Calendar.YEAR));

		return sDia + "-" + sMonth + "-" + year;
	}

	public static String formatAsCurrency(float ammount) {
		return CurrencyFormatter.format(ammount);
	}

	public static String formatAsPorcentaje(float ammount) {
		DecimalFormat formateador = new DecimalFormat("####.##%");
		return formateador.format(ammount);
	}

	public static String formatAsFecha(Date date) {
		return Utils.formatAsFecha(date, Utils.getDefaultTimezone().getDisplayName());
	}

	public static String formatAsFecha(Date date, String timezone) {
		Calendar cal = null;
		if (timezone == null) {
			cal = Calendar.getInstance(Utils.getDefaultTimezone());
		} else {
			cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		}
		cal.setTime(date);
		String dia = String.valueOf(cal.get(Calendar.DATE));
		String mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
		if (Integer.parseInt(mes) < 10) {
			mes = "0" + mes;
		}
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String fecha = dia + "-" + mes + "-" + year;

		return fecha;
	}

	public static String capitalize(String string) {
		return StringUtils.capitalize(string);
	}

	public static Calendar setInicioDia(Calendar c) {
		if (c == null) {
			return null;
		}
		c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));

		return c;
	}

	public static Calendar setFinDia(Calendar c) {
		if (c == null) {
			return null;
		}

		c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND));

		return c;
	}

	public static TimeZone getDefaultTimezone() {
		return TimeZone.getTimeZone("Europe/Madrid");
	}

	public static String getFileStoragePath() {
		String path = System.getenv("HOME");

		if (path == null) {
			path = "C:/Users/Vane/Documents/dentoolFiles/";
		} else if (path.equals("/home/wildfly")) {
			path = "/home/bitnami/";
		}

		return path;
	}
}
