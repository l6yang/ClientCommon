package com.sample.client;

import android.content.Context;

import androidx.annotation.NonNull;

import com.loyal.client.RxConfig;
import com.loyal.client.server.BasicCallSubscriber;
import com.loyal.client.RetroCallManager;

import retrofit2.Call;

public class HttpSubscriber<T> extends BasicCallSubscriber<T> implements HttpServer {
    private HttpServer server;

    public HttpSubscriber(Context context) {
        super(context);
    }

    public HttpSubscriber(Context context, @NonNull RxConfig config) {
        super(context, config);
    }

    public HttpSubscriber(Context context, @NonNull RxConfig config, int theme) {
        super(context, config, theme);
    }

    @Override
    public void createServer(RetroCallManager manager) {
        server = manager.createServer(HttpServer.class);
    }

    @Override
    public Call<String> request(String url) {
        return server.request(url);
    }
}
