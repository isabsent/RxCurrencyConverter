package com.s95ammar.rxcurrencyconverter.models;

import com.s95ammar.rxcurrencyconverter.models.retrofit.ApiService;
import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {
	CurrencyDao dao;
	ApiService api;

	@Inject
	public Repository(CurrencyDao dao, ApiService api) {
		this.dao = dao;
		this.api = api;
	}
}
