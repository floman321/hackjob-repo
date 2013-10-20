package test;
import java.util.HashMap;

import com.example.omnishare.CreateMeeting;
import com.example.omnishare.DBController;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import junit.framework.TestCase;


public class OmniShareTest extends AndroidTestCase
{
	
	public void testDBController()
	{
		 // TODO		
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
	}

	public void testInsertMeeting()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		int dbOldSize = dbController.getAllMeetings().size();
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test meetingName");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);		
		
		assertEquals(dbOldSize + 1, dbController.getAllMeetings().size());
	}

	public void testUpdateMeeting()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test meetingName");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);
		
		HashMap<String, String> updatedMeeting = new HashMap<String, String>();
		updatedMeeting.put("meetingId", "1");
		updatedMeeting.put("meetingName", "Test meetingName Updated");
		updatedMeeting.put("meetingLocation", "Test meetingLocation Updated");
		updatedMeeting.put("meetingDate", "Test meetingDate Updated");
		updatedMeeting.put("meetingCode", "Test meetingCode Updated");
		dbController.updateMeeting(updatedMeeting);
		
		assertEquals("Test meetingName Updated", dbController.getMeetingInfo("1").get("meetingName"));
		assertEquals("Test meetingLocation Updated", dbController.getMeetingInfo("1").get("meetingLocation"));
		assertEquals("Test meetingDate Updated", dbController.getMeetingInfo("1").get("meetingDate"));
		assertEquals("Test meetingCode Updated", dbController.getMeetingInfo("1").get("meetingCode"));
	}

	public void testDeleteMeeting()
	{	
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test DeleteMeeting");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		
		int dbOldSize = dbController.getAllMeetings().size();
		
		dbController.insertMeeting(testMeeting);
		System.out.println("MEETINGLISTSIZE " + dbController.getAllMeetings().size());
				
		assertEquals(dbOldSize + 1, dbController.getAllMeetings().size());
		
		String meetingID = dbController.getMeeting("Test DeleteMeeting").get("meetingId");
		dbController.deleteMeeting(meetingID);
		
		assertEquals(dbOldSize, dbController.getAllMeetings().size());
	}

	public void testGetAllMeetings()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		int dbOldSize = dbController.getAllMeetings().size();
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test meetingName");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);		
				
		assertEquals(dbOldSize + 1, dbController.getAllMeetings().size());
	}

	public void testGetMeeting()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test GetMeetingName");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);
		
		String meetingID = dbController.getMeeting("Test GetMeetingName").get("meetingId");
		
		assertEquals("Test GetMeetingName", dbController.getMeetingInfo(meetingID).get("meetingName"));
	}

	public void testGetMeetingInfo()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test meetingName");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);
		
		String meetingID = dbController.getMeeting("Test GetMeetingName").get("meetingId");
		
		assertEquals("Test meetingName Updated", dbController.getMeetingInfo("1").get("meetingName"));
		assertEquals("Test meetingLocation Updated", dbController.getMeetingInfo("1").get("meetingLocation"));
		assertEquals("Test meetingDate Updated", dbController.getMeetingInfo("1").get("meetingDate"));
		assertEquals("Test meetingCode Updated", dbController.getMeetingInfo("1").get("meetingCode"));
		
	}

	public void testInsertMeetingFile()
	{
		DBController dbController = new DBController(getContext());
		assertNotNull(dbController);
		
		HashMap<String, String> testMeeting = new HashMap<String, String>();
		testMeeting.put("meetingName", "Test InsertMeetingFile");
		testMeeting.put("meetingLocation", "Test meetingLocation");
		testMeeting.put("meetingDate", "Test meetingDate");
		testMeeting.put("meetingCode", "Test meetingCode");
		dbController.insertMeeting(testMeeting);
		
		String meetingID = dbController.getMeeting("Test InsertMeetingFile").get("meetingId");
		
		HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("fileName", "Test InsertFileName");
        queryValues.put("fileLocation", "Test InsertFileName");
        queryValues.put("fileMeetingRef", meetingID);
        dbController.insertMeetingFile(queryValues);
        
        assertEquals(1, dbController.getAllMeetingFiles(meetingID).size());
	}

	public void testUpdateMeetingFile()
	{
		fail("Not yet implemented"); // TODO
	}

	public void testDeleteMeetingFile()
	{
		fail("Not yet implemented"); // TODO
	}

	public void testGetAllMeetingFiles()
	{
		fail("Not yet implemented"); // TODO
	}



}
