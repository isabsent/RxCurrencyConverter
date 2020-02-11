package com.s95ammar.rxcurrencyconverter.views.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
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

	@BindView(R.id.editText_amount)
	EditText editTextAmount;

	@BindView(R.id.progressBar)
	ProgressBar progressBar;

	@BindView(R.id.textView_warning_error)
	TextView tvWarningError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
		ButterKnife.bind(this);
		viewModel.getOnDatabaseUpdate().observe(this, this::observeDatabaseUpdate);
		viewModel.getSpinnersList().observe(this, this::setUpSpinners);
	}

	private void observeDatabaseUpdate(Result result) {
		Log.d(t, "observeDatabaseUpdate: " + result.status);
		switch (result.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				break;
			case ERROR:
				setLoading(false);
				viewModel.getOnOfflineDataChecked().observe(this, this::observeOfflineDataCheck);
				break;
		}
	}

	private void observeOfflineDataCheck(Result result) {
		switch (result.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				showOutOfDateWarning();
				break;
			case ERROR:
				setLoading(false);
				showDataMissingError();
				break;
		}

	}

	private void setLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
		editTextAmount.setEnabled(!isLoading);
		spinnerFrom.setEnabled(!isLoading);
		spinnerFrom.setEnabled(!isLoading);

	}

	private void setUpSpinners(List<String> spinnerRows) {
		if (!spinnerRows.isEmpty()) {
			spinnerFrom.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
			spinnerTo.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
		}
	}

	@OnItemSelected({R.id.spinner_from, R.id.spinner_to})
	void getSpinnersOnItemSelectedListener(AdapterView<?> parent, View view) {
		String selection = (String) parent.getSelectedItem();
		if (view != null && !selection.equals(BLANK))
			((TextView) view.findViewById(R.id.textView_spinner)).setText(selection.substring(0, CURRENCY_CODE_LENGTH));
	}

	private void showOutOfDateWarning() {
		tvWarningError.setVisibility(View.VISIBLE);
		tvWarningError.setTextColor(ContextCompat.getColor(this, R.color.colorWarning));
		tvWarningError.setText(R.string.warning_message);
	}

	private void showDataMissingError() {
		tvWarningError.setVisibility(View.VISIBLE);
		tvWarningError.setTextColor(ContextCompat.getColor(this, R.color.colorError));
		tvWarningError.setText(R.string.error_message);
	}

	private String getFromCode() {
		return ((TextView) spinnerFrom.getSelectedView().findViewById(R.id.textView_spinner)).getText().toString();
	}

	private String getToCode() {
		return ((TextView) spinnerTo.getSelectedView().findViewById(R.id.textView_spinner)).getText().toString();
	}

	@OnClick(R.id.button_convert)
	void convert() {
		String input = editTextAmount.getText().toString();
		viewModel.getCurrencyByCode(getFromCode()); // TODO: observe update
		viewModel.getCurrencyByCode(getToCode()); // TODO: observe update
		viewModel.convert(getFromCode(), getToCode(), (input.isEmpty() ? 1.0 : Double.valueOf(input)));
	}

}
