package com.example.omnishare;

import java.util.List;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;
import com.samsung.chord.ChordManager.INetworkListener;

import android.content.Context;
import android.widget.Toast;

public class ChordMain {
	protected static final String HOST_SENDPDF_PAGE_UPDATE = "1";
	protected static final String GUEST_REQUEST_PDFPAGE = "3";
	protected static final String MESSAGE_NEW_FILE_UPLOADED = "2";
	protected static final String CHANNEL_JOINED = "BOOBCHANNEL";
	protected ChordManager mChordManager = null;
	protected boolean bStarted = false;
	protected int mSelectedInterface = -1;
	private Context currContext;
	
	public ChordMain(Context context){
		/****************************************************
         * 1. GetInstance
         ****************************************************/
        
		currContext = context;
        mChordManager = ChordManager.getInstance(context);                
        //mLogView.appendLog("    getInstance");

        /****************************************************
         * 2. Set some values before start If you want to use secured channel,
         * you should enable SecureMode. Please refer
         * UseSecureChannelFragment.java mChordManager.enableSecureMode(true);
         * 
         *
         * Once you will use sendFile or sendMultiFiles, you have to call setTempDirectory  
         * mChordManager.setTempDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()
         *       + "/Chord");
         ****************************************************/
       // mLogView.appendLog("    setLooper");
        mChordManager.setHandleEventLooper(context.getMainLooper());

        /**
         * Optional. If you need listening network changed, you can set callback
         * before starting chord.
         */
        mChordManager.setNetworkListener(new INetworkListener() {

            @Override
            public void onDisconnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                    //Toast.makeText(context, getInterfaceName(interfaceType) + " is disconnected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnected(int interfaceType) {
                if (interfaceType == mSelectedInterface) {
                   // Toast.makeText(getActivity(),getInterfaceName(interfaceType) + " is connected",Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
	
	public void startChord() {
        /**
         * 3. Start Chord using the first interface in the list of available
         * interfaces.
         */
        List<Integer> infList =mChordManager.getAvailableInterfaceTypes();
        if(infList.isEmpty()){
            //mLogView.appendLog("    There is no available connection.");
            return;
        }
        
        int interfaceType = infList.get(0);
        
        int nError = mChordManager.start(interfaceType, mManagerListener);
        mSelectedInterface = interfaceType;
        //mLogView.appendLog("    start(" + getInterfaceName(interfaceType) + ")");
        //mStart_stop_btn.setEnabled(false);

        if (ChordManager.ERROR_INVALID_STATE == nError) {
           // mLogView.appendLog("    Invalid state!");
        } else if (ChordManager.ERROR_INVALID_INTERFACE == nError) {
           // mLogView.appendLog("    Invalid connection!");
        } else if (ChordManager.ERROR_INVALID_PARAM == nError) {
           // mLogView.appendLog("    Invalid argument!");
        } else if (ChordManager.ERROR_FAILED == nError) {
           // mLogView.appendLog("    Fail to start! - internal error ");
        }

    }
	
	/**
     * ChordManagerListener
     */
	public IChordManagerListener mManagerListener = new IChordManagerListener() {

        @Override
        public void onStarted(String nodeName, int reason) {
            /**
             * 4. Chord has started successfully
             */
            bStarted = true;
           // mStart_stop_btn.setText(R.string.stop);
           // mStart_stop_btn.setEnabled(true);

            if (reason == STARTED_BY_USER) {
                // Success to start by calling start() method
              //  mLogView.appendLog("    >onStarted(" + nodeName + ", STARTED_BY_USER)");
            	//myNode = nodeName;
                joinTestChannel();
            } else if (reason == STARTED_BY_RECONNECTION) {
                // Re-start by network re-connection.
              //  mLogView.appendLog("    >onStarted(" + nodeName + ", STARTED_BY_RECONNECTION)");
            }

        }

        @Override
        public void onStopped(int reason) {
            /**
             * 8. Chord has stopped successfully
             */
            bStarted = false;
            //mStart_stop_btn.setText(R.string.start);
            //mStart_stop_btn.setEnabled(true);

            if (STOPPED_BY_USER == reason) {
                // Success to stop by calling stop() method
               // mLogView.appendLog("    >onStopped(STOPPED_BY_USER)");
            } else if (NETWORK_DISCONNECTED == reason) {
                // Stopped by network disconnected
               // mLogView.appendLog("    >onStopped(NETWORK_DISCONNECTED)");
            }
        }

        @Override
        public void onNetworkDisconnected() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
            
        }
    };
    
    protected void joinTestChannel() {
        IChordChannel channel = null;
        
        /**
         * 5. Join my channel
         */
       // mLogView.appendLog("    joinChannel");
        channel = mChordManager.joinChannel(CHANNEL_JOINED, mChannelListener);
        //myChannel = channel.getName();

        if (channel == null) {
          //  mLogView.appendLog("    Fail to joinChannel");
        }
    }
    
    protected void stopChord() {
        if (mChordManager == null)
            return;

        /**
         * If you registered NetworkListener, you should unregister it.
         */
        mChordManager.setNetworkListener(null);

        /**
         * 7. Stop Chord. You can call leaveChannel explicitly.
         * mChordManager.leaveChannel(CHORD_HELLO_TEST_CHANNEL);
         */
        //mLogView.appendLog("    stop");
        mChordManager.stop();
        //mStart_stop_btn.setEnabled(false);

    }

    // ***************************************************
    // ChordChannelListener
    // ***************************************************
    public IChordChannelListener mChannelListener = new IChordChannelListener() {

        /**
         * Called when a node leave event is raised on the channel.
         */
        @Override
        public void onNodeLeft(String fromNode, String fromChannel) {
            //mLogView.appendLog("    >onNodeLeft(" + fromNode + ")");
        }

        /**
         * Called when a node join event is raised on the channel
         */
        @Override
        public void onNodeJoined(String fromNode, String fromChannel) {
            //mLogView.appendLog("    >onNodeJoined(" + fromNode + ")");
        	
            /**
             * 6. Send data to joined node
             */
        	//sendToAll(fromNode, fromChannel);
            System.out.println("A device has joined chord network");
           // mLogView.appendLog("    sendData(" + fromNode + ", " + new String(payload[0]) + ")");
        }

        /**
         * Called when the data message received from the node.
         */
        @Override
        public void onDataReceived(String fromNode, String fromChannel, String payloadType,
                byte[][] payload) {

            /**
             * 6. Received data from other node
             */
        	
        	if(payloadType.equals(HOST_SENDPDF_PAGE_UPDATE)){
        		Integer mPageNum = Integer.parseInt(new String(payload[0]));
        		
        	} else if (payloadType.equals(GUEST_REQUEST_PDFPAGE)){
        		
        	} else if (Integer.parseInt(payloadType) == 2){
        		ServerInterface.syncFiles(currContext);
        		
        	} else {
        		
        	}
        	System.out.println(new String(payload[0]));
        	
            if(payloadType.equals(MESSAGE_NEW_FILE_UPLOADED)){
                //mLogView.appendLog("    >onDataReceived(" + fromNode + ", " + new String( payload[0]) + ")");
            }
        }

        /**
         * The following callBacks are not used in this Fragment. Please refer
         * to the SendFilesFragment.java
         */
        @Override
        public void onMultiFilesWillReceive(String fromNode, String fromChannel, String fileName,
                String taskId, int totalCount, String fileType, long fileSize) {

        }

        @Override
        public void onMultiFilesSent(String toNode, String toChannel, String fileName,
                String taskId, int index, String fileType) {

        }

        @Override
        public void onMultiFilesReceived(String fromNode, String fromChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onMultiFilesFinished(String node, String channel, String taskId, int reason) {

        }

        @Override
        public void onMultiFilesFailed(String node, String channel, String fileName, String taskId,
                int index, int reason) {

        }

        @Override
        public void onMultiFilesChunkSent(String toNode, String toChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, long offset,
                long chunkSize) {

        }

        @Override
        public void onMultiFilesChunkReceived(String fromNode, String fromChannel, String fileName,
                String taskId, int index, String fileType, long fileSize, long offset) {

        }

        @Override
        public void onFileWillReceive(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize) {

        }

        @Override
        public void onFileSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId) {

        }

        @Override
        public void onFileReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, String tmpFilePath) {

        }

        @Override
        public void onFileFailed(String node, String channel, String fileName, String hash,
                String exchangeId, int reason) {

        }

        @Override
        public void onFileChunkSent(String toNode, String toChannel, String fileName, String hash,
                String fileType, String exchangeId, long fileSize, long offset, long chunkSize) {

        }

        @Override
        public void onFileChunkReceived(String fromNode, String fromChannel, String fileName,
                String hash, String fileType, String exchangeId, long fileSize, long offset) {

        }

    };
    
    public void sendToAll(String message, int messageType){
    	byte[][] payload = new byte[1][];
        payload[0] = message.getBytes();
        
        IChordChannel channel = mChordManager.getJoinedChannel(CHANNEL_JOINED);
        //add message type switch here
        
        channel.sendDataToAll(MESSAGE_NEW_FILE_UPLOADED, payload);
    }
    
    public ChordManager getChordManager(){
    	return mChordManager;
    }
}
