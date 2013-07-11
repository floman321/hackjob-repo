package com.example.omnishare;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void switchme(View view){
		final int id = view.getId();
		switch(id){
		case R.id.btnMyMeetings:
			Intent myIntent = new Intent(MainActivity.this, MyMeetings.class);
			//myIntent.putExtra("key", value); //Optional parameters
			MainActivity.this.startActivity(myIntent);
			break;
		case R.id.btnScanAvailableNetworks:
			Intent myIntent2 = new Intent(MainActivity.this, ScanNetworks.class);
			//myIntent.putExtra("key", value); //Optional parameters
			MainActivity.this.startActivity(myIntent2);
			break;	
		}
		
	}
	
}
