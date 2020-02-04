package com.s95ammar.rxcurrencyconverter.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.s95ammar.rxcurrencyconverter.R;
import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {
	@Inject
	ViewModelProvider.Factory factory;
	MainViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
	}
}
