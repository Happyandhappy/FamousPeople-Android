package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Share {
	 /**  
	  * 分享功能  
	  * @param context 上下文  
	  * @param activityTitle Activity
	  * @param msgTitle
	  * @param msgText
	  * @param imgPath
	  */  
	public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText, String imgPath) {  

		String filepath = "file:///"
				+ Environment.getExternalStorageDirectory().toString()
				+ imgPath;

		Log.e("filepath", filepath);

		Intent intent = new Intent(Intent.ACTION_SEND);  
		if (imgPath == null || imgPath.equals("")) {  
			intent.setType("text/plain");  
		} else {
			intent.setType("image/*"); 
			//
			intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(filepath));  
		}  
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
		intent.putExtra(Intent.EXTRA_TEXT, msgText);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		context.startActivity(Intent.createChooser(intent, activityTitle));  
	}

	
	/**

	 */
	public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText, Bitmap bmp) {  

		//
		Intent intent = new Intent(Intent.ACTION_SEND);  
		if (bmp == null) {  
			intent.setType("text/plain");  
		} else {
			//
			savePNG(bmp, "/test.png");
			String filepath = "file:///"
					+ Environment.getExternalStorageDirectory().toString()
					+ "/test.png";
			Log.e("filepath", filepath);
			intent.setType("image/*");
			//
			intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(filepath));  
		}  
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);  
		intent.putExtra(Intent.EXTRA_TEXT, msgText);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		context.startActivity(Intent.createChooser(intent, activityTitle));  
	}

	//
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

}
