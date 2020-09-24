package com.loyal.client.server;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.loyal.client.RetroCallManager;
import com.loyal.client.RxConfig;
import com.loyal.client.RxHandler;
import com.loyal.client.impl.DialogCallback;
import com.loyal.client.impl.ProgressCancelListener;
import com.loyal.client.impl.RxImpl;
import com.loyal.client.impl.RxSubscriberListener;
import com.loyal.client.impl.UnSubscriberListener;

/**
 * 顶层设计
 */
public abstract class BasicSubscriber<T, R extends RetroCallManager> implements RxImpl<T>, ProgressCancelListener, UnSubscriberListener {
    private RxSubscriberListener<T> subscribeListener;
    private int mWhat = 2;
    private Object object;
    private boolean showProgressDialog = false;//default=false
    private RxHandler rxHandler;
    private DialogCallback dialogCallback;
    private R manager;

    protected @NonNull
    abstract R manager();

    public abstract void createServer(R manager);

    public BasicSubscriber(Context context) {
        this(context, RxConfig.getInstance());
    }

    public BasicSubscriber(Context context, @NonNull RxConfig config) {
        this(context, config, 0);
    }

    public BasicSubscriber(Context context, @NonNull RxConfig config, int dialogTheme) {
        initDialog(context, dialogTheme);
        manager = manager();
        manager.build(config);
        createServer(manager);
    }

    private void initDialog(Context context, int theme) {
        if (null != context) {
            rxHandler = new RxHandler(context, theme, this);
            setDialogMessage("").setCancelable(true).setCanceledOnTouchOutside(true);
        }
    }

    public String baseUrl() {
        return manager.baseUrl();
    }

    @Override
    public RxImpl<T> setWhat(@IntRange(from = 2, to = 1000) int what) {
        this.mWhat = what;
        return this;
    }

    @Override
    public RxImpl<T> showProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog = showProgressDialog;
        return this;
    }

    @Override
    public RxImpl<T> setDialogMessage(CharSequence message) {
        if (null != rxHandler) {
            rxHandler.setMessage(message);
        }
        return this;
    }

    @Override
    public RxImpl<T> setTag(Object objTag) {
        this.object = objTag;
        return this;
    }

    @Override
    public RxImpl<T> setCancelable(boolean cancelable) {
        if (null != rxHandler)
            rxHandler.setCancelable(cancelable);
        return this;
    }

    /**
     * 自定义加载事件
     */
    @Override
    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean flag) {
        if (null != rxHandler)
            rxHandler.setCanceledOnTouchOutside(flag);
    }

    @Override
    public void setSubscribeListener(RxSubscriberListener<T> listener) {
        this.subscribeListener = listener;
    }

    protected void showDialog() {
        if (null != dialogCallback) {
            dialogCallback.onShown();
            return;
        }
        if (showProgressDialog && null != rxHandler) {
            rxHandler.showDialog();
        }
    }

    private void dismissDialog() {
        if (null != dialogCallback) {
            dialogCallback.onDismiss();
            return;
        }
        showProgressDialog = false;
        if (null != rxHandler) {
            rxHandler.dismissDialog();
            rxHandler = null;
        }
    }

    /**
     * 回调
     */
    public void onNext(T result) {
        if (null != subscribeListener)
            subscribeListener.onResult(mWhat, object, result);
    }

    public void onError(Throwable e) {
        dismissDialog();
        boolean cancelError = TextUtils.equals("已取消操作", null == e ? "" : e.getMessage());
        if (null != subscribeListener)
            subscribeListener.onError(mWhat, object, e);
        if (cancelError) {
            subscribeListener = null;
        }
        dispose();
    }

    public void onComplete() {
        dismissDialog();
        dispose();
    }

    @Override
    public void onCancelProgress() {
        onError(new Exception("已取消操作"));
        onComplete();
    }

    /*中断/取消任务*/
    @Override
    public void unsubscribe() {
        onComplete();
    }
}
