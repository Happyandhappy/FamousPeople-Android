package jp.co.vantageapps.famous.app;

import java.nio.ByteBuffer;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.MotionEvent.PointerCoords;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;


@SuppressLint({ "NewApi", "DefaultLocale" })
public class FaceDetectionActivity extends SuruPassActivity {
	
	public enum ActivityType {
		Acitivity_GIF,
		Acitivity_FS
	};	
	
	public static ActivityType acivityType;
	int nLeft = 0;
	int nTop = 0;
	float scale = 1.0f;
	private Builder pDialog;
	private Button backBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facedetection);

		LinearLayout layout = (LinearLayout) findViewById(R.id.faceDetectionView);
		layout.addView(new MyView(this, ((GlobalClass) getApplication()).getImage()));
		backBtn = (Button)findViewById(R.id.btn_backto_main);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				closeSuruPass();
			}
		});

		showSuruPass(SuruPassType.BottomBanner);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 123:
			
			if (resultCode == RESULT_OK) {
				closeSuruPass();
				String rare = ((GlobalClass) getApplication()).getRecognitionResult().get("rare");
				if (rare.isEmpty()){
					
					Intent i = new Intent(FaceDetectionActivity.this, FSRecognitionResultActivity.class);
					i.putExtra("RollFinishEffect", true);
					startActivity(i);
					finish();
				} else {
					Intent i = new Intent(FaceDetectionActivity.this, FSAnalyseResultActivity.class);
					startActivity(i);
					finish();
				}
			} else if (resultCode == RESULT_CANCELED) {
	            pDialog = new AlertDialog.Builder(FaceDetectionActivity.this);
	            pDialog.setTitle("エラー");
	            pDialog.setMessage("ネットワーク接続がオフラインです。ネットワーク設定をご確認ください。");
	            pDialog.setIcon(1);
	            pDialog.show();
			}
			break;
		}
	}

	@SuppressLint("DefaultLocale")
	private class MyView extends View {
		private Bitmap image;
		FaceDetectionResult[] detectionResult = null;

		public MyView(Context context, final Bitmap image) {
			super(context);
			this.image = image;
			int w = image.getWidth();
			int h = image.getHeight();
			int s = (w * 32 + 31) / 32 * 4;
			ByteBuffer buff = ByteBuffer.allocate(s * h);
			image.copyPixelsToBuffer(buff);
			Log.e("CRRASH: buff size = ", String.valueOf(buff.capacity()));
			
			detectionResult = DetectorReal.detectFace(buff.array(), w, h, 1);

			buff.clear();
			buff = null;
			System.gc();
			
			HashMap<String, String> recognitionResult = new HashMap<String, String>();        	
			/* recognitionResult.put("url", "https://www.sandokan.com");
        	recognitionResult.put("job", "æœ‰ï¿½??äººè¨ºæ–­");
        	recognitionResult.put("name", "Hyon NalDo");
        	recognitionResult.put("score", "70.4%");     	*/  
        	((GlobalClass)getApplication()).setRecognitionResult(recognitionResult); 
			
			LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			this.setLayoutParams(param);

			this.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int count = event.getPointerCount();
					if (count == 1) {
						PointerCoords outPointerCoords = new PointerCoords();
						event.getPointerCoords(0, outPointerCoords);

						int x = (int) outPointerCoords.x;
						int y = (int) outPointerCoords.y;

						if (detectionResult != null) {
							for (int i = 0; i < detectionResult.length; i++) {

								int fleft = (int) (detectionResult[i].getFaceRect().left * scale + nLeft);
								int ftop = (int) (detectionResult[i].getFaceRect().top * scale + nTop);
								int fright = (int) (detectionResult[i].getFaceRect().right * scale + nLeft);
								int fbottom = (int) (detectionResult[i].getFaceRect().bottom * scale + nTop);

                                int[] landMarks = detectionResult[i].getLandMarks();

								int fEeyeLY = (int)(landMarks[1] * scale + nTop);
								int fEeyeRY = (int)(landMarks[3] * scale + nTop);
								
								if (fleft <= x && x <= fright && ftop <= y && y < fbottom) {
								
									Bitmap faceImage = null;
									
									if ((FaceDetectionActivity.acivityType == ActivityType.Acitivity_GIF) ||
									 (FaceDetectionActivity.acivityType == ActivityType.Acitivity_FS)){

										fleft = (int) (detectionResult[i].getFaceRect().left);
										ftop = (int) (detectionResult[i].getFaceRect().top);
										fright = (int) (detectionResult[i].getFaceRect().right);
										fbottom = (int) (detectionResult[i].getFaceRect().bottom);

										fEeyeLY = landMarks[1];
										fEeyeRY = landMarks[3];

										float zT = 1.8f;
								        float zB = 1.2f;
								        float left, top, right, bottom;

								        float aEyeY = (fEeyeLY + fEeyeRY) / 2.0f;
								        float topDist = Math.abs(aEyeY - ftop) * zT;
								        top =  aEyeY - topDist;

								        //float bottomDist = topDist * 1.362f;
								        float bottomDist = Math.abs(fbottom - aEyeY) * zB;
								        bottom =  aEyeY + bottomDist;

								        float fHeight = bottom - top;
								        left  = fleft  + (fright-fleft) / 2.0f - fHeight * 0.9f / 2.0f;
								        right = fleft + (fright-fleft) / 2.0f + fHeight * 0.9f / 2.0f;

								        if (left < 0) left = 0;
								        if (right >= (image.getWidth()-1))
								            right =  image.getWidth()-1;

								        if (top < 0) top = 0;
								        if (bottom >= (image.getHeight()-1))
								            bottom =  image.getHeight()-1;

								        int fW = (int)(right-left);
								        int fH = (int)(bottom-top);


								        Log.e("CRASH face position = ", String.valueOf(left) + "," +
								        		String.valueOf(top) + "," +
								        		String.valueOf(fW) + "," +
								        		String.valueOf(fH));
								        faceImage = Bitmap.createBitmap(image, (int)left, (int)top,
								        		fW,fH );
								        Log.e("CRASH faceImage Size = ", String.valueOf(faceImage.getByteCount()));

									} else {

										int dx = (fright - fleft + 1) * 1 / 3;
										int dy = (fbottom - ftop + 1) * 1 / 3;

										int sx = fleft - dx;
										int sy = ftop - dy;

										if (sx < 0)
											sx = 0;

										if (sy < 0)
											sy = 0;

										int ex = fright + dx * 2;
										if (ex > image.getWidth() - 1)
											ex = image.getWidth() - 1;

										int ey = fbottom;
										for (int j = 0; j < landMarks.length / 2; j++) {
											if (ey < landMarks[j * 2 + 1])
												ey = landMarks[j * 2 + 1];
										}
										ey = ey + dy * 2;

										if (ey > image.getHeight() - 1)
											ey = image.getHeight() - 1;

										faceImage = Bitmap.createBitmap(image, sx, sy, ex - sx + 1, ey - sy + 1);
									}
									
									
									
									((GlobalClass) getApplication()).setDetectionResult(detectionResult[i]);
									 
									((GlobalClass) getApplication()).setFace(faceImage);
									 
									if (FaceDetectionActivity.acivityType == ActivityType.Acitivity_FS){

										Intent intent = new Intent(FaceDetectionActivity.this,
												FSServerConnectionActivity.class);
										Log.e("CRASH", "new Intent");
										startActivityForResult(intent, 123);	
										Log.e("CRASH", "startActivityForResult");

									} else if (FaceDetectionActivity.acivityType == ActivityType.Acitivity_GIF){

										closeSuruPass();
										((GlobalClass)getApplicationContext()).setMaskSelNumber(0);
										Intent nextScreen = new Intent(FaceDetectionActivity.this, GifFaceMaskActivity.class);
										startActivity(nextScreen);
										finish();
									}	
									
									break;
								}
							}
						}
					}
					return false;
				}
			});
			
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			Rect clipRect = canvas.getClipBounds();
			int w = clipRect.width();
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

			nLeft = dst.left;
			nTop = dst.top;

			canvas.drawBitmap(image, null, dst, null);

			Paint myPaint = new Paint();
			if (detectionResult != null) {
				for (int i = 0; i < detectionResult.length; i++) {
					myPaint.setColor(Color.GREEN);
					myPaint.setStyle(Paint.Style.STROKE);
					myPaint.setStrokeWidth(3);
					int x = (int) (detectionResult[i].getFaceRect().left * scale + dst.left);
					int y = (int) (detectionResult[i].getFaceRect().top * scale + dst.top);
					int x2 = (int) (detectionResult[i].getFaceRect().right * scale + dst.left);
					int y2 = (int) (detectionResult[i].getFaceRect().bottom * scale + dst.top);
					canvas.drawRect(x, y, x2, y2, myPaint);

					int[] landMarks = detectionResult[i].getLandMarks();

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    myPaint.setColor(Color.YELLOW);

					//draw 2 eyes
                    canvas.drawCircle(landMarks[1] * scale + dst.left, landMarks[2] * scale + dst.top, 2, myPaint);
                    canvas.drawCircle(landMarks[4] * scale + dst.left, landMarks[5] * scale + dst.top, 2, myPaint);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (acivityType == ActivityType.Acitivity_FS){
			// TODO Auto-generated method stub
			Intent i = new Intent(FaceDetectionActivity.this, FamousPeopleActivity.class);
			startActivity(i);
			finish();
		}else if (acivityType == ActivityType.Acitivity_GIF){
			Intent i = new Intent(FaceDetectionActivity.this, FamousPeopleActivity.class);
			startActivity(i);
			finish();
		}	 
	}
}
