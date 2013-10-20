package com.example.omnishare;


import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.os.Bundle;

public class PdfhostviewActivity extends PdfViewerActivity
{

	ChordMain chordmain;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		chordmain = new ChordMain(getApplicationContext());
		chordmain.startChord();
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy()
	{		
		super.onDestroy();
		
	}
	
	@Override
	public void sendPageNotification()
	{
		String payload = "" + getPage();
		System.out.println("CHORD sendPageNotification to all");
		chordmain.sendToAll(payload, 1);
	}
	
	@Override
	public void getPageNotification()
	{
		
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
