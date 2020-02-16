package com.s95ammar.rxcurrencyconverter.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.models.Repository;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.models.data.Conversion;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.s95ammar.rxcurrencyconverter.util.Util.BLANK;

public class MainViewModel extends ViewModel {

	private Repository repository;

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
	}

	public Observable<Result<List<Currency>>> getUsdRatesToAll() {
		return repository.getUsdRatesToAll();
	}

	public Observable<Result<Conversion>> getRate(String from, String to, double amount) {
		return repository.getRate(from, to, amount);
	}

	public List<String> getCurrenciesNamesList(@NonNull List<Currency> currencies) {
		List<String> names = new ArrayList<>(currencies.size());
		names.add(BLANK);
		for (int i = 0; i < currencies.size(); i++) {
			Currency currency = currencies.get(i);
			names.add(currency.getCode() + " - " + currency.getName());
		}
		return names;
	}

}
