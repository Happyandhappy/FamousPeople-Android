package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.vantageapps.famous.app.FSServerConnectionActivity.JsonType;
import jp.co.vantageapps.famous.app.SuruPassActivity.SuruPassType;

import net.nend.android.NendAdInterstitial.NendAdInterstitialClickType;
import net.nend.android.NendAdInterstitial.NendAdInterstitialStatusCode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import it.partytrack.sdk.Track;

public class FSRankingShowActivity extends SuruPassActivity implements AdapterView.OnItemSelectedListener {
	//NendDialog mDialog;
	
	public enum RankingKind {
		SINDAN_RANKING,
		MYRANKING,
	}
	
	public enum RankingType {
		RANKING_TOTAL,
		RANKING_CATEGORY,
		RANKING_GROUP,
		RANKING_PEOPLE;
		
		private static Map<Integer, RankingType> ss = new TreeMap<Integer, RankingType>();
	    private static final int START_VALUE = 0;
	    private int value;

	    static {
	        for(int i=0; i<values().length; i++)
	        {
	            values()[i].value = START_VALUE + i;
	            ss.put(values()[i].value, values()[i]);
	        }
	    }

	    public static RankingType fromInt(int i) {
	        return ss.get(i);
	    }

	    public int value() {
	    	return value;
	    }
	}
	
	Button btn_Total, btn_Category, btn_Group, btn_People;
	Button btn_LeftArrow, btn_TypeChange, btn_RightArrow;
	Button btn_Close;
	
	RankingKind rankingKind = RankingKind.SINDAN_RANKING;
	RankingType rankingType = RankingType.RANKING_TOTAL;
	
	ListView						rankingdata_ListView;
	RankingDataListViewAdapter		rankingdatalistview_Adapter;

	Spinner	spinner_RankingType;

	JSONArray rankingtype_category_list = null;
	JSONArray rankingtype_group_list = null;
	JSONArray rankingtype_people_list = null;
	
	ArrayList<SectionData> sections = null;

	String startNo, count, key1, key2, famousid, udid, delid;
	
	FrameLayout		rankingdetail_FrameLayout;
	View			rankingdetail_View;
	
	ProgressBar		rankingdata_loading_ProgressBar;
	
	RankingShowResultHandler	rankingShowResultHandler;
	String	strResultMessage = "";
	
