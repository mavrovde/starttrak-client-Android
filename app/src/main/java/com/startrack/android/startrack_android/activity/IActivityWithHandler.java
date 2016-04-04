package com.startrack.android.startrack_android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.startrack.android.startrack_android.api.ApiHandler;

/**
 * Created by vrogovskiy on 2/23/16.
 */
public interface IActivityWithHandler {
    public Handler getHandler();
    public Context getContext();
    public void hideProgressAndShowViews();
    public Activity getActivity();
    public void showAuthorizarionDialogForXing();
}
