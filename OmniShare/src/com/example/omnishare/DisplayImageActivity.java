package com.example.omnishare;

import net.sf.andpdf.pdfviewer.AllShareService;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageView;
//TODO
public class DisplayImageActivity extends Activity
{
	AllShareService mAllShareService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		
		ImageView image = (ImageView)findViewById(R.id.imageView_displayimage);
		String fileName = getIntent().getStringExtra("filePath");
		Bitmap fullSizeImage = new BitmapFactory().decodeFile(fileName);
		//Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileName),180,190);
		
		image.setImageBitmap(fullSizeImage);
		
		getMenuInflater().inflate(R.menu.display_image, menu);
		
		displayOnScreen(fileName);
		
		
		
		return true;
	}
	
	private void displayOnScreen(String fileName)
	{
		mAllShareService = new AllShareService(getApplicationContext());   

        if(mAllShareService == null)
        {
        	System.out.println("mAllShareService == null");        	
        }     
        
        mAllShareService.init(getApplicationContext());   
        mAllShareService.start(fileName);
        
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mAllShareService.stop();
		super.onDestroy();
	}

}
