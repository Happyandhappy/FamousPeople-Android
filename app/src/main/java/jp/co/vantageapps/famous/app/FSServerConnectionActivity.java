package jp.co.vantageapps.famous.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;




import com.five_corp.ad.*;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class FSServerConnectionActivity extends Activity implements View.OnLayoutChangeListener, AnimationListener{
 
	
	public enum JsonType {
		JsonNone,
		JsonStart,
		JsonProcessing,
		JsonComplete
	};	
	
	private JsonType _jsonType = JsonType.JsonStart;
    //private ProgressDialog pDialog;

    private ImageView takeImageView;
    private ImageView scopeImageView;
    private TextView txtCountdown;
    private ImageButton resultBtn;

    private Rect takeImagRect = null, topRect = null, leftRect = null, scopeRect = null;
   
    public static final String TAG_RESULT = "result";
    public static final String TAG_COUNT = "count";
    public static final String TAG_URL = "url";
    public static final String TAG_JOB = "job";
    public static final String TAG_NAME = "name";
    public static final String TAG_SCORE = "score";
    public static final String TAG_FAMOUSID = "famousid";
    public static final String TAG_RARE = "rare";
    public static float Duration = 2500; // ms.
    
    HashMap<String, String> recognitionResult = null;
    boolean isSuccess = false;
    Activity mainActivity;
    Recognize recognize = null;
    LinearLayout topLayout;
    LinearLayout leftLayout;
    
    int iround, roundCount;
	int takewidth, takeheight, viewleft, viewtop, scopeW, scopeH, originX,originY;
	float unitY;
	
	
	
	FiveAdW320H180 w320h180;
	LinearLayout fiveAdLayout;
    
    private synchronized JsonType getJsonType()
	{
		return _jsonType;
	}
	
	private synchronized void setJsonType(JsonType jsontype)
	{
		_jsonType = jsontype;
	}
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	final Activity activity = this;
    	/*
    	FiveAdConfig config = new FiveAdConfig("2");
    	config.formats = EnumSet.of(
    			FiveAdFormat.INTERSTITIAL_LANDSCAPE, // インタースティシャル（横向き）
    			FiveAdFormat.INTERSTITIAL_PORTRAIT, // インタースティシャル（縦向き）
    			FiveAdFormat.IN_FEED, // インフィード
    			FiveAdFormat.BOUNCE, // バウンス
    			FiveAdFormat.W320_H180, // 横320x縦180の動画枠
    			FiveAdFormat.W300_H250 // 横300x縦250の動画枠
    	);
    	//config.isTest = true;
    	FiveAd.initialize(this, config);
    	FiveAd fiveAd = FiveAd.getSingleton();
    	fiveAd.enableLoading(true);
    	*/

//		w320h180 = new FiveAdW320H180(activity, "9706");
//		w320h180.setListener(new Listener(activity, "W320H180"));
//		w320h180.loadAd();
        
    	Log.e("CRASH", "FSServerConnectionActivity: onCreate1");
	    
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverconnection);
               
