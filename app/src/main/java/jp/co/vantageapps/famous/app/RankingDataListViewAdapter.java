package jp.co.vantageapps.famous.app;

import java.util.ArrayList;

import jp.co.vantageapps.famous.app.FSRankingShowActivity.RankingType;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RankingDataListViewAdapter extends BaseAdapter {

	private Context context;
	private FSRankingShowActivity parentActivity;
	private ArrayList<RankingDataCell> drawerItems;

	public RankingDataListViewAdapter(Context context, FSRankingShowActivity parent,
			ArrayList<RankingDataCell> drawerItems) {
		this.context = context;
		this.parentActivity = parent;
		this.drawerItems = drawerItems;
	}

	@Override
	public int getCount() {
		return drawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return drawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.listviewcell_rankingdata, null);
			
			final RankingDataCell cell = (RankingDataCell) getItem(position);
			
			final RelativeLayout layout_RankingCell = (RelativeLayout) convertView.findViewById(R.id.rankingcell_layout);
			layout_RankingCell.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					parentActivity.showRankingDetails(cell.username,cell.famousname, cell.syncrate, cell.userphoto);
				}
			});
			
			final TextView txtviewNo = (TextView) convertView.findViewById(R.id.number);
			txtviewNo.setText(cell.no.replaceAll("\n", ""));
			
			final TextView txtviewFamousname = (TextView) convertView.findViewById(R.id.famouspeople_name);
			txtviewFamousname.setText(cell.famousname.replaceAll("\n", ""));
			
			final TextView txtviewUsername = (TextView) convertView.findViewById(R.id.user_name);
			txtviewUsername.setText(cell.username.replaceAll("\n", ""));
			
			final TextView txtviewSyncrate = (TextView) convertView.findViewById(R.id.syncrate);
			txtviewSyncrate.setText(cell.syncrate.replaceAll("\n", ""));
			
			final TextView txtviewPhotoLoading = (TextView) convertView.findViewById(R.id.userphoto_loading);
			final ImageView imgviewUserPhoto = (ImageView) convertView.findViewById(R.id.userphoto);
			if(cell.imgurl.equals("") == false)
			{
				if(cell.userphoto != null){
					imgviewUserPhoto.setImageBitmap(cell.userphoto);
					txtviewPhotoLoading.setVisibility(View.INVISIBLE);
				}else{
					imgviewUserPhoto.setImageBitmap(null);
					txtviewPhotoLoading.setVisibility(View.VISIBLE);
				}
			}else{
				imgviewUserPhoto.setImageBitmap(null);
				txtviewPhotoLoading.setVisibility(View.INVISIBLE);
			}
			
			final Button btnDelRankingItem = (Button) convertView.findViewById(R.id.btn_del_rankingitem);
			btnDelRankingItem.setVisibility(cell.isMyRanking ? View.VISIBLE : View.INVISIBLE);
			btnDelRankingItem.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					parentActivity.deleteRankingCell(cell.rankid);					
				}
			});
		}		
		
		return convertView;
	}

}