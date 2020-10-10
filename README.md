`@Deprecated`

![photo_2020-10-01_14-34-55](https://user-images.githubusercontent.com/32682273/95655604-4979ca00-0b11-11eb-919c-596bb6d3cb0b.jpg)

# RxCurrencyConverter

An android app with a very simple UI, that uses a REST API to do simple currency conversions using RxJava & Retrofit, while storing the data using Room for offline usage, following SSOT (single source of truth) principle.
Used API: https://rapidapi.com/natkapral/api/currency-converter5.

- [Principles](#principles)
  - [SSOT (single source of truth)](#ssot-single-source-of-truth)
  - [Single responsibility](#single-responsibility)
- [App preview & different scenarios](#app-preview--different-scenarios)
  - [CONNECTION ✅](#connection-)
  - [CONNECTION ❌ STORED DATA ✅ ➟ USER RECONNECTS ✅](#connection--stored-data---user-reconnects-)
  - [CONNECTION ❌ STORED DATA ❌ ➟ USER RECONNECTS ✅](#connection--stored-data---user-reconnects--1)

## Principles

### SSOT (single source of truth):
> The [view](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/views/activities/MainActivity.java) always receives the data from the database. If the app is connected to the internet, the database gets updated, but the data is received from the database anyway.
>
> ![SSOT](https://user-images.githubusercontent.com/32682273/74683559-065d9600-51d2-11ea-8b64-f2f88133e25e.jpg)

### Single responsibility:
>  - The [view](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/views/activities/MainActivity.java) doesn't care how the data is fetched. It only passes a request from user input, observes the [`Result`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/Result.java), and displays it.
>  - Determining the availability and source of the data, is the responsibility of the [`NetworkBoundResource`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/NetworkBoundResource.java) class, but it doesn't handle the data fetching process.
>  - Fetching the data is the responsibility of the [`Repository`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/Repository.java).


## App preview & different scenarios:

### CONNECTION ✅

>  - On startup, all data is updated.
>  - When the user makes a conversion request, the selected currencies' data in the database are updated and the result is displayed accordingly.
>  - If the user disconnects, the next conversion request will show a warning (as shown in the next scenario).
>
>    ![SUCCESS2](https://user-images.githubusercontent.com/32682273/74679550-d8268900-51c6-11ea-996c-0c75bafcbe15.gif)

### CONNECTION ❌ STORED DATA ✅ ➟ USER RECONNECTS ✅

>  - On startup, a warning message is shown.
>  - When the user makes a conversion request, the selected currencies are fetched from the database and the result is displayed with the time of the last update and a warning.
>  - If the user reconnects, the next conversion request will hide the warning.
>
>    ![WARNING2](https://user-images.githubusercontent.com/32682273/74679552-d8bf1f80-51c6-11ea-8f5c-67842881ae03.gif)

### CONNECTION ❌ STORED DATA ❌ ➟ USER RECONNECTS ✅

>  - On startup, an error is shown with a `RETRY` button and user input is blocked.
>  - If the user reconnects, clicking the `RETRY` button will hide the error, update all data and unblock user input.
>
>    ![ERROR2](https://user-images.githubusercontent.com/32682273/74679553-d957b600-51c6-11ea-8be9-5a4d836f6cad.gif)
