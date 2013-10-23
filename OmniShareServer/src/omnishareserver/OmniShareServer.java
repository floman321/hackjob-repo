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
import java.util.Scanner;
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
        Server omniShareServer = new Server();
        Thread t1 = new Thread(omniShareServer);
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
        
        System.out.println("UDP Beacon started \n");
        UDPBeacon beacon = new UDPBeacon();
        Thread t2 = new Thread(beacon);
        t2.start();
        
        String input = "";
        Scanner scanner  = new Scanner(System.in);
        while(!input.equals("exit"))
        {
            System.out.println("Please enter an option: \n"
                    +        "\nCommand:    | Action:");
            System.out.println("save        | Saves current session to oodb for later use.");
            System.out.println("exit        | Exit server");
            input = scanner.nextLine();
            switch(input)
            {               
                case "save":
                {
                    Database db = new Database();
                    db.open();
                    db.addSession(omniShareServer.getCurrentSession());
                    db.close();
                    break;
                }
                case "exit":
                {
                    try
                    {
                        omniShareServer.shutdown();
                        beacon.shutdown();
                    }
                    catch(Exception e)
                    {                        
                    }
                    return;                    
                }
            }
        }
        

    }
}
