package com.example.omnishare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScanNetworks extends Activity{
	
	public String myIP = "";
	
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
        myIP = Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
       System.out.println("GUEST NETWORK ADDRESS FROM WIM " + myIP);       
       
       new FindServerTask().execute(myIP);	       
	}
	
	public void switchme(View view)
	{
		final int id = view.getId();
		switch(id){
		case R.id.btn_joinnetwork:
			//Intent validation = new Intent(ScanNetworks.this, AccessCodeActivity.class);			
			//startActivityForResult(validation, 1);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScanNetworks.this);	 
				// set title
				alertDialogBuilder.setTitle("Please Enter AccessCode");
				final EditText input = new EditText(getApplicationContext());
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				
				// set dialog message
				alertDialogBuilder.setView(input);			
				alertDialogBuilder								
					.setCancelable(false)
					.setPositiveButton("Enter",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id)
						{
							
							String accessCode = input.getText().toString();
							System.out.println("IN Alert Dialog VALIDATE BTN " + accessCode);
							if(ServerInterface.authUser(accessCode, getApplicationContext()))
							{
								 Toast toast = Toast.makeText(getApplicationContext(), "Valid Access Code, please wait...", Toast.LENGTH_LONG);
						    	 toast.show();
								 Intent intent = new Intent(getApplicationContext(), GuestJoinedNetwork.class);								
								 ScanNetworks.this.startActivity(intent);
							}
							else
							{
								 Toast toast = Toast.makeText(getApplicationContext(), "Invalid Access Code, please try again...", Toast.LENGTH_LONG);
						    	 toast.show();
							}
						}
					  });
			
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
			
			break;
		}	
	}
	
	public void refresh(View v)
	{		
		 new FindServerTask().execute(myIP);			
	}
	
	   private class FindServerTask extends AsyncTask<String, Integer, HashMap<String,String>> 
	   {
			HashMap<String,String> serverList;
			
			
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
		      Button refreshBtn = (Button) findViewById(R.id.btn_guest_refresh);
		      refreshBtn.setEnabled(false);
		   }
		 
		   @Override
		   protected HashMap<String,String> doInBackground(String... params) 
		   {
		      String IP=params[0];
		      serverList = new HashMap<String,String>();
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
		                	String key = ServerInterface.getMeetingName(value, getApplicationContext());
		                	if(key.length() > 0)
		                	{
		                		serverList.put(key, value); //k = name, v = ip
		                	}
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
		protected void onPostExecute(HashMap<String,String> result)
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
						//String hostAddress = (String) newHost.getText();
						String hostAddress = serverList.get((String) newHost.getText());
						System.out.println("Host " + hostAddress + " send to sharedPrefs File");
					    SharedPreferences settings = getSharedPreferences("OmniShareHostsFile", 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString("Host", hostAddress);						
				        editor.commit();
				        Toast toast = Toast.makeText(getApplicationContext(), hostAddress+ " set as Host...", Toast.LENGTH_LONG);
				    	toast.show();
					}
				});
		      System.out.println("onPostExecute ServerFinder " + serverList.size());
		      ArrayList<String> serverNameList = new ArrayList<String>();

		      for(String key: serverList.keySet())
		      {
		    	  serverNameList.add(key);
		      }
		     
		      ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_listnetwork_item, serverNameList);
		      lv.setAdapter(adapter);
		      if(serverNameList.isEmpty())
              {
                Toast toast = Toast.makeText(getApplicationContext(), "No active servers found.", Toast.LENGTH_LONG);
                toast.show();
              }
		      
		      Button refreshBtn = (Button) findViewById(R.id.btn_guest_refresh);
		      refreshBtn.setEnabled(true);
		   }
	   }
	
}
