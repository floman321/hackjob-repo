/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package omnishareserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Skimmel
 */
public class OmniShareServer
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        System.out.println("Server Started");
        Thread t1 = new Thread(new Server());
	t1.start();
        
        //THIS PART WORKS FOR THE CLIENT SIDE FUNCTIONALITY, BUT NEED TO SEND FILENAME WITH
      /*  Socket socket = new Socket("127.0.0.1", 5000);   
        InputStream in = new FileInputStream("send.jpg");
        OutputStream out = socket.getOutputStream();
        copy(in, out);
        out.close();
        in.close();*/
      /*
        Socket socket = new Socket("127.0.0.1", 5000);  
  
        //Send file  
        File myFile = new File("send.jpg");  
        byte[] mybytearray = new byte[(int) myFile.length()];  
          
        FileInputStream fis = new FileInputStream(myFile);  
        BufferedInputStream bis = new BufferedInputStream(fis);  
               
        DataInputStream dis = new DataInputStream(bis);     
        dis.readFully(mybytearray, 0, mybytearray.length);  
          
        OutputStream os = socket.getOutputStream();  
          
        //Sending file name and file size to the server  
        DataOutputStream dos = new DataOutputStream(os);     
        dos.writeUTF(myFile.getName());     
        dos.writeLong(mybytearray.length);     
        dos.write(mybytearray, 0, mybytearray.length);     
        dos.flush();      
          
  
        socket.close();  
        fis.close();
        bis.close();
        * 
        * 
*/
        /*
        //SERVER CODE
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("TEST1");
        tempList.add("test2");
      
        Socket socket = new Socket("127.0.0.1", 5000);  
  
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);     
        dos.writeUTF("FILELIST_REQ");       
        ObjectOutputStream ous = new ObjectOutputStream(os);
        ous.writeObject(tempList);

        socket.close();
        ous.close();
        dos.close();
        
        
         //CLIENT CODE
                        InputStream in2 = socket.getInputStream();
                        ObjectInputStream ois = new ObjectInputStream(in2);
                        ArrayList<String> newList = (ArrayList<String>) ois.readObject();
                        
                        for(int i = 0; i < newList.size(); i++)
                        {
                            System.out.println("newList @ " + i + "   " + newList.get(i));
                        }
                        
                        */
     /*   ous.close();  */
        
    }
    
    static void copy(InputStream in, OutputStream out) throws IOException 
    {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) 
        {
            out.write(buf, 0, len);
        }
    }
    
  
	
	public static ArrayList<String> findOmniShareServers() throws UnknownHostException, IOException
        {
            ArrayList<String> hostList = new ArrayList<>();
            InetAddress myIP = InetAddress.getLocalHost();
            String tempIP = myIP.getHostAddress().toString().substring(0, myIP.getHostAddress().toString().length() - 3);
            InetAddress tempAddr = null;
            Socket tempSocket = null;
            for(int i = 100; i < 256; i++)
		{
			try
			{					
				tempAddr = InetAddress.getByName(tempIP + i);				
				System.out.println(tempAddr.getHostAddress());
			//	System.out.println(InetAddress.getByName(tempIP + i).isReachable(900) + " " + tempIP + i);
				if(tempAddr.isReachable(1100))
                                {
                                
                                    tempSocket = new Socket(tempIP+i, 5000);

                                    if(tempSocket.isConnected())
                                    {
                                        System.out.println("Server Found @ " + tempSocket.getInetAddress().getHostAddress());
                                        hostList.add(tempIP+i);
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
            return hostList;
        }
}
