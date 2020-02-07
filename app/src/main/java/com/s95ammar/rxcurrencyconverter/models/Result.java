package com.s95ammar.rxcurrencyconverter.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// A generic class that contains data and status about loading this data.

public class Result<T> {

	@NonNull
	public final AuthStatus status;

	@Nullable
	public final T data;

	@Nullable
	public final String message;


	public Result(@NonNull AuthStatus status, @Nullable T data, @Nullable String message) {
		this.status = status;
		this.data = data;
		this.message = message;
	}

	public static <T> Result<T> success() {
		return new Result<>(AuthStatus.SUCCESS, null, null);
	}

	public static <T> Result<T> success(@Nullable T data) {
		return new Result<>(AuthStatus.SUCCESS, data, null);
	}

	public static <T> Result<T> error(String msg, @Nullable T data) {
		return new Result<>(AuthStatus.ERROR, data, msg);
	}

	public static <T> Result<T> error(String msg) {
		return new Result<>(AuthStatus.ERROR, null, msg);
	}

	public static <T> Result<T> loading() {
		return new Result<>(AuthStatus.LOADING, null, null);
	}

	public static <T> Result<T> loading(@Nullable T data) {
		return new Result<>(AuthStatus.LOADING, data, null);
	}


	public enum AuthStatus {SUCCESS, ERROR, LOADING}

}
