package com.s95ammar.rxcurrencyconverter.models.retrofit;

import java.util.Map;

public class CurrenciesResponse {

	private Map<String, String> currencies;

	public Map<String, String> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Map<String, String> currencies) {
		this.currencies = currencies;
	}

	@Override
	public String toString() {
		return "CurrenciesResponse{" +
				"currencies=" + currencies +
				'}';
	}
}
