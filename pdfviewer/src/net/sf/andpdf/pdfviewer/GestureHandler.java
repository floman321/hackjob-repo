/**
 *
 * Sample source code for AllShare Framework SDK
 *
 * Copyright (C) 2012 Samsung Electronics Co., Ltd.
 * All Rights Reserved.
 *
 * @file GestureHandler.java
 * @date January 29, 2013
 *
 */

package net.sf.andpdf.pdfviewer;

import android.content.Context;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

/**
 * Handles scale gestures.
 *
 * When scaling occurs GestureHandler.Listener.onScale() is called.
 * When translation occurs GestureHandler.Listener.onTranslate() is called.
 */
public class GestureHandler extends SimpleOnScaleGestureListener {
    private Listener mListener;
    private ScaleEvent mScaleEvent;

    ScaleGestureDetector mScaleGestureDetector;

    public GestureHandler(Context context, Listener listener) {
        mListener = listener;
        mScaleEvent = new ScaleEvent();

        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        return true;
    }

    public static interface Listener {
        void onScale(ScaleEvent event);
    }

    public static class ScaleEvent {
        float mScaleFactor = 1;
        float mScaleX;
        float mScaleY;

        public float getScaleFactorDelta() {
            return mScaleFactor;
        }

        public float getScalePointX() {
            return mScaleX;
        }

        public float getScalePointY() {
            return mScaleY;
        }

        void reset() {
            mScaleFactor = 1;
            mScaleX = 0;
            mScaleY = 0;
        }

        @Override
        public String toString() {
            return "ScaleEvent::scaleFactorDelta: " + mScaleFactor + ", ScaleEvent::scalePointX:" + mScaleX + ", ScaleEvent::scalePointY:" + mScaleY;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mScaleEvent.mScaleX = detector.getFocusX();
        mScaleEvent.mScaleY = detector.getFocusY();
        mScaleEvent.mScaleFactor = detector.getScaleFactor();

        mListener.onScale(mScaleEvent);

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
    }

    private static void log(String message) {
        Log.d(GestureHandler.class.getName(), message);
    }
}
