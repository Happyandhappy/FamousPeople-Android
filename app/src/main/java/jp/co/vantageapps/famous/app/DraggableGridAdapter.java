package jp.co.vantageapps.famous.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.co.vantageapps.famous.app.ItemInfo.ItemProcessType;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;


@SuppressWarnings("rawtypes")
@SuppressLint("NewApi")
public class DraggableGridAdapter extends ArrayAdapter {
	
	private Context context;
	private int layoutResourceId;
	private ArrayList data = new ArrayList();
	
	private Activity parent;

	private ItemGroup itemGroup;
	
	@SuppressWarnings("unchecked")
	public DraggableGridAdapter(Context context, int layoutResourceId,
			ArrayList data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	public void clearall()
	{
		data.clear();
		this.clear();
	}
	
	@SuppressWarnings("unchecked")
	public DraggableGridAdapter(Context context, int layoutResourceId,
			ItemGroup itemGroup, Activity parent) {
		
		super(context, layoutResourceId, itemGroup.itemInfos);		
		
		//super(context, layoutResourceId, itemGroup.itemInfos);
		//super(context, layoutResourceId);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.itemGroup = itemGroup;
		this.parent = parent;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) 
		{	LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.imageCell);
			holder.pBar = (ProgressBar)row.findViewById(R.id.gifPBar);
			holder.pBar.animate();
			
			row.setTag(holder);
		} else {		
			holder = (ViewHolder) row.getTag();
		}
		
		ItemInfo item = itemGroup.getItemInfo(position);
		item.tag = holder;
		item.tagView = row;
		
		if (item.gif() == null)
		{
			AssetManager am = this.parent.getAssets();
			InputStream in;
			try {
				in = am.open(itemGroup.path + item.name + ".gif");
				GifAnimationDrawableEx gif = new GifAnimationDrawableEx(in); 
				gif.setOneShot(false);
				item.setgif(gif);
			} catch (IOException e) {}
		}
		
		if (item.gif() != null){
			if(holder.image.getDrawable() == null) {
				//holder.image
				holder.image.setImageDrawable(item.gif());
				item.gif().setVisible(true, true);
				
				if (item.process == ItemProcessType.ItemComplete)
					item.tag.pBar.setVisibility(View.GONE);
				else
					item.tag.pBar.setVisibility(View.VISIBLE);
			}
		}
		
		return row;
	}
	
	
	/*public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) 
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			
			holder.imageTitle = (TextView) row.findViewById(R.id.text);
			holder.image = (ImageView) row.findViewById(R.id.imageCell);
			row.setTag(holder);
		} else {		
			holder = (ViewHolder) row.getTag();
		}

		ImageItem item = (ImageItem) data.get(position);
		holder.imageTitle.setText(item.getTitle());
		holder.image.setImageBitmap(item.getImage());
		return row;
	}*/

	public class ViewHolder {
		ProgressBar pBar;
		//TextView imageTitle;
		ImageView image;		
	}
}