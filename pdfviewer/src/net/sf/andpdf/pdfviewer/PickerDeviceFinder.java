package net.sf.andpdf.pdfviewer;

import com.sec.android.allshare.Device;
import com.sec.android.allshare.DeviceFinder;
import com.sec.android.allshare.media.ImageViewer;

import java.util.ArrayList;
import java.util.List;

final class PickerDeviceFinder {
    // Suppress default constructor for noninstantiability
    private PickerDeviceFinder() {}

    /**
     * Helper method that returns list of devices that match given picker type.
     *
     * Purpose of this method is to encapsulate deviceFinder.getDevices(..) method to unify device
     * finding between different classes.
     *
     * Used internally in package.
     *
     * @param deviceFinder AllShare device finder instance
     * @param pickerType Type of picker for which devices will be returned
     * @return Not null list of devices
     */
    static List<Device> getDevices(final DeviceFinder deviceFinder,
                                   final PickerType pickerType) {
        List<Device> devices = new ArrayList<Device>();

        if (deviceFinder != null) {
            List<Device> foundDevices = deviceFinder.getDevices(pickerType.toDeviceType());

            if (foundDevices != null) {
                if (pickerType.equals(PickerType.IMAGE_VIEWER_WITH_VIEW_CONTROLLER)) {
                    for (Device foundDevice : foundDevices) {
                        ImageViewer imageViewer = (ImageViewer) foundDevice;

                        if (imageViewer.getViewController() != null) {
                            devices.add(imageViewer);
                        }
                    }
                }
                else {
                    devices = foundDevices;
                }
            }
        }

        return devices;
    }
}
