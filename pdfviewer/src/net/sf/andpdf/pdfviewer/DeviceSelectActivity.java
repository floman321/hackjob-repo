/**
 *
 * Sample source code for AllShare Framework SDK
 *
 * Copyright (C) 2013 Samsung Electronics Co., Ltd.
 * All Rights Reserved.
 *
 * @file DeviceSelectActivity.java
 *
 */

package net.sf.andpdf.pdfviewer;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sec.android.allshare.Device;
import com.sec.android.allshare.Device.DeviceType;
import com.sec.android.allshare.DeviceFinder.IDeviceFinderEventListener;
import com.sec.android.allshare.ERROR;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceConnector.IServiceConnectEventListener;
import com.sec.android.allshare.ServiceConnector.ServiceState;
import com.sec.android.allshare.ServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity enabling user to select a remote AllShare device for playback.
 */
public class DeviceSelectActivity extends ListActivity implements IServiceConnectEventListener, IDeviceFinderEventListener, OnClickListener {

    /**
     * Reference to AllShare Service
     */
    private ServiceProvider mAllshareService = null;

    /**
     * Current list of AllShare devices
     */
    private List<Device> mDevices = new ArrayList<Device>();

    /**
     * Type of AllShare devices that should be presented to user
     */
    private PickerType mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = PickerType.valueOf(getIntent().getStringExtra("deviceType"));

        setContentView(R.layout.device_select);

        TextView noDevicesTextView = (TextView) findViewById(R.id.no_devices);
        noDevicesTextView.setVisibility(View.GONE);

        if (isFrameworkInstalled()) {
            noDevicesTextView.setText(R.string.device_picker_no_devices);
        } else {
            noDevicesTextView.setText(R.string.device_picker_framework_not_installed);
            findViewById(R.id.refresh_button).setVisibility(View.GONE);
        }

        getListView().setEmptyView(noDevicesTextView);

        setListAdapter(new DevicesAdapter());

        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.refresh_button).setOnClickListener(this);

        ServiceConnector.createServiceProvider(this, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disconnect from AllShare Service
        ServiceConnector.deleteServiceProvider(mAllshareService);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
		if (id == R.id.cancel_button)
		{
			// Finish activity without returning result.
			finish();
		}
		else if (id == R.id.refresh_button)
		{
			// Rescan network for AllShare devices.
			mAllshareService.getDeviceFinder().refresh();
		}
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Device device = mDevices.get(position);
        Intent ret = new Intent();
        ret.putExtra("deviceId", device.getID());
        ret.putExtra("deviceName", device.getName());
        ret.putExtra("deviceType", device.getDeviceType().toString());
        setResult(RESULT_OK, ret);
        finish();
    }


    ///////////////////////////////////////////////////////////////////////////
    // This methods handle connecting and disconnecting of AllShare Service
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreated(ServiceProvider service, ServiceState state) {
        mAllshareService = service;

        // Listen for new devices
        service.getDeviceFinder().setDeviceFinderEventListener(mType.toDeviceType(), this);

        // Search for new devices (once)
        service.getDeviceFinder().refresh();

        // Show initial device list
        refreshDevicesList();
    }

    @Override
    public void onDeleted(ServiceProvider service) {
        // Remove listeners so that no references remain
        // and GC can collect the service and this activity.
        mAllshareService.getDeviceFinder().setDeviceFinderEventListener(mType.toDeviceType(), null);
        mAllshareService = null;
    }


    ///////////////////////////////////////////////////////////////////////////
    // This methods handle devices appearing and disappearing in network
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDeviceAdded(DeviceType type, Device device, ERROR error) {
        refreshDevicesList();
    }

    @Override
    public void onDeviceRemoved(DeviceType type, Device device, ERROR error) {
        refreshDevicesList();
    }

    /**
     * Refresh the list of displayed devices.
     */
    private void refreshDevicesList() {
        mDevices = PickerDeviceFinder.getDevices(mAllshareService.getDeviceFinder(), mType);
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     * Adapter for displaying devices in a list.
     */
    private class DevicesAdapter extends BaseAdapter {
        /**
         * Cache for device icons
         */
        private LruCache<Uri, Bitmap> mIconsCache;

        public DevicesAdapter() {
            // We create a 1MB cache for icons, with item size being the bitmap size in bytes.
            mIconsCache = new LruCache<Uri, Bitmap>(1024 * 1024) {
                @Override
                protected int sizeOf(Uri key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Device getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.device_item, null);
            }

            TextView label = (TextView) convertView.findViewById(R.id.deviceName);
            ImageView icon = (ImageView) convertView.findViewById(R.id.deviceIcon);

            Device device = mDevices.get(position);

            label.setText(device.getName());

            Uri iconPath = device.getIcon();
            icon.setTag(iconPath);
            if (iconPath != null) {
                Bitmap b = mIconsCache.get(iconPath);
                if (b == null) {
                    // Clear the image so we don't display stale icon.
                    icon.setImageResource(R.drawable.pdf);
                    new IconLoader(icon).execute(iconPath);
                } else {
                    icon.setImageBitmap(b);
                }
            } else {
                icon.setImageResource(R.drawable.pdf);
            }

            return convertView;
        }

        private class IconLoader extends AsyncTask<Uri, Void, Bitmap> {
            private final ImageView mImageView;

            IconLoader(ImageView imageView) {
                mImageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(Uri... params) {
                InputStream in = null;
                try {
                    URL url = new URL(params[0].toString());
                    URLConnection conn = url.openConnection();
                    in = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);

                    // Add the bitmap to cache.
                    if (mIconsCache != null) {
                        mIconsCache.put(params[0], bitmap);
                    }
                    //return the bitmap only if target image view is still valid
                    if(params[0].equals(mImageView.getTag()))
                        return bitmap;
                    else
                        return null;
                } catch (IOException e) {
                    // Failed to retrieve icon, ignore it
                    return null;
                }finally{
                    if(in!=null)
                        try { in.close(); } catch (IOException e) {}
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null && mIconsCache != null) {
                    mImageView.setImageBitmap(result);
                }
            }
        }
    }

    /**
     * Returns true if AllShare Framework is installed on device.
     */
    private boolean isFrameworkInstalled() {
        ERROR status = ServiceConnector.createServiceProvider(this, new IServiceConnectEventListener() {
            @Override
            public void onCreated(ServiceProvider serviceProvider, ServiceState serviceState) {
                // we don't need this service provider
                ServiceConnector.deleteServiceProvider(serviceProvider);
            }

            @Override
            public void onDeleted(ServiceProvider serviceProvider) {
            }
        });

        return status != ERROR.FRAMEWORK_NOT_INSTALLED;
    }


}
