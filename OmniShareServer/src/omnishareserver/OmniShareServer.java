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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
    {
        System.out.println("Server Started");
        Thread t1 = new Thread(new Server());
	t1.start();
       
        /*
        MulticastSocket mcs = new MulticastSocket(5001);
        String isOmnishare = "IS_OMNISHARE_HOST";
        byte[] buffer = isOmnishare.getBytes();
        
        InetAddress group = InetAddress.getByName("192.168.0.1");
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 5001);
        mcs.send(packet);
        */
        
        //UDP TEST START FOR SERVER FINDING
        
        System.out.println("UDP Beacon started");
        UDPBeacon beacon = new UDPBeacon();
        Thread t2 = new Thread(beacon);
        t2.start();
        
        /*
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
          //  break;
        }
            
            */
        
        /*
        {//CODE TO SET ACCESSCODE
        Socket socket = new Socket("192.168.1.101", 5000);
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);     
        
        dos.writeUTF("SET_ACCESSCODE");   
        String accessCode = "XYZ1423";
        ObjectOutputStream ous = new ObjectOutputStream(os);
        ous.writeObject(accessCode); 
        ous.close();
        
        dos.close();
        socket.close();
        }
        
        
        {//code to validate password
        Socket socket = new Socket("192.168.1.101", 5000);
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);     
        
        dos.writeUTF("ACCESSCODE_AUTH");   
        String accessCode = "XYZ123";
        ObjectOutputStream ous = new ObjectOutputStream(os);
        ous.writeObject(accessCode); 
        
        
        InputStream in2 = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in2);
        Boolean result = (Boolean) ois.readObject();
        System.out.println("auth RESULT received from Server " + result);     
        ous.close();
        ois.close();
        }
        
        {//code to set active
            Socket socket = new Socket("192.168.1.101", 5000);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);     

            dos.writeUTF("SET_ACTIVE");   
            Boolean active = true;
            ObjectOutputStream ous = new ObjectOutputStream(os);
            ous.writeObject(true); 
            ous.close();
            dos.close();
            socket.close();
        }
        
        {//code to isActive
            Socket socket = new Socket("192.168.1.101", 5000);
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);     

            dos.writeUTF("IS_ACTIVE");   
          
            InputStream in2 = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in2);
            Boolean result = (Boolean) ois.readObject();
            System.out.println("isactive from Server " + result);     
         
            ois.close();
            
//            ous.close();
            dos.close();
            socket.close();
            
        }
        
        */
        
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
