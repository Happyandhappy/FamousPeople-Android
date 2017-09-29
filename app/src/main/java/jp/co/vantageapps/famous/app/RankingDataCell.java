package jp.co.vantageapps.famous.app;

import android.graphics.Bitmap;

public class RankingDataCell {
	
	public String no;	
	public String famousname;
	public String username;
	public String syncrate;
	public String rankid;
	public String imgurl;
	public Bitmap userphoto = null;
	public boolean isMyRanking;
	
	
	public RankingDataCell() {

	}
		
	public RankingDataCell(String no, String famousname, String username, String syncrate, String rankid, String imgurl, boolean isMyRanking) {
		super();
		
		this.no = no;
		this.famousname = famousname;
		this.username = username;
		this.syncrate = syncrate;
		this.rankid = rankid;
		this.imgurl = imgurl;
		this.isMyRanking = isMyRanking;
	}

}
