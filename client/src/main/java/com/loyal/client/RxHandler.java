package com.loyal.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.loyal.client.impl.ProgressCancelListener;

public class RxHandler extends Handler implements DialogInterface.OnCancelListener {
    private ProgressDialog progressDialog;
    private Context context;
    private ProgressCancelListener listener;

    public RxHandler(Context context, int theme, ProgressCancelListener cancelListener) {
        this.context = context;
        initDialog(theme);
        this.listener = cancelListener;
    }

    private void initDialog(int theme) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context, theme);
            progressDialog.setOnCancelListener(this);
        }
    }

    public void setMessage(CharSequence message) {
        if (null != progressDialog)
            progressDialog.setMessage(message);
    }

    public void setCancelable(boolean cancelable) {
        if (null != progressDialog)
            progressDialog.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean flag) {
        if (null != progressDialog)
            progressDialog.setCanceledOnTouchOutside(flag);
    }

    public void dismissDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showDialog() {
        if (context instanceof Activity && ((Activity) context).isFinishing())
            return;
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (null != listener)
            listener.onCancelProgress();
    }
}
