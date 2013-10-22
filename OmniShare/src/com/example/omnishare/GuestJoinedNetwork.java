package com.example.omnishare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GuestJoinedNetwork extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	ChordMain chordmain;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest_joined_network);

		
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		chordmain = new ChordMain(this.getApplicationContext());
		chordmain.startChord();
		ServerInterface.syncFiles(getApplicationContext());
		
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		chordmain.stopChord();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guest_joined_network, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		if(tab.getPosition() == 1) //1 is the id of the file list fragment
        {
            View rootView = mViewPager.getRootView();   
            
            final ArrayList<String> fileList = ServerInterface.getCurrentFileList(getApplicationContext());
            if (fileList != null)
            {
                System.out.println("FileList Size " + fileList.size());
            }
            
            //for file list and on click functionality                        
            
            if (fileList != null && fileList.size() != 0)
            {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mViewPager.getChildAt(tab.getPosition()).getContext(),R.layout.activity_fileitem, fileList);
                ListView lv = (ListView) rootView.findViewById(R.id.lv_fragment_filelist);
                lv.setOnItemClickListener(new OnItemClickListener() {
 
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                    {
                        TextView fileName = (TextView) view.findViewById(R.id.rowtext);
                        String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
                        
                        File directory = Environment.getExternalStorageDirectory();
                        
                        
                        String filePath = directory.getAbsolutePath() + "/" + fileList.get(position);
                        String tempFilePathString = filePath.toLowerCase();
                        
                        System.out.println("1 CurrentView Onclick " + filePath);
                        
                        if(tempFilePathString.contains(".pdf"))
                        {
                            Intent pdfIntent = new Intent(getApplicationContext(), PdfGuestViewActivity.class);
                            pdfIntent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, filePath);  
                            startActivity(pdfIntent);                       
                        }else
                        if(tempFilePathString.contains(".jpg") || tempFilePathString.contains(".jpeg") || tempFilePathString.contains(".bmp") || tempFilePathString.contains(".png"))
                        {
                            Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
                            intent.putExtra("filePath", filePath);
                            startActivity(intent);                      
                        }
                        else
                        if(tempFilePathString.contains(".ppt"))
                        {
                            Intent intent = new Intent(getApplicationContext(), PPTViewActivity.class);
                            intent.putExtra("filePath", filePath);
                            startActivity(intent);                      
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), DisplayVideoActivity.class);
                            intent.putExtra("filePath", filePath);
                            startActivity(intent);                      
                        }                       
                    }
                });
                lv.setAdapter(arrayAdapter);
            }
        }   
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch(position)
			{
				case 0:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;					
				}
				case 1:
				{
					Fragment fragment = new FileListFragment();
					Bundle args = new Bundle();
					args.putInt(FileListFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;					
				}
				case 2:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;					
				}
				default:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				}
			
			}
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.str_currentview).toUpperCase(l);
			case 1:
				return getString(R.string.str_filelist).toUpperCase(l);
			case 2:
				return getString(R.string.str_view).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}
		
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_guest_joined_network_dummy, container,false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			
		
			 
			return rootView;
		}
	}
	
	
	public static class FileListFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public FileListFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_filelist_guest_joined_activity,container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			
			final ArrayList<String> fileList = ServerInterface.getCurrentFileList(getActivity());
			

			if (fileList != null && fileList.size() != 0)
			{
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FileListFragment.this.getActivity(),R.layout.activity_fileitem, fileList);
				ListView lv = (ListView) rootView.findViewById(R.id.lv_fragment_filelist);
				
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id)
					{
						//TextView fileName = (TextView) view.findViewById(R.id.rowtext);
						//String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
						
						File directory = Environment.getExternalStorageDirectory();
						
						
						String filePath = directory.getAbsolutePath() + "/" + fileList.get(position);
						String tempFilePathString = filePath.toLowerCase();
						
		                System.out.println("2 CurrentView Onclick " + filePath);
		                
		                if(tempFilePathString.contains(".pdf"))
		                {
		                	Intent pdfIntent = new Intent(getActivity(), PdfGuestViewActivity.class);
			                pdfIntent.putExtra(PdfGuestViewActivity.EXTRA_PDFFILENAME, filePath);	
			                startActivity(pdfIntent);		                
		                }else
	                	if(tempFilePathString.contains(".jpg") || tempFilePathString.contains(".jpeg") || tempFilePathString.contains(".bmp") || tempFilePathString.contains(".png"))
		                {
		                	Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }
	                	else
	                	if(tempFilePathString.contains(".ppt"))
		                {
		                	Intent intent = new Intent(getActivity(), PPTViewActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }
	                	else
	                	{
		                	Intent intent = new Intent(getActivity(), DisplayVideoActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }						
					}
				});
				
				lv.setAdapter(arrayAdapter);
			}
			return rootView;
		}
		
	
	}
	
	public void suggestFile(View v)
	{
		System.out.println("SuggestFile Clicked");
		Intent intent = new Intent(getBaseContext(), AddFilesActivity.class);
		String falseMeetingId = "-1";
		intent.putExtra("meetingId", falseMeetingId);
		startActivityForResult(intent, 1);	
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		System.out.println("REQ CODE " + requestCode + "  RES CODE " + resultCode);
		
		if (requestCode == 1) 
		{
		     if(resultCode == RESULT_OK)
		     {    
		    	ArrayList<String> fileList = data.getStringArrayListExtra("fileList");
		    	if(fileList != null && !fileList.isEmpty())
		    	{
		    		chordmain.sendToAll(fileList.get(0), 4);
		    	}
		    	else
		    	{
		    		if(fileList == null)
		    		{
		    			System.out.println("fileList in result = null");
		    		}
		    	}
		    	
		 	 }		 
	     }
	     if (resultCode == RESULT_CANCELED)
	     {  
	    	 
	     }
     }
		
		
	

	public void syncFiles(View v) throws IOException
	{
		//ServerInterface.syncFiles(getApplicationContext());		
		new SyncFilesTask().execute();			
	}
	
	
	 private class SyncFilesTask extends AsyncTask<String, Integer, String> 
	   {

		   Toast toast = Toast.makeText(getApplicationContext(), "Syncing files, please wait", Toast.LENGTH_LONG);
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();	
		      toast.show();
		   }
	
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) 
		   {
		      super.onProgressUpdate(values);		
		   }
		 
		   @Override
		protected void onPostExecute(String result)
		   {
			   super.onPostExecute(result);
			   System.out.println("Done Receiving files from server");
			   toast.cancel();
			  // finish();
		   }

			@Override
			protected String doInBackground(String... params)
			{				
				ServerInterface.syncFiles(getApplicationContext());				
				return "Done with file get/sync";
			}
	   }
	 

	
	
}
