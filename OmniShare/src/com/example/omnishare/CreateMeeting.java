package com.example.omnishare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class CreateMeeting extends Activity
{
	private static final String LOGCAT = null;
	
	EditText meetingName;
	EditText meetingLocation;
	EditText meetingDate;
	EditText meetingCode;
	
	DBController dbController = new DBController(this);
	
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
	
		this.callHomeActivity(view);
	}
	
	public void callHomeActivity(View view)
	{
		Intent objIntent = new Intent(getApplicationContext(),MyMeetings.class);
		startActivity(objIntent);
	}
	
	public void addFiles(View view)
	{			
		Intent objIndent = new Intent(getApplicationContext(), AddFilesActivity.class);		
		startActivityForResult(objIndent, RESULT_OK); //TODO Fix THIS
		
	}	
}
