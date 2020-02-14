package com.s95ammar.rxcurrencyconverter.models;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

abstract class NetworkBoundResource<LocalType, RemoteType> {
	private final String t = "log_NBR";

//	TODO: handle disposables
	public NetworkBoundResource(ObservableEmitter<Result<LocalType>> emitter) {
		emitter.onNext(Result.loading());
		createCall()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<RemoteType>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onSuccess(RemoteType response) {
						saveCallResult(response)
								.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(
										new Action() {
											@Override
											public void run() throws Exception {
												loadFromDb()
														.subscribeOn(Schedulers.io())
														.observeOn(AndroidSchedulers.mainThread())
														.map(data -> Result.success(data))
														.subscribe(value -> {
															emitter.onNext(value);
															emitter.onComplete();
														});
											}
										}
								);
					}

					@Override
					public void onError(Throwable e) {
						loadFromDb()
								.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(new SingleObserver<LocalType>() {
									@Override
									public void onSubscribe(Disposable d) {

									}

									@Override
									public void onSuccess(LocalType data) {
										if (isLocalDataMissing(data)) {
											emitter.onNext(Result.error("missing data"));
										} else {
											emitter.onNext(Result.warning(data, "not fresh data"));
										}
										emitter.onComplete();
									}

									@Override
									public void onError(Throwable e) {
										emitter.onNext(Result.error(e.getLocalizedMessage()));
										emitter.onComplete();
									}
								});
					}
				});

	}


	@MainThread
	protected abstract Single<RemoteType> createCall();

	@WorkerThread
	protected abstract Completable saveCallResult(RemoteType response);

	@MainThread
	protected abstract Single<LocalType> loadFromDb();

	private boolean isLocalDataMissing(LocalType data) {
		return (data == null) || (data instanceof List && ((List) data).isEmpty());
	}

}