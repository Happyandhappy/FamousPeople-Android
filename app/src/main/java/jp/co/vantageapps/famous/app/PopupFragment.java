package jp.co.vantageapps.famous.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PopupFragment extends Fragment {
	
	public static FamousPeopleActivity mainActivity;
	
	public static View view;
	public static PopupFragment frageview;
	
	private TextView btn1;
	private TextView btn2;
	private TextView btn3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_popup,
				container, false);
		
		btn1 = (TextView)view.findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedClass.savePopup(mainActivity, 1);
				PopupFragment.view.setVisibility(View.GONE);
				Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=jp.co.vantageapps.famous.app");
		        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		        startActivity(intent);
			}
		});
		
		btn2 = (TextView)view.findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedClass.savePopup(mainActivity, 0);
				PopupFragment.view.setVisibility(View.GONE);
			}
		});		
		
		btn3 = (TextView)view.findViewById(R.id.button3);
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupFragment.view.setVisibility(View.GONE);
			}
		});	

		frageview = this;
		return view;
	}
	
	
	
	public static void setShow(boolean bShow){
		if (bShow){
			view.setVisibility(View.VISIBLE);			
		}
		else
			view.setVisibility(View.INVISIBLE);
	}
	
}
