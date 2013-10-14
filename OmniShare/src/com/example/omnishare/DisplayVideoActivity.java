package com.example.omnishare;

import com.sec.android.allshare.media.ContentInfo;

import net.sf.andpdf.pdfviewer.AllShareService;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class DisplayVideoActivity extends Activity
{
	AllShareService mAllShareService;
	private boolean wasPlaying = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_video);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.display_video, menu);
		String fileName = getIntent().getStringExtra("filePath");	
		mAllShareService = new AllShareService(getApplicationContext());   
        if(mAllShareService == null)
        {
        	System.out.println("mAllShareService == null");        	
        }             
        mAllShareService.init(getApplicationContext());      		
		return true;
	}
	
	public void play(View view)
	{	
		if(wasPlaying)
		{
			mAllShareService.resumeVideo();
		}
		else
		{
			wasPlaying = true;
			String fileName = getIntent().getStringExtra("filePath");	
			mAllShareService.startVideo(fileName);	
		}
		
	}
	
	public void pause(View view)
	{		
		String fileName = getIntent().getStringExtra("filePath");	
		mAllShareService.pauseVideo();	
		
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mAllShareService.stop();
		super.onDestroy();
	}

}
