package com.loyal.client.impl;

public interface RxSubscriberListener<T> {
    void onResult(int what, Object tag, T result);

    void onError(int what, Object tag, Throwable e);
}