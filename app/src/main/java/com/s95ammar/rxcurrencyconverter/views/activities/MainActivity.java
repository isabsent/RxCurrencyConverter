package com.s95ammar.rxcurrencyconverter.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.models.Result;
import com.s95ammar.rxcurrencyconverter.models.data.Conversion;
import com.s95ammar.rxcurrencyconverter.models.data.Currency;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.s95ammar.rxcurrencyconverter.util.Util.BLANK;
import static com.s95ammar.rxcurrencyconverter.util.Util.CURRENCY_CODE_LENGTH;
import static com.s95ammar.rxcurrencyconverter.util.Util.KEY_SPINNER_FROM_POSITION;
import static com.s95ammar.rxcurrencyconverter.util.Util.KEY_SPINNER_TO_POSITION;
import static com.s95ammar.rxcurrencyconverter.util.Util.SINGLE_UNIT;
import static com.s95ammar.rxcurrencyconverter.util.Util.isWithinLast10Sec;

public class MainActivity extends DaggerAppCompatActivity {

	private CompositeDisposable disposables = new CompositeDisposable();

	@Inject ViewModelProvider.Factory factory;
	private MainViewModel viewModel;
	@BindView(R.id.spinner_from) Spinner spinnerFrom;
	@BindView(R.id.spinner_to) Spinner spinnerTo;
	@BindView(R.id.editText_amount) EditText editTextAmount;
	@BindView(R.id.progressBar) ProgressBar progressBar;
	@BindView(R.id.textView_warning_error) TextView textViewWarningError;
	@BindView(R.id.textView_result) TextView textViewResult;
	@BindView(R.id.textView_result_value) TextView textViewResultValue;
	@BindView(R.id.textView_exchange_rate) TextView textViewExRate;
	@BindView(R.id.textView_exchange_rate_value) TextView textViewExRateValue;
	@BindView(R.id.textView_last_updated) TextView textViewLastUpdated;
	@BindView(R.id.textView_last_updated_value) TextView textViewLastUpdatedValue;
	@BindView(R.id.button_convert) Button buttonConvert;
	@BindView(R.id.button_retry) Button buttonRetry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
		ButterKnife.bind(this);
		fetchDataThen(() -> resetSpinnersSelection(savedInstanceState));
	}

	private void fetchDataThen(@Nullable Action onComplete) {
		disposables.add(viewModel.getUsdRatesToAll()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					handleRatesResult(result);
					if (onComplete != null) onComplete.run();
				}));
	}

	private void handleRatesResult(Result<List<Currency>> result) {
		displayResultStatus(result.status);
		switch (result.status) {
			case SUCCESS:
			case WARNING:
				setUpSpinners(viewModel.getCurrenciesNamesList(result.data));
				break;
		}
	}

	private void displayResultStatus(Result.Status status) {
		switch (status) {
			case LOADING:
				setLoading(true);
				break;
			case SUCCESS:
				setLoading(false);
				hideWarningOrError();
				break;
			case WARNING:
				setLoading(false);
				showOutOfDateWarning();
				break;
			case ERROR:
				setLoading(false);
				showDataMissingError();
				break;
		}
	}

	private void resetSpinnersSelection(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			spinnerFrom.setSelection(savedInstanceState.getInt(KEY_SPINNER_FROM_POSITION));
			spinnerTo.setSelection(savedInstanceState.getInt(KEY_SPINNER_TO_POSITION));
		}
	}

	private void setLoading(boolean isLoading) {
		progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
		editTextAmount.setEnabled(!isLoading);
		spinnerFrom.setEnabled(!isLoading);
		spinnerTo.setEnabled(!isLoading);
		buttonConvert.setEnabled(!isLoading);
	}

	private void setUpSpinners(List<String> spinnerRows) {
		spinnerFrom.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
		spinnerTo.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_row, spinnerRows));
	}

	@OnItemSelected({R.id.spinner_from, R.id.spinner_to})
	void getSpinnersOnItemSelectedListener(AdapterView<?> parent, View view) {
		String selection = (String) parent.getSelectedItem();
		if (view != null && !selection.equals(BLANK))
			((TextView) view.findViewById(R.id.textView_spinner)).setText(selection.substring(0, CURRENCY_CODE_LENGTH));
	}

	private void hideWarningOrError() {
		textViewWarningError.setVisibility(View.GONE);
		buttonRetry.setVisibility(View.GONE);
	}

	private void showOutOfDateWarning() {
		textViewWarningError.setVisibility(View.VISIBLE);
		textViewWarningError.setTextColor(ContextCompat.getColor(this, R.color.colorWarning));
		textViewWarningError.setText(R.string.warning_message);
	}

	private void showDataMissingError() {
		textViewWarningError.setVisibility(View.VISIBLE);
		textViewWarningError.setTextColor(ContextCompat.getColor(this, R.color.colorError));
		textViewWarningError.setText(R.string.error_message);
		buttonRetry.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.button_convert)
	void convert() {
		String from = getSpinnerSelection(spinnerFrom);
		String to = getSpinnerSelection(spinnerTo);
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

	@OnClick(R.id.button_retry)
	void reconnect() {
		fetchDataThen(null);
	}

	private String getSpinnerSelection(Spinner spinner) {
		if (spinner.getSelectedView() != null)
			return ((TextView) spinner.getSelectedView().findViewById(R.id.textView_spinner)).getText().toString();
		return BLANK;
	}

	private void handleConversionResult(Result<Conversion> result) {
		displayResultStatus(result.status);
		switch (result.status) {
			case SUCCESS:
			case WARNING:
				displayConversionResult(result.data);
				break;
		}
	}

	private void displayConversionResult(Conversion conversion) {
		textViewResult.setVisibility(conversion.getAmount() == SINGLE_UNIT ? View.GONE : View.VISIBLE);
		textViewResultValue.setVisibility(conversion.getAmount() == SINGLE_UNIT ? View.GONE : View.VISIBLE);
		textViewExRate.setVisibility(View.VISIBLE);
		textViewExRateValue.setVisibility(View.VISIBLE);
		textViewLastUpdated.setVisibility(View.VISIBLE);
		textViewLastUpdatedValue.setVisibility(View.VISIBLE);
		textViewResultValue.setText(conversion.getConversionResultDescription());
		textViewExRateValue.setText(conversion.getExchangeRateDescription());
		if (isWithinLast10Sec(conversion.getConversionTimeInMillis()))
			textViewLastUpdatedValue.setText(R.string.justNow);
		else
			textViewLastUpdatedValue.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(conversion.getConversionTimeInMillis())));
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_SPINNER_FROM_POSITION, spinnerFrom.getSelectedItemPosition());
		outState.putInt(KEY_SPINNER_TO_POSITION, spinnerTo.getSelectedItemPosition());
	}

	@Override
	protected void onDestroy() {
		disposables.clear();
		super.onDestroy();
	}

}
