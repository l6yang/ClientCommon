package com.sample.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HttpServer {
    @GET
    Call<String> request(@Url String url);
}
