package com.example.omnishare;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
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
			ArrayList<String> serverList;
			
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
		   }
		 
		   @Override
		   protected ArrayList<String> doInBackground(String... params) 
		   {
		      String IP=params[0];
		      serverList = new ArrayList<String>();
		      if (android.os.Build.VERSION.SDK_INT > 9) 
				{
				      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				      StrictMode.setThreadPolicy(policy);
			    }
				
		        String tempIP = IP.substring(0, IP.length() - 3); //myIP.getHostAddress().toString().substring(0, myIP.getHostAddress().toString().length() - 3);
		        InetAddress tempAddr = null;
		        Socket tempSocket = null;		        
		        
			    for(int i = 0; i < 256; i++)
				{
			    
			    	
			    	publishProgress(i/2);
					try
					{		
						tempAddr = InetAddress.getByName(tempIP + i);		
						if(tempAddr.isReachable(200))
			            {
							System.out.println("IP " + i + " Was reachable");
			                tempSocket = new Socket(tempAddr, 5000);
			                System.out.println("Socket Created");
			                if(tempSocket.isConnected())
			                {
			                    System.out.println("Server Found @ " + tempSocket.getInetAddress().getHostAddress());
			                    serverList.add(tempAddr.getHostAddress());	
			                    tempSocket.close();
				                tempSocket = null;
			                    publishProgress(100);
			                    break; //early out once OmniShare Server has been found
			                }
			                tempSocket.close();
			                tempSocket = null;
			            }			
					}
					catch (IOException e)
					{
			                System.out.println ("Connection Refused @ " + i);				
					}					
				}    
		        System.out.println("ServerFinder return findOmniShareServers");        
		        return serverList;
		   }
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) 
		   {
		      super.onProgressUpdate(values);
		      ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar_findNetworks);
		      pb.setProgress(values[0]);
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
