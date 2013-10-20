/**
 *
 * Sample source code for AllShare Framework SDK
 *
 * Copyright (C) 2012 Samsung Electronics Co., Ltd.
 * All Rights Reserved.
 *
 * @file AllShareService.java
 * @date February 1, 2013
 *
 */


package net.sf.andpdf.pdfviewer;

import android.content.Context;
import android.util.Log;
import com.sec.android.allshare.Device;
import com.sec.android.allshare.Device.DeviceType;
import com.sec.android.allshare.DeviceFinder;
import com.sec.android.allshare.ERROR;
import com.sec.android.allshare.Item;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceConnector.IServiceConnectEventListener;
import com.sec.android.allshare.ServiceConnector.ServiceState;
import com.sec.android.allshare.ServiceProvider;
import com.sec.android.allshare.app.VideoPlayer;
import com.sec.android.allshare.media.AVPlayer;
import com.sec.android.allshare.media.AVPlayer.AVPlayerState;
import com.sec.android.allshare.media.AVPlayer.IAVPlayerEventListener;
import com.sec.android.allshare.media.ContentInfo;
import com.sec.android.allshare.media.ImageViewer;
import com.sec.android.allshare.media.ViewController;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Abstracts and unifies getting of AllShare service provider.
 */
public class AllShareService {
    private static final Device.DeviceType DEVICE_TYPE = Device.DeviceType.DEVICE_IMAGEVIEWER;

    private static AllShareService mInstance;

    private Context mContext;

    private String mImageDeviceId;
    private String mVideoDeviceId;
    private String mImagePath;

    private ServiceProvider mServiceProvider = null;
    private ImageViewer mImageViewer; // remote AllShare device used to show images
    private ViewController mViewController; // allows to control image on remote device
    private AVPlayer mVideoPlayer;
   

    private ServiceProviderListener mServiceProviderListener;
    private ImageViewerListener mImageViewerListener;
    private ViewControllerListener mViewControllerListener;
    
    private IAVPlayerEventListener mVideoPlayerListener;
    

    private boolean mIsImageViewerStarted = false;
    private boolean mIsImageViewerReady = false; // true if ImageViewer is ready for interaction
    
    boolean mIsVideoViewerStarted = false;
    boolean mIsVideoViewerReady = false; // true if ImageViewer is ready for interaction
    
    private WeakReference<Listener> mListener;

    boolean playing = false;
    
    public ServiceProvider getServiceProvider()
	{
		return mServiceProvider;
	}
    public ImageViewer getImageViewer()
	{
		return mImageViewer;
	}
    
    public AllShareService(Context context)
	{
    	  mContext = context;
        ERROR err = ServiceConnector.createServiceProvider(context, new IServiceConnectEventListener(){     
        	  
    	  @Override 
    	  public void onDeleted(ServiceProvider provider) 
    	  { 
    	// Framework Service is disconnected by application or service problem. 
    		  System.out.println("ALLSHARE SERVICE NOT CONNECTED");
    	  }
			@Override
			public void onCreated(ServiceProvider serviceProvider, ServiceState arg1)
			{
				mServiceProvider = serviceProvider;
				System.out.println("ALLSHARE SERVICE CONNECTED");
				
				System.out.println("ALLSHARE LOOKING FOR DEVICES");
				DeviceFinder df = mServiceProvider.getDeviceFinder();
				ArrayList<Device> ImageDeviceList = df.getDevices(DeviceType.DEVICE_IMAGEVIEWER);
				ArrayList<Device> VideoDeviceList = df.getDevices(DeviceType.DEVICE_AVPLAYER);
				
				System.out.println("ALLSHARE LOOKING FOR DEVICES deviceList.SiZe " +ImageDeviceList.size());
				System.out.println("ALLSHARE LOOKING FOR DEVICES deviceList.SiZe " +VideoDeviceList.size());
				
				int i = 0;
				while((ImageDeviceList.isEmpty() || VideoDeviceList.isEmpty()) && i < 500)
				{
					df.refresh();
					ImageDeviceList = df.getDevices(DeviceType.DEVICE_IMAGEVIEWER);
					VideoDeviceList = df.getDevices(DeviceType.DEVICE_AVPLAYER);
					i++;
				}
				System.out.println("ALLSHARE LOOKING FOR DEVICES deviceList.SiZe AFTER " +ImageDeviceList.size());
				System.out.println("ALLSHARE LOOKING FOR DEVICES deviceList.SiZe AFTER " +VideoDeviceList.size());
				
				if(!ImageDeviceList.isEmpty())
				{
					mImageViewer = (ImageViewer) ImageDeviceList.get(0);
				}
				if(!VideoDeviceList.isEmpty())
				{
					System.out.println("Assigning Device to mVideoPlayer" );
					mVideoPlayer = (AVPlayer) VideoDeviceList.get(0);
				}
				
			} 
        }); 
        
        if( err == ERROR.FRAMEWORK_NOT_INSTALLED) 
        { 
        	System.out.println("ALLSHAREFRAMEWORK NOT INSTALLED");
        }
        else if( err == ERROR.INVALID_ARGUMENT ) 
        { 
        	System.out.println("ALLSHAREFRAMEWORK INVALID ARGUMENT");
        } 
        else 
        { 
        	mInstance = this;
        	System.out.println("ALLSHAREFRAMEWORK INSTALLED");
        } 
       
        
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static interface Listener {
        void onImageReady();
        void onDisconnected();
    }

    public static AllShareService getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void registerListener(Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must be not null");
        }
        mListener = new WeakReference<Listener>(listener);
    }

