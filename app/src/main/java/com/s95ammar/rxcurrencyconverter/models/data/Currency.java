package com.s95ammar.rxcurrencyconverter.models.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Currency {

	@PrimaryKey
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
