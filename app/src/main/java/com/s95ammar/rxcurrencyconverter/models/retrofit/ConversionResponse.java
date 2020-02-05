package com.s95ammar.rxcurrencyconverter.models.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getBaseCurrencyCode() {
		return baseCurrencyCode;
	}

	public void setBaseCurrencyCode(String baseCurrencyCode) {
		this.baseCurrencyCode = baseCurrencyCode;
	}

	public String getBaseCurrencyName() {
		return baseCurrencyName;
	}

	public void setBaseCurrencyName(String baseCurrencyName) {
		this.baseCurrencyName = baseCurrencyName;
	}

	public Map<String, TargetCurrency> getRates() {
		return rates;
	}

	public void setRates(Map<String, TargetCurrency> rates) {
		this.rates = rates;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "ConversionResponse{" +
				"amount=" + amount +
				", baseCurrencyCode='" + baseCurrencyCode + '\'' +
				", baseCurrencyName='" + baseCurrencyName + '\'' +
				", rates=" + rates +
				", status='" + status + '\'' +
				", updatedDate='" + updatedDate + '\'' +
				'}';
	}

	static class TargetCurrency {
		@SerializedName("currency_name")
		private String currencyName;

		private double rate;

		@SerializedName("rate_for_amount")
		private double rateForAmount;

		public String getCurrencyName() {
			return currencyName;
		}

		public void setCurrencyName(String currencyName) {
			this.currencyName = currencyName;
		}

		public double getRate() {
			return rate;
		}

		public void setRate(double rate) {
			this.rate = rate;
		}

		public double getRateForAmount() {
			return rateForAmount;
		}

		public void setRateForAmount(double rateForAmount) {
			this.rateForAmount = rateForAmount;
		}

		@Override
		public String toString() {
			return "currency{" +
					"currencyName='" + currencyName + '\'' +
					", rate=" + rate +
					", rateForAmount=" + rateForAmount +
					'}';
		}
	}
}
