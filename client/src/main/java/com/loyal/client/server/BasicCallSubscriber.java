package com.loyal.client.server;

import android.content.Context;

import androidx.annotation.NonNull;

import com.loyal.client.RetroCallManager;
import com.loyal.client.RxConfig;
import com.loyal.client.impl.RxImpl;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Retrofit2+OkHttp
 */
public abstract class BasicCallSubscriber<T> extends BasicSubscriber<T, RetroCallManager> implements Callback<T> {
    private Call<T> call;

    @NonNull
    @Override
    protected RetroCallManager manager() {
        return RetroCallManager.getInstance();
    }

    public BasicCallSubscriber(Context context) {
        super(context);
    }

    public BasicCallSubscriber(Context context, @NonNull RxConfig config) {
        super(context, config);
    }

    public BasicCallSubscriber(Context context, @NonNull RxConfig config, int theme) {
        super(context, config, theme);
    }

    @Override
    public RxImpl<T> showProgressDialog(boolean showProgressDialog) {
        RxImpl<T> rx = super.showProgressDialog(showProgressDialog);
        if (showProgressDialog)
            showDialog();
        return rx;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        this.call = call;
        if (response.isSuccessful()) {
            onNext(response.body());
            onComplete();
        } else {
            onError(new IOException(response.message()));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        this.call = call;
        onError(t);
    }

    @Override
    public void dispose() {
        if (null != call && !call.isCanceled())
            call.cancel();
    }

}
