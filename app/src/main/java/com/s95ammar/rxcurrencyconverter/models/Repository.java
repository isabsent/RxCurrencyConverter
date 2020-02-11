package com.s95ammar.rxcurrencyconverter.models;

import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ApiService;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ConversionResponse;
import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class Repository {
	private final String t = "log_" + getClass().getSimpleName();

	private CurrencyDao dao;
	private ApiService api;

	@Inject
	public Repository(CurrencyDao dao, ApiService api) {
		this.dao = dao;
		this.api = api;
	}

//	API

	public Single<ConversionResponse> getRatesOf(String from) {
		return api.getRatesOf(from);
	}

	public Single<ConversionResponse> getRate(String from, String to) {
		return api.getRate(from, to);
	}

//	DAO

	public Completable insertCurrency(Currency currency) {
		return dao.insertCurrency(currency);
	}

	public Completable insertCurrencies(List<Currency> currencies) {
		return dao.insertCurrencies(currencies);
	}

	public Completable updateCurrency(Currency currency) {
		return dao.updateCurrency(currency);
	}

	public Single<Currency> getCurrencyByCode(String code) {
		return dao.getCurrencyByCode(code);
	}

	public Single<List<Currency>> getAllCurrencies() {
		return dao.getAllCurrencies();
	}



}
