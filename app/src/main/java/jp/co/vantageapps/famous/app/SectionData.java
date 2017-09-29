package jp.co.vantageapps.famous.app;

import java.util.ArrayList;

public class SectionData {
	
	boolean binit;
	int searchindex;
	String searchword;
	int startno;
	ArrayList<RankingDataCell> rankingarray;
	int allcount;
	
	void initData()
	{
		binit = false;
	    searchindex = -1;
	    searchword = "";
	    startno = 0;
	    allcount = 0;
	    rankingarray = new ArrayList<RankingDataCell>();
	}
	
	void initDataWithOutSearch()
	{
		binit = false;
	    startno = 0;
	    allcount = 0;
	    rankingarray = new ArrayList<RankingDataCell>();
	}
	
	void clearData()
	{
		rankingarray = new ArrayList<RankingDataCell>();
	}
}
