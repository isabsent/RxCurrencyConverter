package com.s95ammar.rxcurrencyconverter.viewModels;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

	private SingleLiveEvent<Result> onDatabaseUpdate = new SingleLiveEvent<>();
	private SingleLiveEvent<Result> onOfflineDataChecked = new SingleLiveEvent<>();

	private MutableLiveData<List<String>> spinnersList = new MutableLiveData<>();

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
		populateDatabaseFromApi();
	}

	private void populateDatabaseFromApi() {
		onDatabaseUpdate.setValue(Result.loading());
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
						List<Currency> currencies = new ArrayList<>(conversionResponse.getRates().size());
						for (Map.Entry<String, ConversionResponse.TargetCurrency> entry : conversionResponse.getRates().entrySet())
							currencies.add(new Currency(
									entry.getKey(),
									entry.getValue().getCurrencyName(),
									entry.getValue().getRate(),
									System.currentTimeMillis()
							));

						populateDataBase(currencies);

					}

					@Override
					public void onError(Throwable e) {
						Log.d(t, "onError: " + e.getLocalizedMessage());
						onDatabaseUpdate.postValue(Result.error(e.getLocalizedMessage()));
						checkOfflineData();
					}
				});
	}

	private void populateDataBase(List<Currency> currencies) {
		disposables.add(insertCurrencies(currencies)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> {
							onDatabaseUpdate.setValue(Result.success());
							spinnersList.setValue(getCurrenciesNamesList(currencies));
						}
				));
	}


	private void checkOfflineData() {
		onOfflineDataChecked.setValue(Result.loading());
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
							onOfflineDataChecked.setValue(Result.error("empty list")); // TODO: USE CONSTANT OR StringRes
						else {
							onOfflineDataChecked.setValue(Result.success());
							spinnersList.setValue(getCurrenciesNamesList(currencies));
						}
					}

					@Override
					public void onError(Throwable e) {
						onOfflineDataChecked.setValue(Result.error(e.getLocalizedMessage()));
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

	public void convert(String fromCode, String toCode, double amount) {
		Single<ConversionResponse.TargetCurrency> singleUsdRateOfFrom =
				repository.getRate(USD, fromCode).map(conversionResponse -> conversionResponse.getRates().get(fromCode));
		Single<ConversionResponse.TargetCurrency> singleUsdRateOfTo =
				repository.getRate(USD, toCode).map(conversionResponse -> conversionResponse.getRates().get(toCode));

		Single.zip(
				singleUsdRateOfFrom,
				singleUsdRateOfTo,
				(targetCurrencyFrom, targetCurrencyTo) -> {
					Currency currency1 =
							new Currency(fromCode, targetCurrencyFrom.getCurrencyName(), targetCurrencyFrom.getRate(), System.currentTimeMillis());
					Currency currency2 =
							new Currency(toCode, targetCurrencyTo.getCurrencyName(), targetCurrencyTo.getRate(), System.currentTimeMillis());
					return Pair.create(currency1, currency2);
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<Pair<Currency, Currency>>() {
					@Override
					public void onSubscribe(Disposable d) {
						disposables.add(d);
					}

					@Override
					public void onSuccess(Pair<Currency, Currency> pair) {
//						TODO
						Log.d(t, "onSuccess: " + pair);
						Log.d(t, "onSuccess: result = " + pair.second.getUsdRate() / pair.first.getUsdRate() * amount);

						repository.updateCurrency(pair.first);
						repository.updateCurrency(pair.second);
					}

					@Override
					public void onError(Throwable e) {
//						TODO
					}
				});

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

	public LiveData<Result> getOnDatabaseUpdate() {
		return onDatabaseUpdate;
	}

	public LiveData<Result> getOnOfflineDataChecked() {
		return onOfflineDataChecked;
	}

	public LiveData<List<String>> getSpinnersList() {
		return spinnersList;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		disposables.clear();
	}
}
