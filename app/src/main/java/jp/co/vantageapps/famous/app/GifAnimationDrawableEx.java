/**
 * Copyright (C) 2013 Orthogonal Labs, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;


/**
 * <p>Creates an AnimationDrawable from a GIF image.</p>
 *
 * @author Femi Omojola <femi@hipmob.com>
 */
public class GifAnimationDrawableEx extends AnimationDrawable
{
	private boolean IsExType = true;
	
	private boolean decoded;
	
	private GifDecoderEx mGifDecoderEx;

	private Bitmap mTmpBitmap;
	
	private ArrayList<GifBitmap> bitmapFrames;

	private int height, width;
	
	public static Activity mainActivity;
	
	
	public GifAnimationDrawableEx(File f) throws IOException
	{
		this(f, false);
	}
	
	public GifAnimationDrawableEx(InputStream is) throws IOException
	{
		this(is, false);
	}
	
	public void cleardata(){
		bitmapFrames.clear();
		bitmapFrames = null;
	}
	
	public GifAnimationDrawableEx(File f, boolean inline) throws IOException
	{
	    this(new BufferedInputStream(new FileInputStream(f), 32768), inline);
	}
	
	@SuppressWarnings("deprecation")
	public GifAnimationDrawableEx(ArrayList<Bitmap> arrayBmp)
	{
		super();
		bitmapFrames = new ArrayList<GifBitmap>();
		for (int i = 0; i < arrayBmp.size(); i++) {
			Bitmap bitmap = arrayBmp.get(i);			
			Bitmap copyTmpFrame = bitmap.copy(bitmap.getConfig(), true);
			addFrame(new BitmapDrawable(copyTmpFrame),0);
			GifBitmap gifBitmap = new GifBitmap();
            gifBitmap.bitmap = bitmap;
            gifBitmap.delay = 100;
        	bitmapFrames.add(gifBitmap);
        	if (i== 0){
        		setOneShot(mGifDecoderEx.getLoopCount() != 0);
                setVisible(true, true);
        	}
		}	
        decoded = true;
		mGifDecoderEx = null;        
	}
	
	
	@SuppressWarnings("deprecation")
	public GifAnimationDrawableEx(InputStream is, boolean inline) throws IOException
	{
		super();
		IsExType = true;
		InputStream bis = is;
		if(!BufferedInputStream.class.isInstance(bis)) 
			bis = new BufferedInputStream(is, 32768);
		decoded = false;
		
		if (IsExType){
			mGifDecoderEx = new GifDecoderEx();
			mGifDecoderEx.read(CalUtility.getBytes(bis));
			
			bitmapFrames = new ArrayList<GifBitmap>();
			int n = mGifDecoderEx.getFrameCount();
			//n = 1;
			for (int i = 0; i < n; i++) {
		     	mGifDecoderEx.advance();
		     	try {
		             mTmpBitmap = mGifDecoderEx.getNextFrame();
	   
		             height = mTmpBitmap.getHeight();
		         	 width = mTmpBitmap.getWidth();
		            
		         } catch (Exception e) {
		            
		         }
		     	int t = mGifDecoderEx.getDelay(i);
		     	//if (i == 0) 		t = 0;
		     	
	            android.util.Log.v("GifAnimationDrawable", "===>Frame "+i+": "+t+"]");
	            Bitmap copyTmpFrame = mTmpBitmap.copy(mTmpBitmap.getConfig(), true);
	            addFrame(new BitmapDrawable(copyTmpFrame), t);
	            GifBitmap gifBitmap = new GifBitmap();
	            gifBitmap.bitmap = copyTmpFrame;
	            gifBitmap.delay = t;
	        	bitmapFrames.add(gifBitmap);
	        	
	        	if (i== 0){
	        		setOneShot(mGifDecoderEx.getLoopCount() != 0);
	                setVisible(true, true);
	        	}
		    } 
			if (n != 1){
				decoded = true;
				mGifDecoderEx = null;
			}else{
				if(inline){
					loader.run();
				}else{
					new Thread(loader).start();
				}
			}
	         
			/**/
		}
	    
	}
	
	public GifBitmap getBitmapFrame(int index){
		GifBitmap bitmap = bitmapFrames.get(index);
		return  bitmap;
	}
	
	public int getDelay(int index){
		GifBitmap bitmap = bitmapFrames.get(index);
		return  bitmap.delay;
	}
	
	public boolean isDecoded(){ return decoded; }
	
	private Runnable loader = new Runnable(){
		@SuppressWarnings("deprecation")
		public void run() 
		{	
			if (IsExType)
			{
				
				final int n = mGifDecoderEx.getFrameCount();
				for (int i = 1; i < n; i++) {
			     	mGifDecoderEx.advance();
			     	try {
			             mTmpBitmap = mGifDecoderEx.getNextFrame();
			             
			             height = mTmpBitmap.getHeight();
			         	 width = mTmpBitmap.getWidth();
			            
			         } catch (Exception e) {
			            
			         }
			     	int t = mGifDecoderEx.getDelay(i);
		            android.util.Log.v("GifAnimationDrawable", "===>Frame "+i+": "+t+"]");
		            addFrame(new BitmapDrawable(mTmpBitmap), t);
		            //Bitmap copyTmpFrame = mTmpBitmap.copy(mTmpBitmap.getConfig(), true);
		            Bitmap copyTmpFrame = mTmpBitmap.copy(Bitmap.Config.ARGB_8888, true);
		            GifBitmap gifBitmap = new GifBitmap();
		            gifBitmap.bitmap = copyTmpFrame;
		            gifBitmap.delay = t;
		        	bitmapFrames.add(gifBitmap);
			    }  
			    decoded = true;
			    mGifDecoderEx = null;
			    
			}
	    }
	};
	
	public int getMinimumHeight(){ return height; }
	public int getMinimumWidth(){ return width; }
	public int getIntrinsicHeight(){ return height; }
	public int getIntrinsicWidth(){ return width; }
}
