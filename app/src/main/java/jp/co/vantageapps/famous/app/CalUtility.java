package jp.co.vantageapps.famous.app;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class CalUtility {
	
	/** 
     * Read bytes from a File into a byte[].
     * 
     * @param file The File to read.
     * @return A byte[] containing the contents of the File.
     * @throws IOException Thrown if the File is too long to read or couldn't be
     * read fully.
     */
	@SuppressLint("NewApi")
	public static Bitmap getResoureBimap(Resources res, int resid)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		  options.inMutable = true;
		}
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap resourcebmp =  BitmapFactory.decodeResource(res, resid, options);
		Bitmap bitmap;
		if (resourcebmp.isMutable()) {
			bitmap = resourcebmp;
		} else {
			bitmap = resourcebmp.copy(Bitmap.Config.ARGB_8888, true);
			resourcebmp.recycle();
		}
		return bitmap;
	}
	
	public static void mkdir(String folderName)
	{
		String root = Environment.getExternalStorageDirectory().toString();		            
		File myDir = new File(root + "/" + folderName+ "/");
		if(!myDir.exists())
			myDir.mkdirs();
	}
	
	
    @SuppressWarnings("resource")
	public static byte[] readBytesFromFile(File file) throws IOException {
      InputStream is = new FileInputStream(file);
      
      // Get the size of the file
      long length = file.length();
  
      // You cannot create an array using a long type.
      // It needs to be an int type.
      // Before converting to an int type, check
      // to ensure that file is not larger than Integer.MAX_VALUE.
      if (length > Integer.MAX_VALUE) {
        throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
      }
  
      // Create the byte array to hold the data
      byte[] bytes = new byte[(int)length];
  
      // Read in the bytes
      int offset = 0;
      int numRead = 0;
      while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
          offset += numRead;
      }
  
      // Ensure all the bytes have been read in
      if (offset < bytes.length) {
          throw new IOException("Could not completely read file " + file.getName());
      }
  
      // Close the input stream and return bytes
      is.close();
      return bytes;
  }
    
    /**
     * Writes the specified byte[] to the specified File path.
     * 
     * @param theFile File Object representing the path to write to.
     * @param bytes The byte[] of data to write to the File.
     * @throws IOException Thrown if there is problem creating or writing the 
     * File.
     */
    public static void writeBytesToFile(File theFile, byte[] bytes) throws IOException {
      BufferedOutputStream bos = null;
      
    try {
      FileOutputStream fos = new FileOutputStream(theFile);
      bos = new BufferedOutputStream(fos); 
      bos.write(bytes);
    }finally {
      if(bos != null) {
        try  {
          //flush and close the BufferedOutputStream
          bos.flush();
          bos.close();
        } catch(Exception e){}
      }
    }
    }
    
	// autio play
	public static void playaudio(Activity mainactivity, String filename)
	{
		
	
		try {
			AssetManager am = mainactivity.getAssets();
    		AssetFileDescriptor afd = am.openFd(filename);
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    
                	Log.v("MEDIA", "Completed");
                    mp.release();                    
                }
            });
            player.setLooping(false);
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        } 
	}
	
	// image processing
	
	public static int getScreenWidth(){
		// Get screen Size.
		/*DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    int screenWidth = metrics.widthPixels;
	    int screenHeight = metrics.heightPixels;*/
	    return -1;

	}
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	public static Bitmap imageRotatedByDegrees(Bitmap bmp, int rotation){
		Matrix matrix = new Matrix();
		
		 Matrix mat = new Matrix();
         mat.postRotate(rotation);
         matrix.setRotate(rotation, bmp.getWidth()/2, bmp.getHeight()/2);
         Bitmap bmpRotate = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
         
         return bmpRotate;
	}
	
	 public static byte[] getBytes(InputStream is) throws IOException {

		    int len;
		    int size = 1024;
		    byte[] buf;

		    if (is instanceof ByteArrayInputStream) {
		      size = is.available();
		      buf = new byte[size];
		      len = is.read(buf, 0, size);
		    } else {
		      ByteArrayOutputStream bos = new ByteArrayOutputStream();
		      buf = new byte[size];
		      while ((len = is.read(buf, 0, size)) != -1)
		        bos.write(buf, 0, len);
		      buf = bos.toByteArray();
		    }
		    return buf;
	}
	 
	 private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    private static final int STRIDE = 200;   // must be >= WIDTH
	   
	private static int[] createColors() {
	        int[] colors = new int[STRIDE * HEIGHT];
	        for (int y = 0; y < HEIGHT; y++) {
	            for (int x = 0; x < WIDTH; x++) {
	                int r = x * 255 / (WIDTH - 1);
	                int g = y * 255 / (HEIGHT - 1);
	                int b = 255 - Math.min(r, g);
	                int a = Math.max(r, g);
	                colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
	            }
	        }
	        return colors;
	 }
	
	public static Bitmap createBitmap(Bitmap.Config config){
		 int[] colors = createColors();
		 
		 Bitmap bmp = Bitmap.createBitmap(colors, 0, STRIDE, WIDTH, HEIGHT,config);
		 return bmp;
	}
	 
	public static Bitmap convert(Activity parent, Bitmap orig, Bitmap.Config config) {
		 Bitmap newBitmap = Bitmap.createBitmap( orig.getWidth(), orig.getHeight(), config );
		 int w = newBitmap.getWidth();
		 int h = newBitmap.getHeight();
		 Canvas c = new Canvas(newBitmap);
		 Drawable drawable = new BitmapDrawable(parent.getResources(), orig);
	     drawable.setBounds(0,0, w,h);
 	     drawable.draw(c);
		 return newBitmap;
	}
	
	public static Bitmap imageByCombiningImageEx(Activity parent, Bitmap firstImage,  int center1X, int center1Y, 
												 Bitmap secondImage, int center2X, int center2Y){
		
		
		Bitmap bitmap = null;
	    try {
	    	
	    	int firstWidth = firstImage.getWidth();
    	    int firstHeight = firstImage.getHeight();
    	    
    	    int secondWidth = secondImage.getWidth();
    	    int secondHeight = secondImage.getHeight();
    	    
    	    //UIImage *image = nil;
    	    int marginleft = Math.max(center1X, center2X);
    	    int marginright = Math.max(firstWidth - center1X, secondWidth - center2X);
    	    
    	    int margintop = Math.max(center1Y, center2Y);
    	    int marginbottom = Math.max(firstHeight - center1Y, secondHeight - center2Y);
    	    
    	    int newImageSizeW = marginleft + marginright;
    	    int newImageSizeH = margintop + marginbottom;
    	    //CGSize newImageSize1 = CGSizeMake(MAX(firstWidth, secondWidth), MAX(firstHeight, secondHeight));
    	    
    	    bitmap = Bitmap.createBitmap(newImageSizeW, newImageSizeH, Config.ARGB_8888);
    	    Canvas c = new Canvas(bitmap);
	        
	        Drawable drawable1 = new BitmapDrawable(parent.getResources(), firstImage);
	        Drawable drawable2 = new BitmapDrawable(parent.getResources(), secondImage);
    	    
    	    int dX = Math.max(center1X, center2X) - center1X;
    	    int dY = Math.max(center1Y, center2Y) - center1Y;
    	        	    
    	    drawable1.setBounds(dX, dY, dX + firstImage.getWidth(), dY + firstImage.getHeight());
    	    
    	    dX = Math.max(center1X, center2X) - center2X;
    	    dY = Math.max(center1Y, center2Y) - center2Y;
    	    drawable2.setBounds(dX, dY, dX + secondImage.getWidth(), dY + secondImage.getHeight());
    	    
    	    drawable1.draw(c);
	        drawable2.draw(c);

	    } catch (Exception e) {
	    }
	    return bitmap;
	}
	
	public static void writeBitmap(Bitmap writebmp, String path, String name){
        String root = Environment.getExternalStorageDirectory().toString();		            
		File myDir = new File(root + "/" + path + "/");
		myDir.delete();
//		if(!myDir.exists())
//			myDir.mkdirs();
//		String fname = name;
//		
//		
//		File file = new File(myDir, fname + ".png");
//		if (file.exists()){
//			file.delete();
//		}
//	    FileOutputStream fOut = null;
//		try {
//			fOut = new FileOutputStream(file);
//			writebmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//			fOut.flush();
//			fOut.close();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}
}