    public void unregisterListener() {
        mListener = null;
    }

    public void start(String deviceId, String imagePath) {
        if (deviceId == null || imagePath == null) {
            throw new IllegalArgumentException("All arguments must be not null");
        }

        if (mIsImageViewerStarted && deviceId.equals(mImageDeviceId) && imagePath.equals(mImagePath)) {
            if (getListener() != null) {
                getListener().onImageReady();
            }
        }
        else {
            mIsImageViewerStarted = true;
            mImageDeviceId = deviceId;
            mImagePath = imagePath;
            mServiceProviderListener = new ServiceProviderListener();
            ServiceConnector.createServiceProvider(mContext, mServiceProviderListener);
        }
    }
    
    public void start(String imagePath) 
    {
        if (imagePath == null) 
        {
            throw new IllegalArgumentException("All arguments must be not null");
        }
            mIsImageViewerStarted = true;           
            mImagePath = imagePath;
            
            if(mServiceProviderListener==null)
            {
	            mServiceProviderListener = new ServiceProviderListener();
	            ServiceConnector.createServiceProvider(mContext, mServiceProviderListener);
            }
        
            Item item = new Item.LocalContentBuilder(mImagePath,"image/jpeg").setTitle("Test").build();
            
            if(item != null && mImageViewer != null)
            {            
            	mImageViewer.show(item, null);
            }      
     }
    
    public void startVideo(String videoPath) 
    {
        if (videoPath == null) 
        {
            throw new IllegalArgumentException("All arguments must be not null");
        }
        
        if(!mIsVideoViewerStarted)
        {
           mIsVideoViewerStarted = true;           
       //     mImagePath = videoPath;
            
            if(mServiceProviderListener==null)
            {
	            mServiceProviderListener = new ServiceProviderListener();
	            ServiceConnector.createServiceProvider(mContext, mServiceProviderListener);
            }
        
            
            String mimetype = "";
            String fileExtension = videoPath.substring(videoPath.indexOf('.')+1).toLowerCase();
            System.out.println("FileExtention " + fileExtension);
            //TODO add TOASTS FOR UNSUPPORTED FILE TYPES
            if(fileExtension.contains("mp3")||fileExtension.contains("mpeg"))
            {
            	mimetype = "video/mpeg";
            }
            else if(fileExtension.contains("mp4"))
            {
            	mimetype = "video/mp4";
            }
            else if(fileExtension.contains("flv"))
            {
            	mimetype = "video/x-flv";
            }
            else if(fileExtension.contains("avi"))
            {
            	mimetype = "video/avi";
            }
            else
            {
            	mimetype = "video/mpeg";
            }
            
            
            
            Item item = new Item.LocalContentBuilder(videoPath,"audio/mpeg").setTitle("Test").build();
            System.out.println("Attempting to play " + videoPath + " of mimetype " + mimetype);
            
            if(item == null)
            {
            	 System.out.println("Item null");
            }
            
            if(mVideoPlayer == null)
            {
            	System.out.println("mVideoPlayer null");       	
            }
            
            if(item != null && mVideoPlayer != null)
            {     
            	System.out.println("Playing " + videoPath);
            	ContentInfo.Builder builder = new ContentInfo.Builder();
         		builder.setStartingPosition(0);
         		ContentInfo info = builder.build();
            	mVideoPlayer.play(item, info);
            	mVideoPlayer.setVolume(20);            	
            }      
        }
        else
        {
        	mVideoPlayer.setVolume(20);
        	mVideoPlayer.resume();
        }
     }
    

    
    public void pauseVideo()
    {
		mVideoPlayer.pause();    	
    }
    
    
 

    public void stop() {
        mIsImageViewerReady = false;
        mIsImageViewerStarted = false;

        if (mImageViewer != null) {
            mImageViewer.stop();
        }
        
        mIsVideoViewerReady = false;
        mIsVideoViewerStarted = false;

        if (mVideoPlayer != null) {
        	mVideoPlayer.stop();
        }

        disconnect();
    }

    public ViewController getViewController() {
        return mViewController;
    }

    public boolean isImageReady() {
        return mIsImageViewerReady;
    }

    private class ServiceProviderListener implements IServiceConnectEventListener {

