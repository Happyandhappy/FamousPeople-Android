package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.vantageapps.famous.app.FSServerConnectionActivity.JsonType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.client.utils.URIUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import it.partytrack.sdk.Track;

public class FSRankingRegisterActivity extends Activity {
	
	public enum JsonType {
		JsonNone,
		JsonStart,
		JsonProcessing,
		JsonComplete
	};
	
	private JsonType _jsonType = JsonType.JsonStart;
	
	ImageView faceImageView;
	TextView similarityTextView;
	TextView usernameTextView;
	
	String strSimilarity;
	String strUserName;
	
	String deviceID;
	
	Button btn_Register, btn_NoRegister, btn_Close;
	
	RankingRegisterResultHandler	rankingRegisterResultHandler;
	String	strResultMessage = "";
	
	private synchronized JsonType getJsonType()
	{
		return _jsonType;
	}
	
	private synchronized void setJsonType(JsonType jsontype)
	{
		_jsonType = jsontype;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rankingregister);
		
		faceImageView = (ImageView) findViewById(R.id.faceImageView);
		faceImageView.setImageBitmap(((GlobalClass) getApplication()).getFace());
		
		strSimilarity = ((GlobalClass) getApplication()).getRecognitionResult().get(FSServerConnectionActivity.TAG_SCORE);		
		similarityTextView = (TextView) findViewById(R.id.similarityTextView);
		similarityTextView.setText(strSimilarity);
		strSimilarity = strSimilarity.substring(0, strSimilarity.length() - 1); 
		
		usernameTextView = (TextView) findViewById(R.id.txtViewName);
		
		deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		
		btn_Register = (Button)findViewById(R.id.btn_register);
		btn_Register.setOnClickListener(new View.OnClickListener() {		
			
			@Override
			public void onClick(View v) {
				
				strUserName = usernameTextView.getText().toString();
				
				if(strUserName.equals(""))
				{
					new AlertDialog.Builder(FSRankingRegisterActivity.this)
	            	.setTitle("ランキング登録")
			        .setMessage("名前を入力してください")
			        .setPositiveButton("OK", null)
			        .create().show();
					
					return;
				}
				
				new AlertDialog.Builder(FSRankingRegisterActivity.this)
		        .setMessage("ランキングに登録します。\n※あとから結果を削除する場合は、診断ランキング画面のマイランキングから削除できます。")
		        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {

		            	rankingRegister();
		            }
		        })
		        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	
		            }
		        })
		        .create().show();
			}
		});
		
		btn_NoRegister = (Button)findViewById(R.id.btn_notregister);
		btn_NoRegister.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FSRankingRegisterActivity.this, FamousPeopleActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		btn_Close = (Button)findViewById(R.id.btn_close);
		btn_Close.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FSRankingRegisterActivity.this, FSRecognitionResultActivity.class);
				i.putExtra("RollFinishEffect", false);
				startActivity(i);				
				finish();
			}
		});
		
		rankingRegisterResultHandler = new RankingRegisterResultHandler();
		
	}
	
	void rankingRegister(){
		
		//Create image file.
		final String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/famouspeople");
		myDir.mkdirs();

		final String fileName = String.format("userphoto-%d.jpg", System.currentTimeMillis());
		
		try
		{
			File file = new File(myDir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			((GlobalClass) getApplication()).getFace().compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			
		}catch(Exception e){
			
		}
		
		//Send Ranking data to server.
		new Thread(new Runnable() {
			public void run() {
				try {
					
					String handlerUrl = GlobalClass.SERVER_URL;
					
					handlerUrl += "?commandType=rank_register";
					handlerUrl += "&username=";	handlerUrl += strUserName;
					handlerUrl += "&synrate=";	handlerUrl += strSimilarity;
					handlerUrl += "&famousid=";	handlerUrl += ((GlobalClass) getApplication()).getRecognitionResult().get(FSServerConnectionActivity.TAG_FAMOUSID);
					handlerUrl += "&udid=";	handlerUrl += deviceID;
					
					URL url = new URL(handlerUrl);
					URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
					handlerUrl = uri.toString();
					
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(handlerUrl);
					MultipartEntity reqEntity = new MultipartEntity();
					
    				String strFileFullPath = root + "/famouspeople/" + fileName;
					
    				if(((Spinner) findViewById(R.id.spinnerRegisterImage)).getSelectedItemPosition() == 0)
    				{
	    				FileBody fileUpload = null;
						fileUpload = new FileBody(new File(strFileFullPath));
						reqEntity.addPart("userpicture", fileUpload);
    				}
					
					post.setEntity(reqEntity);
					HttpResponse res = client.execute(post);
					HttpEntity resEntity = res.getEntity();
					final String response_str = EntityUtils.toString(resEntity);
					
					File file = new File(root + "/famouspeople/" + fileName);
					file.delete();
					
					if (resEntity != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								try
								{
									JSONObject jsonObject = new JSONObject(response_str);
	    							String status = jsonObject.getString("result");
	    							
	    							if(status.equals("ok") )
	    							{
	    								strResultMessage = "登録成功!";
	    								Message msg = new Message();
	    								rankingRegisterResultHandler.sendMessage(msg);
	    								
	    							}else{
	    								strResultMessage = "登録エラー";
	    								Message msg = new Message();
	    								rankingRegisterResultHandler.sendMessage(msg);
	    							}
								}catch (Exception e){
									strResultMessage = "登録エラー";
									Message msg = new Message();
    								rankingRegisterResultHandler.sendMessage(msg);
								}
							}
						});
					}else{
						strResultMessage = "登録エラー";
						Message msg = new Message();
						rankingRegisterResultHandler.sendMessage(msg);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public class ModalDialog {

		private boolean mChoice = false;        
		private boolean mQuitModal = false;     
		
		private Method mMsgQueueNextMethod = null;
		private Field mMsgTargetFiled = null;
		
		public ModalDialog() {
		}
		
		public void showAlertDialog(Context context, String info) {
		    if (!prepareModal()) {
		        return;
		    }
		
		    // build alert dialog
		    AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    builder.setTitle("ランキング登録");
		    builder.setMessage(info);
		    builder.setCancelable(false);
		    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            ModalDialog.this.mQuitModal = true;
		            dialog.dismiss();
		            
	            	Intent i = new Intent(FSRankingRegisterActivity.this, FSRecognitionResultActivity.class);
	            	i.putExtra("RollFinishEffect", false);
					startActivity(i);
					finish();
		        }
		    });
		    
		    AlertDialog alert = builder.create();
		    alert.show();
		    // run in modal mode
		    doModal();
		}
		
		public boolean showConfirmDialog(Context context, String info) {
		    if (!prepareModal()) {
		        return false;
		    }
		
		    // reset choice
		    mChoice = false;
		
		    AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    builder.setMessage(info);
		    builder.setCancelable(false);
		    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            ModalDialog.this.mQuitModal = true;
		            ModalDialog.this.mChoice = true;
		            dialog.dismiss();
		        }
		    });
		
		    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            ModalDialog.this.mQuitModal = true;
		            ModalDialog.this.mChoice = false;
		            dialog.cancel();
		        }
		    });
		
		    AlertDialog alert = builder.create();
		    alert.show();
		
		    doModal();
		    return mChoice;
		}
		
		private boolean prepareModal() {
		    Class<?> clsMsgQueue = null;
		    Class<?> clsMessage = null;
		
		    try {
		        clsMsgQueue = Class.forName("android.os.MessageQueue");
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		        return false;
		    }
		
		    try {
		        clsMessage = Class.forName("android.os.Message");
		    } catch (ClassNotFoundException e) {
		        e.printStackTrace();
		        return false;
		    }
		
		    try {
		        mMsgQueueNextMethod = clsMsgQueue.getDeclaredMethod("next", new Class[]{});
		    } catch (SecurityException e) {
		        e.printStackTrace();
		        return false;
		    } catch (NoSuchMethodException e) {
		        e.printStackTrace();
		        return false;
		    }
		
		    mMsgQueueNextMethod.setAccessible(true);
		
		    try {
		        mMsgTargetFiled = clsMessage.getDeclaredField("target");
		    } catch (SecurityException e) {
		        e.printStackTrace();
		        return false;
		    } catch (NoSuchFieldException e) {
		        e.printStackTrace();
		        return false;
		    }
		
		    mMsgTargetFiled.setAccessible(true);
		    return true;
		}
		
		private void doModal() {
		    mQuitModal = false;
		
		    // get message queue associated with main UI thread
		    MessageQueue queue = Looper.myQueue();
		    while (!mQuitModal) {
		        // call queue.next(), might block
		        Message msg = null;
		        try {
		            msg = (Message)mMsgQueueNextMethod.invoke(queue, new Object[]{});
		        } catch (IllegalArgumentException e) {
		            e.printStackTrace();
		        } catch (IllegalAccessException e) {
		            e.printStackTrace();
		        } catch (InvocationTargetException e) {
		            e.printStackTrace();
		        }
		
		        if (null != msg) {
		            Handler target = null;
		            try {
		                target = (Handler)mMsgTargetFiled.get(msg);
		            } catch (IllegalArgumentException e) {
		                e.printStackTrace();
		            } catch (IllegalAccessException e) {
		                e.printStackTrace();
		            }
		
		            if (target == null) {
		                // No target is a magic identifier for the quit message.
		                mQuitModal = true;
		            }
		
		            target.dispatchMessage(msg);
		            msg.recycle();
		        }
		    }
		}
	}
	
	public class RankingRegisterResultHandler extends Handler {
		
		String strResult = "";
		
		public void handleMessage(Message msg) {
			
			Bundle bundle = msg.getData();
			
			Handler h = new Handler();
	        h.postDelayed(new Runnable() {
	            public void run() {

//	            	new AlertDialog.Builder(FSRankingRegisterActivity.this)
//	            	.setTitle("ランキング登録")
//			        .setMessage(strResultMessage)
//			        .setPositiveButton("よし", new DialogInterface.OnClickListener() {
//			            public void onClick(DialogInterface dialog, int whichButton) {	
//			            	Intent i = new Intent(FSRankingRegisterActivity.this, FSRecognitionResultActivity.class);
//							startActivity(i);
//							finish();
//			            }
//			        })
//			        .create().show();
	            	
	            	ModalDialog dialog = new ModalDialog();
	            	dialog.showAlertDialog(FSRankingRegisterActivity.this, strResultMessage);
	            	
	            }
	        }, 100);
		}
	}
}
