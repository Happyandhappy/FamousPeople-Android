package jp.co.vantageapps.famous.app;

import android.content.Intent;
import android.util.Log;

public class SNSshare_Intent extends Intent{

	SNSshare_Intent(){
		//基本設定
		init();
	}

	/**

	 */
	SNSshare_Intent( String args ){
		//基本設定
		init();
		//
		setMessage(args);
	}
	
	//基本設定
	void init(){
		try {
			setAction(Intent.ACTION_SEND);
            //setType("text/plain");
			setType("image/*"); 
			putExtra(Intent.EXTRA_SUBJECT, "share");
        } catch (Exception e) {
            Log.d("e", "Error");
        }
	}
	
	//
	void setMessage( String args ){
		putExtra(Intent.EXTRA_TEXT, args);
	}
}
