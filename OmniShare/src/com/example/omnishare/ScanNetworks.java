package com.example.omnishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class ScanNetworks extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_availablenetworks);

	}
	
	public void switchme(View view){
		final int id = view.getId();
		switch(id){
		case R.id.btn_joinnetwork:
			Intent myIntent = new Intent(ScanNetworks.this, GuestJoinedNetwork.class);
			//myIntent.putExtra("key", value); //Optional parameters
			ScanNetworks.this.startActivity(myIntent);
			break;
		}
		
	}
}
