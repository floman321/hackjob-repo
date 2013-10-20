/**
 *
 * Sample source code for AllShare Framework SDK
 *
 * Copyright (C) 2013 Samsung Electronics Co., Ltd.
 * All Rights Reserved.
 *
 * @file DevicePicker.java
 */

package net.sf.andpdf.pdfviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sec.android.allshare.Device;
import com.sec.android.allshare.DeviceFinder;
import com.sec.android.allshare.DeviceFinder.IDeviceFinderEventListener;
import com.sec.android.allshare.ERROR;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceConnector.IServiceConnectEventListener;
import com.sec.android.allshare.ServiceConnector.ServiceState;
import com.sec.android.allshare.ServiceProvider;

/**
 * AllShare icon fragment.
 * <p/>
 * Fragment that displays AllShare icon with number of available devices.
 * <p/>
 * If AllShare is connected (active), the icon is blue and clicking on it
 * disconnects the AllShare device.
 * <p/>
 * If AllShare is not connected and there are any available devices,
 * then clicking on the icon displays the device selection dialog and conveys
 * the selected device to host activity.
 * <p/>
 * If there are no detected devices, clicking on icon rescans the network.
 *
 * @version 4
 */
public class DevicePicker extends Fragment implements OnClickListener, IServiceConnectEventListener,
        IDeviceFinderEventListener {

    /**
     * Callback interface for device selection events.
     */
    public interface DevicePickerResult {

        /**
         * User has selected an AllShare device
         *
         * @param device the selected device
         */
        void onDeviceSelected(Device device);

        /**
         * User clicked to disable AllShare
         */
        void onAllShareDisabled();
    }

    /**
     * The type of device we are interested in
     */
    private PickerType mType;

    /**
     * Listener to be notified of events
     */
    private DevicePickerResult mPickerListener;

    /**
     * The AllShare Service reference
     */
    private ServiceProvider mAllShareService;

    /**
     * The ImageView displaying AllShare icon
     */
    private ImageView mIcon;

    /**
     * Flag indicating if AllShare is currently active
     */
    private boolean mActive;

    private String mDeviceId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set view, remember ImageView for icon and setup onclick listener.
        View v = inflater.inflate(R.layout.device_picker, container, false);
        mIcon = (ImageView) v.findViewById(R.id.devicePickerIcon);
        mIcon.setOnClickListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            mDeviceId = savedInstanceState.getString("deviceId");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mDeviceId!=null)
            outState.putString("deviceId", mDeviceId);
    }

    /**
     * @deprecated As of version 2, replaced by {@link com.sec.android.allshare.sample.devicepicker.DevicePicker#setPickerType(PickerType)}.
     *
     * Set the type of device.
     * <p/>
     * This has two effects:
     * <ul>
     * <li>Only devices of this type are counted when displaying number
     * of devices on AllShare icon.
     * <li>Only devices of this type are shown in displayed device select dialog.
     * </ul>
     *
     * @param type The type of device to use
     */
    @Deprecated
    public void setDeviceType(Device.DeviceType type) {
        mType = PickerType.fromDeviceType(type);
    }

    /**
     * Set the type of device picker.
     * <p/>
     * This method replaces {@link com.sec.android.allshare.sample.devicepicker.DevicePicker#setDeviceType(com.sec.android.allshare.Device.DeviceType)}.
     * <p/>
     * This has two effects:
     * <ul>
     * <li>Only devices of this type are counted when displaying number
     * of devices on AllShare icon.
     * <li>Only devices of this type are shown in displayed device select dialog.
     * </ul>
     *
     * @param type The type of device to use
     */
    public void setPickerType(PickerType type) {
        mType = type;
    }

    /**
     * Sets the listener for event notifications.
     *
     * @param listener the new listener
     */
    public void setDeviceSelectedListener(DevicePickerResult listener) {
        mPickerListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // The service provider needs to be created after device type is set
        // It could also be created in onStart or onResume, but we the sooner
        // we create it, the sooner we get devices.
        ServiceConnector.createServiceProvider(getActivity(), this);
    }

    /**
     * Changes the active state of picker.
     * <p/>
     * Picker should be active if AllShare is actively running,
     * i.e. device is connected and used.
     *
     * @param newState new active state
     */
    public void setActive(boolean newState) {
        if (newState == mActive) {
            // No change in state, do nothing
            return;
        }
        mActive = newState;
        mIcon.setImageResource(newState ?
                R.drawable.zoom_in :
                R.drawable.zoom_out); //CHANGE BUTTONS LATER
        
        updateButtonCounter();
    }

    @Override
    public void onClick(View v) {
      /*  if (v != mIcon) {
            return;
        }*/
    	System.out.println("ONCLICK DEVICE PICKER ALLSHARE");

        if (mAllShareService != null) {
            DeviceFinder deviceFinder = mAllShareService.getDeviceFinder();

            int numDevices = PickerDeviceFinder.
                    getDevices(deviceFinder, mType).size();

            // If no devices found, try refreshing the list.
            if (numDevices == 0) {
                deviceFinder.refresh();
            }

            // If we are already active, disable allshare
            if (mActive) {
                setActive(false);
                if (mPickerListener != null) {
                    mPickerListener.onAllShareDisabled();
                }
                return;
            }
        }

        // Devices are available, and we are not connected
        // Ask user to select device
        Intent intent = new Intent(getActivity(), DeviceSelectActivity.class);
        intent.putExtra("deviceType", mType.toString());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onDetach() {
        if (mAllShareService != null) {
            // Disconnect from AllShare Service
            ServiceConnector.deleteServiceProvider(mAllShareService);
            mAllShareService = null;
        }
        super.onDetach();
    }


    ///////////////////////////////////////////////////////////////////////////
    // This methods handle connecting and disconnecting of AllShare Service
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreated(ServiceProvider serviceProvider, ServiceState state) {
        mAllShareService = serviceProvider;
        mAllShareService.getDeviceFinder().
                setDeviceFinderEventListener(mType.toDeviceType(), this);
        mAllShareService.getDeviceFinder().refresh();
        updateButtonCounter();
        if(mDeviceId!=null && mPickerListener != null) {
            Device d = mAllShareService.getDeviceFinder().getDevice(mDeviceId, mType.toDeviceType());
            if(d!=null){
                mPickerListener.onDeviceSelected(d);
                setActive(true);
            }
        }
    }

    @Override
    public void onDeleted(ServiceProvider serviceProvider) {
        if (mAllShareService == serviceProvider) {
            mAllShareService.getDeviceFinder().
                    setDeviceFinderEventListener(mType.toDeviceType(), null);
            mAllShareService = null;
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // These methods handle devices appearing and disappearing in network
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDeviceAdded(Device.DeviceType deviceType, Device device, ERROR state) {
        // We aren't interested in individual devices, only in their number
        updateButtonCounter();
    }

    @Override
    public void onDeviceRemoved(Device.DeviceType deviceType, Device device, ERROR state) {
        // We aren't interested in individual devices, only in their number
        updateButtonCounter();
        //if current device has been removed
        if (device.getID().equals(mDeviceId)) {
            setActive(false);
            if (mPickerListener != null) {
                mPickerListener.onAllShareDisabled();
            }
        }
    }

    /**
     * Methods that selects which icon to display, based on number of
     * available devices in network.
     */
    private void updateButtonCounter() {
        if (mAllShareService == null) {
            // AllShare service is disconnected, no devices available
            mIcon.getDrawable().setLevel(0);
            return;
        }

        int numDevices =
                PickerDeviceFinder.getDevices(mAllShareService.getDeviceFinder(), mType).size();

        mIcon.getDrawable().setLevel(numDevices);
        if (numDevices==0) {
            setActive(false);
        }
    }

    /**
     * Callback when user has selected device in device select activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mDeviceId = data.getStringExtra("deviceId");
            Device.DeviceType type = Device.DeviceType.valueOf(data.getStringExtra("deviceType"));

            if (mAllShareService != null && mPickerListener != null) {
                Device d = mAllShareService.getDeviceFinder().getDevice(mDeviceId, type);
                if(d!=null){
                    mPickerListener.onDeviceSelected(d);
                    setActive(true);
                }
            }
        }
    }
}
