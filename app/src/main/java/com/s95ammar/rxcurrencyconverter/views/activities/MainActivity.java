package com.s95ammar.rxcurrencyconverter.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends DaggerAppCompatActivity {
	private final String t = "log_" + getClass().getSimpleName();

	@Inject
	ViewModelProvider.Factory factory;
	MainViewModel viewModel;

	@BindView(R.id.spinner_from)
	Spinner spinnerFrom;

	@BindView(R.id.spinner_to)
	Spinner spinnerTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
		ButterKnife.bind(this);
		setUpSpinners();
	}

	private void setUpSpinners() {
		final Context activity = this;
		viewModel.getAllCurrencies()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<List<Currency>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onSuccess(List<Currency> currencies) {
						Log.d(t, "onSuccess: " + currencies);
						spinnerFrom.setAdapter(new ArrayAdapter<>(activity, R.layout.spinner_row, currencies));
						spinnerTo.setAdapter(new ArrayAdapter<>(activity, R.layout.spinner_row, currencies));
					}

					@Override
					public void onError(Throwable e) {
						Log.d(t, "onError: " + e.getLocalizedMessage());
					}
				});
	}
}
