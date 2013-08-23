package com.example.omnishare;

//import java.util.ArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
//import android.widget.TextView;
import android.widget.TextView;

public class MeetingListItemDetail extends Activity
{
	private static final String LOGCAT = null;
	
	DBController dbController = new DBController(this);
	EditText meetingName;
	EditText meetingLocation;
	EditText meetingDate;
	EditText meetingAccessCode;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_listitem);

	    meetingName =  (EditText) findViewById(R.id.edit_text_meetingname);
	    meetingLocation =  (EditText) findViewById(R.id.edit_text_location);
	    meetingDate =  (EditText) findViewById(R.id.edit_text_dateofmeeting);
	    meetingAccessCode =  (EditText) findViewById(R.id.edit_text_meetingaccesscode);
	    
	    Intent intent = getIntent();
	    String queryid = intent.getStringExtra("meetingId");
	    HashMap<String, String> meetingList = dbController.getMeetingInfo(queryid);
	    
	    if (meetingList.size() != 0)
		{
	    	meetingName.setText(meetingList.get("meetingName"));
	    	meetingLocation.setText(meetingList.get("meetingLocation"));
	    	meetingDate.setText(meetingList.get("meetingDate"));
	    	meetingAccessCode.setText(meetingList.get("meetingCode"));	    	
		}
	   
	    ArrayList<String> fileList =  intent.getStringArrayListExtra("fileList");
	  //  System.out.println("FileList Size " + fileList.size());
	    if(fileList != null && fileList.size() != 0)
	    {
	    	 ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_fileitem, fileList);	
	    	 ListView lv = (ListView)findViewById(R.id.lv_meetingfilelist);
		     lv.setAdapter(arrayAdapter); 
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
