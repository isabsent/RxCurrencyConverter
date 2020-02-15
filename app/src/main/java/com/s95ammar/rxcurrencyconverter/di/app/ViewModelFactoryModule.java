package com.s95ammar.rxcurrencyconverter.di.app;

import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.viewModels.ViewModelFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

	@Binds
	public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
