package com.s95ammar.rxcurrencyconverter.models;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import io.reactivex.Completable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.s95ammar.rxcurrencyconverter.util.Util.isAnEmptyCollection;

abstract class NetworkBoundResource<LocalType, RemoteType> {

	private CompositeDisposable disposables = new CompositeDisposable();
	private ObservableEmitter<Result<LocalType>> emitter;

	public NetworkBoundResource(ObservableEmitter<Result<LocalType>> emitter) {
		this.emitter = emitter;
		onStart();
	}

	private void onStart() {
		emitter.onNext(Result.loading());
		createCall()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new SingleObserver<RemoteType>() {
					@Override
					public void onSubscribe(Disposable d) {
						disposables.add(d);
					}

					@Override
					public void onSuccess(RemoteType response) {
						storeThenPass(response);
					}

					@Override
					public void onError(Throwable e) {
						tryLoadOfflineData();
					}
				});
	}

	private void storeThenPass(RemoteType response) {
		disposables.add(
				saveCallResult(response)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(() -> disposables.add(
								loadFromDb()
										.subscribeOn(Schedulers.io())
										.observeOn(Schedulers.io())
										.map(Result::success)
										.subscribe(value -> {
											emitter.onNext(value);
											onFinished();
										})
						)));
	}

	private void tryLoadOfflineData() {
		loadFromDb()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(new SingleObserver<LocalType>() {
					@Override
					public void onSubscribe(Disposable d) {
						disposables.add(d);
					}

					@Override
					public void onSuccess(LocalType data) {
						emitter.onNext(isAnEmptyCollection(data) ? Result.error("missing data") : Result.warning(data, "non-fresh data"));
						onFinished();
					}

					@Override
					public void onError(Throwable e) {
						emitter.onNext(Result.error(e.getLocalizedMessage()));
						onFinished();
					}
				});
	}

	private void onFinished() {
		emitter.onComplete();
		disposables.clear();
	}

	@MainThread
	protected abstract Single<RemoteType> createCall();

	@WorkerThread
	protected abstract Completable saveCallResult(RemoteType response);

	@MainThread
	protected abstract Single<LocalType> loadFromDb();

}