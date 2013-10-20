/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package omnishareserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Skimmel
 */
public class ServerTest
{    
   /**
     * Test of run method, of class Server.
     */
    @Test
    public void testRun() throws IOException
    {
        System.out.println("run");
        Server instance = new Server();
        Thread t1 = new Thread(instance);
        t1.start();
        assertNotNull(instance);        
        instance.shutdown();
    }
    
    /**
     * Test of copy method, of class Server.
     */
    @Test
    public void testCopy() throws Exception
    {
        System.out.println("Test of Copy() running...");       
        InputStream in = null;
        OutputStream out = null;
        assertFalse(Server.copy(in, out));
    }

    /**
     * Test of go method, of class Server.
     */
    @Test
    public void testGo() throws IOException
    {
        System.out.println("Test of Go() running");
        Server instance = new Server();  
        Thread t1 = new Thread(instance);
        t1.start();
        assertNotNull(instance);       
        assertTrue(instance.getCurrentSession().isActive()); 
        instance.shutdown();
    }

    /**
     * Test of broadCast method, of class Server.
     */
    @Test
    public void testBroadCast() throws IOException
    {
        System.out.println("Test of Broadcast() running");
        String message = "";
        Server instance = new Server(); 
        Thread t1 = new Thread(instance);
        t1.start();
        instance.broadCast(message);     
        instance.shutdown();
    }

    /**
     * Test of getCurrentSession method, of class Server.
     */
    @Test
    public void testGetCurrentSession() throws IOException
    {
        System.out.println("getCurrentSession");
        Server instance = new Server();  
        Thread t1 = new Thread(instance);
        t1.start();
        Session result = instance.getCurrentSession();
        assertNotNull(result);      
        instance.shutdown();
    }
    
    
    //Integration testing of Server and a mock client over localhost.
    @Test
    public void sendFileTest() throws IOException
    {
        System.out.println("SendFileTest");
        Server instance = new Server();
        Thread t1 = new Thread(instance);
        t1.start();
        File testFile = new File("test.txt");
        assertTrue(testFile.exists());        
        ServerInterface.sendFile(testFile);
        assertSame(ServerInterface.getCurrentFileList().size(), 1); 
        instance.shutdown();
    }
}
