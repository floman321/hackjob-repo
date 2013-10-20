/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package omnishareserver;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Skimmel
 */
public class SessionTest
{    
    /**
     * Test of addDevice method, of class Session.
     */
    @Test
    public void testAddDevice()
    {
        System.out.println("addDevice");
        String dev = "192.168.0.1";
        Session instance = new Session();
        assertTrue(instance.getDeviceList().isEmpty());
        instance.addDevice(dev);
        assertFalse(instance.getDeviceList().isEmpty());
    }

    /**
     * Test of addFile method, of class Session.
     */
    @Test
    public void testAddFile()
    {
        System.out.println("addFile");
        String file = "";
        Session instance = new Session();
        assertTrue(instance.getFileList().isEmpty());
        instance.addFile(file);
        assertFalse(instance.getFileList().isEmpty()); 
       
    }

    /**
     * Test of getFileList method, of class Session.
     */
    @Test
    public void testGetFileList()
    {
        System.out.println("getFileList");
        Session instance = new Session();
        ArrayList expResult = new ArrayList();
        ArrayList result = instance.getFileList();
        assertEquals(expResult, result);        
    }

    /**
     * Test of isActive method, of class Session.
     */
    @Test
    public void testIsActive()
    {
        System.out.println("isActive");
        Session instance = new Session();
        boolean expResult = true;
        boolean result = instance.isActive();
        assertEquals(expResult, result);
    }

    /**
     * Test of disable method, of class Session.
     */
    @Test
    public void testDisable()
    {
        System.out.println("disable");
        Session instance = new Session();
        assertTrue(instance.isActive());
        instance.disable();
        assertFalse(instance.isActive());
    }

    /**
     * Test of hasHost method, of class Session.
     */
    @Test
    public void testHasHost()
    {
        System.out.println("hasHost");
        Session instance = new Session();
        boolean expResult = false;
        boolean result = instance.hasHost();
        assertEquals(expResult, result);
        
    }
    
    
}
