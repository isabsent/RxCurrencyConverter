package com.s95ammar.rxcurrencyconverter.viewModels.helperClasses;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {

	private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creatorsMap;

	@Inject
	public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creatorsMap) {
		this.creatorsMap = creatorsMap;
	}

	@NonNull
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) creatorsMap.get(modelClass).get();

	}
}
