package com.example.omnishare;

import android.os.Bundle;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

public class GuestDisplayImageActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest_display_image);
		
		ImageView image = (ImageView)findViewById(R.id.imageView_guest_displayimage);
		String fileName = getIntent().getStringExtra("filePath");
		Bitmap fullSizeImage = new BitmapFactory().decodeFile(fileName);		
		image.setImageBitmap(fullSizeImage);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guest_display_image, menu);
		return true;
	}	
}
