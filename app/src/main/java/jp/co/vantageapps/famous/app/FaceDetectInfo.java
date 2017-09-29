package jp.co.vantageapps.famous.app;

import org.apache.http.util.ByteArrayBuffer;
import android.graphics.PointF;
import android.graphics.RectF;

public class FaceDetectInfo {
	public static RectF bound = new RectF(0,0,0,0);
	public static PointF eyeL = new PointF(0,0);	
	public static PointF eyeR = new PointF(0,0);	
	public static PointF nose = new PointF(0,0);	
	public static ByteArrayBuffer feature = new ByteArrayBuffer(0);
	public static float  roll = 0.0f; 
}
