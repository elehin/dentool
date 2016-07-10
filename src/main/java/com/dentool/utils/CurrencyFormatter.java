package com.dentool.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {

	public static String format(float ammount) {

		Locale locale = new Locale("es", "ES");
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

		return currencyFormatter.format(ammount);
	}
}
