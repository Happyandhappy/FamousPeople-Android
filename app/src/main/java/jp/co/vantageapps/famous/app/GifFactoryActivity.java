package jp.co.vantageapps.famous.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;



import jp.co.vantageapps.famous.app.DraggableGridViewPager.OnPageChangeListener;
import jp.co.vantageapps.famous.app.DraggableGridViewPager.OnRearrangeListener;
import jp.co.vantageapps.famous.app.ItemInfo.ItemProcessType;



import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;


import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

/**
 * Created by Liuwei on 5/6/2017.
 */
public class GifFactoryActivity extends SuruPassActivity implements View.OnClickListener {

    private static final String TAG = "DraggableGridViewPagerTestActivity";
    private DraggableGridViewPager mDraggableGridViewPager;
    private ArrayAdapter<String> mAdapter = null;
    private DraggableGridAdapter dGridAdapter = null;
    private GifShareFragment gifShareFrament = null;
    private boolean bShareCreate = false;

    private int nCurGroup = -1;

    final private int nThreadCount = 3;
    private int threadLiveCount;
    private ArrayList<GifMakeThread> threadPool;

    public Activity self = null;
    boolean  _stopflag = false;

    ImageButton backbtn;
    ImageButton[] factorybtns = new ImageButton[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        self = this;
        gifShareFrament = new GifShareFragment();
        setContentView(R.layout.activity_gif_factory);
        resetGifFactory(0);

        factorybtns[0] = (ImageButton)findViewById(R.id.ImageMenuButton01);
        factorybtns[1] = (ImageButton)findViewById(R.id.ImageMenuButton02);
        factorybtns[2] = (ImageButton)findViewById(R.id.ImageMenuButton03);
        factorybtns[3] = (ImageButton)findViewById(R.id.ImageMenuButton04);
        //factorybtns[4] = (ImageButton)findViewById(R.id.ImageMenuButton05);
        for (int i = 0; i < 4; i++)
            factorybtns[i].setOnClickListener(this);

        selectbtn(0);

        backbtn = (ImageButton)findViewById(R.id.backbtnfactory);
        backbtn.setOnClickListener(this);
        showSuruPass(SuruPassType.BottomBanner);
        GifShareFragment.mainActivity = this;
    }

    public void onClick(View view) {
        ImageButton obj = (ImageButton)view;
        int nSetGroup = 0;

        if (obj == backbtn){
            closeSuruPass();
            onBackPressed();
            return;
        }

        if (obj == factorybtns[0]){
            nSetGroup = 0;
        }else if (obj == factorybtns[1]){
            nSetGroup = 1;
        }else if (obj == factorybtns[2]){
            nSetGroup = 2;
        }else if (obj == factorybtns[3]){
            nSetGroup = 3;
        }

        selectbtn(nSetGroup);

        if (nCurGroup != nSetGroup){
            terminateThreads();
            System.gc();

            resetGifFactory(nSetGroup);
        }
    }

    private void selectbtn( int selNo){
        for (int i = 0; i < 4; i++){
            if (i == selNo)
                factorybtns[i].setImageResource(R.drawable.cos_checked);
            else
                factorybtns[i].setImageResource(R.drawable.cos_normal);
        }
    }

