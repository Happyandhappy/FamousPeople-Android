package jp.co.vantageapps.famous.app;

import android.graphics.Point;
import android.graphics.Rect;

public class FaceDetectionResult {
	
	public byte[] faceData;
	public Point[] rectPoint;
	public Rect faceRect;
	public int[] facefeaturePoint; //landmark, x, y - 68 array
	
	public byte[] getFaceData() {
		return faceData;
	}
	
	public void setFaceData(byte[] faceData) {
		if (this.faceData != null){
			this.faceData = null;
			System.gc();
		}
		this.faceData = faceData.clone();
	}
	
	
	public Point[] getFaceRectPoint() {
		return rectPoint;
	}
	
	public void setFaceRectPoint(int[] rectPoint) {
		int len = rectPoint.length / 2;
		this.rectPoint = new Point[len];
		for (int i = 0; i < len; i++) {
			this.rectPoint[i] = new Point();
			this.rectPoint[i].x = rectPoint[i * 2];
			this.rectPoint[i].y = rectPoint[i * 2 + 1];
		}

		setFaceRect();
	}
	
	public void setFaceFeaturePoint(int[] featurePoint) {
		int len = featurePoint.length;
		this.facefeaturePoint = new int[len];
		for(int i = 0; i < len; i++){
			this.facefeaturePoint[i] = featurePoint[i];
		}
	}
	
	public int[] getFeaturePoint(){
		return this.facefeaturePoint;
	}

	public int[] getLandMarks() {
		return this.facefeaturePoint;
	}

	public String getFaceDataString() {
		String ret = "";
		if (faceData != null) {
			for (int i = 0; i < faceData.length; i ++) {
				if (i == faceData.length - 1) {
					ret += String.valueOf((int)faceData[i]);
				} else {
					ret += String.valueOf((int)faceData[i]) + ",";
				}
			}
		}
		return ret;
	}

	public void setFaceRect()
	{
		int left = Integer.MAX_VALUE;
		for(int i = 0; i < rectPoint.length; i++)
			if(rectPoint[i].x < left)
				left = rectPoint[i].x;

		int top = Integer.MAX_VALUE;
		for(int i = 0; i < rectPoint.length; i++)
			if(rectPoint[i].y < top)
				top = rectPoint[i].y;

		int right = Integer.MIN_VALUE;
		for(int i = 0; i < rectPoint.length; i++)
			if(rectPoint[i].x > right)
				right = rectPoint[i].x;

		int bottom = Integer.MIN_VALUE;
		for(int i = 0; i < rectPoint.length; i++)
			if(rectPoint[i].y > bottom)
				bottom = rectPoint[i].y;


		faceRect = new Rect(left, top, right, bottom);
	}

	public Rect getFaceRect() {
		return faceRect;
	}
}
