package com.sample.client;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.loyal.client.ConnectUtil;
import com.loyal.client.RxClient;
import com.loyal.client.impl.RxSubscriberListener;

public class MainActivity extends AppCompatActivity implements RxSubscriberListener<String> {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_get:
                request(2, "-er-", false);
                break;
            case R.id.test_http:
                request(4, "-si-", true);
                break;
        }
    }

    private void request(int what, Object tag, boolean show) {
        String jokUrl = "https://api.apiopen.top/getJoke?page=1&count=2&type=video";
        HttpSubscriber<String> subscriber = new HttpSubscriber<>(this);
        subscriber.setWhat(what).setTag(tag).setSubscribeListener(this);
        subscriber.showProgressDialog(show);
        RxClient.callExecute(subscriber.request(jokUrl), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        Log.e("onResult", what + " : " + tag);
        Log.e(TAG, "onResult: " + result);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        Log.e("onError", what + " : " + tag);
        Log.e(TAG, "onError: " + ConnectUtil.getError(e));
    }
}