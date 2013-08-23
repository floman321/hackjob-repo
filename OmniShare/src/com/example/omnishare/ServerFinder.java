package com.example.omnishare;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.StrictMode;

public class ServerFinder implements Runnable
{
	
	private ArrayList<String> mHostList;
	private String hostIP;
	boolean isDone;
	
	public boolean isDone()
	{
		return isDone;
	}
	
	public ServerFinder()
	{
		isDone = false;
		mHostList = new ArrayList();
		System.out.println("ServerFinder Constructor");
	}
	
	public ArrayList<String> getOmniShareServers()
	{	
		return mHostList;
	}
	


	public ArrayList<String> findOmniShareServers(String IP) throws UnknownHostException, IOException
    {
		isDone = false;
		if (android.os.Build.VERSION.SDK_INT > 9) 
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
	    }
		
        String tempIP = IP.substring(0, IP.length() - 3); //myIP.getHostAddress().toString().substring(0, myIP.getHostAddress().toString().length() - 3);
        InetAddress tempAddr = null;
        Socket tempSocket = null;
        
        mHostList.add("TEST ENTRY");
        int index = 0; 
	    for(int i = 0; i < 256; i++)
		{
			try
			{		
				//System.out.println("TEMP IP " + tempIP + i);
				tempAddr = InetAddress.getByName(tempIP + i);		
				//tempAddr = InetAddress.getByName("192.168.1.101"); //TODO: THIS HAS TO BE CORRECTED TO DETERMINE DEVICE IP
				//System.out.println("Host Address Found " + tempAddr.getHostAddress());
				if(tempAddr.isReachable(200))
	            {
					System.out.println("IP " + i + " Was reachable");
	                tempSocket = new Socket(tempAddr, 5000);
	                System.out.println("Socket Createed");
	                if(tempSocket.isConnected())
	                {
	                    System.out.println("Server Found @ " + tempSocket.getInetAddress().getHostAddress());
	                    mHostList.add(tempAddr.getHostAddress());
	                    isDone = true;
	                    break;
	                }
	                tempSocket.close();
	                tempSocket = null;
	            }			
			}
			catch (IOException e)
			{
	                System.out.println (" was Connection Refused " + i);				
			}
			
		}    
	        System.out.println("ServerFinder return findOmniShareServers");        
	        return mHostList;
	    }
	
	public void setHostIP(String IP)
	{
		hostIP = IP;
	}

	@Override
	public void run()
	{
		try
		{
			findOmniShareServers(hostIP);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
