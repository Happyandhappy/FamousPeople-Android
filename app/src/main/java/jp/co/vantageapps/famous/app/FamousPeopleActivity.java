package jp.co.vantageapps.famous.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import net.nend.android.NendAdInterstitial;
import net.nend.android.NendAdInterstitial.NendAdInterstitialClickType;
import net.nend.android.NendAdInterstitial.NendAdInterstitialShowResult;
import net.nend.android.NendAdInterstitial.NendAdInterstitialStatusCode;
import net.nend.android.NendAdInterstitial.OnCompletionListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.five_corp.ad.FiveAd;
import com.five_corp.ad.FiveAdConfig;
import com.five_corp.ad.FiveAdFormat;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.tapjoy.TJConnectListener;
import com.tapjoy.Tapjoy;

import it.partytrack.sdk.Track;
import jp.co.vantageapps.famous.app.FaceDetectionActivity.ActivityType;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import jp.co.adtrack.android.AdManager;

public class FamousPeopleActivity extends SuruPassActivity{
	
	//NendDialog mDialog;
	
	private SharedPreferences mPref1, mPref2;
	private SharedPreferences.Editor mPrefEdit1;
	private static boolean MainOn = true;

	private ImageButton btnGif, btnGoPro;
	private ImageButton btnFaceTest;
	private ImageButton btnRanking;
	private ImageButton btnRankinglock;
	private boolean isSuccess = false;	
	private TextView countTextView; 
	private String manCount, womanCount;
	
	private ServerInfo serverInfo = null;
	private PopupFragment popupFrament = null;
	
	String useruuid = null;
	
	SharedPreferences mpref = null;
	
	String postData;
	
	String strUrl = "https://avp.in-g.org/api/avpid";
	String strAccessKey = "cR0Ss";
	String strAppId = "61";
	
	SharedPreferences CeresPF;
	String message;
	String switches;
	ceres ceres;
	
	private String _adid = "";
	private String _optout = "";
	static AdvertisingIdClient.Info info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITtLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		FiveAdConfig config = new FiveAdConfig("162123");
    	config.formats = EnumSet.of(
    			FiveAdFormat.INTERSTITIAL_LANDSCAPE, // インタースティシャル（横向き）
    			FiveAdFormat.INTERSTITIAL_PORTRAIT, // インタースティシャル（縦向き）
    			FiveAdFormat.IN_FEED, // インフィード
    			FiveAdFormat.BOUNCE, // バウンス
    			FiveAdFormat.W320_H180, // 横320x縦180の動画枠
    			FiveAdFormat.W300_H250 // 横300x縦250の動画枠
    	);
    	//config.isTest = true;

//		FiveAd.initialize(this, config);
//		FiveAd fiveAd = FiveAd.getSingleton();
//		fiveAd.enableLoading(true);

		Log.i("TEST", "onCreate");
		ceres = new ceres(this);
		ceres.execute("http://vantageapps.co.jp/app/android/ceres/famouspeople/switch.txt");

		/*
		CeresPF = getSharedPreferences("CeresPF", MODE_PRIVATE);
		switches = CeresPF.getString("CeresPF", "Start");
		if(switches.equals("End")){
			Log.i("TEST End", "End");
		}else{
			//useruuid = GetDevicesUUID(getBaseContext());
			useruuid = SplashActivity.deviceId;
			ceres = new ceres(this);
			ceres.execute("http://vantageapps.co.jp/app/android/ceres/famouspeople/switch.txt");
		}
		*/
		AdManager ad = new AdManager(this);
		ad.sendConversion();
		
		loadIns();
		//NendAdInterstitial.loadAd(getApplicationContext(), "8c278673ac6f676dae60a1f56d16dad122e23516", 213206);
		//mDialog = new NendDialog(this);
		
		//mDialog.show();
		//lock 情報取得
		MainOn = false;
		
		mPref1 = getSharedPreferences("MainOn", 0);
		mPrefEdit1 = mPref1.edit();
		
		mPref2 = getSharedPreferences("ResultOn", 0);
		
//		String deviceId = Secure.getString(this.getContentResolver(),
//                Secure.ANDROID_ID);
//        Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        
		if (DetectorReal.isInitFaceEngine == false)
		{
//			DetectorReal.setThrethold(DetectorReal.DEFAULT_IDENTIFY_THRETHOLD);
			int status = DetectorReal.initialize();
//			int status = 0;
			Log.d("FACE Engine Init", String.valueOf(status));
			DetectorReal.isInitFaceEngine = true;
		}
		
		Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
		String tapjoySDKKey = "lDw7cs7QSuSV4Z5IBKdmngEC18qvgdz4hRlW1GvSRR8EHWXMlaK_im85fVM3";
		Tapjoy.connect(this.getApplicationContext(),tapjoySDKKey, connectFlags,
				new TJConnectListener() {
					@Override
					public void onConnectSuccess() {
						Log.v("TapJoy","ConnectSuccces");		
					}

					@Override
					public void onConnectFailure() {
						Log.v("TapJoy","N");
					}
		});
		
