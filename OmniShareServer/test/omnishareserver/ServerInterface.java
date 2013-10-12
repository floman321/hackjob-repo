package omnishareserver;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;


//This will act as a Mock Client object for integration testing of the server functionality.
public class ServerInterface
{

    public static ArrayList<String> myCurrentFilesList = new ArrayList<String>();

    public static void syncFiles()
    {
        if (myCurrentFilesList.isEmpty())
        {
            System.out.println("GET FILES SELECTED " + myCurrentFilesList.size());
            getFiles();
        } else
        {
            System.out.println("SYNC FILES SELECTED " + myCurrentFilesList.size());
            getNewFiles();
        }
    }

    public static void getFiles()//to get all current files for this session on server.
    {
        String directory = "receivedFiles";
        String hostAddress = "localhost";       
        File dir = new File(directory);
        if(!dir.exists())
        {
             Boolean created = (new File(directory)).mkdir();
        }
        System.out.println("Directory Exists " + dir.exists());
        
        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("FILES_REQ");


                while (true)
                {
                    InputStream in = socket.getInputStream();
                    DataInputStream clientData = new DataInputStream(in);
                    String message = "";
                    message = clientData.readUTF();

                    OutputStream output = null;
                    if (message.contains("."))
                    {
                        System.out.println("Receiving file " + message + " from " + hostAddress + "...");

                        output = new FileOutputStream(directory + "/" + message);
                        long size = clientData.readLong();
                        int bytesRead = 0;
                        byte[] buffer = new byte[524288];//byte[1024];
                        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
                        {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }

                        myCurrentFilesList.add(message);
                        System.out.println("Received file " + message);
                    } else
                    {
                        output.close();
                        in.close();
                        clientData.close();
                        break;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    public static void getNewFiles()//to get all new files on current session.
    {     
        String directory = "receivedFiles";
        String hostAddress = "localhost";
        
        File dir = new File(directory);
        if(!dir.exists())
        {
            Boolean created = (new File(directory)).mkdir();
        }
        System.out.println("Directory Exists " + dir.exists());
      
        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("FILES_SYNC");

                //send arraylist with					        
                ObjectOutputStream ous = new ObjectOutputStream(os);
                ous.writeObject(myCurrentFilesList);

                while (true)
                {
                    InputStream in = socket.getInputStream();
                    DataInputStream clientData = new DataInputStream(in);
                    String message = "";
                    message = clientData.readUTF();


                    OutputStream output = null;
                    if (message.contains("."))
                    {
                        System.out.println("Receiving file " + message + " from " + hostAddress + "...");

                         output = new FileOutputStream(directory + "/" + message);
                        long size = clientData.readLong();
                        int bytesRead = 0;
                        byte[] buffer = new byte[524288];//byte[1024];
                        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
                        {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }

                        System.out.println("Received file " + message);
                        myCurrentFilesList.add(message);

                    } else
                    {
                        socket.close();
                        ous.close();
                        dos.close();
                        output.close();
                        in.close();
                        clientData.close();
                        break;
                    }
                }

                System.out.println("MyfilesList size AFTER SYNC " + myCurrentFilesList.size());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void setAccessCode(String accessCode)
    {
        String hostAddress = "localhost";

        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                dos.writeUTF("SET_ACCESSCODE");
                ObjectOutputStream ous = new ObjectOutputStream(os);
                ous.writeObject(accessCode);
                ous.close();

                dos.close();
                socket.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean authUser(String accessCode)
    {
        boolean retval = false;
        String hostAddress = "localhost";        
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                dos.writeUTF("ACCESSCODE_AUTH");
                ObjectOutputStream ous = new ObjectOutputStream(os);
                ous.writeObject(accessCode);


                InputStream in2 = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in2);
                retval = (Boolean) ois.readObject();
                System.out.println("auth RESULT received from Server " + retval);
                ous.close();
                ois.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return retval;
    }

    public static void setActive(Boolean value )
    {
        String hostAddress = "localhost";        

        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                dos.writeUTF("SET_ACTIVE");
                ObjectOutputStream ous = new ObjectOutputStream(os);
                ous.writeObject(value);
                ous.close();
                dos.close();
                socket.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    public static boolean isActive()
    {
        boolean retval = false;      
        String hostAddress = "localhost";        

        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("IS_ACTIVE");
                InputStream in2 = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in2);
                retval = (Boolean) ois.readObject();
                System.out.println("isactive from Server " + retval);

                ois.close();
                dos.close();
                socket.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        return retval;
    }

    public static void sendFile(File file)
    {
        String hostAddress = "localhost";      

        if (!hostAddress.equals("NO_HOST_SET") && !getCurrentFileList().contains(file.getName()))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);

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

                socket.close();
                fis.close();
                bis.close();
                dos.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            System.out.println("Transfer done...");
        } else
        {
            System.out.println("File " + file.getName() + " already on server.");
        }
    }

    public static ArrayList<String> getCurrentFileList() // to be called by client devices to get current meeting files, and sync if necessary
    {
        ArrayList<String> retval = null;  
        String hostAddress = "localhost";

        if (!hostAddress.equals("NO_HOST_SET"))
        {
            try
            {
                Socket socket = new Socket(hostAddress, 5000);
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("FILELIST_REQ");

                InputStream in2 = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in2);
                retval = (ArrayList<String>) ois.readObject();

                ois.close();
                dos.close();
                socket.close();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return retval;
    }
}
