package jp.co.vantageapps.famous.app;

import java.io.File;

import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class GifShareFragment extends Fragment {
	
	public static GifFactoryActivity mainActivity;
	
	public enum ShareType {
		facebook,
		twitter,
		mail,
		album,
		line
	};	
	
	private ImageButton btnclose;
	public static ItemInfo item;
	ImageView imageView;
	public static View view;
	public static GifShareFragment frageview;
	
	private ImageButton btnFacebook;
	private ImageButton btnTwitter;
	private ImageButton btnLine;
	private ImageButton btnMail;
	private ImageButton btnAlbum;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_gif_share,
				container, false);
		
		btnclose = (ImageButton)view.findViewById(R.id.btnsharecancel);
		btnclose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GifShareFragment.view.setVisibility(View.INVISIBLE);
			}
		});
		
		btnFacebook = (ImageButton)view.findViewById(R.id.btnfacebook);
		btnFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(ShareType.facebook);
			}
		});
		
		btnTwitter = (ImageButton)view.findViewById(R.id.btntwitter);
		btnTwitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(ShareType.twitter);
			}
		});
		
		btnLine = (ImageButton)view.findViewById(R.id.btnline);
		btnLine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(ShareType.line);
			}
		});
		
		btnMail = (ImageButton)view.findViewById(R.id.btnmail);
		btnMail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(ShareType.mail);
			}
		});
		
		btnAlbum = (ImageButton)view.findViewById(R.id.btnalbum);
		btnAlbum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(ShareType.album);
			}
		});
		
		imageView = (ImageView)view.findViewById(R.id.rareImageView);
		
		if (item != null)
		if (item.gif() != null)
			imageView.setImageDrawable(item.gif());

		frageview = this;
		
		return view;
	}
	
	private void shareByIntent()
	{
		String filelocation = "file:///"
				+ Environment.getExternalStorageDirectory().toString()
				+ item.gifPath;
		
		Log.d("gifpath",item.gifPath);
		
		if (filelocation.isEmpty())
			return;
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/gif");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.gifPath));
		startActivity(Intent.createChooser(share, "Share Gif"));
	}
	
	private void share(ShareType shareType){
		
		switch(shareType)
		{
		case facebook:
			shareByIntent();
			break;
			
		case twitter:
			shareByIntent();
			break;
			
		case line:
			shareByIntent();
			break;
			
		case mail:
			shareMail();
			break;
			
		case album:
			//shareByIntent();
			shareAlbum();
			break;
		}
	}
	
	private void shareAlbum()
	{
		String gifFilePath = item.gifPath;
		
		if (gifFilePath.isEmpty())
			return;
		
		File rfile, wfile;
		rfile = new File(gifFilePath);
		
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	CalUtility.mkdir("famouspeople");
        	wfile = new File(Environment.getExternalStorageDirectory() + "/famouspeople/", "image" + new Date().getTime() + ".gif");
        }
        else {
        	wfile = new File(mainActivity.getFilesDir() , "image" + new Date().getTime() + ".gif");
        }
        
        byte[] rBuf = null;
        try {
        	rBuf = CalUtility.readBytesFromFile(rfile);
        	CalUtility.writeBytesToFile(wfile, rBuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
         
        
		/*ContentValues values = new ContentValues();
	    values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
	    values.put(Images.Media.MIME_TYPE, "image/gif");
	    values.put(MediaStore.MediaColumns.DATA, filePath);
	   mainActivity.getContentResolver().insert(Images.Media.INTERNAL_CONTENT_URI, values);    	
    	*/
    	Builder pDialog = new AlertDialog.Builder(mainActivity);
	    pDialog.setTitle("成功!");
	    pDialog.setMessage("画像がアルバムに保存されました。");
	    pDialog.setIcon(1);
	    pDialog.show();
	}
	
	private void shareMail()
	{
		String gifFilePath = item.gifPath;
		
		if (gifFilePath.isEmpty())
			return;
		
		File rfile, wfile;
		rfile = new File(gifFilePath);
		
		long date = new Date().getTime();
		
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	CalUtility.mkdir("famouspeople");
        	wfile = new File(Environment.getExternalStorageDirectory() + "/famouspeople/", "image" + date + ".gif");
        }
        else {
        	wfile = new File(mainActivity.getFilesDir() , "image" + date + ".gif");
        }
        
        byte[] rBuf = null;
        try {
        	rBuf = CalUtility.readBytesFromFile(rfile);
        	CalUtility.writeBytesToFile(wfile, rBuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		String filelocation = Environment.getExternalStorageDirectory() + "/famouspeople/image" + date + ".gif";
		File sendFile = new File(filelocation); 
		Log.d("gifpath",filelocation);
		
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		
		// send the type to 'email'
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.setType("image/gif");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sendFile));
		
		// the mail subject.
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Gif file");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
		
//		
//		
//		if (filelocation.isEmpty())
//			return;
//		Intent share = new Intent(Intent.ACTION_SEND);

//		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.gifPath));
//		startActivity(Intent.createChooser(share, "Share Gif"));
	}
	
	public void setGif(ItemInfo item)
	{
		if (item != null)
			if (item.gif() != null)
				imageView.setImageDrawable(item.gif());
		
		imageView.invalidate();
	}
	
	public static void setShow(boolean bShow){
		if (bShow){
			view.setVisibility(View.VISIBLE);
			frageview.setGif(item);
		}
		else
			view.setVisibility(View.INVISIBLE);
	}
	
}
