package example.hellomobileworld;

import java.sql.SQLException;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onMyButtonClick(View view)
    {
    	DBConnect verifier = null;
    	
    	try
		{
			verifier = new DBConnect();
		}
		catch (Exception e)
		{
			System.out.println("Error with init of DBConnect");
			e.printStackTrace();
		}
		
    	
    	
    	TextView myTextView = (TextView) findViewById(R.id.myTextView);
    	
    	try
		{
			if(verifier.verify("g", "321") == true)
			{
				myTextView.setVisibility(View.VISIBLE);
			}
			else
			{
				myTextView.setText("Data not found");
				myTextView.setVisibility(View.VISIBLE);
			}
			
			
		}
		catch (SQLException e)
		{
			System.out.println("Error with verifier call");
			e.printStackTrace();
		}
    }
    
}
