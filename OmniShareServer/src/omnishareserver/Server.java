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

                String fileName = "RECEIVED_" + message;
                System.out.println("Message " + message);
                if (message.contains(".")) //ie is a file
                {
                    System.out.println("Receiving file " + fileName + "...");
                    currentSession.addFile(fileName);
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

            } catch (Exception e)
            {
                e.printStackTrace();
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
        
        private void sendfile(File file) throws FileNotFoundException, IOException
        {
                System.out.println("Attemp to send file " + file.getName());
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
	              
	   //    fis.close();
	   //     bis.close();
	   //     dos.close();             
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
            ex.printStackTrace();
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
    

    
    
}
