package com.s95ammar.rxcurrencyconverter.di.app;

import com.s95ammar.rxcurrencyconverter.App;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
		AndroidInjectionModule.class,
		ActivityBuildersModule.class,
		ViewModelFactoryModule.class,
		AppModule.class,
		RoomModule.class,
		RetrofitModule.class
})
public interface AppComponent extends AndroidInjector<App> {

	@Component.Factory
	interface Factory extends AndroidInjector.Factory<App> {
	}
}
