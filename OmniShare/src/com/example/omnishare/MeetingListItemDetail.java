package com.example.omnishare;

//import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
//import android.widget.TextView;

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

	  /*  Intent intent = getIntent();
	     int position = intent.getIntExtra("position", 0);

	    // Here we turn your string.xml in an array
	    
	   
	   String[] myKeys = getResources().getStringArray(R.array.strarr_meetings);
	    
	    TextView myTextView = (TextView) findViewById(R.id.meetingId);
	    myTextView.setText(myKeys[position]);*/
	    //NB this needs to change to a db read.
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
			
		//Add to strarr_meetings
		/*String[] array = getResources().getStringArray(R.array.strarr_meetings);
	    System.out.println("--array.length--"+array.length);
	    List<String> list = new ArrayList<String>();
	    list = Arrays.asList(array);
	    ArrayList<String> arrayList = new ArrayList<String>(list);
	    arrayList.add(meetingName.getText().toString());
	    array = arrayList.toArray(new String[list.size()]);
	    System.out.println("--array.length--"+array.length);*/
		
		
		this.callHomeActivity(view);
	}
	
	public void callHomeActivity(View view)
	{
		Intent objIntent = new Intent(getApplicationContext(),MyMeetings.class);
		startActivity(objIntent);
	}
}
