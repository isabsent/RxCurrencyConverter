package com.s95ammar.rxcurrencyconverter.models.retrofit;

import com.google.gson.annotations.SerializedName;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.s95ammar.rxcurrencyconverter.util.Constants.USD;

public class ConversionResponse {

	private int amount;

	@SerializedName("base_currency_code")
	private String baseCurrencyCode;

	@SerializedName("base_currency_name")
	private String baseCurrencyName;

	private Map<String, TargetCurrency> rates;

	private String status;

	@SerializedName("updated_date")
	private String updatedDate;

	public Map<String, TargetCurrency> getRates() {
		return rates;
	}

	public List<Currency> toCurrenciesListByUsd() {
		if (!baseCurrencyCode.equals(USD))
			throw new RuntimeException("Response base currency must be " + USD + " for creating Currency objects. Found: " + baseCurrencyCode + ".");

		List<Currency> currencies = new ArrayList<>(rates.size());
		for (Map.Entry<String, ConversionResponse.TargetCurrency> entry : getRates().entrySet())
			currencies.add(new Currency(
					entry.getKey(),
					entry.getValue().getCurrencyName(),
					entry.getValue().getRate(),
					System.currentTimeMillis()
			));
		return currencies;
	}

	public Currency toSingleCurrencyByUsd() {
		if (rates.size() != 1)
			throw new RuntimeException("Response must contain rate for exactly one currency to create a single Currency object. Found: " + rates.size() + " rates.");
		List<Currency> currenciesList = toCurrenciesListByUsd();
		return currenciesList.get(0);
	}

	public static class TargetCurrency {
		@SerializedName("currency_name")
		private String currencyName;

		private double rate;

		@SerializedName("rate_for_amount")
		private double rateForAmount;

		public String getCurrencyName() {
			return currencyName;
		}

		public double getRate() {
			return rate;
		}

	}
}
