package com.loyal.client.impl;

import androidx.annotation.IntRange;

public interface RxImpl<T> {
    RxImpl<T> setWhat(@IntRange(from = 2, to = 1000) int what);

    RxImpl<T> showProgressDialog(boolean showProgressDialog);

    RxImpl<T> setDialogMessage(CharSequence message);

    RxImpl<T> setTag(Object objTag);

    RxImpl<T> setCancelable(boolean cancelable);

    void setCanceledOnTouchOutside(boolean flag);

    void setSubscribeListener(RxSubscriberListener<T> listener);

    void setDialogCallback(DialogCallback dialogCallback);

    void dispose();
}
