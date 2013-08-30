package omnishareserver;

import java.util.ArrayList;

/**
 *
 * @author Skimmel
 */
public class Session
{
    private ArrayList<String> devicesConnected;
    private ArrayList<String> filesList;
    private boolean isActive;
    private boolean hasHost;
    private ArrayList<String> hosts;
    private String accessCode; //how to make this secure/enc

    public Session()
    {
        devicesConnected = new ArrayList<>();
        filesList = new ArrayList<>();   
        isActive = true;
        hasHost = false;
        hosts = new ArrayList<>();
    }
    
    public void addDevice(String dev)
    {
        if(!devicesConnected.contains(dev))
        {
            devicesConnected.add(dev);
        }
    }
    
    public void addFile(String file)
    {
        if(!filesList.contains(file))
        {
            filesList.add(file);
        }
    }
    
    public ArrayList<String> getFileList()
    {
        return filesList;
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public void disable()
    {
        isActive = false;
    }
    
}
