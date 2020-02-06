package com.s95ammar.rxcurrencyconverter.viewModels;

import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.models.Repository;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;

public class MainViewModel extends ViewModel {
	private Repository repository;

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
	}

	public Single<List<Currency>> getAllCurrencies() {
		return repository.getAllCurrencies();
	}

}
