package com.example.omnishare;

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
		String fileName = getIntent().getStringExtra("fileName");
		Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileName), 190, 180);
		
		image.setImageBitmap(ThumbImage);
		
		getMenuInflater().inflate(R.menu.display_image, menu);
		return true;
	}

}
