package jp.co.vantageapps.famous.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.vantageapps.famous.app.ItemInfo.ItemProcessType;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


public class GlobalClass extends MultiDexApplication {
	
	public static final String DEFAULT_SERVER_ADRESS = "128.199.167.58";
	
	public static final String SERVER_URL = "http://" + DEFAULT_SERVER_ADRESS + "/famouspeople_ver2/index.php";
	
//	static {
//		try {
//			System.loadLibrary("faceengine");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
    private int maskSelNumber = 0;
	public int getMaskSelNumber() {
		return maskSelNumber;
	}

	public void setMaskSelNumber(int maskSelNumber) {
		this.maskSelNumber = maskSelNumber;
	}

	private Bitmap image = null;
	private Bitmap Face = null;
	private Bitmap maskedFace = null;
	private ArrayList<ItemGroup> configs;
	public static Activity mainActivity;
	int gender;
	HashMap<String, String> recognitionResult;
	FaceDetectionResult detectionResult;
	
	private String key1 = "";
	private String key2 = "";
	private String rare = "";
	
	public void setkey1(String key1){
		this.key1 = key1;
	}
	
	public String getkey1()
	{
		return this.key1;
	}
	
	public void setkey2(String key2){
		this.key2 = key2;
	}
	
	public String getkey2()
	{
		return this.key2;
	}
	
	public void setrare(String rare){
		this.rare = rare;
	}
	
	public String getrare()
	{
		return this.rare;
	}
	
	public int getGender() {
		return gender;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public FaceDetectionResult getDetectionResult() {
		return detectionResult;
	}
		
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		/*if (this.image != null){
			this.image.recycle();
			this.image = null;
			System.gc();
		}*/
		this.image = image;
	}
	
	public void setFace(Bitmap Face)
	{
		/*if (this.Face != null)
		{
			this.Face.recycle();
			this.Face = null;
			System.gc();
		}*/
		this.Face = Face;
	}
	
	public Bitmap getFace()
	{
		return this.Face;
	}
	
	public void setDetectionResult(FaceDetectionResult result) {
//		this.detectionResult = new FaceDetectionResult();
//		this.detectionResult.copydata(result);//result;

		this.detectionResult = result;
	}
	
	public HashMap<String, String> getRecognitionResult() {
		return recognitionResult;
	}
	
	public void setRecognitionResult(HashMap<String, String> result) {
		this.recognitionResult = result;
	}
	
	public void setMaskedFace(Bitmap maskedFace)
	{
		/*if (this.maskedFace != null)
		{
			this.maskedFace.recycle();
			this.maskedFace = null;
			System.gc();
		}*/
		
		this.maskedFace = maskedFace;
	}
	
	public Bitmap getMaskedFace()
	{
		return this.maskedFace;
	}
	
	private ArrayList<String> sortString(JSONArray jsonArray) throws JSONException{
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i<jsonArray.length(); i++){
			data.add(jsonArray.getString(i));
		}
		
		Collections.sort(data);
		
		return data;
	}
	
	
	public void clearConfigGifs()
	{
		if (configs == null)
			return;
		
		for (int i = 0; i < configs.size(); i++) 
		{
			ItemGroup itemGroup = configs.get(i);
			for (int j = 0; j<itemGroup.itemInfos.size(); j++)
			{
				ItemInfo item = (ItemInfo)itemGroup.itemInfos.get(j);
				item.setgif(null);
				item.setprocess(ItemProcessType.ItemNone);
			}
		}
	}
	
	public void readConfigs()
	{
		if (configs != null)
			return;
		
		configs = new ArrayList<ItemGroup>();
		
		try{
					
			String[] configfils = {"0/0_config", "1/1_config", "2/2_config", "3/3_config", "4/4_config"};
			AssetManager am = this.getAssets();
			
			for (int i = 0; i < configfils.length; i++) 
			{
				ItemGroup itemGroup = new ItemGroup();
				itemGroup.path = String.valueOf(i) + "/";
				
				itemGroup.itemInfos = new ArrayList<ItemInfo>();
				StringBuilder buf = new StringBuilder();
			    InputStream json;
				try {					
					json = am.open(configfils[i]);
				    BufferedReader in = new BufferedReader(new InputStreamReader(json, "Cp1252"));
				    String str;
				    while ((str=in.readLine()) != null) {
				      buf.append(str);
				    }
				    in.close();
				    
				    JSONObject jsonObject = new JSONObject(buf.toString());
				    JSONArray jsonArray = jsonObject.names();
				    
				    ArrayList<String> sortArray = sortString(jsonArray);
				    ItemInfo item = null;
				    
				    int size = sortArray.size();
				    for (int j = 0; j < size; j++){
				    
				    	item = new ItemInfo();
				    	
				    	item.name = sortArray.get(j);
				    	
				    	JSONObject frameJson = jsonObject.getJSONObject(item.name);
				    	item.order = frameJson.getBoolean("order");
				    	item.mHeight = frameJson.getInt("mHeadheight");
				    	item.framecount = frameJson.getInt("framecount");
				    	
				    	JSONArray dataArray = frameJson.getJSONArray("data");
				    	for (int k = 0; k < dataArray.length(); k++){
				    		
				    		JSONObject dataJson = new JSONObject(dataArray.getString(k));
				    		FrameInfo frame = new FrameInfo();
				    		frame.x = dataJson.getDouble("x");
				    		frame.y = dataJson.getDouble("y");
				    		frame.r = dataJson.getDouble("r");
				    		
				    		frame.eye_action = dataJson.getInt("eye_action");
				    		frame.mouth_action = dataJson.getInt("mouth_action");
				    		frame.blow_action = dataJson.getInt("blow_action");
				    		frame.head = dataJson.getInt("head");
				    		item.frameInfos.add(frame);
				    	}
				    	
					    itemGroup.itemInfos.add(item);
				    }
			    	
				    Log.v("xxxxx", buf.toString());
				    
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				itemGroup.groupNo = i;
				configs.add(itemGroup);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<ItemGroup> getConfigs()
	{
		return configs;
	}
	
	public ItemGroup getConfig(int index){
		ItemGroup item = (ItemGroup)configs.get(index);
		return item;
	}
	
		
    private String name;
    private String email;
     
 
    public String getName() {
         
        return name;
    }
     
    public void setName(String aName) {
        
       name = aName;
    }
    
    public String getEmail() {
         
        return email;
    }
     
    public void setEmail(String aEmail) {
        
      email = aEmail;
    }
}
