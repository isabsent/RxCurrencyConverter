package com.s95ammar.rxcurrencyconverter.models.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.s95ammar.rxcurrencyconverter.models.data.Currency;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CurrencyDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Completable insertCurrency(Currency currency);

	@Update
	Completable updateCurrency(Currency currency);

	@Query("SELECT * FROM currencies WHERE code = :code")
	Single<Currency> getCurrencyByCode(String code);

	@Query("SELECT * FROM currencies")
	Single<List<Currency>> getAllCurrencies();
}
