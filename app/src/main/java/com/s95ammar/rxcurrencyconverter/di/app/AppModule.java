package com.s95ammar.rxcurrencyconverter.di.app;


import android.app.Application;

import com.s95ammar.rxcurrencyconverter.App;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppModule {

	@Binds
	abstract Application bindApplication(App app);
}
