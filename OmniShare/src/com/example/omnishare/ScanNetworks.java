package com.example.omnishare;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ScanNetworks extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_availablenetworks);
      
        //NEW CODE FOR FINDING SERVER
        WifiManager wim= (WifiManager) getSystemService(WIFI_SERVICE);   //CHECK HIERDIE DING VIR DALK ALLES DOEN RONDOM NETWORK SOEK   
       
        //TODO: Prompt user for permission, check that it waits for a connection to be established
        if(!wim.isWifiEnabled())
        {
        	System.out.println("Enabling WIFI...");        	
        	wim.setWifiEnabled(true);        
        }

       String IP = Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
       System.out.println("NETWORK ADDRESS FROM WIM " + IP);
       
       
       new FindServerTask().execute(IP);	
	}
	
	public void switchme(View view){
		final int id = view.getId();
		switch(id){
		case R.id.btn_joinnetwork:
	 		Intent myIntent = new Intent(ScanNetworks.this, GuestJoinedNetwork.class);
			//myIntent.putExtra("key", value); //Optional parameters
			ScanNetworks.this.startActivity(myIntent);
			break;
		}
		
	}


	
	   private class FindServerTask extends AsyncTask<String, Integer, ArrayList<String>> 
	   {
			ServerFinder serverfinder;
			ArrayList<String> serverList;
			
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
		      System.out.println("onPreExecute()");

		   }
		 
		   @Override
		   protected ArrayList<String> doInBackground(String... params) 
		   {
		      String IP=params[0];
		 
		      serverfinder = new ServerFinder();
		      try
				{
					serverList = serverfinder.findOmniShareServers(IP);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			
				      
				  for (int i = 0; i <= 100; i += 5) 
				  {		        
				     publishProgress(i);
				  }
				  return serverList;
		   }
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) 
		   {
		      super.onProgressUpdate(values);
		      System.out.println("Progress at " + values[0]);
		   }
		 
		   protected void onPostExecute(ArrayList<String> result)
		   {
		      super.onPostExecute(result);
		      System.out.println("onPostExecute");
		      ListView lv = (ListView) findViewById(R.id.lv_networkslist);
		      
		      lv.setOnItemClickListener(new OnItemClickListener() 
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id)
					{
					/*	meetingId = (TextView) view.findViewById(R.id.meetingId);
						String valmeetingId = (meetingId.getText() != null ? meetingId.getText().toString() : "");
						Intent objIndent = new Intent(getApplicationContext(), MeetingListItemDetail.class);
						objIndent.putExtra("meetingId", valmeetingId);
						startActivity(objIndent);*/
						
						TextView newHost = (TextView) view.findViewById(R.id.networkhost);
						String hostAddress = (String) newHost.getText();
						System.out.println("Host " + hostAddress + " send to sharedPrefs File");
					    SharedPreferences settings = getSharedPreferences("OmniShareHostsFile", 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString("Host", hostAddress);						
				        editor.commit();


						
					}
				});
		      
		      ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listnetwork_item, serverList);
		      lv.setAdapter(adapter);
		   }
	   }
	
}
