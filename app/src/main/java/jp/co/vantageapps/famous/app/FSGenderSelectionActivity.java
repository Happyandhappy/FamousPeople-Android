package jp.co.vantageapps.famous.app;

import jp.co.vantageapps.famous.app.SuruPassActivity.SuruPassType;
import net.nend.android.NendAdInterstitial;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;


public class FSGenderSelectionActivity extends SuruPassActivity {
	//NendDialog mDialog;
	
	Button btn_backto_main;
	ImageButton btn_female;
	ImageButton btn_sexless;
	ImageButton btn_male;
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genderselection);
		
		//mDialog = new NendDialog(this);
		
		//mDialog.show();
		
		btn_backto_main = (Button) findViewById(R.id.btn_backto_main);
		btn_backto_main.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		btn_female = (ImageButton) findViewById(R.id.btn_female);
		btn_female.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoCategorySelectionActivity(1);
			}
		});
		
		btn_sexless = (ImageButton) findViewById(R.id.btn_sexless);
		btn_sexless.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoCategorySelectionActivity(2);
			}
		});
		
		btn_male = (ImageButton) findViewById(R.id.btn_male);
		btn_male.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoCategorySelectionActivity(3);
			}
		});
		
		showSuruPass(SuruPassType.InterstitialTop);
		showSuruPass(SuruPassType.BottomBanner);
	}
	
	protected void gotoCategorySelectionActivity(int gender) {
		Intent i = new Intent(FSGenderSelectionActivity.this, FSCategorySelectionActivity.class);
		((GlobalClass)getApplication()).setGender(gender);
		startActivity(i);
	//	finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(FSGenderSelectionActivity.this, FamousPeopleActivity.class);
		startActivity(i);
		finish();
	}
}
