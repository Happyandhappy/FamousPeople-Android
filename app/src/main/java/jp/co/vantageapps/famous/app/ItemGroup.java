package jp.co.vantageapps.famous.app;

import java.util.ArrayList;

public class ItemGroup {
	
	public boolean _load = false;
	public int groupNo;
	public String path = "";
	public ArrayList<ItemInfo> itemInfos;
	
	public ItemInfo getItemInfo(int itemIndex){
		ItemInfo item = (ItemInfo)itemInfos.get(itemIndex);
		return item;
	}
	
	public synchronized void setload(boolean load){
		_load = load;
	}
			
}
