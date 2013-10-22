package com.example.omnishare;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

public class CreateMeeting extends Activity
{
	private static final String LOGCAT = null;
	String y, m, d;
	
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
		
		//to get current date, formatted.
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = df.format(c.getTime());
		meetingDate.setText(formattedDate);
		
		meetingDate.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					DialogFragment newFragment = new DatePickerFragment();
				    newFragment.show(getFragmentManager(), "datePicker");
				}
			}
		});
		
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
		    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listnetwork_item, fileList);
				    lv.setAdapter(adapter);				 
		     }
		     if (resultCode == RESULT_CANCELED) {    		         
		     }
		  }
		}
	
	@SuppressLint("ValidFragment")
	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the current date as the default date in the picker
	        final Calendar c = Calendar.getInstance();
	        int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);

	        // Create a new instance of DatePickerDialog and return it
	        return new DatePickerDialog(getActivity(), this, year, month, day);
	    }

	    public void onDateSet(DatePicker view, int year, int month, int day) {
	        y = Integer.toString(year);
	        m = (month+1 < 10 ? ("0" + Integer.toString(month+1)) : Integer.toString(month+1));
	        d = (day < 10 ? ("0" + Integer.toString(day)) : Integer.toString(day));
	        meetingDate.setText(d + "-" + m + "-" + y);
	    }
	}
	
}
