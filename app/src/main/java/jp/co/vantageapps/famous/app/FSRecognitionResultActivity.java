package jp.co.vantageapps.famous.app;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.nend.android.NendAdInterstitial;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import it.partytrack.sdk.Track;

public class FSRecognitionResultActivity extends SuruPassActivity {
	//NendDialog mDialog;
	FamousPeopleActivity famousPepole;
	private final int LINE_ID = 0;
    private final int FACEBOOK_ID = 1;
    private final int TWITTER_ID = 2;
    private final String[] sharePackages = {"jp.naver.line.android", "com.facebook.katana", "com.twitter.android"};
    private boolean ResultOn = false;
    
    private String postMsg;
    private String score;
    private String name;
    private String job;
    private String url;
    
    private SharedPreferences mPref2;
	private SharedPreferences.Editor mPrefEdit2;

	float scale = 1.0f;
	TextView textViewSimilarity;
	TextView textViewName;
	TextView textViewKind;
	//ImageButton btnshare;
	private ImageButton btnmain, btnrankingregisterlock, btnrankingregister;
	private ImageView s_twitter;
	ImageButton btngo;
	
	MyView myView;
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognitionresult);
		
		//mDialog = new NendDialog(this);
		
		//mDialog.show();
		
		//lock
		mPref2 = getSharedPreferences("ResultOn", 0);
		mPrefEdit2 = mPref2.edit();

		
		Track.event(35755);
		
		myView = new MyView(this, ((GlobalClass) getApplication()).getFace());
		LinearLayout recognizedFaceView = (LinearLayout) findViewById(R.id.recognizedFaceView);
		recognizedFaceView.addView(myView);
		
		textViewSimilarity = (TextView) findViewById(R.id.textview_similarity);
		textViewSimilarity.setText(((GlobalClass) getApplication()).getRecognitionResult().get("score"));
		
		textViewName = (TextView) findViewById(R.id.textView_name);
		
		String name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
		name = name.replaceAll("\n", "");
		textViewName.setText(name);
		
		textViewKind = (TextView) findViewById(R.id.textView_kind);
		String job = ((GlobalClass) getApplication()).getRecognitionResult().get("job");
		job = job.replaceAll("\n", "");
		textViewKind.setText(job);
		
		//btnshare = (ImageButton)findViewById(R.id.btn_share);
		//btnshare.setOnClickListener(new View.OnClickListener() {
			
		//	@Override
		//	public void onClick(View v) {
				//ã‚·ã‚§ã‚¢æ©Ÿèƒ½
		//		share();
		//	}
		//});
		
		btnmain = (ImageButton)findViewById(R.id.btn_gostart);
		btnmain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					//closeSuruPass();
				//showSuruPass(SuruPassType.InterstitialResult);
					// TODO Auto-generated method stub
					onBackPressed();
			}
		});
				
		btngo = (ImageButton)findViewById(R.id.btn_go);
		btngo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					
					String url = ((GlobalClass) getApplication()).getRecognitionResult().get("url");
					Uri uri = Uri.parse(url);
					Intent viewIntent = new Intent("android.intent.action.VIEW", uri);
					startActivity(viewIntent);
					//mDialog.dismiss();
				}catch (Exception e) {
					// TODO: handle exception
				}					
			}
		});
		
		Bundle extra = getIntent().getExtras();
		if(extra.getBoolean("RollFinishEffect") == true)	    
			CalUtility.playaudio(this, "sound effect/roll-finish1.mp3");
		
		//showSuruPass(SuruPassType.TopBanner);
		//showSuruPass(SuruPassType.BottomBanner);
		//showSuruPass(SuruPassType.InterstitialResult);
		
		if(isFinishing() == false){
			showSuruPass(SuruPassType.TopBanner);
			//showSuruPass(SuruPassType.BottomBanner);
			showSuruPass(SuruPassType.InterstitialResult);
		}else{
			closeSuruPass();
		}
	}
	
	//lock
	public void s_twitter(View v){
		ResultOn = true;
		mPrefEdit2.putBoolean("ResultOn", ResultOn);
        btnrankingregister = (ImageButton)findViewById(R.id.btn_rankingregister);
		btnrankingregister.setVisibility(View.VISIBLE);
		score = ((GlobalClass) getApplication()).getRecognitionResult().get("score");
		name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
		job = ((GlobalClass) getApplication()).getRecognitionResult().get("job");
		url = ((GlobalClass) getApplication()).getRecognitionResult().get("url");
		if(appInstalled(TWITTER_ID)){
			Resources r = getResources();
			Bitmap bmp =  BitmapFactory.decodeResource(r, R.drawable.ad);
			famousPepole.savePNG(bmp, "/test.png");
			String filepath = "file:///"
					+ Environment.getExternalStorageDirectory().toString()
					+ "/test.png";
			Log.e("filepath", filepath);
			//String postStr = "ギフトコード3000円分もらえてワロタｗｗｗ　http://click.adzcore.com/1.0.273abf31626c5681d433aedf9843995bc";
			String nameR = name.replace("\n", " ");
			String postStr = "顔の似ている有名人を診断したよ！\r\n『"+nameR+"』に似てるらしいヾ(＠°▽°＠)ﾉ\r\nシンクロ率は"+score+"％ だって！みんなもやってみる？ http://click.adzcore.com/1.0.c29054f30553cfd2ae961b0c56e5882bc";
			Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage(sharePackages[TWITTER_ID]);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath));
            intent.putExtra(Intent.EXTRA_TEXT, postStr);
            startActivityForResult(intent, TWITTER_ID);
        }else {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("エラー");
            builder.setMessage("twitterアカウントを設定して下さい");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    shareAppDl(TWITTER_ID);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
		mPrefEdit2.commit();
	}
	
	public void s_line(View v){
		ResultOn = true;
		mPrefEdit2.putBoolean("ResultOn", ResultOn);
		btnrankingregister = (ImageButton)findViewById(R.id.btn_rankingregister);
		btnrankingregister.setVisibility(View.VISIBLE);
		if(appInstalled(LINE_ID)) {
			
			//String postStr = "ギフトコード3000円分もらえてワロタｗｗｗ　http://click.adzcore.com/1.0.a56ae682dc4ccf1fc701d9571ccab2dec";
			name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
			score = ((GlobalClass) getApplication()).getRecognitionResult().get("score");
			name = name.replace("\n", "");
			String postStr = "顔の似ている有名人を診断したよ！\r\n『"+name+"』に似てるらしいヾ(＠°▽°＠)ﾉ\r\nシンクロ率は"+score+"％ だって！みんなもやってみる？http://click.adzcore.com/1.0.c29054f30553cfd2ae961b0c56e5882bc";
			String s;
			try {
				s = URLEncoder.encode(postStr, "UTF-8");
				String lineString = "line://msg/text/" + s;  
				Intent intent = Intent.parseUri(lineString, Intent.URI_INTENT_SCHEME);  
				startActivityForResult(intent, LINE_ID);				
				
			 	long now = System.currentTimeMillis();
	        	Date date = new Date(now);
	        	SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy/MM/dd");
	        	String dataStr = dateFormat.format(date);
	        	
            	SharedPreferences sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = sharedPref.edit();
            	editor.putString("KEY_SHARE_4", dataStr);
            	editor.commit();
			} catch (Exception e) {  
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("エラー");
	            builder.setMessage("LINE投稿失敗");
	            builder.setCancelable(true);
	            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
	            AlertDialog alert = builder.create();
	            alert.show();
			} 
		}else {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("エラー");
            builder.setMessage("LINEアプリをダウンロードして下さい");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    shareAppDl(LINE_ID);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
		}
		mPrefEdit2.commit();
	}
	
	public void s_facebook(View v){
		ResultOn = true;
		mPrefEdit2.putBoolean("ResultOn", ResultOn);
		btnrankingregister = (ImageButton)findViewById(R.id.btn_rankingregister);
		btnrankingregister.setVisibility(View.VISIBLE);
		score = ((GlobalClass) getApplication()).getRecognitionResult().get("score");
		name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
		job = ((GlobalClass) getApplication()).getRecognitionResult().get("job");
		url = ((GlobalClass) getApplication()).getRecognitionResult().get("url");
		if(appInstalled(FACEBOOK_ID)){
			//String postStr = "ギフトコード3000円分もらえてワロタｗｗｗ　http://click.adzcore.com/1.0.68d50822dadf441e281f2c3c1df53029c";
			String nameR = name.replace("\n", "");
			String postStr = "顔の似ている有名人を診断したよ！\r\n『"+nameR+"』に似てるらしいヾ(＠°▽°＠)ﾉ\r\nシンクロ率は"+score+"％ だって！みんなもやってみる？http://click.adzcore.com/1.0.c29054f30553cfd2ae961b0c56e5882bc";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage(sharePackages[FACEBOOK_ID]);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, postStr);
            startActivityForResult(intent, FACEBOOK_ID);
        }else {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("エラー");
            builder.setMessage("facebookアカウントを設定して下さい");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    shareAppDl(FACEBOOK_ID);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
		mPrefEdit2.commit();
	}
	
	private boolean appInstalled(int shareId)
	{
		try {
            PackageManager pm = this.getPackageManager();
            pm.getApplicationInfo(sharePackages[shareId], PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
	}
	
	private void shareAppDl(int shareId){
        //Uri uri = Uri.parse("market://details?id="+sharePackages[shareId]);
		Uri uri = null;
		switch(shareId) {
			case TWITTER_ID:
				//uri = Uri.parse("http://click.adzcore.com/1.0.4d440622b90da22d1e88fd067a04b1e0c");
				uri = Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android");
				break;
			case FACEBOOK_ID:
				//uri = Uri.parse("http://click.adzcore.com/1.0.4c322613e84cffeb3544b51e2a27c953c");
				uri = Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana");
				break;
			case LINE_ID:
				//uri = Uri.parse("http://click.adzcore.com/1.0.9feae6fd083daf47ea38d63d19985f09c");
				uri = Uri.parse("https://play.google.com/store/apps/details?id=jp.naver.line.android");
				break;
		}
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
	
	private void share()
	{
		score = ((GlobalClass) getApplication()).getRecognitionResult().get("score");
		name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
		job = ((GlobalClass) getApplication()).getRecognitionResult().get("job");
		url = ((GlobalClass) getApplication()).getRecognitionResult().get("url");

		score = score.replaceAll("\n", "");
		name = name.replaceAll("\n", "");
		job = job.replaceAll("\n", "");
		
		//String postMsg = "ä¼¼ã�¦ã�„ã‚‹æœ‰å��äºº : " + name + "\nã‚·ãƒ³ã‚¯ãƒ­çŽ‡ : " + score + "\nè�·æ¥­ : " + job  + " \n(" + url + ")";
		postMsg = "顔の似ている有名人を診断したよ！\r\n『"+name+"』に似てるらしいヾ(＠°▽°＠)ﾉ\r\nシンクロ率は"+score+"％ だって！みんなもやってみる？";
		try {
			shareByIntent(postMsg);
			//startActivity(new SNSshare_Intent(postMsg));
		} catch (Exception e) {
			Log.d("e", "Error");
		}
	}
	
	private void shareByIntent(String msg)
	{
		try{
			//ãƒ¬ã‚¤ã‚¢ã‚¦ãƒˆï¿½?ï¿½ã‚‰bitmapãƒ‡ãƒ¼ã‚¿ã‚’å¼•ï¿½??å‡ºï¿½?ï¿½
			LinearLayout recognizedFaceView = (LinearLayout) findViewById(R.id.recognizedFaceView);
			
			recognizedFaceView.setDrawingCacheEnabled(true);
			Bitmap bmp = Bitmap.createBitmap(recognizedFaceView.getDrawingCache());
						
			new Share().shareMsg(FSRecognitionResultActivity.this, "share", "Share Data", msg, bmp);
			
//			Intent share = new Intent(Intent.ACTION_SEND);
//			share.setAction(Intent.ACTION_SEND);
//			//share.setType("text/plain");
//			share.setType("image/*");//imageã‚’è¡¨ç¤ºï¿½?ï¿½ï¿½??ã‚‹ã‚¿ã‚¤ãƒ—ï¿½?ï¿½å¤‰æ›´
//			share.putExtra(Intent.EXTRA_STREAM, image);//imageã‚’åŸ‹ï¿½?è¾¼ï¿½?ï¿½
//			share.putExtra(Intent.EXTRA_SUBJECT, "Share Data");
//			share.putExtra(Intent.EXTRA_TEXT, msg);
//			startActivity(Intent.createChooser(share, "Share Data"));
		} catch (Exception e) {
	        Log.d("e", "Error");
	    }
	}

	private class MyView extends View {
		public Bitmap image;

		public MyView(Context context, final Bitmap image) {
			super(context);
			this.image = image;
			LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			this.setLayoutParams(param);
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			Rect clipRect = canvas.getClipBounds();
			int w = clipRect.width();
			// int wPortion = w / 10;
			// w = wPortion * 6;
			int h = clipRect.height();
			int imgW = image.getWidth();
			int imgH = image.getHeight();

			float scaleX = ((float) w) / imgW;
			float scaleY = ((float) h) / imgH;
			scale = scaleX;
			if (scaleX > scaleY)
				scale = scaleY;
			imgW = (int) (scale * imgW);
			imgH = (int) (scale * imgH);

			Rect dst = new Rect();
			dst.left = (w - imgW) / 2;
			dst.top = (h - imgH) / 2;
			dst.right = dst.left + imgW;
			dst.bottom = dst.top + imgH;

			canvas.drawBitmap(image, null, dst, null);

			// Paint paint = new Paint();
			// paint.setColor(Color.GREEN);
			// paint.setStyle(Paint.Style.FILL);
			// paint.setTextScaleX(3);
			// paint.set
			// canvas.drawText("similarity", w + 5, h * 2 / 5, paint);
		}
	}
	
	public void rankG(View v){
		String name = ((GlobalClass) getApplication()).getRecognitionResult().get("name");
		if(name.equals("") == true)
		{
			new AlertDialog.Builder(FSRecognitionResultActivity.this)
        	.setTitle("登録")
	        .setMessage("結果がありません!")
	        .setPositiveButton("はい", null)
	        .create().show();
		}else{
			// TODO Auto-generated method stub
			Intent i = new Intent(FSRecognitionResultActivity.this, FSRankingRegisterActivity.class);
			startActivity(i);
		}
	}
	
	public void viewDialog(View v){
		
		new AlertDialog.Builder(this)
		.setTitle("注意")
		.setMessage("ランキングに登録するにはSNSにシェアをするとランキング登録が解放されます！")
		  .setNegativeButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						
					}
				}).show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(FSRecognitionResultActivity.this, FamousPeopleActivity.class);
		startActivity(i);
		finish();
		//mDialog.dismiss();
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
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        //mDialog.destroy();
        /*
        if(isFinishing() == true){
    		closeSuruPass();
		}
		*/
    }	
}