//		fiveAdLayout = (LinearLayout)findViewById(R.id.fiveAd);
//		fiveAdLayout.addView(w320h180);

        
        Log.e("CRASH", "FSServerConnectionActivity: onCreate2");
        mainActivity = this;
               
        topLayout = (LinearLayout)findViewById(R.id.top_layout);
        topLayout.addOnLayoutChangeListener(this);
        
        leftLayout = (LinearLayout)findViewById(R.id.left_layout);        
        leftLayout.addOnLayoutChangeListener(this);
        
        scopeImageView = (ImageView)findViewById(R.id.scopeImageView);
        scopeImageView.addOnLayoutChangeListener(this);
        
        Log.e("CRASH", "FSServerConnectionActivity: onCreate3");
        takeImageView = (ImageView)findViewById(R.id.takeImageView);
        takeImageView.setImageBitmap(((GlobalClass) getApplication()).getFace());
        takeImageView.addOnLayoutChangeListener(this);
        
        Log.e("CRASH", "FSServerConnectionActivity: onCreate4");
        
        resultBtn = (ImageButton)findViewById(R.id.btnresult);
        resultBtn.setVisibility(View.INVISIBLE);
        resultBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try {
		    		AssetFileDescriptor afd = getAssets().openFd("sound effect/robot-startup2.mp3");
		            MediaPlayer player = new MediaPlayer();
		            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
		                    afd.getLength());
		            player.prepare();
		            player.start();
		            player.setOnCompletionListener(new OnCompletionListener() {
		                @Override
		                public void onCompletion(MediaPlayer mp) {
		                    // TODO Auto-generated method stub
		                    mp.release();
		                    Intent intent = getIntent();
		      	            setResult(RESULT_OK, intent);
		      	            ((GlobalClass)getApplication()).setRecognitionResult(recognitionResult);
		      	            finish();
		                }
		            });
		            player.setLooping(false);
		        } catch (IOException e) {
		        	// TODO Auto-generated catch block
		        	e.printStackTrace();
		        } 
			}
		});
        
        txtCountdown = (TextView)findViewById(R.id.txtCountdown);
        txtCountdown.setVisibility(View.VISIBLE);
        
        Log.e("CRASH", "FSServerConnectionActivity: onCreate5");
        
    	float interval = 30; // duration = interval * 100.
        CountTimerThread st = new CountTimerThread(interval);
		st.start();
		Log.e("CRASH", "FSServerConnectionActivity: onCreate6");
        
        // json start.
		recognize = new Recognize();
		recognize.execute();
		Log.e("CRASH", "FSServerConnectionActivity: onCreate7");
        
        // play computer1.mp3
        CalUtility.playaudio(this, "sound effect/computer1.mp3");
      
        Log.e("CRASH", "FSServerConnectionActivity: onCreate8");
    }
    
	private class CountTimerThread extends Thread{
			
		float interval ;
		public CountTimerThread(float interval) { // ONLY WORKS AFTER SAVING
			this.interval = interval;
		}
			
		@Override 
		public void run(){
			
			try{
				Thread.sleep((long) 1000);
			}catch(Exception e) {}
			
			int percent = 0;
			while (true)
			{
				try{
					Thread.sleep((long) interval);
				}catch(Exception e) {}
				
				percent++;
				
				handler.percent = percent;
				handler.sendEmptyMessage(0);
				
				if (percent== 100){
					while (true) {
						if(getJsonType() == JsonType.JsonComplete)
							break;	    			
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					handler.percent = 101;
					handler.sendEmptyMessage(0);
					break;
				}
			}
		}
		
		private MsgHander handler = new MsgHander();
		
		@SuppressLint("HandlerLeak")
		public class MsgHander extends Handler{
			int percent = 0;
			public void handleMessage(Message msg) {
				
				if (percent <= 100){
					String str = "カイセキチュウ... " + String.valueOf(percent) + "%";
					txtCountdown.setText(str);	
				}else if (percent == 101){
					scopeImageView.setVisibility(View.INVISIBLE);
					txtCountdown.setVisibility(View.INVISIBLE);    	
					if(isSuccess){
			        	resultBtn.setVisibility(View.VISIBLE);
			        } else{
			        	//recognize.cancel(true);
			        	Intent intent = getIntent();
			            setResult(RESULT_CANCELED, intent);
			            finish();
			        }
				}
			}
		}
	}
    
    @Override
	public void onLayoutChange(View arg0, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		// TODO Auto-generated method stub
    	
    	if (arg0 == topLayout)
    	{
    		if (topRect == null)
				topRect = new Rect(left, top, right, bottom);
    	}
    	
    	if (arg0 == leftLayout)
    	{
    		if (leftRect == null)
    			leftRect = new Rect(left, top, right, bottom);
    	}
    	
    	if (arg0 == scopeImageView)
    	{
    		 if (scopeRect == null)
				scopeRect = new Rect(left, top, right, bottom); 
    			
    	}
    	
    	if (arg0 == takeImageView)
    	{
    		if (takeImagRect == null)
				takeImagRect = new Rect(left, top, right, bottom);    			
    	}
    		
		if (takeImagRect != null && topRect != null && leftRect != null && scopeRect!=null)
		{
			takewidth = takeImagRect.width();
    		takeheight = takeImagRect.height();
    		viewleft = leftRect.width();
    		viewtop = topRect.height();
    		scopeW = scopeRect.width();
			scopeH = scopeRect.height();
			originX = viewleft + 7;
			originY = viewtop + 7;
			unitY = ((float)takeheight / roundCount);
    		roundCount = 10;
    		iround = 0;
    		setScopeAnimation(iround, roundCount);
		}
	}
    
    private void setScopeAnimation (int iround, int rounCount )
    {
    	int startX, endX, startY, endY;
		startX = originX + takewidth * (iround % 2);
		endX = originX + takewidth - takewidth * (iround % 2);
		
		startY = originY + (int)(iround * unitY);
		endY = originY + (int)((iround+1) * unitY);
		if (endY >= (originY + takeheight))
			endY = originY + takeheight;
		
		startX -= scopeW/2;
		endX -= scopeH/2;
		startY -= scopeW/2;
		endY -= scopeH/2;
		
		TranslateAnimation transAnim = new TranslateAnimation(startX,endX,startY,endY);
		transAnim.setRepeatCount(0);
    	transAnim.setDuration((long) (Duration * 0.8 / roundCount));
    	transAnim.setAnimationListener(this);
    	scopeImageView.startAnimation(transAnim);
    }
 	  
    @Override
	public void onBackPressed() {
		
	}
 
    private class Recognize extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
        	setJsonType(JsonType.JsonStart);
        	isSuccess = false;
            super.onPreExecute();
            /*pDialog = new ProgressDialog(FSServerConnectionActivity.this);
            pDialog.setMessage("ï¿½?ï¿½ï¿½?ï¿½ã‚‰ï¿½??ï¿½?ï¿½å¾…ï¿½?ï¿½ï¿½??ï¿½?ï¿½ï¿½?ï¿½ï¿½?ï¿½...");
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

//		// Get data from server
//        @SuppressLint("DefaultLocale")
//		@Override
//        protected Void doInBackground(Void... arg0) {
//
//        	setJsonType(JsonType.JsonProcessing);
//            ServiceHandler sh = new ServiceHandler();
//            String genderString = new String();
//            switch(((GlobalClass)getApplication()).getGender()) {
//            case 1:
//            	genderString = "woman";
//            	break;
//            case 2:
//            	genderString = "any";
//            	break;
//            case 3:
//            	genderString = "man";
//            	break;
//            }
//
//
//            String featureSizeString = String.format("%d", ((GlobalClass)getApplication()).getDetectionResult().getFaceData().length);
//            String featureDataString = ((GlobalClass)getApplication()).getDetectionResult().getFaceDataString();
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>(7);
//            params.add(new BasicNameValuePair("commandType", "famous_request"));
//            params.add(new BasicNameValuePair("gender", genderString));
//            params.add(new BasicNameValuePair("featureSize", featureSizeString));
//            params.add(new BasicNameValuePair("featuredata", featureDataString));
//            Log.e("FeatureData:", featureDataString);
//
//			String key1 = ((GlobalClass)getApplication()).getkey1();
//			String key2 = ((GlobalClass)getApplication()).getkey2();
//			String srare = ((GlobalClass)getApplication()).getrare();
//			params.add(new BasicNameValuePair("key1", key1));
//			params.add(new BasicNameValuePair("key2", key2));
//			params.add(new BasicNameValuePair("rare", srare));
//
//            String jsonStr = sh.makeServiceCall(GlobalClass.SERVER_URL, ServiceHandler.POST, params);
//
//            if (jsonStr != null) {
//
//            	try {
//                	JSONObject jsonObj = new JSONObject(jsonStr);
//                    String result = jsonObj.getString(TAG_RESULT);
//
//                    if(result.equals("ok")) {
//
//                    	String url = "";
//                    	String job = "";
//                    	String name = "";
//                    	String score = "";
//                    	String famousid = "";
//                    	String rare = "";
//
//                    	String  count = jsonObj.getString(TAG_COUNT);
//                    	int nCount = Integer.parseInt(count);
//                    	//int nCount = jsonObj.getInt(TAG_COUNT);
//                    	if (nCount != 0)
//                    	{    url = jsonObj.getString(TAG_URL);
//	                    	 job = jsonObj.getString(TAG_JOB);
//	                    	 name = jsonObj.getString(TAG_NAME);
//	                    	 score = jsonObj.getString(TAG_SCORE);
//	                    	 famousid = jsonObj.getString(TAG_FAMOUSID);
//	                    	 rare = jsonObj.getString(TAG_RARE);
//                    	}
//
//                    	recognitionResult = new HashMap<String, String>();
//                    	recognitionResult.put(TAG_URL, url);
//                    	recognitionResult.put(TAG_JOB, job);
//                    	recognitionResult.put(TAG_NAME, name);
//                    	recognitionResult.put(TAG_SCORE, score);
//                    	recognitionResult.put(TAG_FAMOUSID, famousid);
//                    	recognitionResult.put(TAG_RARE, rare);
//                    	isSuccess = true;
//                    	setJsonType( JsonType.JsonComplete);
//
//                    }else {
//                    	setJsonType( JsonType.JsonComplete);
//
//                    }
//
//                } catch (JSONException e) {
//
//                	setJsonType( JsonType.JsonComplete);
//                    e.printStackTrace();
//                }
//            } else {
//            	setJsonType( JsonType.JsonComplete);
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }
//            return null;
//        }

        // Get data from local db
        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Void... arg0) {

            setJsonType(JsonType.JsonProcessing);

			String url = "";
			String job = "";
			String name = "";
			String score = "";
			String famousid = "";
			String rare = "";
			double highScore = 0.0f;

            String genderString = new String();
            switch(((GlobalClass)getApplication()).getGender()) {
                case 1:
                    genderString = "woman";
                    break;
                case 2:
                    genderString = "any";
                    break;
                case 3:
                    genderString = "man";
                    break;
            }
            String key1 = ((GlobalClass)getApplication()).getkey1();
            String key2 = ((GlobalClass)getApplication()).getkey2();
            String srare = ((GlobalClass)getApplication()).getrare();

            DatabaseAdapter mDbHelper = new DatabaseAdapter(FSServerConnectionActivity.this);
            mDbHelper.createDatabase();
            mDbHelper.open();

            Cursor mCur = mDbHelper.getSimilarFamousPeople(genderString, key1, key2, srare);

			byte[] featuredata1 = ((GlobalClass)getApplication()).getDetectionResult().getFaceData();

			if(mCur.getCount()>0){
				if(mCur.moveToFirst()){
					do{
						String tempfeaturedata = mCur.getString(mCur.getColumnIndex("featuredata"));
						String[] data_arr = tempfeaturedata.split(",");

						byte[] featuredata2 = new byte[data_arr.length];
						for(int i = 0; i < data_arr.length; i++)
							featuredata2[i] = (byte)(Integer.parseInt(data_arr[i]));

						double match_score = DetectorReal.identify(featuredata1, featuredata2);

						if(match_score > highScore)
						{
							highScore = match_score;

							url = mCur.getString(mCur.getColumnIndex(TAG_URL));
							job = mCur.getString(mCur.getColumnIndex(TAG_JOB));
							name = mCur.getString(mCur.getColumnIndex(TAG_NAME));
							score = String.format("%.1f%%", highScore * 100);
							famousid = mCur.getString(mCur.getColumnIndex("id"));
							rare = mCur.getString(mCur.getColumnIndex(TAG_RARE));

							isSuccess = true;
						}

					}while (mCur.moveToNext());
				}
			}

            mDbHelper.close();

			recognitionResult = new HashMap<String, String>();
			recognitionResult.put(TAG_URL, url);
			recognitionResult.put(TAG_JOB, job);
			recognitionResult.put(TAG_NAME, name);
			recognitionResult.put(TAG_SCORE, score);
			recognitionResult.put(TAG_FAMOUSID, famousid);
			recognitionResult.put(TAG_RARE, rare);

			setJsonType( JsonType.JsonComplete);

            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/
            //isSuccess = true;
            setJsonType( JsonType.JsonComplete);
        }
    }

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		iround++;
		if (iround <= roundCount)
			setScopeAnimation(iround, roundCount);
		else
			scopeImageView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

}