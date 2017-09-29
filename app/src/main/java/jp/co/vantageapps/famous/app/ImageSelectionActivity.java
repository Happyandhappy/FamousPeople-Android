package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import net.nend.android.NendAdInterstitial;

import jp.co.vantageapps.famous.app.FaceDetectionActivity.ActivityType;
import jp.co.vantageapps.famous.app.SuruPassActivity.SuruPassType;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageSelectionActivity extends SuruPassActivity {
	
		
	public static ActivityType acivityType;
	
	Button btn_backto_category;
	ImageButton btn_camera;
	ImageButton btn_album;
	Bitmap takePicture;
	TextView title;
	private String imgPath;
	final private int MAXCAP_SIZE = 512;
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;
    
    private int cameramethod = 2;
    
    
    
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageselection);
		

		title = (TextView)findViewById(R.id.textView_imageselection);
		if (acivityType == ActivityType.Acitivity_GIF)
			title.setText("コラ画像作成");
		
		btn_backto_category = (Button) findViewById(R.id.btn_backto_category);
		btn_backto_category.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSuruPass();
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		
		btn_camera = (ImageButton) findViewById(R.id.btn_camera);
		btn_camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameramethod == 1){
					// TODO Auto-generated method stub
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
		            startActivityForResult(intent, CAPTURE_IMAGE);
				}else if (cameramethod == 2){
					// TODO Auto-generated method stub
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
	                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	                startActivityForResult(intent,CAPTURE_IMAGE);
				}
			}
		});

		
		btn_album = (ImageButton) findViewById(R.id.btn_album);
		btn_album.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
			}
		});
		
		if(isFinishing() == false){
			showSuruPass(SuruPassType.InterstitialTop);
			showSuruPass(SuruPassType.BottomBanner);
		}else{
			closeSuruPass();
		}
		//showSuruPass(SuruPassType.InterstitialTop);
		//showSuruPass(SuruPassType.BottomBanner);
		//showSuruPass(SuruPassType.InterstitialTop);	
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
	        if (resultCode == Activity.RESULT_OK) {
	            if (requestCode == CAPTURE_IMAGE) {

	            	if (cameramethod == 1){

	            		takePicture = rotateImage(getImagePath());

	            	}else if (cameramethod == 2){

	            		File f = new File(Environment.getExternalStorageDirectory().toString());

		                for (File temp : f.listFiles()) {
		                    if (temp.getName().equals("temp.jpg")) {
		                        f = temp;
		                        break;
		                    }
		                }
		                takePicture = rotateImage(f.getAbsolutePath());
		                f.delete();

		                /* try
		                {
		                   takePicture = rotateImage(f.getAbsolutePath());
		                   String path = android.os.Environment
		                            .getExternalStorageDirectory()
		                            + File.separator
		                            + "Phoenix" + File.separator + "default";
		                    f.delete();

		                    /*OutputStream outFile = null;
		                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
		                    try {
		                        outFile = new FileOutputStream(file);
		                        takePicture.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
		                        outFile.flush();
		                        outFile.close();
		                    } catch (FileNotFoundException e) {
		                        e.printStackTrace();
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    } catch (Exception e) {
		                        e.printStackTrace();
		                    }

		                } catch (Exception e) {
		                    e.printStackTrace();
		                }*/
	            	}

	            } else if (requestCode == PICK_IMAGE) {
	            	//takePicture = BitmapFactory.decodeFile(getAbsolutePath(data.getData()));

//	            	takePicture = rotateImage(getAbsolutePath(data.getData()));
                    takePicture = rotateImage(ImageFilePath.getPath(ImageSelectionActivity.this, data.getData()));

//                    try {
//                        InputStream bitmap=getAssets().open("4.jpeg");
//                        takePicture=BitmapFactory.decodeStream(bitmap);
//                    } catch (IOException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
	            }
	        }

	        if (takePicture != null){
	        	closeSuruPass();
	        	FaceDetectionActivity.acivityType = ImageSelectionActivity.acivityType;
	        	((GlobalClass)getApplication()).setImage(takePicture);
				Intent i = new Intent(ImageSelectionActivity.this, FaceDetectionActivity.class);
				startActivity(i);
			//	finish();
	        }
    }

	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
	    int width = image.getWidth();
	    int height = image.getHeight();

	    float bitmapRatio = (float)width / (float) height;
	    if (bitmapRatio > 0) {
	        width = maxSize;
	        height = (int) (width / bitmapRatio);
	    } else {
	        height = maxSize;
	        width = (int) (height * bitmapRatio);
	    }
	    return Bitmap.createScaledBitmap(image, width, height, true);
	}
	
    private Bitmap rotateImage(final String path) {
       
    	Bitmap rotateBitmap = null;
        Bitmap b = decodeFileFromPath(path);
   
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                  
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
                default:
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        FileOutputStream out1 = null;
        File file;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
            	CalUtility.mkdir("famouspeople");
                file = new File(Environment.getExternalStorageDirectory() + "/famouspeople/", "image" + new Date().getTime() + ".jpg");
            }
            else {
                file = new File(getFilesDir() , "image" + new Date().getTime() + ".jpg");
            }
            out1 = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out1);
            rotateBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            
            Log.e("SAVE FAMOUSE", file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out1.close();
            } catch (Throwable ignore) {

            }
        }
        
        return rotateBitmap;
    }

    private Bitmap decodeFileFromPath(String path){
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
           // Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            Bitmap b = getResizedBitmap(BitmapFactory.decodeStream(in, null, o2), MAXCAP_SIZE);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAbsolutePath(Uri uri) {
        if(Build.VERSION.SDK_INT >= 19){
        	String arr[] = uri.getLastPathSegment().split(":");
        	String id;
        	if (arr.length > 1)
            	id = arr[1];
        	else
        		id = uri.getLastPathSegment();
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            final String imageOrderBy = null;
            Uri tempUri = getUri();
            Cursor imageCursor = getContentResolver().query(tempUri, imageColumns,
                    MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);
            if (imageCursor.moveToFirst()) {
                return imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }else{
                return null;
            }
        }else{
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }

    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public Uri setImageUri() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	CalUtility.mkdir("famouspeople");
            File file = new File(Environment.getExternalStorageDirectory() + "/famouspeople/", "image" + new Date().getTime() + ".jpg");
            Uri imgUri = Uri.fromFile(file);
            this.imgPath = file.getAbsolutePath();
            return imgUri;
        }
        else {
            File file = new File(getFilesDir() , "image" + new Date().getTime() + ".jpg");
            Uri imgUri = Uri.fromFile(file);
            this.imgPath = file.getAbsolutePath();
            return imgUri;
        }
    }

    public String getImagePath() {
        return imgPath;
    }

	@Override
	public void onBackPressed() {
	
		Intent i = null;
		if (ImageSelectionActivity.acivityType == ActivityType.Acitivity_FS){
			// TODO Auto-generated method stub
			i = new Intent(ImageSelectionActivity.this,
					FSCategorySelectionActivity.class);
		} else if(ImageSelectionActivity.acivityType == ActivityType.Acitivity_GIF){
			// TODO Auto-generated method stub
			i = new Intent(ImageSelectionActivity.this, FamousPeopleActivity.class);
	       
		}
		startActivity(i);
		finish();
	}
	

	/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        super.onActivityResult(requestCode, resultCode, data);
    
        if (resultCode == RESULT_OK) 
        {
        	takePicture = null;
        	
            if (requestCode == 1) 
            {
            	File f = new File(Environment.getExternalStorageDirectory().toString());
                
            	for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                
                try 
                {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    
                    Bitmap tempBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions); 
                    
                    ExifInterface ei = new ExifInterface(f.getAbsolutePath());
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    
                    switch(orientation)
                    {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                    	takePicture = CalUtility.imageRotatedByDegrees(tempBitmap, -90);
                    	break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                    	takePicture = CalUtility.imageRotatedByDegrees(tempBitmap, -180);
                    	break;
                    	
                    case ExifInterface.ORIENTATION_ROTATE_270:
                    	takePicture = CalUtility.imageRotatedByDegrees(tempBitmap, -90);
                    	break;
                    }
                    
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        takePicture.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            } else if (requestCode == 2) {
 
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                takePicture = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath+"");
                //viewImage.setImageBitmap(thumbnail);
            }
            
            if(takePicture != null) {
            	FaceDetectionActivity.acivityType = ActivityType.Acitivity_FS;
            	((GlobalClass)getApplication()).setImage(takePicture);
				Intent i = new Intent(FSImageSelectionActivity.this, FaceDetectionActivity.class);
				startActivity(i);
				finish();
            }
        }
	}*/
}
