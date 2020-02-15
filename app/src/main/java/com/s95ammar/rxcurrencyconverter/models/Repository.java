package com.s95ammar.rxcurrencyconverter.models;


import com.s95ammar.rxcurrencyconverter.models.data.Conversion;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ApiService;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ConversionResponse;
import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.s95ammar.rxcurrencyconverter.util.Constants.USD;

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

	public Observable<Result<List<Currency>>> getUsdRatesToAll() {
		return Observable.create(emitter -> new NetworkBoundResource<List<Currency>, ConversionResponse>(emitter) {
			@Override
			protected Single<ConversionResponse> createCall() {
				return api.getRatesToAll(USD);
			}

			@Override
			protected Completable saveCallResult(ConversionResponse response) {
				return dao.insertCurrencies(response.toCurrenciesListByUsd());
			}

			@Override
			protected Single<List<Currency>> loadFromDb() {
				return dao.getAllCurrencies();
			}
		});
	}

	public Observable<Result<Conversion>> getRate(String from, String to, double amount) {

		return Observable.zip(
				getUsdRateTo(from),
				getUsdRateTo(to),
				(resultOrigin, resultDestination) -> {
					if (resultOrigin.status == Result.Status.LOADING || resultDestination.status == Result.Status.LOADING) {
						return Result.loading();
					} else if (resultOrigin.status == Result.Status.SUCCESS && resultDestination.status == Result.Status.SUCCESS) {
						return Result.success(
								new Conversion(from, to, amount, resultDestination.data.getUsdRate() / resultOrigin.data.getUsdRate())
						);
					} else if (resultOrigin.status == Result.Status.WARNING || resultDestination.status == Result.Status.WARNING) {
						return Result.warning(
								new Conversion(from, to, amount, resultDestination.data.getUsdRate() / resultOrigin.data.getUsdRate()),
								resultOrigin.message
						);
					} else {
						return Result.error(resultDestination.message);
					}
				}
		);
	}

	private Observable<Result<Currency>> getUsdRateTo(String code) {
		return Observable.create(emitter ->
				new NetworkBoundResource<Currency, ConversionResponse>(emitter) {

					@Override
					protected Single<ConversionResponse> createCall() {
						return api.getRate(USD, code);
					}

					@Override
					protected Completable saveCallResult(ConversionResponse response) {
						return dao.insertCurrency(response.toSingleCurrencyByUsd());
					}

					@Override
					protected Single<Currency> loadFromDb() {
						return dao.getCurrencyByCode(code);
					}
				});
	}

}