		Tapjoy.setDebugEnabled(false);
		
		Track.start(this, 4798, "73de60ccddbf4fa5e07580679b3ddb86");
		//test 2015/09/01
		//((GlobalClass) getApplicationContext()).readConfigs();
		
		countTextView = (TextView)findViewById(R.id.countinfo);		
		
		serverInfo = new ServerInfo();
		serverInfo.execute();
		
		btnRanking = (ImageButton)findViewById(R.id.btnRanking);
		
		btnGif = (ImageButton)findViewById(R.id.btnMakeGif);
		btnGif.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				
				closeSuruPass();
				//test 2015/09/01
				((GlobalClass) getApplicationContext()).readConfigs();
				//serverInfo.cancel(true);
				ImageSelectionActivity.acivityType = ActivityType.Acitivity_GIF;
				Intent nextScreen = new Intent(FamousPeopleActivity.this, ImageSelectionActivity.class);
	            startActivity(nextScreen);
	           // mDialog.dismiss();
			}
		});
		
		btnFaceTest = (ImageButton)findViewById(R.id.btnFindFamous);
		btnFaceTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				closeSuruPass();
				
				//serverInfo.cancel(true);
				// TODO Auto-generated method stub
				Intent i = new Intent(FamousPeopleActivity.this, FSGenderSelectionActivity.class);
				//Intent i = new Intent(FamousPeopleActivity.this, FSAnalyseResultActivity.class);
				startActivity(i);
				
				//finish();
				
				//mDialog.dismiss();
			}
		});
		
		btnGoPro = (ImageButton)findViewById(R.id.btnGoPro);
		btnGoPro.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				String postStr = "アンドロイドのOSバージョン(任意)：\n使用機種(任意):\n障害内容：";
				Uri uri2 = Uri.parse("mailto:support@vantageapps.co.jp");
				Intent intent2 = new Intent(Intent.ACTION_SENDTO, uri2);
				intent2.putExtra(Intent.EXTRA_SUBJECT, "問題報告：有名人診断　Android版 ver.18.0");
				intent2.putExtra(Intent.EXTRA_TEXT, postStr);
				Intent mailer = Intent.createChooser(intent2, "メール：");
				try {
					startActivity(mailer);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		//lock		
		
		int popupvalue = SharedClass.getPopup(this);		
		if (popupvalue == 1){
			SharedClass.saveRunCount(this);
			int runcount = SharedClass.getRunCount(this);
			Log.e("Runcount: ", String.valueOf(runcount));
			if (runcount == 3){
				popupFrament = new PopupFragment();
				PopupFragment.mainActivity = this;
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.popup_frame, popupFrament).commit();
			}
		}
		//showSuruPass(SuruPassType.TopBanner);
		
		if(isFinishing() == false){
			showSuruPass(SuruPassType.InterstitialTop);
			showSuruPass(SuruPassType.BottomBanner);
		}else{
			closeSuruPass();
		}
		
		/*
		// 表示結果が返却される
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
        */
		//showSuruPass(SuruPassType.InterstitialTop);	
				
	}
	
	protected void onStart(){
		super.onStart();
		// データロード
		SharedPreferences user_data = getSharedPreferences("user_data", Context.MODE_PRIVATE);
		//_uuid = user_data.getString("uuid", "");
		_adid = user_data.getString("adid", "");
		_optout = user_data.getString("optout", "");
				
		Log.v("tag", "onstart");
	}

	private class ServerInfo extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(FSServerConnectionActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

		// Get data from server
//        @Override
//        protected Void doInBackground(Void... arg0) {
//
//            ServiceHandler sh = new ServiceHandler();
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>(4);
//            params.add(new BasicNameValuePair("commandType", "count_request"));
//
//            String jsonStr = sh.makeServiceCall(GlobalClass.SERVER_URL, ServiceHandler.POST, params);
//
//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//
//                    String result = jsonObj.getString("result");
//                    if(result.equals("ok")) {
//                    	manCount = jsonObj.getString("man");
//                    	womanCount = jsonObj.getString("woman");
//                    	isSuccess = true;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }
//            return null;
//        }

		// Get data from local db
		@Override
        protected Void doInBackground(Void... arg0) {

			DatabaseAdapter mDbHelper = new DatabaseAdapter(FamousPeopleActivity.this);
			mDbHelper.createDatabase();
			mDbHelper.open();

			manCount = Integer.toString(mDbHelper.getFamousPeopleManCount());

			womanCount = Integer.toString(mDbHelper.getFamousPeopleWomanCount());

			isSuccess = true;

			mDbHelper.close();

            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(isSuccess) {
            	countTextView.setText("男性データ " + manCount + "件" + " / 女性データ " + womanCount + "件");
   	            
            } else {
            	countTextView.setText("男性データ" + "-" + "件" + " / 女性データ" + "-"+ "件");
   	         }
        }
    }
	/*
	public void viewDialog(View v){
		new AlertDialog.Builder(this)
		.setTitle("お願い")
		.setMessage("Twitterにshareするとランキング機能が解放されます！")
		.setPositiveButton("つぶやく！",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						MainOn = true;
						mPrefEdit1.putBoolean("MainOn", MainOn);
						btnRanking = (ImageButton)findViewById(R.id.btnRanking);
						btnRanking.setVisibility(View.VISIBLE);
						mPrefEdit1.commit();
						sendShareTwit();
						
						
					}
				})
		  .setNegativeButton("いやだ",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						showIns();
					}
				}).show();
	}
	*/
	public void viewGo(View v){
			Intent i = new Intent(FamousPeopleActivity.this, FSRankingShowActivity.class);
			startActivity(i);
	}
	
	private void sendShareTwit() {
	    try {
	      
	    	Resources r = getResources();
			Bitmap bmp =  BitmapFactory.decodeResource(r, R.drawable.ad);//ここにshareしたい画像の名前を入れる(drawableに入れること)

		   savePNG(bmp, "/test.png");
		String filepath = "file:///"
						+ Environment.getExternalStorageDirectory().toString()
						+ "/test.png";
				Log.e("filepath", filepath);
				String postStr = "超本格な顔診断アプリ登場！顔認証システムを搭載したアプリで似ている有名人を調べちゃおう！\nダウンロードはここから▼\n http://click.adzcore.com/1.0.c29054f30553cfd2ae961b0c56e5882bc";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage("com.twitter.android");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath));
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT, postStr);
            startActivityForResult(intent, 2);
	        
	    } catch (final ActivityNotFoundException e) {
	    	
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("エラー");
            builder.setMessage("twitterアカウントを設定して下さい");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Uri uri =  Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android");
    		        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    		        startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
	    	
	    }
	}
	
	public static void savePNG(Bitmap bmp, String name) {
		try {
			//
			FileOutputStream fos = new FileOutputStream(Environment
					.getExternalStorageDirectory().toString() + name);
			bmp.compress(CompressFormat.PNG, 100, fos);
			Log.d("path", Environment
					.getExternalStorageDirectory().toString() + name);
			try {
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// インタースティシャル広告クリック通知
    
    public void onClick(NendAdInterstitialClickType clickType) {
        // クリックに応じて処理行う
        switch (clickType) {
        case CLOSE:
            break;
        case DOWNLOAD:
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
    
    public String GetDevicesUUID(Context mContext) {
    	final TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		
		 final String tmDevice, tmSerial, androidId;
		   tmDevice = "" + tm.getDeviceId();
		   tmSerial = "" + tm.getSimSerialNumber();
		   androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		
		   UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		   String deviceId = deviceUuid.toString();
		   Log.v("test devaiceID",deviceId);
        return deviceId;
	}
    
    public class ceres extends AsyncTask<String, String, String>{
		private Activity m_Activity;
		
		public ceres(Activity activity){
			this.m_Activity = activity;
		}
		
		@Override
		protected String doInBackground(String... urls) {
			// TODO 自動生成されたメソッド・スタブ
			StringBuilder builder = new StringBuilder();
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = null;
			HttpPost httppost = new HttpPost(urls[0]);
			try{
				response = httpClient.execute(httppost);
				StatusLine statusline = response.getStatusLine();
				if(statusline.getStatusCode() == 200){
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while((line = reader.readLine())!=null){
						Log.i("TEST OK_ver", line);
						builder.append(line);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			return builder.toString();
		}
		
		protected void onPostExecute(String result){
			message = result;
			
			if(message.equals("N")){
				Log.i("TEST N", message);
			}else{
				Log.i("TEST Y", message);
				
				AsyncGetAdInfo asyncGetInfo = new AsyncGetAdInfo(FamousPeopleActivity.this);		
				asyncGetInfo.execute();
				
				/*
				postData = "/?key=" + strAccessKey + "&adid=" + useruuid + "&optout_flg=1" + "&appid=" + strAppId;
				Log.i("test", postData);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl+postData));
			    startActivity(intent); 
			    */
				
				/*
				postData = "/?key=" + strAccessKey + "&adid=" + useruuid + "&optout_flg=1" + "&appid=" + strAppId;
				Log.i("test", postData);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl+postData));
			    startActivity(intent); 
			    
			    CeresPF = getSharedPreferences("CeresPF", MODE_PRIVATE);
				SharedPreferences.Editor editor = CeresPF.edit();
				editor.putString("CeresPF", "End");
				editor.commit();
				*/
			}
		}
	}

    @Override
    protected void onDestroy() {
    	Log.i("TEST main onDestroy", "onDestroy");
        super.onDestroy();
        /*
        if(isFinishing() == true){
        	closeSuruPass();
		}
		*/
        //mDialog.destroy();
    }
    
    @Override
    protected void onPause(){
    	Log.i("TEST main onPause", "onPause");
    	super.onPause();
    	/*
    	if(isFinishing() == true){
    		closeSuruPass();
		}
		*/
		
    }
    
    public class AsyncGetAdInfo extends AsyncTask<Void, Void, Info>
	{
		private Context _myContext = null;

	    public AsyncGetAdInfo(Context context) {
	    	this._myContext = context;
	    }
	    
		@Override
		protected Info doInBackground(Void... params) {
			// TODO 自動生成されたメソッド・スタブ
			try {
				info = AdvertisingIdClient.getAdvertisingIdInfo(_myContext);
				return info;
			} catch (IOException e) {
				// GooglePlayServicesへの接続が失敗
				return null;
			} catch (GooglePlayServicesNotAvailableException e) {
				// GooglePlayがデバイスにインストールされていなかった
				return null;
			} catch (GooglePlayServicesRepairableException e) {
				// GooglePlayServicesを使ううえでエラーが起きた
				return null;
			} catch (NoClassDefFoundError e) {
				// com.google.android.gmsがない
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Info info) {
			//super.onPostExecute(info);
			
			onGetAdInfo(info);
			
		}
	}
    
    /**************************************************
	 * CallBack
	 **************************************************/
	public void onGetAdInfo(Info info) {
		// TODO 自動生成されたメソッド・スタブ
		
		SharedPreferences user_data = getSharedPreferences("user_data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = user_data.edit();
		
		if(info != null) {
			String now_adid = info.getId();
//			String now_adid = "";
			String now_optout = "1";
			
			if(now_adid.length() == 0){
				Log.i("test", "length :: 0");
				now_adid = UUID.randomUUID().toString();
			}
			
			boolean testbool = info.isLimitAdTrackingEnabled();
			Log.i("test 1", String.valueOf(testbool));
			
			if(info.isLimitAdTrackingEnabled()){
				Log.i("test 2", String.valueOf(info.isLimitAdTrackingEnabled()));
				now_optout = "2";
			}else{
				now_optout = "1";
			}
			
			boolean fChgADID = false;
			if(!_adid.equals(now_adid)){
				// ADIDが変わった
				fChgADID = true;
				_adid = now_adid;
			}
			if(!_optout.equals(now_optout)){
				fChgADID = true;
				_optout = now_optout;
			}
			
			editor.putString("adid", _adid);
			editor.putString("optout", _optout);
			editor.apply();
			
			Log.d("AVPAPP", "AdID:" + _adid);
			Log.d("AVPAPP", "optout:" + _optout);
			
			if(!now_adid.equals(_adid) || fChgADID == true){
				// 初回起動
				/*
				_uuid = UUID.randomUUID().toString();
				editor.putString("uuid", _uuid);
				editor.apply();
				*/
				// ブラウザ起動
				String stUrl = strUrl + "/?key=" + strAccessKey + "&adid=" + _adid + "&optout_flg=" + _optout + "&appid=" + strAppId;
				Log.i("test ::", stUrl);
				
				/*
				CeresPF = getSharedPreferences("CeresPF", MODE_PRIVATE);
				SharedPreferences.Editor Cereseditor = CeresPF.edit();
				Cereseditor.putString("CeresPF", "End");
				Cereseditor.commit();
				*/
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stUrl)); 
		        startActivity(intent);
		        
			}else{
				Log.i("test ::", "return");
				return;
			}
		}else{
			Log.i("test ::", "info null");
			return;
		}
	}
}
