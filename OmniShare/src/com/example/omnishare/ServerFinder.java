package com.example.omnishare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	
	
	public ArrayList<String> findOmniShareServersUDP(String IP) throws IOException
	{
		//ArrayList<String> serverList = new ArrayList<String>();
		
	    MulticastSocket socket = new MulticastSocket(5001);
        String testString = "IS_OMNISHARE_HOST";
        byte[] buf = testString.getBytes();
        InetAddress group = InetAddress.getByName("230.0.0.1");
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 5001);
        socket.send(packet);
        socket.send(new DatagramPacket(buf, buf.length, group, 5001));
        socket.send(new DatagramPacket(buf, buf.length, group, 5001));
        
        
        
        //find response        
           
        socket.joinGroup(group);
        DatagramPacket responsePacket;

        // get a few response
        while(true)
        {
            byte[] responsebuf = new byte[256];
            responsePacket = new DatagramPacket(responsebuf, responsebuf.length);
            socket.receive(responsePacket);                
            String received = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Data RESPONSE via UDP: " + received + " from host @ " + responsePacket.getAddress().getHostAddress());
          
            if(received.equals("IS_OMNISHARE_HOST"))
            {
            	System.out.println("REPLY VIA UDP " + received + " from host @ " + responsePacket.getAddress().getHostAddress());
                
            	mHostList.add(responsePacket.getAddress().getHostAddress());
            	break; // temporary
            }
        }
        
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
