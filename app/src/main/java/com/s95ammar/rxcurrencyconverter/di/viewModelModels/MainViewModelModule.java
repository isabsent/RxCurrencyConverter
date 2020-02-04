package com.s95ammar.rxcurrencyconverter.di.viewModelModels;

import androidx.lifecycle.ViewModel;

import com.s95ammar.rxcurrencyconverter.viewModels.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelModule {
	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel.class)
	public abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);
}
