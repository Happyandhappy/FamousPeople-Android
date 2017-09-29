package jp.co.vantageapps.famous.app;

import java.util.ArrayList;
import java.util.List;

import net.nend.android.NendAdInterstitial;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.vantageapps.famous.app.FaceDetectionActivity.ActivityType;
import jp.co.vantageapps.famous.app.SuruPassActivity.SuruPassType;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class FSCategorySelectionActivity extends SuruPassActivity {
	//NendDialog mDialog;
	
	private ProgressDialog pDialog;
	TextView textView3;
	Spinner spin_key1;
	Spinner spin_key2;
	Button btn_ok;
	Button btn_all;
	CheckBox chk_rare;
	
	boolean isSuccess = false;	
	Activity mainActivity;
	
	ArrayAdapter<String> adapter1 = null, adapter2 = null;
	ArrayList<String> key1 = null;
	ArrayList<String> key1_value = null;
	
	ArrayList<String> key2 = null;
	ArrayList<String> key2_value = null; 

	ArrayList<String> rare = null;

	ServerInfo serverInfo = null;
	
/*	static final String[] key1_value = new String[] {"全てのカテゴリ", "タレント・俳優・女優", "お笑い芸人", "歌手",
			"スポーツ選手", "アナウンサー", "文化人", "モデル", "LGBT", "海外俳優", "韓国俳優&K-POP" };
			
	static String[] key1 = new String[] {"", "talent", 
									"owarai",
									"singer",
									"sports",
									"announcer",
									"cultural",
									"model",
									"lgbt",
									"global",
									"kpop"
	};
	
	static String[] key2 = new String[] {
		"",
		"akb",
		"morning",
		"takaraduka"
	};
	
	static String[] key2_value = new String[] { 
		"全てのグループ", 
		"AKB48グループ", 
		"ハロプロ", 
		"宝塚出身"};
*/

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categoryselection);
		
		//mDialog = new NendDialog(this);
		
		//mDialog.show();
		
		mainActivity = this;
		// set key1
		spin_key1 = (Spinner) findViewById(R.id.spinner_key1);		
		spin_key2 = (Spinner) findViewById(R.id.spinner_key2);	
		
		chk_rare = (CheckBox)findViewById(R.id.checkbox_rare);
		String gRare = ((GlobalClass)getApplication()).getrare();
		boolean bCheck = gRare.isEmpty();
		chk_rare.setChecked(!bCheck);
		
		//test();
		serverInfo = new ServerInfo();
		serverInfo.execute();

		btn_all = (Button)findViewById(R.id.allbtn);
		btn_all.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//serverInfo.cancel(true);
				closeSuruPass();
				// TODO Auto-generated method stub
				((GlobalClass)getApplication()).setkey1("");
				((GlobalClass)getApplication()).setkey2("");
				((GlobalClass)getApplication()).setrare("");
				ImageSelectionActivity.acivityType = ActivityType.Acitivity_FS;
				Intent i = new Intent(FSCategorySelectionActivity.this,
						ImageSelectionActivity.class);
								
				startActivity(i);
				finish();
				//mDialog.dismiss();
			}
		});
		
		
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSuruPass();
				//serverInfo.cancel(true);
				
				// TODO Auto-generated method stub
				int key1Index = spin_key1.getSelectedItemPosition();
				((GlobalClass)getApplication()).setkey1(key1.get(key1Index));
				
				int key2Index = spin_key2.getSelectedItemPosition();
				((GlobalClass)getApplication()).setkey2(key2.get(key2Index));
				
				boolean bRare = chk_rare.isChecked();
				if (bRare){
					String rareStr = "";
					for (int k = 0; k< rare.size(); k++){
						rareStr += rare.get(k);
						if (k != (rare.size()-1))
							rareStr +=",";
					}
					((GlobalClass)getApplication()).setrare(rareStr);
				}
				else 
					((GlobalClass)getApplication()).setrare("");
				ImageSelectionActivity.acivityType = ActivityType.Acitivity_FS;
				Intent i = new Intent(FSCategorySelectionActivity.this,
						ImageSelectionActivity.class);				
				startActivity(i);
				finish();
				//mDialog.dismiss();
			}
		});
		//showSuruPass(SuruPassType.InterstitialTop);
		showSuruPass(SuruPassType.BottomBanner);
	}
		
	
	@Override
	public void onBackPressed() {
		
		int key1Index = spin_key1.getSelectedItemPosition();
		
		((GlobalClass)getApplication()).setkey1(key1.get(key1Index));
				
		int key2Index = spin_key2.getSelectedItemPosition();
		((GlobalClass)getApplication()).setkey2(key2.get(key2Index));
			
		boolean bRare = chk_rare.isChecked();
		if (bRare){
			String rareStr = "";
			for (int k = 0; k< rare.size(); k++){
				rareStr += rare.get(k);
				if (k != (rare.size()-1))
					rareStr +=",";
			}
			((GlobalClass)getApplication()).setrare(rareStr);
		}
		else 
			((GlobalClass)getApplication()).setrare("");
				
		
		//serverInfo.cancel(true);
		closeSuruPass();
		// TODO Auto-generated method stub
		Intent i = new Intent(FSCategorySelectionActivity.this, FSGenderSelectionActivity.class);
		startActivity(i);
		finish();
	}
	
	private class ServerInfo extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(FSCategorySelectionActivity.this);
            pDialog.setMessage("しばらくお待ちください...");
            pDialog.setCancelable(false);
            pDialog.show();
            
            if (key1 != null)
            	key1.clear();
            if (key1_value != null)
            	key1_value.clear();
            key1 = new ArrayList<String>();
    		key1_value = new ArrayList<String>();
    		key1.add("");
    		key1_value.add("全てのカテゴリ");
            
            if (key2 != null)
            	key2.clear();
            if (key2_value != null)
            	key2_value.clear();
            key2 = new ArrayList<String>();
    		key2_value = new ArrayList<String>();
    		key2.add("");
    		key2_value.add("全てのグループ");
    		
            if (rare != null)
            	rare.clear();
            rare = new ArrayList<String>();
        }