    private void showShareDiaog(int position){

        ItemGroup itemGroup = getItemGroup(nCurGroup);
        GifShareFragment.item = itemGroup.getItemInfo(position);

        if (bShareCreate){
            GifShareFragment.setShow(true);
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.share_frame, gifShareFrament).commit();
            bShareCreate = true;
        }
    }

    public void terminateThreads(){
        if (threadPool != null){

            setStopflag(true);
            while (true) {
                if (threadLiveCount == 0)
                    break;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void resetGifFactory(int nGroupNo)
    {
        nCurGroup = nGroupNo;

        mDraggableGridViewPager = (DraggableGridViewPager) findViewById(R.id.draggable_grid_view_pager);
        GifAnimationDrawableEx.mainActivity = this;

        dGridAdapter = new DraggableGridAdapter(this, R.layout.row_grid, getItemGroup(nCurGroup), this);
        mDraggableGridViewPager.setAdapter(dGridAdapter);
        mDraggableGridViewPager.requestLayout();
        mDraggableGridViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v(TAG, "onPageScrolled position=" + position + ", positionOffset=" + positionOffset
                        + ", positionOffsetPixels=" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected position=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged state=" + state);
            }
        });

        mDraggableGridViewPager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showShareDiaog(position);
            }
        });

        mDraggableGridViewPager.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //showToast(((TextView) view).getText().toString() + " long clicked!!!");
                return true;
            }
        });

        mDraggableGridViewPager.setOnRearrangeListener(new OnRearrangeListener() {
            @Override
            public void onRearrange(int oldIndex, int newIndex) {
                Log.i(TAG, "OnRearrangeListener.onRearrange from=" + oldIndex + ", to=" + newIndex);
                String item = mAdapter.getItem(oldIndex);
                mAdapter.setNotifyOnChange(false);
                mAdapter.remove(item);
                mAdapter.insert(item, newIndex);
                mAdapter.notifyDataSetChanged();
            }
        });

        resetThreadPool(nThreadCount);
    }

    private synchronized boolean stopflag()
    {
        return _stopflag;
    }

    private synchronized void setStopflag(boolean sflag)
    {
        _stopflag = sflag;
    }

    private void resetThreadPool(int nThCount)
    {
        threadPool = new ArrayList<GifMakeThread>();
        setStopflag(false);
        threadLiveCount = 0;
        ItemGroup itemGroup = getItemGroup(nCurGroup);
        for (int i = 0; i < nThCount; i++){
            GifMakeThread gt = new GifMakeThread(itemGroup);
            gt.start();
            threadPool.add(gt);
            threadLiveCount++;
        }
    }

    private class GifMakeThread extends Thread{

        ItemGroup itemGroup;

        public GifMakeThread(ItemGroup itemGroup) { // ONLY WORKS AFTER SAVING
            this.itemGroup = itemGroup;
        }

        @Override
        public void run(){

            while (true)
            {
                if (stopflag())
                {
                    threadLiveCount--;
                    Log.e("Thread:", "complete");
                    return;
                }

                ItemInfo item = null;
                for (int i = 0; i < itemGroup.itemInfos.size(); i++){
                    ItemInfo temp = itemGroup.getItemInfo(i);
                    if (temp.process == ItemProcessType.ItemNone)
                    {
                        item = temp;
                        break;
                    }
                }

                if (item == null)
                    break;

                if (item.gif() == null)
                    continue;

                if (item.gif().isDecoded() == false)
                    continue;

                item.setprocess(ItemProcessType.ItemProcessing);

                int frameCount = item.frameInfos.size();

                ArrayList<Bitmap> newFrames = new ArrayList<Bitmap>();

                GifAnimationDrawableEx gif = item.gif();

                for (int i = 0; i < frameCount; i++){

                    GifBitmap iFrame = gif.getBitmapFrame(i);
                    FrameInfo fInfo = item.frameInfos.get(i);
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    Bitmap maskedFace = globalVariable.getMaskedFace();
                    boolean order = item.order;
                    double mHeight = item.mHeight;

                    double scale = 1.0;
                    double xF = fInfo.x / scale;
                    double yF = fInfo.y / scale;
                    double r = fInfo.r;

                    // resize masked face.
                    double zScale = maskedFace.getHeight() / mHeight;
                    int mWidth = (int)(maskedFace.getWidth() / zScale);
                    Bitmap scaleImage = Bitmap.createScaledBitmap(maskedFace, mWidth, (int)mHeight, true);

                    // rotate;
                    Bitmap headImage = CalUtility.imageRotatedByDegrees(scaleImage, (int)r);
                    double xH = headImage.getWidth() / 2.0;
                    double yH = headImage.getHeight() / 2.0;

                    int head = fInfo.head;
                    Bitmap combineImg = null;

                    if (head == -1)
                    {
                        combineImg = iFrame.bitmap;

                    } else {
                        if ((head == 1) || (order == false)){
                            combineImg = CalUtility.imageByCombiningImageEx(self, iFrame.bitmap,
                                    (int)xF, (int)yF, headImage, (int)xH, (int)yH);
                        }else {
                            combineImg = CalUtility.imageByCombiningImageEx(self,  headImage, (int)xH, (int)yH,
                                    iFrame.bitmap, (int)xF, (int)yF);
                        }

                        //  String writeStr = item.name + "_"+ String.valueOf(i);
//		                CalUtility.writeBitmap(combineImg, "CombineImages", writeStr );
                    }

                    Bitmap normalImage = Bitmap.createScaledBitmap(combineImg, iFrame.bitmap.getWidth(),
                            (int)iFrame.bitmap.getHeight(), true);

                    newFrames.add(normalImage);

                    if (stopflag())
                    {
                        threadLiveCount--;
                        item.setprocess(ItemProcessType.ItemNone);
                        return;
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                boolean bMakeGif = true;
                if (bMakeGif){

                    // make new gif with new frames.
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Gifs/");
                    if(!myDir.exists())
                        myDir.mkdirs();
                    String fname = item.name;

                    File file = new File(myDir, fname + ".gif");
                    String gifPath = myDir + "/" + fname + ".gif";

                    if (file.exists()){
                        file.delete();
                    }

                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        AnimatedGifMaker gifs = new AnimatedGifMaker();
                        gifs.start(out);

                        gifs.setRepeat(0);
                        gifs.setTransparent(new Color());

                        int nFrames = newFrames.size();

                        for (int k = 0; k < nFrames; k++) {
                            gifs.setDelay(gif.getDelay(k));
                            gifs.addFrame(newFrames.get(k));
                        }

                        newFrames.clear();
                        newFrames = null;

                        gifs.finish();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    GifAnimationDrawableEx newGif = null;
                    try {
                        newGif = new GifAnimationDrawableEx(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (newGif != null){
                        item.setgif(newGif);
                        item.gifPath = gifPath;
                        item.process = ItemProcessType.ItemComplete;
                    }

                    if (stopflag() == false){
                        handler.item = item;
                        handler.sendEmptyMessage(0);
                    }
                }
            }

            threadLiveCount--;
            Log.e("Thread:", "complete");
        }
    }

    private MyHandler handler = new MyHandler();

    @SuppressLint("HandlerLeak")
    public class MyHandler extends Handler
    {
        public ItemInfo item;

        public void handleMessage(Message msg) {


            if (item.tag != null){
                item.tag.image.setImageDrawable(item.gif());
            }

            if (item.tagView != null){
                item.tagView.invalidate();
                item.gif().setVisible(true, true);
            }

            if (item.tag.pBar != null)
                item.tag.pBar.setVisibility(View.GONE);


            System.gc();
            Log.e("Handler:", "complete");
        }
    }

    private ItemGroup getItemGroup(int groupIndex)
    {
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        ItemGroup itemGroup = globalVariable.getConfig(groupIndex);
        return itemGroup;
    }

    /*private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}*/

    @Override
    public void onBackPressed() {


        terminateThreads();

        handler = null;

        Log.e("Factory:", "complete1");

        ((GlobalClass) getApplicationContext()).clearConfigGifs();


        System.gc();

        // TODO Auto-generated method stub
        Intent nextScreen = new Intent(GifFactoryActivity.this, GifFaceMaskActivity.class);
        startActivity(nextScreen);
        finish();

        Log.e("Factory:", "complete2");
    }
}
