package jp.co.vantageapps.famous.app;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	long 	tmBegin , tmNow, tmInBegin;
	Timer timer_1 = new Timer();
	TextView txtView;
	
	
	
	static String deviceId = null;
	
	Thread adIdThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient
                        .getAdvertisingIdInfo(getApplicationContext());
                deviceId = adInfo.getId();
                final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                Log.d("DEBUG", "AndroidAdID : " + deviceId);
                Log.d("DEBUG", "OptoutFlag : " + String.valueOf(isLAT));
            } catch (Exception e) {
            }
        }
    });
	
	
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        //adIdThread.start();	
        
        
        tmBegin = System.currentTimeMillis();
        tmInBegin = tmBegin;
        txtView = (TextView)findViewById(R.id.textView1);
        timer_1.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					tmNow = System.currentTimeMillis();
					if ( tmBegin > 0 && tmNow - tmBegin > 1000 )
					{
//						CommonData.bConnected = false;
//						CommonData.clientlib.end();
						showMainActivity();
					
						this.cancel();
						return;
					}
					
					Log.e("passtime:", String.valueOf(tmNow - tmInBegin));
					
					tmInBegin = tmNow;
				} catch (Exception e) {
					this.cancel();
				}
			}
		}, 0, 100);        
		
		
		/*KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);*/
    }
    
    public void showMainActivity(){
    	tmBegin = 0;
		Intent mainIntent = new Intent(SplashActivity.this, FamousPeopleActivity.class);
		startActivity(mainIntent);
		timer_1.cancel();
		this.finish();
    }
}