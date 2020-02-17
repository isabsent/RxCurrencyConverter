# RxCurrencyConverter

An android app with a very simple UI, that uses a REST API to do simple currency conversions using RxJava & Retrofit, while storing the data using Room for offline usage, following SSOT (single source of truth) principle.
Used API: https://rapidapi.com/natkapral/api/currency-converter5.


## About

### SSOT (single source of truth) principle:
> The data is always sent to the [view](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/views/activities/MainActivity.java) from the database. If the app is connected to the internet, the database gets updated, then the updated data is sent.
>
> ![SSOT](https://user-images.githubusercontent.com/32682273/74683559-065d9600-51d2-11ea-8b64-f2f88133e25e.jpg)

### Single responsibility
>  - The [view](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/views/activities/MainActivity.java) doesn't care where the data is coming from. It passes a request from user input, observes the [`Result`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/Result.java), and then displays it.
>  - Determining the availability and source of the data, is the responsibility of the [`NetworkBoundResource`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/NetworkBoundResource.java) class, but it doesn't handle the data fetching process.
>  - Fetching the data is the responsibility of the [`Repository`](https://github.com/s95ammar/RxCurrencyConverter/blob/master/app/src/main/java/com/s95ammar/rxcurrencyconverter/models/Repository.java).


## Scenarios:

CONNECTION ✅

>  - On start up, all data in the database is updated.
>  - When the user makes a conversion request, the selected currencies' data in the database are updated and the result is displayed accordingly.
>
>    ![SUCCESS2](https://user-images.githubusercontent.com/32682273/74679550-d8268900-51c6-11ea-996c-0c75bafcbe15.gif)

CONNECTION ❌ STORED DATA ✅ ➟ USER RECONNECTS ✅

>  - On start up, a warning message is shown.
>  - When the user makes a conversion request, the selected currencies are fetched from the database and the result is displayed with the time of the last update and a warning.
>  - If the user reconnects, next conversion request will hide the warning.
>
>    ![WARNING2](https://user-images.githubusercontent.com/32682273/74679552-d8bf1f80-51c6-11ea-8f5c-67842881ae03.gif)

CONNECTION ❌ STORED DATA ❌ ➟ USER RECONNECTS ✅

>  - On start up, an error is shown with a `RETRY` button and user input is blocked.
>  - If the user reconnects, clicking the `RETRY` button will hide the error, update all data and unblock user input.
>
>    ![ERROR2](https://user-images.githubusercontent.com/32682273/74679553-d957b600-51c6-11ea-8be9-5a4d836f6cad.gif)