//		// Get data from server
//        @Override
//        protected Void doInBackground(Void... arg0) {
//
//            ServiceHandler sh = new ServiceHandler();
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>(4);
//            params.add(new BasicNameValuePair("commandType", "category_request"));
//
//            String jsonStr = sh.makeServiceCall(GlobalClass.SERVER_URL, ServiceHandler.POST, params);
//
//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//                    String result = jsonObj.getString("result");
//
//                    if(result.equals("ok")) {
//                    	JSONArray skey1 = jsonObj.getJSONArray("key1");
//                    	if (skey1 != null){
//	                    	for (int i = 0; i < skey1.length(); i++){
//	                    		JSONObject pair = (JSONObject)skey1.get(i);
//	                    		String keyName = pair.getString("key");
//	                    		String keyValue = pair.getString("value");
//	                    		key1.add(keyName);
//	                    		key1_value.add(keyValue);
//	                    	}
//                    	}
//
//                    	JSONArray skey2 = jsonObj.getJSONArray("key2");
//                    	if (skey2 != null){
//	                    	for (int i = 0; i < skey2.length(); i++){
//	                    		JSONObject pair = (JSONObject)skey2.get(i);
//	                    		String keyName = pair.getString("key");
//	                    		String keyValue = pair.getString("value");
//	                    		key2.add(keyName);
//	                    		key2_value.add(keyValue);
//	                    	}
//                    	}
//
//                    	JSONArray srare = jsonObj.getJSONArray("rare");
//                    	if (srare != null){
//                    		for (int i = 0; i < srare.length(); i++){
//                    			JSONObject pair = (JSONObject)srare.get(i);
//	                    		String key = pair.getString("key");
//	                    		String check = pair.getString("check");
//	                    		if (check.equals("1"))
//	                    			rare.add(key);
//                    		}
//                    	}
//                    	isSuccess = true;
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }
//        	return null;
//        }

		// Get data from local db
        @Override
        protected Void doInBackground(Void... arg0) {

			DatabaseAdapter mDbHelper = new DatabaseAdapter(FSCategorySelectionActivity.this);
			mDbHelper.createDatabase();
			mDbHelper.open();


			Cursor mCur = mDbHelper.getKey1Table();

			if(mCur.getCount()>0){
				if(mCur.moveToFirst()){
					do{
						String keyName = mCur.getString(mCur.getColumnIndex("skey"));
						String keyValue = mCur.getString(mCur.getColumnIndex("value"));
						key1.add(keyName);
						key1_value.add(keyValue);

					}while (mCur.moveToNext());
				}
			}


			mCur = mDbHelper.getKey2Table();

			if(mCur.getCount()>0){
				if(mCur.moveToFirst()){
					do{
						String keyName = mCur.getString(mCur.getColumnIndex("skey"));
						String keyValue = mCur.getString(mCur.getColumnIndex("value"));
						key2.add(keyName);
						key2_value.add(keyValue);

					}while (mCur.moveToNext());
				}
			}


			mCur = mDbHelper.getRareTable();

			if(mCur.getCount()>0){
				if(mCur.moveToFirst()){
					do{
						String key = mCur.getString(mCur.getColumnIndex("skey"));
						String value = mCur.getString(mCur.getColumnIndex("value"));
						String check = mCur.getString(mCur.getColumnIndex("prop"));

						if (check.equals("1"))
							rare.add(key);

					}while (mCur.moveToNext());
				}
			}


			isSuccess = true;

			mDbHelper.close();

        	return null;
        }
        
        @Override
        protected void onPostExecute(Void result) 
        {
            super.onPostExecute(result);            
           
        	adapter1 = new ArrayAdapter<String>(mainActivity,
    				android.R.layout.simple_spinner_item, key1_value);
    		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		spin_key1.setAdapter(adapter1);
    		String gkey1 = ((GlobalClass)getApplication()).getkey1();
    		for(int i = 0; i < key1.size(); i++ ){
    			String ikey1 = key1.get(i);
    			if (ikey1.equals(gkey1)){
    				spin_key1.setSelection(i);
    				break;
    			}
    		}
        	
    		adapter2 = new ArrayAdapter<String>(mainActivity,
    				android.R.layout.simple_spinner_item, key2_value);
    		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		spin_key2.setAdapter(adapter2);
    		String gkey2 = ((GlobalClass)getApplication()).getkey2();
    		for(int i = 0; i < key2.size(); i++ ){
    			String ikey2 = key2.get(i);
    			if (ikey2.equals(gkey2)){
    				spin_key2.setSelection(i);
    				break;
    			}
    		}            
            
            if (pDialog.isShowing())
                pDialog.dismiss();            
          }
    }
	
	void test(){
		  
        if (key1 != null)
        	key1.clear();
        if (key1_value != null)
        	key1_value.clear();
        key1 = new ArrayList<String>();
		key1_value = new ArrayList<String>();
		key1.add("");
		key1_value.add("全てのカテゴリ");
        
        if (key2 != null)
        	key2.clear();
        if (key2_value != null)
        	key2_value.clear();
        key2 = new ArrayList<String>();
		key2_value = new ArrayList<String>();
		key2.add("");
		key2_value.add("全てのグループ");
		
        if (rare != null)
        	rare.clear();
        rare = new ArrayList<String>();
        
        adapter1 = new ArrayAdapter<String>(mainActivity,
				android.R.layout.simple_spinner_item, key1_value);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_key1.setAdapter(adapter1);
		String gkey1 = ((GlobalClass)getApplication()).getkey1();
		for(int i = 0; i < key1.size(); i++ ){
			String ikey1 = key1.get(i);
			if (ikey1.equals(gkey1)){
				spin_key1.setSelection(i);
				break;
			}
		}
    	
		adapter2 = new ArrayAdapter<String>(mainActivity,
				android.R.layout.simple_spinner_item, key2_value);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_key2.setAdapter(adapter2);
		String gkey2 = ((GlobalClass)getApplication()).getkey2();
		for(int i = 0; i < key2.size(); i++ ){
			String ikey2 = key2.get(i);
			if (ikey2.equals(gkey2)){
				spin_key2.setSelection(i);
				break;
			}
		}            
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        //mDialog.destroy();
    }	
}
