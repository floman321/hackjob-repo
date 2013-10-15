package com.example.omnishare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class AccessCodeActivity extends Activity
{
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_access_code);			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.access_code, menu);
		return true;
	}

	public void validate(View view)
	{		
		EditText et = (EditText)findViewById(R.id.edit_text_password);
		String accessCode = et.getText().toString();
		System.out.println("IN VALIDATE BTN " + accessCode);
		if(ServerInterface.authUser(accessCode, getApplicationContext()))
		{
			Intent returnIntent = new Intent();
			setResult(RESULT_OK, returnIntent);
	        finish();
		}
		else
		{
			Intent returnIntent = new Intent();
			//setResult(RESULT_CANCELED, returnIntent); THIS IS THE RIGHT ONE
			setResult(RESULT_OK, returnIntent);
	        finish();
		}
	}
	
}
