package com.example.omnishare;

//import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
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

		//TODO THIS HAS TO BE REPLACED WITH DB FUNCTIONALITY
		
	/*	ArrayList<String> fileList = getCurrentFileList();
		if (fileList != null)
			System.out.println("FileList Size " + fileList.size());

		if (fileList != null && fileList.size() != 0)
		{
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
					R.layout.activity_fileitem, fileList);
			ListView lv = (ListView) findViewById(R.id.lv_meetingfilelist);
			lv.setAdapter(arrayAdapter);
		}*/
		
		ArrayList<HashMap<String, String>> meetingFileList = dbController.getAllMeetingFiles();
		if (meetingList.size() != 0)
		{
			ListView lv = getListView();
			
			/*lv.setOnItemClickListener(new OnItemClickListener() 
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
			});*/
			
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
		Intent objIndent = new Intent(getApplicationContext(), AddFilesActivity.class);
		objIndent.putExtra("meetingId", queryid);
		startActivityForResult(objIndent, RESULT_OK); // TODO Fix THIS

	}

	public ArrayList<String> getCurrentFileList() // to be called by client
													// devices to get current
													// meeting files, and sync
													// in necessary
	{
		ArrayList<String> retval = null;
		if (android.os.Build.VERSION.SDK_INT > 9) // TO ALLOW FOR PERMISSIONS TO
													// OPEN NEW SOCKET ON
													// ANDROID DEVICES
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		SharedPreferences settings = getSharedPreferences("OmniShareHostsFile",0);
		String hostAddress = settings.getString("Host", "NO_HOST_SET");

		try
		{
			Socket socket = new Socket(hostAddress, 5000);
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF("FILELIST_REQ");

			InputStream in2 = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(in2);
			retval = (ArrayList<String>) ois.readObject();

			ois.close();
			dos.close();
			socket.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return retval;
	}

}
