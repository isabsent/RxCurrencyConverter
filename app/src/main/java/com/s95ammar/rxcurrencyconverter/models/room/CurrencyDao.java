package com.s95ammar.rxcurrencyconverter.models.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.s95ammar.rxcurrencyconverter.models.data.Currency;

@Dao
public interface CurrencyDao {

	@Insert
	void insertCurrency(Currency currency);

	@Update
	void updateCurrency(Currency currency);

	@Query("SELECT * FROM currency WHERE code = :code")
	LiveData<Currency> getCurrencyByCode(String code);
}
