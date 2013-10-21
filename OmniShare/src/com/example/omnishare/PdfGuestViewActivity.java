package com.example.omnishare;

import net.sf.andpdf.pdfviewer.PdfGuestViewerActivity;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

public class PdfGuestViewActivity extends PdfGuestViewerActivity
{
	ChordMain chordmain;
	MessageReceiver broadcastReceiver;
	
	private class MessageReceiver extends BroadcastReceiver 
	{		
		@Override
		   public void onReceive(Context context, Intent intent) 
		   {    
				System.out.println("BroadcastReceiver on receive");			
				int newPage = Integer.parseInt(intent.getStringExtra("pageNumber"));
				if(newPage != getPage())
				{
					setPage(newPage);
				}
		   }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);		
		chordmain = new ChordMain(getApplicationContext());
		chordmain.startChord();
		broadcastReceiver = new MessageReceiver();		
		registerReceiver(broadcastReceiver, new IntentFilter("com.example.omnishare.PDFUPDATE_MESSAGE"));	
		
		//request current page from host
		String message = "Page_Request";	
		chordmain.sendToAll(message, 3);
		
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		String message = "Page_Request";
		chordmain.sendToAll(message, 3);
		
		registerReceiver(broadcastReceiver, new IntentFilter("com.example.omnishare.FILESUGGEST_MESSAGE"));
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();		
		chordmain.stopChord();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pdf_guest_view, menu);
		return true;
	}

	
	 @Override
	public int getPreviousPageImageResource() { return R.drawable.left_arrow; }
     @Override
	public int getNextPageImageResource() { return R.drawable.right_arrow; }
     @Override
	public int getZoomInImageResource() { return R.drawable.zoom_in; }
     @Override
	public int getZoomOutImageResource() { return R.drawable.zoom_out; }
     @Override
	public int getPdfPasswordLayoutResource() { return R.layout.pdf_file_password; }
     @Override
	public int getPdfPageNumberResource() { return R.layout.dialog_pagenumber; }
     @Override
	public int getPdfPasswordEditField() { return R.id.etPassword; }
     @Override
	public int getPdfPasswordOkButton() { return R.id.btOK; }
     @Override
	public int getPdfPasswordExitButton() { return R.id.btExit; }
     @Override
	public int getPdfPageNumberEditField() { return R.id.pagenum_edit; }
}
