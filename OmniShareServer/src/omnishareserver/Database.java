package omnishareserver;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import omnishareserver.Session;

public class Database
{
    private ObjectContainer database = null;
    private final String fileName = "database.db";
    
    public void open()
    {
           database = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), fileName);
    }
    public void close()
    {
            database.close();
    }
    public void addSession(Session session) throws IOException
    {
        if(database != null)
        {            
            database.store(session);
            database.commit();
        }
        else
        {
            System.out.println("Database Null");
        }
    }


        
}
	