	private String swi = "off";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rankingshow);
		loadIns();
		//mDialog = new NendDialog(this);
		//mDialog.show();
		
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowmanager.getDefaultDisplay().getMetrics(metrics);
		
		double a = metrics.widthPixels;
		double b = metrics.heightPixels;
		double c = 0;
		
		if(metrics.densityDpi == 160){
			c = 1;
			Log.i("metrics.densityDpi 160", String.valueOf(metrics.densityDpi));
		}else if(metrics.densityDpi == 240){
			c = 1.5;
			Log.i("metrics.densityDpi 240", String.valueOf(metrics.densityDpi));
		}else if(metrics.densityDpi == 320){
			c = 2;
			Log.i("metrics.densityDpi 320", String.valueOf(metrics.densityDpi));
		}else if(metrics.densityDpi == 480){
			c = 3;
			Log.i("metrics.densityDpi 480", String.valueOf(metrics.densityDpi));
		}else if(metrics.densityDpi == 640){
			c = 4;
			Log.i("metrics.densityDpi 640", String.valueOf(metrics.densityDpi));
		}
		Log.i("Window Size", String.valueOf(a) + "," + String.valueOf(b) + "," + String.valueOf(c) );
		
		
		double Xdp = a / c;
		double Ydp = b / c;
		double sum = Xdp / Ydp;
		
		Log.i("Window Dp", String.valueOf(Xdp) + "," + String.valueOf(Ydp) + "," + String.valueOf(sum) );
		if(sum != 0.5625 || sum != 0.5625){
			LinearLayout lineout = (LinearLayout)findViewById(R.id.LinearLayoutInterstitialBottom);
			lineout.setVisibility(View.INVISIBLE);
			
			LayoutParams params = (LayoutParams)lineout.getLayoutParams();
			params.width = 0;
			params.height = 0;
			lineout.setLayoutParams(params);
		}
		
		updateUI();
		
		btn_Total = (Button)findViewById(R.id.btn_rankingtype_total);
		btn_Total.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				rankingType = RankingType.RANKING_TOTAL;
				
				updateUI();
				
				openSection();
				
				showIns();
			}
		});
		
		btn_Category = (Button)findViewById(R.id.btn_rankingtype_category);
		btn_Category.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				rankingType = RankingType.RANKING_CATEGORY;
				
				updateUI();
				
				openSection();
				
				showIns();
			}
		});
		
		btn_Group = (Button)findViewById(R.id.btn_rankingtype_group);
		btn_Group.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				rankingType = RankingType.RANKING_GROUP;
				
				updateUI();
				
				openSection();
				
				showIns();
			}
		});
		
		btn_People = (Button)findViewById(R.id.btn_rankingtype_people);
		btn_People.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				rankingType = RankingType.RANKING_PEOPLE;
				
				updateUI();
				
				openSection();
				
				showIns();
			}
		});
		
		btn_LeftArrow = (Button)findViewById(R.id.btn_prev_pagestep);
		btn_LeftArrow.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				SectionData secData = sections.get(rankingType.value());
			    if (secData.startno > 0) {
			        secData.startno -= 5;
			        
			        if (secData.startno < 0)
			            secData.startno = 0;
			        
			        secData.clearData();
			        
			        showIns();
			        
			        reqRankData(null);
			    }
			}
		});
		
		btn_TypeChange = (Button)findViewById(R.id.btn_rankingkind_change);
		btn_TypeChange.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(rankingKind == RankingKind.SINDAN_RANKING)
					rankingKind = RankingKind.MYRANKING;
				else
					rankingKind = RankingKind.SINDAN_RANKING;
				
				updateUI();
				
				initViewWithOutSearch(rankingType.value());
				
				showIns();
			}
		});
		
		btn_RightArrow = (Button)findViewById(R.id.btn_next_pagestep);
		btn_RightArrow.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				SectionData secData = sections.get(rankingType.value());
			    if (secData.startno < secData.allcount - 5) {
			        secData.startno += 5;
			        
			        secData.clearData();
			        
			        reqRankData(null);
			        
			        showIns();
			    }
			}
		});
		
		btn_Close = (Button)findViewById(R.id.btn_close);
		btn_Close.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
					goBack();
			}
		});
        
		//RankingType Spinner
		spinner_RankingType = (Spinner) findViewById(R.id.spinner_rankingtype);
		spinner_RankingType.setOnItemSelectedListener(this);
		
		rankingdetail_FrameLayout = (FrameLayout)findViewById(R.id.rankingdetail_layout);
		LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rankingdetail_View = inflater.inflate(R.layout.subview_rankingdetail, null);
		rankingdetail_FrameLayout.addView(rankingdetail_View);
		rankingdetail_FrameLayout.setVisibility(View.INVISIBLE);
				
		((Button)rankingdetail_View.findViewById(R.id.btn_closeview)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				rankingdetail_FrameLayout.setVisibility(View.INVISIBLE);
			}
		});
		
		rankingdata_loading_ProgressBar = (ProgressBar) findViewById(R.id.rankingdata_loading_progress);
		rankingdata_loading_ProgressBar.setVisibility(View.INVISIBLE);
		
		updateMoveButton();
		
		initView(0);

		//showSuruPass(SuruPassType.InterstitialTop);
		//showSuruPass(SuruPassType.BottomBanner);
