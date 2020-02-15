package com.s95ammar.rxcurrencyconverter.util;

import java.util.AbstractMap.SimpleEntry;

public class Constants {
	public static final String BASE_URL = "https://currency-converter5.p.rapidapi.com/currency/";
	public static final SimpleEntry<String, String> HEADER_HOST = new SimpleEntry<>("x-rapidapi-host", "currency-converter5.p.rapidapi.com");
	public static final SimpleEntry<String, String> HEADER_KEY = new SimpleEntry<>("x-rapidapi-key", "5463a50e64msha8b0f9b33d8f4edp121681jsn076c5988a5d9");

	public static final String DATABASE_NAME  = "currencies";

	public static final String USD = "USD";
	public static final String BLANK = "";
	public static final int CURRENCY_CODE_LENGTH = 3;
	public static final double SINGLE_UNIT = 1.0;

	public static final String KEY_SPINNER_FROM_POSITION = "spinner_from_position";
	public static final String KEY_SPINNER_TO_POSITION = "spinner_to_position";

}
