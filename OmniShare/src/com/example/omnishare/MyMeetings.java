package com.example.omnishare;

import java.util.ArrayList;
import java.util.HashMap;


//import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyMeetings extends ListActivity{
	
	DBController dbController = new DBController(this);
	TextView meetingId;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mymeetings);
        
        ArrayList<HashMap<String, String>> meetingList = dbController.getAllMeetings();
		if (meetingList.size() != 0)
		{
			ListView lv = getListView();
			
			lv.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id)
				{
					meetingId = (TextView) view.findViewById(R.id.meetingId);
					String valmeetingId = (meetingId.getText() != null ? meetingId.getText().toString() : "");
					Intent objIndent = new Intent(getApplicationContext(), MeetingListItemDetail.class);
					objIndent.putExtra("meetingId", valmeetingId);
					startActivity(objIndent);
				}
			});
			
			ListAdapter adapter = new SimpleAdapter(this, meetingList, R.layout.activity_listmeetingextracheese, new String[] {"meetingId", "meetingName" }, new int[] {R.id.meetingId, R.id.meetingName });
			lv.setAdapter(adapter);
		}
        
    }

    
    public void switch_createMeeting(View view){
    	Intent intent = new Intent(MyMeetings.this, CreateMeeting.class);
    	startActivity(intent);
    }
}
