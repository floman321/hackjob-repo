/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package omnishareserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static org.junit.Assert.*;
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
        instance.run();      
        assertNotNull(instance);   
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
    public void testGo()
    {
        System.out.println("Test of Go() running");
        Server instance = new Server();        
        assertNotNull(instance);
        instance.go();   
        assertTrue(instance.getCurrentSession().isActive());
    }

    /**
     * Test of broadCast method, of class Server.
     */
    @Test
    public void testBroadCast()
    {
        System.out.println("Test of Broadcast() running");
        String message = "";
        Server instance = new Server();
        instance.broadCast(message);    
    }
}
