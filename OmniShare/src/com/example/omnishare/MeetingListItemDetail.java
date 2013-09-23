package com.example.omnishare;

//import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
//import android.widget.TextView;
import android.widget.TextView;

public class MeetingListItemDetail extends ListActivity
{
	private static final String LOGCAT = null;

	DBController dbController = new DBController(this);
	EditText meetingName;
	EditText meetingLocation;
	EditText meetingDate;
	EditText meetingAccessCode;
	
	ArrayList<String> fileList = new ArrayList<String>();
	
	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listitem);

		meetingName = (EditText) findViewById(R.id.edit_text_meetingname);
		meetingLocation = (EditText) findViewById(R.id.edit_text_location);
		meetingDate = (EditText) findViewById(R.id.edit_text_dateofmeeting);
		meetingAccessCode = (EditText) findViewById(R.id.edit_text_meetingaccesscode);

		Intent intent = getIntent();
		String queryid = intent.getStringExtra("meetingId");
		HashMap<String, String> meetingList = dbController
				.getMeetingInfo(queryid);

		if (meetingList.size() != 0)
		{
			meetingName.setText(meetingList.get("meetingName"));
			meetingLocation.setText(meetingList.get("meetingLocation"));
			meetingDate.setText(meetingList.get("meetingDate"));
			meetingAccessCode.setText(meetingList.get("meetingCode"));
		}

		
		final ArrayList<HashMap<String, String>> meetingFileList = dbController.getAllMeetingFiles(queryid);
		
		if (meetingList.size() != 0)
		{
			ListView lv = getListView();
			
			lv.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override ////FOR TEST ONLY
				public void onItemClick(AdapterView<?> parent, View view,int position, long id)
				{
					TextView fileName = (TextView) view.findViewById(R.id.fileName);
					String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
										
					 //TEST by B for pdf viewer activity
					String filePath = meetingFileList.get(position).get("fileLocation");
	                System.out.println("PDF VIEW TEST " + filePath);
	                
	                if(filePath.contains(".pdf"))
	                {
		                Intent pdfIntent = new Intent(getApplicationContext(), PdfhostviewActivity.class);
		                pdfIntent.putExtra(PdfhostviewActivity.EXTRA_PDFFILENAME, filePath);
		                startActivity(pdfIntent);
	                }
					
				}
			});
			
			ListAdapter adapter = new SimpleAdapter(this, meetingFileList, R.layout.activity_fileitemrepresentation, new String[] {"fileId", "fileName" }, new int[] {R.id.fileId, R.id.fileName });
			lv.setAdapter(adapter);
		}

	}

	public void updateCurrentMeeting(View view)
	{
		Log.d(LOGCAT, "UpdateMeeting called");
		System.out.println("UpdateMeeting Called");
		Intent intent = getIntent();
		String queryid = intent.getStringExtra("meetingId");
		HashMap<String, String> queryValues = new HashMap<String, String>();
		queryValues.put("meetingId", queryid.toString());
		queryValues.put("meetingName", meetingName.getText().toString());
		queryValues.put("meetingLocation", meetingLocation.getText().toString());
		queryValues.put("meetingDate", meetingDate.getText().toString());
		queryValues.put("meetingCode", meetingAccessCode.getText().toString());
		dbController.updateMeeting(queryValues);
		
	

		this.callHomeActivity(view);
	}
	
	public void removeCurrentMeeting(View view)
	{
		Log.d(LOGCAT, "RemoveMeeting called");
		System.out.println("RemoveMeeting Called");
		Intent intent = getIntent();
		String queryid = intent.getStringExtra("meetingId");
		dbController.deleteMeeting(queryid);

		this.callHomeActivity(view);
	}
	
	public void switchHostViewStarter(View view){
		
		final int id = view.getId();
		switch(id){
		case R.id.btn_startAsHost:
			//ADDED TO START ASYNC FILE TRANSFER BEFORE SWITCHING ACTIVITY
			Intent intent = getIntent();
			String queryid = intent.getStringExtra("meetingId");
			ArrayList<HashMap<String, String>> tempmeetingFileList = dbController.getAllMeetingFiles(queryid);
			ArrayList<String> actualFileNames = new ArrayList<String>();
			
			System.out.println("tempmeetingfileList.size " + tempmeetingFileList.size() + "query ID " + queryid);
			
			//to add the 3 column of the db to a list of absolute file paths
			for(int k = 0; k< tempmeetingFileList.size(); k++)
			{
				Collection<String> fileList = tempmeetingFileList.get(k).values();
				Iterator<String> i = fileList.iterator();
				int y = 0;
				while(i.hasNext())
				{
					String temp = i.next();
					if(y == 3) //3rd Attribute from the cursor.
					{
						actualFileNames.add(temp);
					}
					
					System.out.println(y++ +  " FILE IN FILE LIST BEFORE TRANSFER " + temp);
				}
			}
			//update details of session 			
			ServerInterface.setActive(true, getApplicationContext());
			ServerInterface.setAccessCode(meetingAccessCode.getText().toString(), getApplicationContext());
	
			new SendFilesTask().execute(actualFileNames);
			
	 		Intent myIntent = new Intent(MeetingListItemDetail.this, HostStartView.class);
			//myIntent.putExtra("key", value); //Optional parameters
	 		MeetingListItemDetail.this.startActivity(myIntent);
			break;
		}		
	}

	public void callHomeActivity(View view)
	{
		Intent objIntent = new Intent(getApplicationContext(), MyMeetings.class);
		startActivity(objIntent);
	}

	public void addFiles(View view)
	{
		Intent intent = getIntent();
		String queryid = intent.getStringExtra("meetingId");
		Intent resultIntent = new Intent(getApplicationContext(), AddFilesActivity.class);
		resultIntent.putExtra("meetingId", queryid);
		startActivityForResult(resultIntent, 1); 

	}
	
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			  System.out.println("REQ CODE " + requestCode + "  RES CODE " + resultCode);
			
			if (requestCode == 1) {
	
			     if(resultCode == RESULT_OK){      
			    		//fileList = data.getStringArrayListExtra("fileList");	
			    	 String queryid = data.getStringExtra("meetingId");	
			    	 System.out.println("meeting ID in callback " + queryid);
			    	 final ArrayList<HashMap<String, String>> meetingFileList = dbController.getAllMeetingFiles(queryid);
			 		
			    	 if (meetingFileList.size() != 0)
			 		{
			 			ListView lv = getListView();
			 			
			 			lv.setOnItemClickListener(new OnItemClickListener() 
			 			{
			 				@Override //FOR TEST ONLY
							public void onItemClick(AdapterView<?> parent, View view,int position, long id)
							{
								TextView fileName = (TextView) view.findViewById(R.id.fileName);
								String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
													
								 //TEST by B for pdf viewer activity
								String filePath = meetingFileList.get(position).get("fileLocation");
				                System.out.println("PDF VIEW TEST " + filePath);
				                
				                if(filePath.contains(".pdf"))
				                {
					                Intent pdfIntent = new Intent(getApplicationContext(), PdfhostviewActivity.class);
					                pdfIntent.putExtra(PdfhostviewActivity.EXTRA_PDFFILENAME, filePath);
					                startActivity(pdfIntent);
				                }
								
							}
			 			});
			 			
			 			ListAdapter adapter = new SimpleAdapter(this, meetingFileList, R.layout.activity_fileitemrepresentation, new String[] {"fileId", "fileName" }, new int[] {R.id.fileId, R.id.fileName });
			 			lv.setAdapter(adapter);
			 		}		 
			     }
			     if (resultCode == RESULT_CANCELED) {    		         
			     }
			  }
			}

	 private class SendFilesTask extends AsyncTask<ArrayList<String>, Integer, String> {

	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            System.out.println("onPreExecute() SendFilesTask");

	        }

	        @Override
	        protected void onProgressUpdate(Integer... values) {
	            super.onProgressUpdate(values);
	            System.out.println("Progress at " + values[0]);

	       //     ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarAddFiles);
	     //       pb.setProgress(values[0]);
	        }

	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	            System.out.println("Done sending files to server");
	      //      Intent intent = getIntent();
	    //        intent.putStringArrayListExtra("fileList", fileList);
	            finish();
	        }

	        @Override
	        protected String doInBackground(ArrayList<String>... inputs) {
	        	if(!inputs[0].isEmpty())
	        	{
		            float counter = 100 / inputs[0].size();
		            for (int i = 0; i < inputs[0].size(); i++) {
		                ServerInterface.sendFile(new File(inputs[0].get(i)), getApplicationContext());
		                publishProgress((int) ((i + 1) * counter));
		            }
	        	}else
	        	{
	        		System.out.println("DO IN BG FILELIST WAS EMPTY");
	        	}
	            return "Done sending " + inputs[0].size() +" files to server";
	        }
	    }
}
