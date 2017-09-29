package jp.co.vantageapps.famous.app;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.five_corp.ad.FiveAdInterface;
import com.five_corp.ad.FiveAdListener;

public class Listener implements FiveAdListener {
    private final Activity mActivity;
    private final String mType;

    private static final String TAG = Listener.class.toString();

    public Listener(Activity activity, String type) {
        mActivity = activity;
        mType = type;
    }

    private void log(String message) {
        //Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }


    @Override public void onFiveAdLoad(FiveAdInterface f) {
        log(mType + ": onFiveAdLoad. " + f.getSlotId());
    }
    @Override public void onFiveAdError(FiveAdInterface f, FiveAdListener.ErrorCode errorCode) {
        log(mType + ": onFiveAdError. " + f.getSlotId() + ": " + errorCode);
    }
    @Override public void onFiveAdClick(FiveAdInterface f) {
        log(mType + ": onFiveAdClick. " + f.getSlotId());
    }
    @Override public void onFiveAdClose(FiveAdInterface f) {
        log(mType + ": onFiveAdClose. " + f.getSlotId());
    }
    @Override public void onFiveAdStart(FiveAdInterface f) {
        log(mType + ": onFiveAdStart. " + f.getSlotId());
    }
    @Override public void onFiveAdPause(FiveAdInterface f) {
        log(mType + ": onFiveAdPause. " + f.getSlotId());
    }
    @Override public void onFiveAdResume(FiveAdInterface f) {
        log(mType + ": onFiveAdResume. " + f.getSlotId());
    }
    @Override public void onFiveAdViewThrough(FiveAdInterface f) {
        log(mType + ": onFiveAdViewThrough. " + f.getSlotId());
    }
    @Override public void onFiveAdReplay(FiveAdInterface f) {
        log(mType + ": onFiveAdReplay. " + f.getSlotId());
    }
/*
	@Override
	public void onFiveAdClick(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdClose(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdError(FiveAdInterface arg0, ErrorCode arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdLoad(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdPause(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdReplay(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdResume(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdStart(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onFiveAdViewThrough(FiveAdInterface arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
*/
}