package com.s95ammar.rxcurrencyconverter.di.app;

import com.s95ammar.rxcurrencyconverter.models.retrofit.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.s95ammar.rxcurrencyconverter.util.Util.BASE_URL;
import static com.s95ammar.rxcurrencyconverter.util.Util.HEADER_HOST;
import static com.s95ammar.rxcurrencyconverter.util.Util.HEADER_KEY;

@Module
public abstract class RetrofitModule {

	@Singleton
	@Provides
	public static OkHttpClient provideOkHttpClient() {
		return new OkHttpClient.Builder()
				.addInterceptor(chain -> {
					Request authorisedRequest = chain.request().newBuilder()
							.addHeader(HEADER_HOST.getKey(), HEADER_HOST.getValue())
							.addHeader(HEADER_KEY.getKey(), HEADER_KEY.getValue())
							.build();
					return chain.proceed(authorisedRequest);
				})
				.build();
	}

	@Singleton
	@Provides
	public static ApiService provideApiService(OkHttpClient okHttpClient) {
		return new Retrofit.Builder().baseUrl(BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(okHttpClient)
				.build()
				.create(ApiService.class);
	}

}
