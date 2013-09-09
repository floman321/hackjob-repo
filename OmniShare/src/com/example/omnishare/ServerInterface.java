package com.example.omnishare;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.example.omnishare.GuestJoinedNetwork.FileListFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StrictMode;

public class ServerInterface
{
	
	public static void syncFiles(Context context)
	{
		File directory = Environment.getExternalStorageDirectory();
		SharedPreferences settings = context.getSharedPreferences("OmniShareHostsFile", 0);
	    String hostAddress = settings.getString("Host", "NO_HOST_SET");
	   
	    
	    if (android.os.Build.VERSION.SDK_INT > 9) //TO ALLOW FOR PERMISSIONS TO OPEN NEW SOCKET ON ANDROID DEVICES
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
	    }	
	    
	    if(!hostAddress.equals("NO_HOST_SET"))
	    {
		    try
	        {
		    	Socket socket = new Socket(hostAddress, 5000);  
		    	OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.writeUTF("FILES_REQ");
		    	
		    	
				while(true)
		        {
				InputStream in = socket.getInputStream();
		        DataInputStream clientData = new DataInputStream(in);
		        String message ="";                
		        message = clientData.readUTF();
        
		      
		        	OutputStream output = null;
					if(message.contains("."))
					{
						System.out.println("Receiving file " + message + " from " + hostAddress + "...");
					
						output = new FileOutputStream(directory + "/" + message);
				        long size = clientData.readLong();
				        int bytesRead = 0;
				        byte[] buffer = new byte[524288];//byte[1024];
				        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
				        {
				            output.write(buffer, 0, bytesRead);
				            size -= bytesRead;
				        }
				        // Closing the FileOutputStream handle  
				        
				        System.out.println("Received file " + message);
					}
					else
					{
						output.close();
				        in.close();
				        clientData.close();
						break;
					}
		        }
				
				
				
	        }catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	    }
	
	
	}
	
	
	public static void sendFile(File file, Context context)
	{
		SharedPreferences settings = context.getSharedPreferences("OmniShareHostsFile", 0);
	    String hostAddress = settings.getString("Host", "NO_HOST_SET");
	    System.out.println("Attempt to send file " + file.getAbsolutePath() + " to " + hostAddress);
	    
	    if (android.os.Build.VERSION.SDK_INT > 9) //TO ALLOW FOR PERMISSIONS TO OPEN NEW SOCKET ON ANDROID DEVICES
		{
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
	    }	
	    
	    if(!hostAddress.equals("NO_HOST_SET"))
	    {
		    try
	        {
		    Socket socket = new Socket(hostAddress, 5000);  
		                 
	        byte[] mybytearray = new byte[(int) file.length()];  
	          
	        FileInputStream fis = new FileInputStream(file);  
	        BufferedInputStream bis = new BufferedInputStream(fis);  
	               
	        DataInputStream dis = new DataInputStream(bis);     
	        dis.readFully(mybytearray, 0, mybytearray.length);  
	          
	        OutputStream os = socket.getOutputStream();  
	          
	        DataOutputStream dos = new DataOutputStream(os);          
	       
	        dos.writeUTF(file.getName());     
	        dos.writeLong(mybytearray.length);     
	        dos.write(mybytearray, 0, mybytearray.length);     
	        dos.flush();      
	        
	        socket.close();  	        
	        fis.close();
	        bis.close();
	        dos.close();
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		    
		    System.out.println("Transfer done...");
	    }
	}
	
	public static ArrayList<String> getCurrentFileList(Context context) // to be called by client devices to get current meeting files, and sync if necessary
	{
		ArrayList<String> retval = null;
		if (android.os.Build.VERSION.SDK_INT > 9) // TO ALLOW FOR PERMISSIONS TO OPEN NEW SOCKET ON ANDROID DEVICES
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		SharedPreferences settings = context.getSharedPreferences("OmniShareHostsFile", 0);
		String hostAddress = settings.getString("Host", "NO_HOST_SET");

		if(!hostAddress.equals("NO_HOST_SET"))
		{
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
		}

		return retval;
	}	
	
	
	
	
}
