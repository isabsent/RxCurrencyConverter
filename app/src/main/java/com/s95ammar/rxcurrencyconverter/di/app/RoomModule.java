package com.s95ammar.rxcurrencyconverter.di.app;

import android.app.Application;

import androidx.room.Room;

import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDao;
import com.s95ammar.rxcurrencyconverter.models.room.CurrencyDataBase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.s95ammar.rxcurrencyconverter.util.Util.DATABASE_NAME;

@Module
public abstract class RoomModule {
	@Singleton
	@Provides
	public static CurrencyDataBase provideCurrencyDataBase(Application application) {
		return Room.databaseBuilder(application.getApplicationContext(), CurrencyDataBase.class, DATABASE_NAME)
				.fallbackToDestructiveMigration()
				.build();
	}

	@Singleton
	@Provides
	public static CurrencyDao provideCurrencyDao(CurrencyDataBase currencyDataBase) {
		return currencyDataBase.getCurrencyDao();
	}

}
