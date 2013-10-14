package com.example.omnishare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class CreateMeeting extends Activity
{
	private static final String LOGCAT = null;
	
	EditText meetingName;
	EditText meetingLocation;
	EditText meetingDate;
	EditText meetingCode;
	
	DBController dbController = new DBController(this);
	
	ArrayList<String> fileList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_meeting);
		
		meetingName = (EditText) findViewById(R.id.edit_text_createmeetingname);
		meetingLocation = (EditText) findViewById(R.id.edit_text_createlocation);
		meetingDate = (EditText) findViewById(R.id.edit_text_createdateofmeeting);
		meetingCode = (EditText) findViewById(R.id.edit_text_createmeetingaccesscode);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_meeting, menu);
		return true;
	}
	
	
	public void createNewMeeting(View view)
	{
		Log.d(LOGCAT, "AddnewMeeting called");
		System.out.println("AddMeeting Called");
		HashMap<String, String> queryValues = new HashMap<String, String>();
		queryValues.put("meetingName", meetingName.getText().toString());
		queryValues.put("meetingLocation", meetingLocation.getText().toString());
		queryValues.put("meetingDate", meetingDate.getText().toString());
		queryValues.put("meetingCode", meetingCode.getText().toString());
		dbController.insertMeeting(queryValues);			
			
		//FOR ADDED FILESLIST
		if(fileList != null && !fileList.isEmpty())
		{
			Collections.sort(fileList);
			HashMap<String, String> query = dbController.getMeeting(meetingName.getText().toString());
			String queryid = query.get("meetingId");
			if(queryid != null)
			{
				System.out.println("Query ID " + queryid);
				for(int i = 0; i < fileList.size(); i++)
				{
					System.out.println("FILELIST ADD @" + fileList.get(i));
					File file = new File(fileList.get(i));
					HashMap<String, String> queryValuesFiles = new HashMap<String, String>();
					queryValuesFiles.put("fileName", file.getName().toString());
					queryValuesFiles.put("fileLocation", file.getAbsolutePath().toString());
					queryValuesFiles.put("fileMeetingRef", queryid.toString());
					System.out.println("Call to dbController " + file.getName());
		            dbController.insertMeetingFile(queryValuesFiles);
				}
			}
			else
			{
				System.out.println("INVALID QUERY ID");
			}
			
			
		}
		else
		{
			System.out.println("INVALID/EMPTY FILELIST");
		}
		
		
		
		//check vir finish hier insit
		finish();
		
		//this.callHomeActivity(view);
	}
	
	public void callHomeActivity(View view)
	{
		Intent objIntent = new Intent(getApplicationContext(),MyMeetings.class);
		startActivity(objIntent);
	}
	
	public void addFiles(View view)
	{			
		Intent intent = new Intent(this, AddFilesActivity.class);		
		startActivityForResult(intent, 1); 
		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      
		    		//fileList = data.getStringArrayListExtra("fileList");	
		    		
		    		ArrayList<String> tempFiles = data.getStringArrayListExtra("fileList");
		    		
		    		for(int i = 0; i < tempFiles.size(); i++)
		    		{
		    			if(!fileList.contains(tempFiles.get(i)))
		    			{
		    				fileList.add(tempFiles.get(i));
		    			}
		    		}
		    		
		    		
		    		ListView lv = (ListView)findViewById(R.id.lv_createmeetingfilelist);
		    		ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listnetwork_item, fileList);
				    lv.setAdapter(adapter);				 
		     }
		     if (resultCode == RESULT_CANCELED) {    		         
		     }
		  }
		}
	
}
