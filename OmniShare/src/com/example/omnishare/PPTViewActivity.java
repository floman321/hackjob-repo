package com.example.omnishare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class PPTViewActivity extends Activity
{

	int i = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pptview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pptview, menu);
		return true;
	}
	
	
	public void foward(View view) throws Exception
	{
			System.out.println("IN FORWARD");
			//String filePath = getIntent().getStringExtra("filePath");
			
			
			
			
			
		/*	FileInputStream is = new FileInputStream(filePath);
		    SlideShow ppt = new SlideShow(is);
		    is.close();
		    
		    File directory = Environment.getExternalStorageDirectory();        	
	    	String filename = "tmp_AllShare_ppt";
		    FileOutputStream out = new FileOutputStream(filename + i + ".png");
		 
		    Slide[] slide = ppt.getSlides();
		    slide[i].getSlideRecord().getPPDrawing().writeOut(out);
	       out.close();
	       
	        // save the output
	        
	      
	        
	       
	    
	        
	        ImageView iv = (ImageView)findViewById(R.id.imageview_ppt);
	        //Convert bitmap to byte array
	        Bitmap bi = BitmapFactory.decodeFile(filename+ i + ".png");
	        iv.setImageBitmap(bi);
	        i++;*/
		    
	}

}
