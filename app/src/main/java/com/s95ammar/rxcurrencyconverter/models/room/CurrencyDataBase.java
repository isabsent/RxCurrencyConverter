package com.s95ammar.rxcurrencyconverter.models.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.s95ammar.rxcurrencyconverter.models.data.Currency;

@Database(entities = Currency.class, version = 1)
public abstract class CurrencyDataBase extends RoomDatabase {
	public abstract CurrencyDao getCurrencyDao();
}
