package com.s95ammar.rxcurrencyconverter.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static com.s95ammar.rxcurrencyconverter.util.Constants.BLANK;
import static com.s95ammar.rxcurrencyconverter.util.Constants.CURRENCY_CODE_LENGTH;

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
		spinnerFrom.setOnItemSelectedListener(getSpinnersOnItemSelectedListener());
		spinnerTo.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
		spinnerTo.setOnItemSelectedListener(getSpinnersOnItemSelectedListener());
	}

	private void setLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
	}

	private AdapterView.OnItemSelectedListener getSpinnersOnItemSelectedListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selection = (String) parent.getSelectedItem();
				if (!selection.equals(BLANK))
					((TextView) view.findViewById(R.id.textView_spinner)).setText(selection.substring(0, CURRENCY_CODE_LENGTH));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}

	@OnClick(R.id.button_convert)
	void convert() {
		String fromCode = (String) spinnerFrom.getSelectedItem();
		String toCode = (String) spinnerTo.getSelectedItem();
		// TODO
	}

}
