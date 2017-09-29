package jp.co.vantageapps.famous.app;



public class DetectorReal {
	
    static {
		try {
			System.loadLibrary("faceengine");//pmin
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInitFaceEngine = false;
	public static native int initialize();
	public static native FaceDetectionResult[] detectFace(byte[] bmpArray, int width, int heigh, int imgType);
	public static native double identify(byte[] inFeature, int len1, byte[] dbFeature, int len2);
	public static native boolean close();

	public static double identify(byte[] result1, byte[] result2) {
		if (result1 != null && result2 != null) {
			return identify(result1, result1.length, result2, result2.length);
		}
		return 0;
	}
}
