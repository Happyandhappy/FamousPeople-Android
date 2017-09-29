package jp.co.vantageapps.famous.app;


import java.util.Timer;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class FSAnalyseResultActivity extends Activity {

	ImageView rareImageView;
	Bitmap rareBmp;
	Bitmap angleBitmap;
	Rect r;
	
	Timer playTimer = new Timer();
	long tmBegin, tmNow;
	
	float startAngle = -360;
	float endAngle = 0;
	float startScale = 0.1f;
	float endScale = 1.0f;
	float interval = 100; // ms
	float duration = 2200; // ms
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyseresults);
		
		r = null;
		rareImageView = (ImageView)findViewById(R.id.rareImageView);
		String rare = ((GlobalClass) getApplication()).getRecognitionResult().get("rare");
		int result_id = R.drawable.result_a;
		if (rare.equals("a"))
		{
			result_id = R.drawable.result_a;
		}
		else if (rare.equals("b"))
		{
			result_id = R.drawable.result_b;
		}	
		else if (rare.equals("c"))
		{
			result_id = R.drawable.result_c;
		}
		else if (rare.equals("d"))
		{
			result_id = R.drawable.result_d;
		}
		else if (rare.equals("e"))
		{
			result_id = R.drawable.result_e;
		}
		else if (rare.equals("s"))
		{
			result_id = R.drawable.result_s;
		}
		else if (rare.equals("z"))
		{
			result_id = R.drawable.result_z;
		}
		
		rareBmp = CalUtility.getResoureBimap(getResources(), result_id);//BitmapFactory.decodeResource(getResources(), result_id);
		rareImageView.setImageBitmap(rareBmp);		
		rareImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View arg0, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (r == null) {
					r = new Rect(left, top, right, bottom);
					AnimationSet sets = new AnimationSet(false);
					//AlphaAnimation alphaAnim = new AlphaAnimation(0.5f, 1.0f);
					RotateAnimation rotateAnim = new RotateAnimation(0f, 360f, r.width()/2, r.height()/2);
					rotateAnim.setInterpolator(new LinearInterpolator());
					int rotateTime = 3;
					rotateAnim.setRepeatCount((rotateTime - 1));
					rotateAnim.setDuration((long) (duration * 1.1f) / rotateTime);
					ScaleAnimation scaleAnim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					scaleAnim.setDuration((long) (duration * 1.1f));
					//sets.addAnimation(alphaAnim);
					sets.addAnimation(rotateAnim);
					sets.addAnimation(scaleAnim);
					
					sets.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							
							try {
								Thread.sleep((long) 300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Intent i = new Intent(FSAnalyseResultActivity.this, FSRecognitionResultActivity.class);
							i.putExtra("RollFinishEffect", true);
							startActivity(i);
							finish();
						}
					});
					
					rareImageView.startAnimation(sets);
				}
			}
		});
		
		CalUtility.playaudio(this, "sound effect/drumroll.m4a");
	}
	
	 @Override
	public void onBackPressed() {
	}	
}
