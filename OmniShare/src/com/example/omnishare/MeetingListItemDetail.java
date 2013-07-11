package com.example.omnishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MeetingListItemDetail extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_listitem);

	    Intent intent = getIntent();
	    int position = intent.getIntExtra("position", 0);

	    // Here we turn your string.xml in an array
	    String[] myKeys = getResources().getStringArray(R.array.strarr_meetings);

	    TextView myTextView = (TextView) findViewById(R.id.meeting_textview);
	    myTextView.setText(myKeys[position]);

	}
}
