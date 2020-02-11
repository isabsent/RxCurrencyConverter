package com.s95ammar.rxcurrencyconverter.models.retrofit;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

	@GET("convert")
	Single<ConversionResponse> convert(
			@Query("from") String from,
			@Query("to") String to,
			@Query("amount") int amount
	);

	@GET("convert")
	Single<ConversionResponse> getRate(
			@Query("from") String from,
			@Query("to") String to
	);

	@GET("convert")
	Single<ConversionResponse> getRatesOf(
			@Query("from") String from
	);


}
