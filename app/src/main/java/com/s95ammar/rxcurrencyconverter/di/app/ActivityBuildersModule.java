package com.s95ammar.rxcurrencyconverter.di.app;

import android.app.Activity;

import com.s95ammar.rxcurrencyconverter.di.viewModelModels.MainViewModelModule;
import com.s95ammar.rxcurrencyconverter.views.activities.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

	@ContributesAndroidInjector(modules = MainViewModelModule.class)
	public abstract MainActivity contributeMainActivity();
}
