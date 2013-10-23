package omnishareserver;

/**
 *
 * @author Skimmel
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.nio.*;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channel;

public class Server implements Runnable
{

    private static ExecutorService tpe;
    ArrayList clientOutputStreams;
    Session currentSession;
    ServerSocket serverSocket;

    public Server()
    {
        tpe = Executors.newCachedThreadPool();
        clientOutputStreams = new ArrayList();
        currentSession = new Session();
    }

    @Override
    public void run()
    {
        this.go();
    }

    public class ClientHandler implements Runnable
    {

        BufferedReader bufferedReader;
        InputStreamReader isReader;
        Socket socket;

        public ClientHandler(Socket clientSocket) //constructor
        {
            try
            {
                socket = clientSocket;
                isReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(isReader);

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        public void run()
        {
            try
            {
                InputStream in = socket.getInputStream();
                DataInputStream clientData = new DataInputStream(in);
                String message ="";                
                message = clientData.readUTF();

                String fileName = message;
                System.out.println("Message " + message);
                if (message.contains(".")) //ie is a file
                {
                    System.out.println("Receiving file " + fileName + "...");                   
                    OutputStream output = new FileOutputStream(fileName);
                    long size = clientData.readLong();
                    int bytesRead = 0;
                    byte[] buffer = new byte[524288];//byte[1024];
                    while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
                    {
                        output.write(buffer, 0, bytesRead);
                        size -= bytesRead;
                    }
                    // Closing the FileOutputStream handle  
                    output.close();
                    in.close();
                    clientData.close();
                    System.out.println("Received file " + fileName);
                     currentSession.addFile(fileName);
                } else if (message.contains("FILELIST_REQ"))//client wants a filelist to compare with, send it through
                {
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream ous = new ObjectOutputStream(os);
                    ous.writeObject(currentSession.getFileList());
                    socket.close();
                    ous.close();
                }
                else if (message.contains("FILES_REQ"))
                {
                   sendFiles(socket);      
                }
                else if (message.contains("FILES_SYNC")) 
                {
                   InputStream in2 = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(in2);
                    ArrayList<String> guestList = (ArrayList<String>) ois.readObject();

                    for(int i = 0; i < guestList.size(); i++)
                    {
                        System.out.println("newList @ " + i + "   " + guestList.get(i));
                    }
                    
                   syncFiles(socket, guestList);    
                   
                }                
                else if (message.contains("ACCESSCODE_AUTH"))
                {
                    // InputStream in2 = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(in);                 
                    String accessCode = (String) ois.readObject();
                    System.out.println("acces Code rec " + accessCode);
                    Boolean result = currentSession.checkAccessCode(accessCode);
                    
                    //send response
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream ous = new ObjectOutputStream(os);
                    ous.writeObject(result);
                    socket.close();
                    ous.close();
                    ois.close();
                }
                else if(message.contains("SET_ACCESSCODE"))
                {
                    ObjectInputStream ois = new ObjectInputStream(in);                 
                    String accessCode = (String) ois.readObject();
                    System.out.println("Set accessCode to " + accessCode);
                    currentSession.setAccessCode(accessCode);
                    ois.close();
                }
                else if(message.contains("SET_ACTIVE"))
                {
                    ObjectInputStream ois = new ObjectInputStream(in);                 
                    Boolean active = (Boolean) ois.readObject();
                    System.out.println("Set active to " + active);
                    currentSession.setActive(active);
                    ois.close();
                }
                else if(message.contains("IS_ACTIVE"))
                {                              
                    Boolean active = currentSession.isActive();
                    System.out.println("is active  " + active);                 
                    //send reply
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream ous = new ObjectOutputStream(os);                   
                   
                    ous.writeObject(active);
                    socket.close();
                    ous.close();
                }
                else if(message.contains("GET_MEETINGNAME") && currentSession.isActive())
                {                              
                    String meetingName = currentSession.getMeetingName();
                    System.out.println("GET_MEETINGNAME  " + meetingName);                 
                    //send reply
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream ous = new ObjectOutputStream(os);                   
                   
                    ous.writeObject(meetingName);
                    socket.close();
                    ous.close();
                }
                else if(message.contains("SET_MEETINGNAME"))
                {                              
                    ObjectInputStream ois = new ObjectInputStream(in);                 
                    String meetingName = (String) ois.readObject();
                    System.out.println("SET_MEETINGNAME " + meetingName);
                    currentSession.setMeetingName(meetingName);
                    ois.close();
                }

            } catch (Exception e)
            {
                System.out.println("Unidentified request, ignored...");
            }
        }

        private void sendFiles(Socket socket) throws IOException, ClassNotFoundException
        {
            ArrayList<String> tempList = currentSession.getFileList();
            for(int i = 0; i < tempList.size(); i++)
            {
                sendfile(new File(tempList.get(i)));
            }              
            socket.close();  	
        }
        
        private void syncFiles(Socket socket, ArrayList<String> currentFiles) throws IOException, ClassNotFoundException
        {
            ArrayList<String> tempList = currentSession.getFileList();
            for(int i = 0; i < tempList.size(); i++)
            {
                if(!currentFiles.contains(tempList.get(i)))//Only send files not in guest's list
                {
                    sendfile(new File(tempList.get(i)));
                }
            }              
            socket.close();  	
        }
        
        private void sendfile(File file) throws FileNotFoundException, IOException
        {
                System.out.println("Attempt to send file " + file.getName() + " to " + socket.getInetAddress());
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
        }
    }

    static boolean copy(InputStream in, OutputStream out)
    {        
        boolean retval = true;
        try
        {
            byte[] buf = new byte[8192];
            int len = 0;
            while ((len = in.read(buf)) != -1)
            {
                out.write(buf, 0, len);
            }  
        }catch(Exception e)
        {
            return false;
        }        
        return retval;
    }

    public void go()
    {
        try
        {
            serverSocket = new ServerSocket(5000);
            while (currentSession.isActive())
            {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                currentSession.addDevice(clientSocket.getInetAddress().toString());

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                tpe.execute(new Thread(new ClientHandler(clientSocket)));
                System.out.println("Got a connection @ " + dateFormat.format(date) + " Guest IP: " + clientSocket.getInetAddress().getHostAddress());

            }
        } catch (Exception ex)
        {
            System.out.println("Server Shutdown");
        }
    }

    public void broadCast(String message)
    {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext())
        {
            try
            {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public Session getCurrentSession()
    {
        return currentSession;
    }
    
    public void shutdown() throws IOException
    {
        if(serverSocket!= null)
        {   
            serverSocket.close();
        }
    }
}