        @Override
        public void onCreated(ServiceProvider serviceProvider, ServiceState serviceState) {
            mServiceProvider = serviceProvider;
            System.out.println("ServiceProviderListener connecting ALLSHARE");
            
            registerImageViewerListener();
            registerVideoPlayerListener();
            
            
            mViewController = mImageViewer.getViewController();

            if (mViewController != null) {
                registerViewControllerListener();
                // connects to ViewController
                // on successful connection corresponding listener will be called
                mViewController.connect();
            }
            
            
            
          //  connect();
        }
        private void registerVideoPlayerListener()
		{
        	 if (mVideoPlayer != null && mVideoPlayerListener == null)
        	 {
        		 mVideoPlayerListener = new VideoPlayerListener();
        		 mVideoPlayer.setEventListener(mVideoPlayerListener);
        	 }
             
			
		}
		@Override
        public void onDeleted(ServiceProvider serviceProvider) {
            mServiceProvider = null;
        }

    }
    /**
     * Listener responsible for handling ImageViewer events
     */
    private class ImageViewerListener implements ImageViewer.IImageViewerEventListener {
        @Override
        public void onDeviceChanged(ImageViewer.ImageViewerState imageViewerState, ERROR error) {
            switch (imageViewerState) {
                case BUFFERING:
                	System.out.println("ImageViewerListener BUFFERING");
                    break;
                case SHOWING:
                    if (mIsImageViewerStarted && !mIsImageViewerReady) {
                        mIsImageViewerReady = true;
                        if (getListener() != null) {
                            getListener().onImageReady();
                        }
                    }
                    System.out.println("ImageViewerListener SHOWING");
                    break;
                case CONTENT_CHANGED:
                	System.out.println("ImageViewerListener CONTENT_CHANGED");
                    disconnect();
                    break;
            }
        }
    };
    
    private class VideoPlayerListener implements AVPlayer.IAVPlayerEventListener {
		@Override
		public void onDeviceChanged(AVPlayerState state, ERROR arg1)
		{
			switch(state)
			{
			case BUFFERING:
            	System.out.println("AVPlayerState BUFFERING");
                break;
            case PLAYING:
                if (mIsVideoViewerStarted && !mIsVideoViewerReady) {
                	mIsVideoViewerReady = true;
                    
                }
                System.out.println("AVPlayerState PLAYING");
                break;
			}
			
		}
    };

    /**
     * Listener responsible for handling ViewController connect and disconnect events.
     */
    private class ViewControllerListener implements  ViewController.IResponseListener {
        @Override
        public void onConnectResponseReceived(
                ViewController viewController, ERROR result) {
        	
        	System.out.println("ViewControllerListner Response mimageviwereistarted " + mIsImageViewerStarted + " ready " + mIsImageViewerReady);
            if (mIsImageViewerStarted) {
                // creates AllShare Item from image URI
                Item item =
                        new Item.LocalContentBuilder(mImagePath,"image/png").
                        setTitle("Test").
                        build();

                // shows image on ImageViewer device
                mImageViewer.show(item, null);
            }
        }

        @Override
        public void onDisconnectResponseReceived(
                ViewController viewController, ERROR result) {
            mIsImageViewerReady = false;
            if (getListener() != null) {
                getListener().onDisconnected();
            }
        }
    };

     void connect() {
        if (mServiceProvider != null && mIsImageViewerStarted) {
            // gets ImageViewer Device selected earlier in device selection Activity
            DeviceFinder deviceFinder = mServiceProvider.getDeviceFinder();
            mImageViewer =
                    (ImageViewer) deviceFinder.getDevice(mImageDeviceId, DEVICE_TYPE);

            if (mImageViewer != null) {
                registerImageViewerListener();
                mViewController = mImageViewer.getViewController();

                if (mViewController != null) {
                    registerViewControllerListener();
                    // connects to ViewController
                    // on successful connection corresponding listener will be called
                    mViewController.connect();
                }
            }
        }
    }

     void disconnect() {
        if (mViewController != null) {
            mViewController.disconnect();
        }

        unregisterImageViewerListener();
        unregisterViewControllerListener();

        ServiceConnector.deleteServiceProvider(mServiceProvider);
    }

    private void registerImageViewerListener() {
        if (mImageViewer != null && mImageViewerListener == null) {
            mImageViewerListener = new ImageViewerListener();
            mImageViewer.setEventListener(mImageViewerListener);
        }
    }

    private void registerViewControllerListener() {
        if (mViewController != null && mViewControllerListener == null) {
            mViewControllerListener = new ViewControllerListener();
            mViewController.setResponseListener(mViewControllerListener);
        }
    }

    private void unregisterImageViewerListener() {
        if (mImageViewer != null) {
            mImageViewer.setEventListener(null);
            mImageViewerListener = null;
        }
    }

    private void unregisterViewControllerListener() {
        if (mViewController != null) {
            mViewController.setEventListener(null);
            mViewControllerListener = null;
        }
    }

    private Listener getListener() {
        if (mListener == null) {
            return null;
        }

        return mListener.get();
    }

    @SuppressWarnings("UnusedDeclaration")
    private static void log(String message) {
        Log.d(AllShareService.class.getSimpleName(), message);
    }
}
