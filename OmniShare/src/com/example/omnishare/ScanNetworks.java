package com.example.omnishare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
	
	public void switchme(View view)
	{
		final int id = view.getId();
		switch(id){
		case R.id.btn_joinnetwork:
			Intent validation = new Intent(ScanNetworks.this, AccessCodeActivity.class);			
			startActivityForResult(validation, 1);
			break;
		}	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  System.out.println("REQ CODE " + requestCode + "  RES CODE " + resultCode);
		
		if (requestCode == 1) 
		{
		     if(resultCode == RESULT_OK)
		     {
		    	 Toast toast = Toast.makeText(getApplicationContext(), "Valid Access Code, please wait...", Toast.LENGTH_LONG);
		    	 toast.show();
		    	 Intent myIntent = new Intent(ScanNetworks.this, GuestJoinedNetwork.class);			
				 ScanNetworks.this.startActivity(myIntent);
		     }
		     else
		     {
		    	 Toast toast = Toast.makeText(getApplicationContext(), "Invalid Access Code, please try again...", Toast.LENGTH_LONG);
		    	 toast.show();
		     }
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
	
		        String newIP = "";
		        int dotCount = 0;
		        for(int i = 0; i < IP.length(); i++)
		        {
		        	if(dotCount < 3)
		        	{
		        		newIP += IP.charAt(i);
		        	}
		        	if(IP.charAt(i) == '.')
		        	{
		        		dotCount++;
		        	}
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
		                	serverList.add(responsePacket.getAddress().getHostAddress());
		                	socket.leaveGroup(group);
		                	socket.close();
		                	publishProgress(100);
		                	break; // temporary, Needs to change to find more than one server
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
		      ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar_findNetworks);
		      pb.setProgress(values[0]);
		   }
		 
		   @Override
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
		      System.out.println("onPostExecute ServerFinder " + serverList.size());
		      ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listnetwork_item, serverList);
		      lv.setAdapter(adapter);
		   }
	   }
	
}
