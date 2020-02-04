package com.s95ammar.rxcurrencyconverter.viewModels;

import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.models.Repository;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {
	private Repository repository;

	@Inject
	public MainViewModel(Repository repository) {
		this.repository = repository;
	}
}
