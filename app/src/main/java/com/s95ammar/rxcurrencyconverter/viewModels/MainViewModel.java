package com.s95ammar.rxcurrencyconverter.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.models.Repository;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.models.retrofit.ConversionResponse;
import com.s95ammar.rxcurrencyconverter.viewModels.helperClasses.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.s95ammar.rxcurrencyconverter.util.Constants.BLANK;
import static com.s95ammar.rxcurrencyconverter.util.Constants.USD;

public class MainViewModel extends ViewModel {
	private final String t = "log_" + getClass().getSimpleName();

	private Repository repository;

	private CompositeDisposable disposables = new CompositeDisposable();

	private SingleLiveEvent<Result<List<String>>> onDatabasePopulation = new SingleLiveEvent<>();
	private SingleLiveEvent<Result<List<String>>> onSavedDataChecked = new SingleLiveEvent<>();

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
		populateDatabaseFromApi();
	}

	private void populateDatabaseFromApi() {
		onDatabasePopulation.setValue(Result.loading());
		getRatesOf(USD)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.computation())
				.subscribe(new SingleObserver<ConversionResponse>() {
					@Override
					public void onSubscribe(Disposable d) {
						disposables.add(d);
					}

					@Override
					public void onSuccess(ConversionResponse conversionResponse) {
						populateDataBase(conversionResponse.getRates());
					}

					@Override
					public void onError(Throwable e) {
						Log.d(t, "onError: " + e.getLocalizedMessage());
						onDatabasePopulation.postValue(Result.error(e.getLocalizedMessage()));
					}
				});
	}

	private void populateDataBase(Map<String, ConversionResponse.TargetCurrency> rates) {
		List<Currency> currencies = new ArrayList<>(rates.size());
		for (Map.Entry<String, ConversionResponse.TargetCurrency> entry : rates.entrySet())
			currencies.add(new Currency(
					entry.getKey(),
					entry.getValue().getCurrencyName(),
					entry.getValue().getRate(),
					System.currentTimeMillis()
			));
		disposables.add(insertCurrencies(currencies)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> onDatabasePopulation.setValue(Result.success(getCurrenciesNamesList(currencies)))));
	}


	public void checkSavedData() {
		onSavedDataChecked.setValue(Result.loading());
		getAllCurrencies()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<List<Currency>>() {
					@Override
					public void onSubscribe(Disposable d) {
						disposables.add(d);
					}

					@Override
					public void onSuccess(List<Currency> currencies) {
						if (currencies.isEmpty())
							onSavedDataChecked.setValue(Result.error("empty list")); // TODO: USE CONSTANT OR StringRes
						else
							onSavedDataChecked.setValue(Result.success(getCurrenciesNamesList(currencies)));
					}

					@Override
					public void onError(Throwable e) {
						onSavedDataChecked.setValue(Result.error(e.getLocalizedMessage()));
					}
				});
	}

	private List<String> getCurrenciesNamesList(List<Currency> currencies) {
		List<String> names = new ArrayList<>(currencies.size());
		names.add(BLANK);
		for (int i = 0; i < currencies.size(); i++) {
			Currency currency = currencies.get(i);
			names.add(currency.getCode() + " - " + currency.getName());
		}
		return names;
	}

//	API

	public Single<ConversionResponse> getRatesOf(String from) {
		return repository.getRatesOf(from);
	}

//	DAO

	public Completable insertCurrency(Currency currency) {
		return repository.insertCurrency(currency);
	}

	public Completable insertCurrencies(List<Currency> currencies) {
		return repository.insertCurrencies(currencies);
	}

	public Completable updateCurrency(Currency currency) {
		return repository.updateCurrency(currency);
	}

	public Single<Currency> getCurrencyByCode(String code) {
		return repository.getCurrencyByCode(code);
	}

	public Single<List<Currency>> getAllCurrencies() {
		return repository.getAllCurrencies();
	}


// Getters & setters

	public LiveData<Result<List<String>>> getOnDatabasePopulation() {
		return onDatabasePopulation;
	}

	public LiveData<Result<List<String>>> getOnSavedDataChecked() {
		return onSavedDataChecked;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		disposables.clear();
	}
}
