package jp.co.vantageapps.famous.app;

import net.nend.android.NendAdInterstitial;
import net.nend.android.NendAdInterstitial.NendAdInterstitialClickType;
import net.nend.android.NendAdInterstitial.NendAdInterstitialShowResult;
import net.nend.android.NendAdInterstitial.NendAdInterstitialStatusCode;
import tokyo.suru_pass.AdContext;
import tokyo.suru_pass.Interstitial;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import jp.co.imobile.sdkads.android.ImobileSdkAd;
import jp.co.imobile.sdkads.android.ImobileIconParams;

public class SuruPassActivity extends FragmentActivity {
	
	public static final String SURUPASS_MEDIA_ID = "a01f3775-43a3-4485-aa62-2e234788fc81";
	public static final int TOP_INTER_ID = 503;
	public static final int RESULT_INTER_ID = 506;
	public static final int TOP_BANNER_ID = 1269;
	public static final int BOTTOM_BANNER_ID = 510;
	public static final String PUBLISHER_ID = "28461";
	public static final String MEDIA_ID = "167178";
	public static final String SPOT_ID_TOP = "444822";
	public static final String SPOT_ID_INTER_1 = "444817";
	public static final String SPOT_ID_INTER_2 = "444819";

	protected  AdContext adContext = null;
	protected  Interstitial interstitial = null;
	
	public enum SuruPassType {
		InterstitialTop,
		InterstitialResult,
		TopBanner,
		BottomBanner
	};	
	
	protected void closeSuruPass()
	{
		
	}
	
	public void loadIns(){
		NendAdInterstitial.loadAd(getApplicationContext(), "b1f7cf3d489c204a52447fea01fc77adc39acfb0", 395676);
	}
	
	public void showIns(){
		NendAdInterstitialShowResult result = NendAdInterstitial.showAd(this);
        
        // 表示結果に応じて処理を行う
        switch (result) {
        case AD_SHOW_SUCCESS:
        	Log.v("AD_SHOW_SUCCESS", "AD_SHOW_SUCCESS");
            break;
        case AD_SHOW_ALREADY:
        	Log.v("AD_SHOW_ALREADY", "AD_SHOW_ALREADY");
            break;
        case AD_FREQUENCY_NOT_RECHABLE:
        	Log.v("AD_FREQUENCY_NOT_RECHABLE", "AD_FREQUENCY_NOT_RECHABLE");
            break;
        case AD_REQUEST_INCOMPLETE:
        	Log.v("AD_REQUEST_INCOMPLETE", "AD_REQUEST_INCOMPLETE");
            break;
        case AD_LOAD_INCOMPLETE:
        	Log.v("AD_LOAD_INCOMPLETE", "AD_LOAD_INCOMPLETE");
            break;
        case AD_DOWNLOAD_INCOMPLETE:
        	Log.v("AD_DOWNLOAD_INCOMPLETE", "AD_DOWNLOAD_INCOMPLETE");
            break;
        default:
            break;
        }	
	}
	
	public void finishiIns(){
		NendAdInterstitialShowResult result = NendAdInterstitial.showAd(this);
        
        // 表示結果に応じて処理を行う
        switch (result) {
        case AD_SHOW_SUCCESS:
        	Log.v("AD_SHOW_SUCCESS", "AD_SHOW_SUCCESS");
            break;
        case AD_SHOW_ALREADY:
        	Log.v("AD_SHOW_ALREADY", "AD_SHOW_ALREADY");
            break;
        case AD_FREQUENCY_NOT_RECHABLE:
        	Log.v("AD_FREQUENCY_NOT_RECHABLE", "AD_FREQUENCY_NOT_RECHABLE");
            break;
        case AD_REQUEST_INCOMPLETE:
        	Log.v("AD_REQUEST_INCOMPLETE", "AD_REQUEST_INCOMPLETE");
            break;
        case AD_LOAD_INCOMPLETE:
        	Log.v("AD_LOAD_INCOMPLETE", "AD_LOAD_INCOMPLETE");
            break;
        case AD_DOWNLOAD_INCOMPLETE:
        	Log.v("AD_DOWNLOAD_INCOMPLETE", "AD_DOWNLOAD_INCOMPLETE");
            break;
        default:
            break;
        }	
	}
	
