package com.s95ammar.rxcurrencyconverter;

import com.s95ammar.rxcurrencyconverter.di.app.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class App extends DaggerApplication {

	@Override
	protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
		return DaggerAppComponent.factory().create(this);
	}
}
