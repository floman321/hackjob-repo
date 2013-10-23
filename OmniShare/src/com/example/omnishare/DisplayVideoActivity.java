package com.example.omnishare;

import net.sf.andpdf.pdfviewer.AllShareService;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

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
		//String fileName = getIntent().getStringExtra("filePath");	
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
		Button play = (Button) findViewById(R.id.btnPlay);
		if(wasPlaying)
		{
			String fileName = getIntent().getStringExtra("filePath");	
			mAllShareService.startVideo(fileName);				
			play.setText("Pause");
		}
		else
		{
			play.setText("Play");
			mAllShareService.pauseVideo();	
		}		
		wasPlaying = !wasPlaying;		
	}
		
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mAllShareService.stop();
		super.onDestroy();
	}
}
