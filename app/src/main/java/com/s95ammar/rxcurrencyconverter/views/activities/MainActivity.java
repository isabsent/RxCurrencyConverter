package com.s95ammar.rxcurrencyconverter.views.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.models.data.Conversion;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.s95ammar.rxcurrencyconverter.util.Constants.BLANK;
import static com.s95ammar.rxcurrencyconverter.util.Constants.CURRENCY_CODE_LENGTH;
import static com.s95ammar.rxcurrencyconverter.util.Constants.KEY_SPINNER_FROM_POSITION;
import static com.s95ammar.rxcurrencyconverter.util.Constants.KEY_SPINNER_TO_POSITION;
import static com.s95ammar.rxcurrencyconverter.util.Constants.SINGLE_UNIT;

public class MainActivity extends DaggerAppCompatActivity {
	private final String t = "log_" + getClass().getSimpleName();

	private CompositeDisposable disposables = new CompositeDisposable();

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

	@BindView(R.id.textView_result_value)
	TextView textViewResultValue;

	@BindView(R.id.textView_exchange_rate_value)
	TextView textViewExRateValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
		ButterKnife.bind(this);
//		TODO: handle configuration changes
		disposables.add(viewModel.getRatesOfUsd()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleRatesResult));
	}

	private void handleRatesResult(Result<List<Currency>> result) {
		switch (result.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				setUpSpinners(viewModel.getCurrenciesNamesList(result.data));
				break;
			case WARNING:
				setLoading(false);
				setUpSpinners(viewModel.getCurrenciesNamesList(result.data));
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
		spinnerTo.setEnabled(!isLoading);

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

	@OnClick(R.id.button_convert)
	void convert() {
		String from = ((TextView) spinnerFrom.getSelectedView().findViewById(R.id.textView_spinner)).getText().toString();
		String to = ((TextView) spinnerTo.getSelectedView().findViewById(R.id.textView_spinner)).getText().toString();
		String input = editTextAmount.getText().toString();
		double amount = (input.isEmpty() ? SINGLE_UNIT : Double.valueOf(input));

		if (!from.equals(BLANK) && !to.equals(BLANK)) {
			disposables.add(viewModel.getRate(from, to, amount)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(this::handleConversionResult)
			);
		}
	}

	private void handleConversionResult(Result<Conversion> conversionResult) {
		switch (conversionResult.status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				displayConversionResult(conversionResult.data);
				break;
			case WARNING:
				setLoading(false);
				displayConversionResult(conversionResult.data);
				showOutOfDateWarning();
				break;
			case ERROR:
				setLoading(false);
				showDataMissingError();
				break;
		}
	}

	private void displayConversionResult(Conversion conversion) {
		findViewById(R.id.textView_result).setVisibility(conversion.getAmount() == SINGLE_UNIT ? View.GONE : View.VISIBLE);
		textViewResultValue.setVisibility(conversion.getAmount() == SINGLE_UNIT ? View.GONE : View.VISIBLE);
		findViewById(R.id.textView_exchange_rate).setVisibility(View.VISIBLE);
		textViewExRateValue.setVisibility(View.VISIBLE);
		textViewResultValue.setText(conversion.getConversionResultDescription());
		textViewExRateValue.setText(conversion.getExchangeRateDescription());
	}

	@Override
	protected void onDestroy() {
		disposables.clear();
		super.onDestroy();
	}

}
