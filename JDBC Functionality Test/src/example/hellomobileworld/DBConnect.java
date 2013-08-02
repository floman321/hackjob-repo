package example.hellomobileworld;

import java.sql.*;


public class DBConnect
{
    
 private static final String dbClassName = "com.mysql.jdbc.Driver";
 private static final String CONNECTION = "jdbc:mysql://192.168.1.102/android";
 private Connection con;
 
 public DBConnect() throws ClassNotFoundException,SQLException, IllegalAccessException, InstantiationException
 {
   System.out.println(dbClassName);   
//   Class.forName(dbClassName);  
   Class.forName("com.mysql.jdbc.Driver").newInstance();
  
 }
 
 public boolean verify(String user,String pass) throws SQLException
 {
     boolean retval = false;
     con = DriverManager.getConnection(CONNECTION,"root","123");
     Statement statement = con.createStatement();
     ResultSet results = statement.executeQuery("SELECT * FROM User WHERE User_LoginName ='"+user+"' AND User_Password ='" + pass+"'" );
     
     
      if(results.next())
      {
         retval = true;
      }
      
     
      
     con.close(); 
     con = null;
     statement.close();
     statement = null;
     results.close();
     results = null;
      
      
     return retval;
 }
 
 
}
 