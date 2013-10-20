package omnishareserver;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Skimmel
 */
public class UDPBeacon implements Runnable
{
    boolean running = true;
    
    @Override
    public void run()
    {
        InetAddress address = null;
        MulticastSocket socket = null; 
        
        try
        {
            socket = new MulticastSocket(5001);
            address = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(address);

               
            while(running)
            {
                DatagramPacket requestPacket;   
                byte[] buf = new byte[256];
                requestPacket = new DatagramPacket(buf, buf.length);
                socket.receive(requestPacket);                
                String received = new String(requestPacket.getData(), 0, requestPacket.getLength());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                
               
                if(received.equals("IS_OMNISHARE_HOST"))
                {                    
                    String response = "OMNISHARE_TRUE";
                    byte[] responseBuffer = response.getBytes();
                  //   System.out.println("Beacon Responds " + response + " @ " + dateFormat.format(date));
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address,  5001);
                    socket.send(responsePacket);
                    
                }
            }            
           socket.leaveGroup(address);
           socket.close();    
        }
        catch(Exception e)
        {           
            e.printStackTrace();
            System.out.println("Malformed UDP packet received.");
        }
       
    }
    
}
