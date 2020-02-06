package com.s95ammar.rxcurrencyconverter.models;

import android.util.Log;

import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ApiService;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ConversionResponse;
import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDao;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
		populateDatabaseFromApi();
	}

	private void populateDatabaseFromApi() {
		Log.d(t, "populateDatabaseFromApi: ");
		api.getRatesOf(USD)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new SingleObserver<ConversionResponse>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onSuccess(ConversionResponse conversionResponse) {
						for (Map.Entry<String, ConversionResponse.TargetCurrency> entry : conversionResponse.getRates().entrySet()) {
							ConversionResponse.TargetCurrency targetCurrency = entry.getValue();
							Currency currency = new Currency(
									entry.getKey(),
									targetCurrency.getCurrencyName(),
									targetCurrency.getRate(),
									System.currentTimeMillis()
							);
							dao.insertCurrency(currency).subscribe();
						}
					}

					@Override
					public void onError(Throwable e) {
						Log.d(t, "onError: " + e.getLocalizedMessage());
					}
				});
	}

	public Single<List<Currency>> getAllCurrencies() {
		return dao.getAllCurrencies();
	}



}
