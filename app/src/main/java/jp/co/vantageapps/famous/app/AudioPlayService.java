package jp.co.vantageapps.famous.app;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;


public class AudioPlayService extends IntentService {

	public static String filename;
	public AudioPlayService() {
		super("AudioPlayService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			// TODO Auto-generated method stub
			 	MediaPlayer player = new MediaPlayer();
	            AssetManager am =getBaseContext().getAssets();
	        	try {
	        		AssetFileDescriptor afd = am.openFd(filename);
	                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
	                        afd.getLength());
	                player.setLooping(false);
		            player.prepare();
		            player.start();
	        	} catch(Exception e){};
	            player.setOnCompletionListener(new OnCompletionListener() {

	                @Override
	                public void onCompletion(MediaPlayer mp) {
	                    // TODO Auto-generated method stub
	                	mp.release();                    
	                }
	            });
			}
		}, "AudioPlayService").start();
		return super.onStartCommand(intent, flags, startId);
	}
}
