package com.example.expensetracker_1.APIServices;

import com.example.expensetracker_1.Domain.CurrencyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface CurrencyApiService {
    @GET
    Call<CurrencyResponse> getExchangeRates(@Url String url);
}