//		rankingShowResultHandler = new RankingShowResultHandler();		
	}
	
	public void updateUI()
	{
		if(rankingKind == RankingKind.SINDAN_RANKING)
		{
			((TextView) findViewById(R.id.txtview_rankingkind_title)).setText("診断ランキング");
			((ImageView) findViewById(R.id.imgview_rankingkind_title)).setImageResource(R.drawable.crown);
			((Button) findViewById(R.id.btn_rankingkind_change)).setText("マイランキング");
		}
		else
		{
			((TextView) findViewById(R.id.txtview_rankingkind_title)).setText("マイランキング");
			((ImageView) findViewById(R.id.imgview_rankingkind_title)).setImageResource(R.drawable.myrankmark);
			((Button) findViewById(R.id.btn_rankingkind_change)).setText("診断ランキング");
		}
		
		((TextView) findViewById(R.id.lblselected_rankingtype_total)).setVisibility(rankingType == RankingType.RANKING_TOTAL ? View.VISIBLE : View.INVISIBLE);
		((TextView) findViewById(R.id.lblselected_rankingtype_category)).setVisibility(rankingType == RankingType.RANKING_CATEGORY ? View.VISIBLE : View.INVISIBLE);
		((TextView) findViewById(R.id.lblselected_rankingtype_group)).setVisibility(rankingType == RankingType.RANKING_GROUP ? View.VISIBLE : View.INVISIBLE);
		((TextView) findViewById(R.id.lblselected_rankingtype_people)).setVisibility(rankingType == RankingType.RANKING_PEOPLE ? View.VISIBLE : View.INVISIBLE);
		
		if(rankingType == RankingType.RANKING_TOTAL)
			((TextView) findViewById(R.id.txtview_rankingtype_title)).setText("総合ランキング");
		else if(rankingType == RankingType.RANKING_CATEGORY)
			((TextView) findViewById(R.id.txtview_rankingtype_title)).setText("カテゴリを選択");
		else if(rankingType == RankingType.RANKING_GROUP)
			((TextView) findViewById(R.id.txtview_rankingtype_title)).setText("グループを選択");
		else if(rankingType == RankingType.RANKING_PEOPLE)
			((TextView) findViewById(R.id.txtview_rankingtype_title)).setText("人物を選択");
		
		((Spinner) findViewById(R.id.spinner_rankingtype)).setVisibility(rankingType == RankingType.RANKING_TOTAL ? View.INVISIBLE : View.VISIBLE);
	}
	
	private void LongTask(){
		try{
			//showIns();
			swi = "on";
			Thread.sleep(5000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	void initView(int nSecNo)
	{
		sections = new ArrayList<SectionData>();
		for (int i = 0; i < 4; i++) {
	        SectionData section = new SectionData();
	        section.initData();
	        sections.add(section);
	    }
		
		openSection();
	}
	
	void initViewWithOutSearch(int nSecNo)
	{
		for (int i = 0; i < 4; i++) {
	        SectionData section = sections.get(i);
	        section.initDataWithOutSearch();
	    }
		
		openSection();
	}
	
	void openSection() {
		
		SectionData section = sections.get(rankingType.value());
		
		switch(rankingType) {
			case RANKING_TOTAL:
				if (section.binit == false)
					reqRankData(null);
				else
					updateRankingListView();
				
				break;
				
			case RANKING_CATEGORY:
			case RANKING_GROUP:
				if(rankingtype_category_list == null){
					reqCategory();
				}else{
					updateFilters();
					spinner_RankingType.setSelection(section.searchindex + 1);
					
					if (section.searchindex == -1)
	                {
	                    section.initData();
	                    updateRankingListView();
	                }
	                else if (section.binit == true)
	                	updateRankingListView();
	                else
	                    reqRankData(null);
				}				
				break;
				
			case RANKING_PEOPLE:
				if(rankingtype_people_list == null){
					reqCategory();
				}else{
					updateFilters();
					spinner_RankingType.setSelection(section.searchindex + 1);
					
					if (section.searchindex == -1)
	                {
	                    section.initData();
	                    updateRankingListView();
	                }
	                else if (section.binit == true)
	                	updateRankingListView();
	                else
	                    reqRankData(null);
				}				
				break;
		}
		
		updateMoveButton();
	}
	
	void reqRankData(String delRankId) {
		
		rankingdata_loading_ProgressBar.setVisibility(View.VISIBLE);
		
		SectionData secData = sections.get(rankingType.value());
		
		startNo = String.valueOf(secData.startno);
		count = String.valueOf(5);
		
		key1 = "";
		key2 = "";
		famousid = "";
		udid = "";
		delid = "";
	    
	    if (rankingType == RankingType.RANKING_CATEGORY)
	        key1 = secData.searchword;
	    
	    if (rankingType == RankingType.RANKING_GROUP)
	        key2 = secData.searchword;

	    if (rankingType == RankingType.RANKING_PEOPLE)
	        famousid = secData.searchword;
	    
	    udid = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
	    
	    if (delRankId != null)
	        delid = delRankId;
	    
	  //Request category data to server.
	  		new Thread(new Runnable() {
	  			public void run() {
	  				try {
	  					
	  					String handlerUrl = GlobalClass.SERVER_URL;
	  		            
	  					HttpClient client = new DefaultHttpClient();
	  					HttpPost post = new HttpPost(handlerUrl);
	  					
	  					MultipartEntity reqEntity = new MultipartEntity();
	  					
	  					reqEntity.addPart("commandType", new StringBody("rank_request"));
	  					reqEntity.addPart("start_num", new StringBody(startNo));
	  					reqEntity.addPart("count_num", new StringBody(count));
	  					
	  					if (rankingKind == RankingKind.MYRANKING)
	  						reqEntity.addPart("udid", new StringBody(udid));
	  					
	  					if (key1.equals("") == false)
	  						reqEntity.addPart("key1", new StringBody(key1));
	  					
	  					if (key2.equals("") == false)
	  						reqEntity.addPart("key2", new StringBody(key2));
	  					
	  					if (famousid.equals("") == false)
	  						reqEntity.addPart("famousid", new StringBody(famousid));
	  					
	  					if (delid.equals("") == false)
	  						reqEntity.addPart("delid", new StringBody(delid));
	  					
	  					post.setEntity(reqEntity);
	  					HttpResponse res = client.execute(post);
	  					HttpEntity resEntity = res.getEntity();
	  					final String response_str = EntityUtils.toString(resEntity);
	  					
	  					if (resEntity != null) {
	  						runOnUiThread(new Runnable() {
	  							public void run() {
	  								try
	  								{
	  									JSONObject jsonObject = new JSONObject(response_str);
	  	    							String status = jsonObject.getString("result");
	  	    							
	  	    							if(status.equals("ok") )
	  	    							{
	  	    								SectionData secData = sections.get(rankingType.value());
	  	    								secData.clearData();
	  	    								
	  	    								int startno = jsonObject.getInt("startno");
	  	    								int allcount = jsonObject.getInt("allcount");
	  	    								secData.startno = startno;
	  	    					            secData.allcount = allcount;
	  	    					            
	  	    					            if(allcount > 0)
	  	    					            {
		  	    					            JSONArray rankdata = jsonObject.getJSONArray("rankdata");
		  	    					            for(int i = 0; i < rankdata.length(); i++)
		  	    					            {
		  	    					            	JSONObject object = rankdata.getJSONObject(i);
		  	    					            	
		  	    					            	String imgurl = object.has("imgurl") ? object.getString("imgurl") : "";
		  	    					            	if(imgurl.equals("") == false)
		  	    					            		imgurl = "http://" + GlobalClass.DEFAULT_SERVER_ADRESS + imgurl;
		  	    					            	
		  	    					            	String rankid = object.has("rankid") ? object.getString("rankid") : "";
		  	    					            	String famousname = object.has("famousname") ? object.getString("famousname") : "";
		  	    					            	String username = object.has("username") ? object.getString("username") : "";
		  	    					            	String syncrate = object.has("syncrate") ? object.getString("syncrate") : "";
		  	    					            	String no = String.valueOf(startno + i + 1);
		  	    					            	
		  	    					            	secData.rankingarray.add(new RankingDataCell(no, famousname, username, syncrate, rankid, imgurl, rankingKind == RankingKind.MYRANKING));
		  	    					            }
	  	    					            }
	  	    					            
	  	    					            secData.binit = true;
	  	    					            
//  	    					            	deleteid = "";
  	    					            	
  	    					            	updateRankingListView();
  	    					            	
  	    					            	updateMoveButton();
	  	    					            
  	    					            	reqImgData();
  	    					            	
	  	    							}else{
	  	    							
	  	    							}
	  								}catch (Exception e){
	  									
	  								}
	  								
	  								rankingdata_loading_ProgressBar.setVisibility(View.INVISIBLE);
	  							}
	  						});
	  					}else{

	  					}
	  					
	  				} catch (Exception e) {
	  					e.printStackTrace();
	  				}
	  			}
	  		}).start();
	}
	
	void reqImgData() {
		SectionData secData = sections.get(rankingType.value());
		
		for(int i = 0; i < secData.rankingarray.size(); i++){
			
			final RankingDataCell cell = secData.rankingarray.get(i);
			
			new Thread(new Runnable() {
				public void run() {
					if(cell.imgurl.equals("") == false)
						 cell.userphoto = DownloadImage(cell.imgurl);
					
					runOnUiThread(new Runnable() {
							public void run() {
								updateRankingListView();			
							}
					});					
				}
			}).start();
		}
	}
	
	void reqCategory() {

		rankingdata_loading_ProgressBar.setVisibility(View.VISIBLE);
		
		//Request category data to server.
		new Thread(new Runnable() {
			public void run() {
				try {
					
					String handlerUrl = GlobalClass.SERVER_URL;
		            
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(handlerUrl);
					
					MultipartEntity reqEntity = new MultipartEntity();
					
					if(rankingType == RankingType.RANKING_PEOPLE){
						reqEntity.addPart("commandType", new StringBody("rare_request"));
						reqEntity.addPart("rare", new StringBody("s,a"));
					}else{
						reqEntity.addPart("commandType", new StringBody("category_request"));	
					}
					
					post.setEntity(reqEntity);
					HttpResponse res = client.execute(post);
					HttpEntity resEntity = res.getEntity();
					final String response_str = EntityUtils.toString(resEntity);
					
					if (resEntity != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								try
								{
									JSONObject jsonObject = new JSONObject(response_str);
	    							String status = jsonObject.getString("result");
	    							
	    							if(status.equals("ok") )
	    							{
	    								if(rankingType == RankingType.RANKING_PEOPLE){
	    									rankingtype_people_list = jsonObject.getJSONArray("rare");	
	    								}else{
	    									rankingtype_category_list = jsonObject.getJSONArray("key1");
		    								rankingtype_group_list = jsonObject.getJSONArray("key2");
	    								}
	    								
	    								updateFilters();
	    								
	    							}else{
	    							
	    							}
								}catch (Exception e){
									
								}
								
								rankingdata_loading_ProgressBar.setVisibility(View.INVISIBLE);
							}
						});
					}else{

					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	void updateRankingListView() {
		
		rankingdata_ListView = (ListView) findViewById(R.id.listview_rankingdata);
		
		SectionData secData = sections.get(rankingType.value());
		
		rankingdatalistview_Adapter = new RankingDataListViewAdapter(this, this, secData.rankingarray);		
		rankingdata_ListView.setAdapter(rankingdatalistview_Adapter);
		
//		rankingdatalistview_Adapter.notifyDataSetChanged();
	}
		
	public void updateFilters() {
        
        ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        JSONArray valueArray = null;
        if(rankingType == RankingType.RANKING_TOTAL)
        	valueArray = null;
        else if(rankingType == RankingType.RANKING_CATEGORY)
        	valueArray = rankingtype_category_list;
		else if(rankingType == RankingType.RANKING_GROUP)
			valueArray = rankingtype_group_list;
		else if(rankingType == RankingType.RANKING_PEOPLE)
			valueArray = rankingtype_people_list;

        try{
    		if(valueArray != null)
			{
    			adapterDay.add("選択");
    			
				for(int i = 0; i < valueArray.length(); i++)
				{
					JSONObject jsonObj = valueArray.getJSONObject(i);
					adapterDay.add(jsonObj.has("value") ? jsonObj.getString("value") : "");
				}
			}

        }catch(Exception e){
        	
        }        

        spinner_RankingType.setAdapter(adapterDay);
	}
	
	void updateMoveButton() {
		Button prevBtn = (Button) findViewById(R.id.btn_prev_pagestep);
    	Button nextBtn = (Button) findViewById(R.id.btn_next_pagestep);
    	
    	if(sections == null)
    	{
    		prevBtn.setEnabled(false);
    		prevBtn.setBackgroundResource(R.layout.custom_btn_prev_pagestep_disabled);
    		
	        nextBtn.setEnabled(false);
	        nextBtn.setBackgroundResource(R.layout.custom_btn_next_pagestep_disabled);
	        
	        return;
    	}
    	
		SectionData secData = sections.get(rankingType.value());
	    
		if (secData.binit && (secData.allcount > 0)) {
	        if (secData.startno < 5)
	        {
	        	prevBtn.setEnabled(false);
	        	prevBtn.setBackgroundResource(R.layout.custom_btn_prev_pagestep_disabled);
	        }else{
	        	prevBtn.setEnabled(true);
	        	prevBtn.setBackgroundResource(R.layout.custom_btn_prev_pagestep_enabled);
	        }	        	
	            
	        if (secData.startno >= (secData.allcount - 5))
	        {
	        	nextBtn.setEnabled(false);
	        	nextBtn.setBackgroundResource(R.layout.custom_btn_next_pagestep_disabled);
	        }
	        else
	        {
	        	nextBtn.setEnabled(true);
	        	nextBtn.setBackgroundResource(R.layout.custom_btn_next_pagestep_enabled);
	        }
	        
	    }else {
	    	prevBtn.setEnabled(false);
	    	prevBtn.setBackgroundResource(R.layout.custom_btn_prev_pagestep_disabled);
	    	
	        nextBtn.setEnabled(false);
	        nextBtn.setBackgroundResource(R.layout.custom_btn_next_pagestep_disabled);
	    }
	}
	
	public void deleteRankingCell(final String delid) {
		new AlertDialog.Builder(FSRankingShowActivity.this)
		.setTitle("削除")
        .setMessage("ランキングを削除します")
        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            	SectionData secData = sections.get(rankingType.value());
            	
            	secData.clearData();
            	
            	reqRankData(delid);
            }
        })
        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            }
        })
        .create().show();
	}
	
	public void showRankingDetails(String username,String fpname, String syncrate, Bitmap userphoto) {
		
		if(rankingdetail_FrameLayout.getVisibility() == View.VISIBLE)
			return;
		
		((TextView)rankingdetail_View.findViewById(R.id.user_name)).setText(username);	
		((TextView)rankingdetail_View.findViewById(R.id.fp_name)).setText(fpname);	
		((TextView)rankingdetail_View.findViewById(R.id.syncrate)).setText(syncrate);
		((ImageView)rankingdetail_View.findViewById(R.id.userphoto)).setImageBitmap(userphoto);
		
		rankingdetail_FrameLayout.setVisibility(View.VISIBLE);
	}
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//		Toast.makeText(this, ((TextView)v.findViewById(android.R.id.text1)).getText().toString(), Toast.LENGTH_LONG).show();
		
		int selectedIndex = position - 1;
		
		SectionData secData = sections.get(rankingType.value());
		secData.searchindex = selectedIndex;
		
		if(selectedIndex >= 0){
			try{
				JSONObject jsonObj;
				
				switch(rankingType) {
					case RANKING_CATEGORY:
						jsonObj = rankingtype_category_list.getJSONObject(selectedIndex);
						secData.searchword = jsonObj.has("key") ? jsonObj.getString("key") : "";
						break;
						
					case RANKING_GROUP:
						jsonObj = rankingtype_group_list.getJSONObject(selectedIndex);
						secData.searchword = jsonObj.has("key") ? jsonObj.getString("key") : "";
						break;
						
					case RANKING_PEOPLE:
						jsonObj = rankingtype_people_list.getJSONObject(selectedIndex);
						secData.searchword = jsonObj.has("key") ? jsonObj.getString("key") : "";
						break;
				}
				
				reqRankData(null);
				
	        }catch(Exception e){
	        	
	        }
		}else{
			secData.initData();
			
			updateRankingListView();
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
//		Toast.makeText(this.getActivity(), "", Toast.LENGTH_LONG).show();
	}
	
	public class RankingShowResultHandler extends Handler {
		
		String strResult = "";
		
		public void handleMessage(Message msg) {
			
			Bundle bundle = msg.getData();
			
			Handler h = new Handler();
	        h.postDelayed(new Runnable() {
	            public void run() {
	            	
	            }
	        }, 100);
		}
	}

	void goBack(){
		Intent i = new Intent(FSRankingShowActivity.this, FamousPeopleActivity.class);
		startActivity(i);
		finish();
	}
	
	private InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
       if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return in;     
    }
	
    private Bitmap DownloadImage(String URL)
    {
        Bitmap bitmap = null;
        InputStream in = null;        
        try {
            in = OpenHttpConnection(URL);
            if(in != null)
            {
            	bitmap = BitmapFactory.decodeStream(in);
            	in.close();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;                
    }
    
    //
    public void onClick(NendAdInterstitialClickType clickType) {
        // クリックに応じて処理行う
        switch (clickType) {
        case CLOSE:
        	Log.i("AD_rank_CLOSE", "CLOSE");
            break;
        case DOWNLOAD:
        	Log.i("AD_rank_DOWNLOAD", "DOWNLOAD");
            break;
        default :
            break;
        }
        // 広告クリックをログに出力
        Log.d("AD_TAG", clickType.name());
    }
    
  
     //広告受信通知
  
    public void onCompletion(NendAdInterstitialStatusCode statusCode) {
        // 受信結果に応じて処理を行う
        switch (statusCode) {
        case SUCCESS:
        	Log.v("AD_SUCCESS", "SUCCESS");
            break;
        case FAILED_AD_DOWNLOAD:
        	Log.v("AD_FAILED_AD_DOWNLOAD", "FAILED_AD_DOWNLOAD");
            break;
        case INVALID_RESPONSE_TYPE:
        	Log.v("AD_INVALID_RESPONSE_TYPE", "INVALID_RESPONSE_TYPE");
            break;
        case FAILED_AD_INCOMPLETE:
        	Log.v("AD_FAILED_AD_INCOMPLETE", "FAILED_AD_INCOMPLETE");
            break;
        case FAILED_AD_REQUEST:
        	Log.v("AD_FAILED_AD_REQUEST", "FAILED_AD_REQUEST");
            break;
        default:
            break;
        }
        // 広告受信結果をログに出力
        Log.d("AD_TAG", statusCode.name());
    }
}
