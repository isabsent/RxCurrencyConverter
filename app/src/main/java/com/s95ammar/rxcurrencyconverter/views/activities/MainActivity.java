package com.s95ammar.rxcurrencyconverter.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {
	private final String t = "log_" + getClass().getSimpleName();

	@Inject
	ViewModelProvider.Factory factory;
	MainViewModel viewModel;

	@BindView(R.id.spinner_from)
	Spinner spinnerFrom;

	@BindView(R.id.spinner_to)
	Spinner spinnerTo;

	@BindView((R.id.progressBar))
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
		ButterKnife.bind(this);
		viewModel.getOnDatabasePopulation().observe(this, this::observeDatabasePopulation);
	}

	private void observeDatabasePopulation(Result<List<String>> result) {
		switch (result.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				setUpSpinners(result.data);
				break;

			case ERROR:
				setLoading(false);
				viewModel.checkSavedData();
				viewModel.getOnSavedDataChecked().observe(this, this::observeSavedDataCheck);
				break;
		}
	}

	private void observeSavedDataCheck(Result<List<String>> result) {
		switch (result.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				setUpSpinners(result.data);
//				TODO: SHOW WARNING
				break;
			case ERROR:
				setLoading(false);
//				TODO: SHOW ERROR
				break;
		}

	}

	private void setUpSpinners(List<String> spinnerRows) {
		spinnerFrom.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
		spinnerTo.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
	}

	private void setLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
	}

}
