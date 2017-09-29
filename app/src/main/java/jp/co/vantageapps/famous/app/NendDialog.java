package jp.co.vantageapps.famous.app;

import net.nend.android.NendAdInterstitial;
import net.nend.android.NendAdView;
import net.nend.android.NendAdView.NendError;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class NendDialog extends Dialog {

    NendAdView mNendAdView;
    Context mContext;
    Activity activity;
    public NendDialog(Context context) {
        super(context);
        mContext = context;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nend_dialog);
        mNendAdView = (NendAdView) findViewById(R.id.nend);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNendAdView.loadAd();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNendAdView.pause();
    }

    public void destroy(){
        dismiss();
    }

}
