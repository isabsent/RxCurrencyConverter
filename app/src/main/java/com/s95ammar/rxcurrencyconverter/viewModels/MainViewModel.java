package com.s95ammar.rxcurrencyconverter.viewModels;

import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.models.Repository;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.models.data.Conversion;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.s95ammar.rxcurrencyconverter.util.Constants.BLANK;

public class MainViewModel extends ViewModel {
	private final String t = "log_" + getClass().getSimpleName();

	private Repository repository;

	private CompositeDisposable disposables = new CompositeDisposable();

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
	}

	public Observable<Result<List<Currency>>> getRatesOfUsd() {
		return repository.getRatesOfUsd();
	}

	public Observable<Result<Conversion>> getRate(String from, String to, double amount) {
		return repository.getRate(from, to, amount);
	}

	public List<String> getCurrenciesNamesList(@NotNull List<Currency> currencies) {
		List<String> names = new ArrayList<>(currencies.size());
		names.add(BLANK);
		for (int i = 0; i < currencies.size(); i++) {
			Currency currency = currencies.get(i);
			names.add(currency.getCode() + " - " + currency.getName());
		}
		return names;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		disposables.clear();
	}
}
