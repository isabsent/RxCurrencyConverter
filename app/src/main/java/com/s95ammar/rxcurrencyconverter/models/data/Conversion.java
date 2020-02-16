package com.s95ammar.rxcurrencyconverter.models.data;

import static com.s95ammar.rxcurrencyconverter.util.Util.FOUR_DECIMALS_FORMAT;
import static com.s95ammar.rxcurrencyconverter.util.Util.SINGLE_UNIT;

public class Conversion {
	private String fromCode;
	private String toCode;

	private double amount;

	private double exchangeRate;
	private long conversionTimeInMillis;

	public Conversion(Currency currencyFrom, Currency currencyTo, double amount, long conversionTimeInMillis) {
		fromCode = currencyFrom.getCode();
		toCode = currencyTo.getCode();
		this.amount = amount;
		exchangeRate = currencyTo.getUsdRate() / currencyFrom.getUsdRate();
		this.conversionTimeInMillis = conversionTimeInMillis;
	}

	public double getAmount() {
		return amount;
	}

	public double getConversionResult() {
		return amount * exchangeRate;
	}

	public long getConversionTimeInMillis() {
		return conversionTimeInMillis;
	}

	public String getConversionResultDescription() {
		return amount + " " + fromCode + " = " + FOUR_DECIMALS_FORMAT.format(getConversionResult()) + " " + toCode;
	}

	public String getExchangeRateDescription() {
		return SINGLE_UNIT + " " + fromCode + " = " + FOUR_DECIMALS_FORMAT.format(exchangeRate) + " " + toCode;
	}

}
