package jp.co.vantageapps.famous.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedClass {
	
	private static String RUN_KEY = "runcount_asda12";
	
	private static String POPUP_KEY = "popup_123123";
	
	public static void saveRunCount(Activity main){
		SharedPreferences sharedPref =  main.getSharedPreferences(RUN_KEY, Context.MODE_PRIVATE);
		int runcount = sharedPref.getInt(RUN_KEY, 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		runcount++;
		if (runcount > 3) runcount = 1;
		editor.putInt(RUN_KEY, runcount);
		editor.commit();
	}
	
	public static int getRunCount(Activity main){
		SharedPreferences sharedPref =  main.getSharedPreferences(RUN_KEY, Context.MODE_PRIVATE);
		int runcount = sharedPref.getInt(RUN_KEY, 0);
		return runcount;
	}
	
	public static void savePopup(Activity main, int popupvalue){
		SharedPreferences sharedPref =  main.getSharedPreferences(POPUP_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(POPUP_KEY, popupvalue);
		editor.commit();
	}
	
	public static int getPopup(Activity main){
		SharedPreferences sharedPref =  main.getSharedPreferences(POPUP_KEY, Context.MODE_PRIVATE);
		int popupvalue = sharedPref.getInt(POPUP_KEY, 1);
		return popupvalue;
	}
}
