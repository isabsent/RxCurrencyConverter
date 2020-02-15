package com.s95ammar.rxcurrencyconverter.models.data;

import java.text.DecimalFormat;

import static com.s95ammar.rxcurrencyconverter.util.Util.SINGLE_UNIT;

public class Conversion {
	private String fromCode;
	private String toCode;

	private double amount;

	private double exchangeRate;
	private long conversionTimeInMillis;

	private static final DecimalFormat FOUR_DECIMAL_PLACES = new DecimalFormat("#.####");

	public Conversion(String fromCode, String toCode, double amount, double exchangeRate) {
		this.fromCode = fromCode;
		this.toCode = toCode;
		this.amount = amount;
		this.exchangeRate = exchangeRate;
		conversionTimeInMillis = System.currentTimeMillis();
	}

	public double getAmount() {
		return amount;
	}

	public double getConversionResult() {
		return amount * exchangeRate;
	}

	public String getConversionResultDescription() {
		return amount + " " + fromCode + " = " + FOUR_DECIMAL_PLACES.format(getConversionResult()) + " " + toCode;
	}

	public String getExchangeRateDescription() {
		return SINGLE_UNIT + " " + fromCode + " = " + FOUR_DECIMAL_PLACES.format(exchangeRate) + " " + toCode;
	}

}
