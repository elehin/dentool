package com.dentool.utils;

import com.dentool.model.Paciente;

public class Utils {

	/**
	 * Función que elimina acentos y caracteres especiales de una cadena de
	 * texto.
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

	public static void copyPaciente(Paciente origen, Paciente destino) {
		destino.setName(origen.getName());
		destino.setApellidos(origen.getApellidos());
		destino.setTelefono(origen.getTelefono());
		destino.setDireccion(origen.getDireccion());
		destino.setAlergias(origen.getAlergias());
		destino.setFechaNacimiento(origen.getFechaNacimiento());
	}

}
