package jp.co.vantageapps.famous.app;

import java.util.ArrayList;

import android.view.View;

import jp.co.vantageapps.famous.app.DraggableGridAdapter.ViewHolder;

public class ItemInfo {

	public enum ItemProcessType {
		ItemNone,
		ItemProcessing,
		ItemComplete
	};	
	
	public ItemProcessType process = ItemProcessType.ItemNone;
	
	public String name = "";
	public boolean      order;
	public double       mHeight;
	public int       framecount;
	public ArrayList<FrameInfo> frameInfos = new ArrayList<FrameInfo>();

	public ViewHolder	tag;
	public View 		tagView;
	private GifAnimationDrawableEx _gif = null;
	public String 		gifPath = "";
	
	public synchronized GifAnimationDrawableEx gif(){
		return _gif;
	}
	
	public synchronized void setgif(GifAnimationDrawableEx gif){
		
		if (_gif != null){
			_gif.cleardata();
			_gif = null;
			
		}
		_gif = gif;
	}
	
	public synchronized void setprocess(ItemProcessType process){
		this.process = process;
	}
}
