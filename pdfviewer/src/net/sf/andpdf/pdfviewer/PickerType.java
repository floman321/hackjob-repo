package net.sf.andpdf.pdfviewer;

import com.sec.android.allshare.Device;

/**
 * Device picker device types.
 */
public enum PickerType {
    IMAGE_VIEWER,
    IMAGE_VIEWER_WITH_VIEW_CONTROLLER,
    AV_PLAYER,
    PROVIDER,
    FILE_RECEIVER,
    TV_CONTROLLER,
    SLIDE_SHOW_PLAYER,
    UNKNOWN;

    public static PickerType fromDeviceType(final Device.DeviceType deviceType) {
        switch (deviceType) {
            case DEVICE_IMAGEVIEWER:
                return IMAGE_VIEWER;
            case DEVICE_AVPLAYER:
                return AV_PLAYER;
            case DEVICE_PROVIDER:
                return PROVIDER;
            case DEVICE_FILERECEIVER:
                return FILE_RECEIVER;
            case DEVICE_TV_CONTROLLER:
                return TV_CONTROLLER;
            case DEVICE_SLIDESHOWPLAYER:
                return SLIDE_SHOW_PLAYER;
            default:
                return UNKNOWN;
        }
    }

    public Device.DeviceType toDeviceType() {
        switch (this) {
            case IMAGE_VIEWER:
                return Device.DeviceType.DEVICE_IMAGEVIEWER;
            case IMAGE_VIEWER_WITH_VIEW_CONTROLLER:
                return Device.DeviceType.DEVICE_IMAGEVIEWER;
            case AV_PLAYER:
                return Device.DeviceType.DEVICE_AVPLAYER;
            case PROVIDER:
                return Device.DeviceType.DEVICE_PROVIDER;
            case FILE_RECEIVER:
                return Device.DeviceType.DEVICE_FILERECEIVER;
            case TV_CONTROLLER:
                return Device.DeviceType.DEVICE_TV_CONTROLLER;
            case SLIDE_SHOW_PLAYER:
                return Device.DeviceType.DEVICE_SLIDESHOWPLAYER;
            default:
                return Device.DeviceType.UNKNOWN;
        }
    }
}
