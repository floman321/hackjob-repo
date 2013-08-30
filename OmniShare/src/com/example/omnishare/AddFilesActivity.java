package com.example.omnishare;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddFilesActivity extends ListActivity
{
	
	//for file list 
	 private ArrayList<String> item = null;
	 private ArrayList<String> path = null;
	 private String root;
	 private TextView myPath;
	 ArrayList<String> fileList = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_files);
		
	    //for file List
	    
	    myPath = (TextView)findViewById(R.id.path);        
        root = Environment.getExternalStorageDirectory().getPath();        
        getDir(root);
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_files, menu);
		return true;
	}
	
	
	private void getDir(String dirPath)
	{
		myPath.setText("Location: " + dirPath);
	     item = new ArrayList<String>();
	     path = new ArrayList<String>();
	     File f = new File(dirPath);
	     File[] files = f.listFiles();
	     
	     if(!dirPath.equals(root))
	     {
	      item.add(root);
	      path.add(root);
	      item.add("../");
	      path.add(f.getParent()); 
	     }
	     
	     for(int i=0; i < files.length; i++)
	     {
	      File file = files[i];
	      
	      if(!file.isHidden() && file.canRead())
	      {
	       path.add(file.getPath());
	          if(file.isDirectory())
	          {
	           item.add(file.getName() + "/");
	          }else
	          {
	           item.add(file.getName());
	          }
	      } 
	     }

	     
	     ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.activity_fileitem, item);	
	     setListAdapter(fileList); 
	}
	
	@Override
	 protected void onListItemClick(ListView l, View v, int position, long id)
	{
	  File file = new File(path.get(position));
	  
	  if (file.isDirectory())
	  {
	   if(file.canRead())
	   {
	    getDir(path.get(position));
	   }else
	   {
	    new AlertDialog.Builder(this)
	     .setIcon(R.drawable.ic_launcher)
	     .setTitle("[" + file.getName() + "] folder can't be read!")
	     .setPositiveButton("OK", null).show(); 
	   } 
	  }else
	  {
	   new AlertDialog.Builder(this)
	     .setIcon(R.drawable.ic_launcher)
	     .setTitle("[" + file.getName() + "] was added to the meeting.")
	     .setPositiveButton("OK", null).show();
	   
	   if(!fileList.contains(file.getAbsolutePath()));
	   {
		   fileList.add(file.getAbsolutePath());
		   System.out.println("File " + file.getAbsolutePath() + "  added to fileList");
	   }
	 //  sendFile(file); THIS WORKS
	   
      }
	 }
		
	public void sendFiles(View view)
	{			
		 new SendFilesTask().execute(fileList);			
	}	
	
	   private class SendFilesTask extends AsyncTask<ArrayList<String>, Integer, String> 
	   {
			
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
		      System.out.println("onPreExecute() SendFilesTask");

		   }
	
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) 
		   {
		      super.onProgressUpdate(values);
		      System.out.println("Progress at " + values[0]);
		      
		      ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarAddFiles);
		      pb.setProgress(values[0]);
		   }
		 
		   protected void onPostExecute(String result)
		   {
			   super.onPostExecute(result);
			   System.out.println("Done sending files to server");
			   Intent intent = getIntent();
			   intent.putStringArrayListExtra("fileList", fileList);
     		   finish();
		   }

		@Override
		protected String doInBackground(ArrayList<String>... inputs)
		{
			float counter = 100/inputs[0].size();
			for(int i = 0; i < inputs[0].size(); i++)
			{
				sendFile(new File(inputs[0].get(i)));
				publishProgress((int)((i+1)*counter));
			}			
			
			return "Done sending files to server";			
		}
		
		
	   }
	
	
	protected void sendFile(File file)
	{
		SharedPreferences settings = getSharedPreferences("OmniShareHostsFile", 0);
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

}
