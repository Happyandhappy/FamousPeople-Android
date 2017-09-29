package jp.co.vantageapps.famous.app;

import jp.co.vantageapps.famous.app.FaceDetectionActivity.ActivityType;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GifFaceMaskActivity extends Activity implements View.OnClickListener {

	Bitmap facePicture;
	Bitmap maskedFace;
	ImageView maskImageView;
	ImageButton[] maskbtns = new ImageButton[4];
	ImageButton okBtn;
	ImageButton backbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gif_face_mask);
		
		okBtn = (ImageButton)findViewById(R.id.okbtn);
		okBtn.setOnClickListener(this);
		maskImageView = (ImageView)findViewById(R.id.maskImageView);
		
		final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
		facePicture = globalVariable.getFace();
		
		
		maskbtns[0] = (ImageButton)findViewById(R.id.maskbtn01);
		maskbtns[1] = (ImageButton)findViewById(R.id.maskbtn02);
		maskbtns[2] = (ImageButton)findViewById(R.id.maskbtn03);
		maskbtns[3] = (ImageButton)findViewById(R.id.maskbtn04);

		selectbtn(((GlobalClass) getApplicationContext()).getMaskSelNumber());
		Bitmap oldMaskface = globalVariable.getMaskedFace();
		if (oldMaskface == null){
			maskedFace = getMaskedFace(facePicture, R.raw.mask1);
			globalVariable.setMaskedFace(maskedFace);
		}else{
			maskedFace = oldMaskface;
		}
		
		maskImageView.setImageBitmap(maskedFace);
		
		//maskImageView.setImageBitmap(facePicture);
		
		backbtn = (ImageButton)findViewById(R.id.backbtnmask);
		backbtn.setOnClickListener(this);
		
		for (int i = 0; i < 4; i++){
			maskbtns[i].setOnClickListener(this);
		}
	}
	
	
	@SuppressLint("NewApi")
	public void onClick(View view) {
		
		
		ImageButton obj = (ImageButton)view;
		
		if (backbtn == obj){
			// TODO Auto-generated method stub
			onBackPressed();
            return;
		}
		
		if (obj == okBtn){
			final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
			globalVariable.setMaskedFace(maskedFace);
			globalVariable.clearConfigGifs();			
			Intent nextScreen = new Intent(GifFaceMaskActivity.this, GifFactoryActivity.class);
        	startActivity(nextScreen);
        	finish();
			return;
		}		
		

		int id  = R.raw.mask1;
		if (obj == maskbtns[0])
		{
			id = R.raw.mask1;
			selectbtn( 0);
		}
		else if (obj == maskbtns[1])
		{
			id = R.raw.mask2;
			selectbtn( 1);
		}
		else if (obj == maskbtns[2])
		{
			id = R.raw.mask3;
			selectbtn( 2);
		}
		else if (obj == maskbtns[3])
		{
			id = R.raw.mask4;
			selectbtn( 3);
		}
				
		maskedFace = getMaskedFace(facePicture, id);
		maskImageView.setImageBitmap(maskedFace);
	}
	
	private void selectbtn( int selNo){
		((GlobalClass) getApplicationContext()).setMaskSelNumber(selNo);
		for (int i = 0; i < 4; i++){
			switch(i){
			case 0:
				if (i == selNo)
					maskbtns[i].setImageResource(R.drawable.emoji_hair1_press);
				else 
					maskbtns[i].setImageResource(R.drawable.emoji_hair1);
				break;
			case 1:
				if (i == selNo)
					maskbtns[i].setImageResource(R.drawable.emoji_hair2_press);
				else 
					maskbtns[i].setImageResource(R.drawable.emoji_hair2);
				break;
			case 2:
				if (i == selNo)
					maskbtns[i].setImageResource(R.drawable.emoji_hair3_press);
				else 
					maskbtns[i].setImageResource(R.drawable.emoji_hair3);
				break;
			case 3:
				if (i == selNo)
					maskbtns[i].setImageResource(R.drawable.emoji_hair4_press);
				else 
					maskbtns[i].setImageResource(R.drawable.emoji_hair4);
				break;
			}
		}
	}
	
	@SuppressLint("NewApi")
	public Bitmap getMaskedFace(Bitmap source, int maskid){
		
		Bitmap maskBitmap = CalUtility.getResoureBimap(this.getResources(), maskid);
		Bitmap resizedBmp = Bitmap.createScaledBitmap(source, (int)maskBitmap.getWidth(), (int)maskBitmap.getHeight(), false);
		resizedBmp.setHasAlpha(true);
		Canvas canvas = new Canvas(resizedBmp);		 
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(maskBitmap, 0, 0, paint);
		//maskBitmap.recycle();
		return resizedBmp;	
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
		globalVariable.setFace(null);
		globalVariable.setMaskedFace(null);
		ImageSelectionActivity.acivityType = ActivityType.Acitivity_GIF;
		Intent nextScreen = new Intent(GifFaceMaskActivity.this, ImageSelectionActivity.class);
        startActivity(nextScreen);
        finish();
	}

}