	protected void showSuruPass(SuruPassType mode)
	{
		
		try {
			adContext = new AdContext(this, SURUPASS_MEDIA_ID/*, BuildConfig.DEBUG*/);
			
			//ImobileSdkAd.registerSpotFullScreen(this, PUBLISHER_ID, MEDIA_ID, SPOT_ID_INTER_1); //2.広告取得開始
			//ImobileSdkAd.start(SPOT_ID_INTER_1);
			
			//ImobileSdkAd.registerSpotFullScreen(this, PUBLISHER_ID, MEDIA_ID, SPOT_ID_INTER_2); //2.広告取得開始
			//ImobileSdkAd.start(SPOT_ID_INTER_2);
			
			LinearLayout suruPassLayout = null;
			Interstitial interstitial = null;
			switch (mode) {
			case InterstitialTop:
				//1.広告スポットの登録	
							//show dialog
					interstitial = adContext.createInterstitial(this, TOP_INTER_ID);
					interstitial.show();
//				ImobileSdkAd.showAd(this, SPOT_ID_INTER_1);
				//NendAdInterstitial.showAd(this);
				//interstitial = adContext.createInterstitial(this, TOP_INTER_ID);
				//interstitial.show();
				break;
			case InterstitialResult:
				
//				ImobileSdkAd.showAd(this, SPOT_ID_INTER_2);
				interstitial = adContext.createInterstitial(this, RESULT_INTER_ID);
				interstitial.show();
				break;
			case TopBanner:
				// 【表示する座標を指定する場合】
//				// 1.広告スポットの登録
//				ImobileSdkAd.registerSpotInline(this, PUBLISHER_ID, MEDIA_ID, SPOT_ID_TOP); // 2.広告取得開始
//				ImobileSdkAd.start(SPOT_ID_TOP);
//				// 3.広告表示(表示する座標を指定)
//				ImobileIconParams params = new ImobileIconParams(); params.setIconNumber(4);
//				params.setIconTitleShadowEnable(false); 
//				params.setIconTitleFontColor("#000000");
//				// 3.広告表示(表示するViewGroupを指定)
//				ViewGroup adLayout = (ViewGroup)findViewById(R.id.LinearLayoutInterstitialTop); 
//				ImobileSdkAd.showAd(this, SPOT_ID_TOP, adLayout, params);
				
				suruPassLayout = (LinearLayout) findViewById(R.id.LinearLayoutInterstitialTop);
				suruPassLayout.addView(adContext.createBanner(this, TOP_BANNER_ID));
				break;
			case BottomBanner:
				// 【表示する座標を指定する場合】
//				// 1.広告スポットの登録
//				ImobileSdkAd.registerSpotInline(this, PUBLISHER_ID, MEDIA_ID, SPOT_ID_TOP); // 2.広告取得開始
//				ImobileSdkAd.start(SPOT_ID_TOP);
//				// 3.広告表示(表示する座標を指定)
//				ImobileIconParams params2 = new ImobileIconParams(); params2.setIconNumber(4);
//				params2.setIconTitleShadowEnable(false); 
//				params2.setIconTitleFontColor("#000000");
//				// 3.広告表示(表示するViewGroupを指定)
//				ViewGroup adLayout2 = (ViewGroup)findViewById(R.id.LinearLayoutInterstitialBottom); 
//				ImobileSdkAd.showAd(this, SPOT_ID_TOP, adLayout2, params2);
					suruPassLayout = (LinearLayout) findViewById(R.id.LinearLayoutInterstitialBottom);
					suruPassLayout.addView(adContext.createBanner(this, BOTTOM_BANNER_ID));

				
				//suruPassLayout = (LinearLayout) findViewById(R.id.LinearLayoutInterstitialBottom);
				//suruPassLayout.addView(adContext.createBanner(this, BOTTOM_BANNER_ID));
				break;
			}
			
		} catch(Exception e){}
	}
	
	 public void onClick(NendAdInterstitialClickType clickType) {
	        // クリックに応じて処理行う
	        switch (clickType) {
	        case CLOSE:
	        	Log.i("AD_suru_CLOSE", "CLOSE");
	            break;
	        case DOWNLOAD:
	        	Log.i("AD_suru_DOWNLOAD", "DOWNLOAD");
	            break;
	        default :
	            break;
	        }
	        // 広告クリックをログに出力
	        Log.d("AD_TAG", clickType.name());
	    }
	    
	  
	     //広告受信通知
	  
	    public void onCompletion(NendAdInterstitialStatusCode statusCode) {
	        // 受信結果に応じて処理を行う
	        switch (statusCode) {
	        case SUCCESS:
	        	Log.v("AD_SUCCESS", "SUCCESS");
	            break;
	        case FAILED_AD_DOWNLOAD:
	        	Log.v("AD_FAILED_AD_DOWNLOAD", "FAILED_AD_DOWNLOAD");
	            break;
	        case INVALID_RESPONSE_TYPE:
	        	Log.v("AD_INVALID_RESPONSE_TYPE", "INVALID_RESPONSE_TYPE");
	            break;
	        case FAILED_AD_INCOMPLETE:
	        	Log.v("AD_FAILED_AD_INCOMPLETE", "FAILED_AD_INCOMPLETE");
	            break;
	        case FAILED_AD_REQUEST:
	        	Log.v("AD_FAILED_AD_REQUEST", "FAILED_AD_REQUEST");
	            break;
	        default:
	            break;
	        }
	        // 広告受信結果をログに出力
	        Log.d("AD_TAG", statusCode.name());
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        //mDialog.destroy();
	    }
}
