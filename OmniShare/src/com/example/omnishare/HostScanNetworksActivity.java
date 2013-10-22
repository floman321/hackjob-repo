package com.example.omnishare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;


import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HostScanNetworksActivity extends Activity
{
	
	String myIP = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_scan_networks);		
        //NEW CODE FOR FINDING SERVER
        WifiManager wim= (WifiManager) getSystemService(WIFI_SERVICE);   //CHECK HIERDIE DING VIR DALK ALLES DOEN RONDOM NETWORK SOEK   
	    myIP = Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());	  
	    new FindServerTask().execute(myIP);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_scan_networks, menu);
		return true;
	}

	public void refresh(View v)
	{		
		 new FindServerTask().execute(myIP);			
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
			
		        //Find Servers using UDP BroadCast packets.
		        try
				{		       
		    		MulticastSocket socket = new MulticastSocket(5001);
		            String requestString = "IS_OMNISHARE_HOST";
		            byte[] buf = requestString.getBytes();
		            InetAddress group = InetAddress.getByName("230.0.0.1");
		        	socket.joinGroup(group);
		        	int retry = 0;
		        	
		    		while(retry < 1000)//timeout if server not found.
		            {		    		
			            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 5001);
			            socket.send(packet);	            
			            //listen for responses   
			            publishProgress(50);	
		                byte[] responsebuf = new byte[256];
		                DatagramPacket responsePacket = new DatagramPacket(responsebuf, responsebuf.length);
		                socket.receive(responsePacket);                
		                String responseString = new String(responsePacket.getData(), 0, responsePacket.getLength());		             
		                if(responseString.equals("OMNISHARE_TRUE"))
		                {
		                	
		                	String value = responsePacket.getAddress().getHostAddress();		                	
		                	serverList.add(value); 
		                	socket.leaveGroup(group);
		                	socket.close();
		                	publishProgress(100);
		                	break; 
		                }else
		                {
		                	retry++;
		                	continue;
		                }
		            }
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.out.println("ERROR WITH UDP SERVERLIST");
				}		       
		        publishProgress(100);	
		        System.out.println("ServerFinder return findOmniShareServers");        
		        return serverList;
		   }
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) 
		   {
		      super.onProgressUpdate(values);
		      ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar_host_findNetworks);
		      pb.setProgress(values[0]);
		   }
		 
		   @Override
		protected void onPostExecute(ArrayList<String> result)
		   {
		      super.onPostExecute(result);
		      System.out.println("onPostExecute");
		      ListView lv = (ListView) findViewById(R.id.lv_host_networkslist);
		      
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
				        Toast toast = Toast.makeText(getApplicationContext(), hostAddress+ " set as Host...", Toast.LENGTH_LONG);
				    	toast.show();
				    	finish();
					}
				});
		      System.out.println("onPostExecute ServerFinder " + serverList.size());

		     
		      ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listnetwork_item, serverList);
		      lv.setAdapter(adapter);
		   }
	   }
}
