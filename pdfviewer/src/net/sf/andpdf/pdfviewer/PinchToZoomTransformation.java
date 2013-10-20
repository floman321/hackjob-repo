/**
 *
 * Sample source code for AllShare Framework SDK
 *
 * Copyright (C) 2012 Samsung Electronics Co., Ltd.
 * All Rights Reserved.
 *
 * @file PinchToZoomTransformation.java
 * @date January 29, 2013
 *
 */

package net.sf.andpdf.pdfviewer;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;


/**
 * Class abstracts pinch-to-zoom matrix transformations.
 */
public class PinchToZoomTransformation {
    private final int mImageWidth;
    private final int mImageHeight;
    private final int mViewPortWidth;
    private final int mViewPortHeight;
    
    private float mMaxScale = Float.MAX_VALUE;
    
    private Matrix mCurrentMatrix;
    private int mCurrentRotation = 0;

    public enum RotationDirection {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }

    public PinchToZoomTransformation(int imageWidth, int imageHeight, int viewPortWidth, int viewPortHeight) {
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mViewPortWidth = viewPortWidth;
        mViewPortHeight = viewPortHeight;

        mCurrentMatrix = new Matrix();
    }

    /**
     * Returns maximum scale.
     *
     * @return Current maximum scale.
     */
    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * Sets maximum scale (relative to scale when image fits view port bounds).
     */
    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
    }

    /**
     * Returns current matrix.
     *
     * @return Current transformation matrix.
     */
    public Matrix getMatrix() {
        return mCurrentMatrix;
    }

    /**
     * Returns current rotation.
     *
     * @return Current rotation.
     */
    public int getRotation() {
        return mCurrentRotation;
    }

    /**
     * Returns rectangle that represent image with matrix transformations applied.
     *
     * @return Rectangle that represent image with matrix transformations applied.
     */
    public RectF getRect() {
        RectF currentImageRect = new RectF(0, 0, mImageWidth, mImageHeight);
        mCurrentMatrix.mapRect(currentImageRect);

        return currentImageRect;
    }

    /**
     * Sets current matrix scale and translation to fit image in center.
     */
    public void setScaleToFitInCenter() {
        setScaleInCenter(1);
    }

    /**
     * Changes current transformation by given scale factor at given point within bounds.
     *
     * Scaling transformation is relative to current scale.
     *
     * Scaling at point other than origin (0, 0) in most cases results in translate transformation applied.
     * 
     * Transformations are limited by minimum/maximum scale and view port bounds.
     * They are adjusted in a way that they never result in exceeding limits.
     * In corner cases this means that even if scale factor is different than zero no scaling may occur.
     *
     * Returned value is a scale factor that was applied after limits check.
     *
     * @param scaleFactor Scale factor, i.e. delta of scale.
     *                    Positive values result in respectively bigger magnification.
     *                    Negative values result in respcetively smaller magnification.
     *                    Zero value results in no transformation applied.
     * @param scalePointX X coordinate of scale point.
     * @param scalePointY Y coordinate of scale point.
     * @return Delta of scale that was applied to matrix after bounds check.
     */
    public float updateScaleInBounds(float scaleFactor, float scalePointX, float scalePointY) {
        float lastScale = getMatrixScale(mCurrentMatrix);

        mCurrentMatrix.postScale(scaleFactor, scaleFactor, scalePointX, scalePointY);

        RectF currentRect = new RectF(0, 0, mImageWidth, mImageHeight);
        mCurrentMatrix.mapRect(currentRect);

        // bounds (limits) check

        if (currentRect.width() < mViewPortWidth && currentRect.height() < mViewPortHeight) { // image is smaller that viewport
            setScaleToFitInCenter();
        }
        else if (currentRect.width() > mMaxScale * mViewPortWidth || currentRect.height() > mMaxScale * mViewPortHeight) { // image is bigger that viewport
            setScaleInCenter(mMaxScale);
        }
        else {
            if (currentRect.width() >= mViewPortWidth) {
                // translations removes left and right margins that may appear when scaling
                mCurrentMatrix.postTranslate(- Math.max(0, currentRect.left), 0); // left edge
                mCurrentMatrix.postTranslate(Math.max(0, mViewPortWidth - currentRect.right), 0); // right edge
            }
            else {
                // center horizontally
                mCurrentMatrix.postTranslate((mViewPortWidth - currentRect.width()) / 2 - currentRect.left, 0);
            }

            if (currentRect.height() >= mViewPortHeight) {
                // translations removes top and bottom margins that may appear when scaling
                mCurrentMatrix.postTranslate(0, - Math.max(0, currentRect.top)); // top edge
                mCurrentMatrix.postTranslate(0, Math.max(0, mViewPortHeight - currentRect.bottom)); // bottom edge
            }
            else {
                // center vertically
                mCurrentMatrix.postTranslate(0, (mViewPortHeight - currentRect.height()) / 2 - currentRect.top);
            }
        }

        float currentScale = getMatrixScale(mCurrentMatrix);
        return currentScale / lastScale;
    }

    /**
     * Resets all image transformations.
     */
    public void reset() {
        mCurrentRotation = 0;

        setScaleToFitInCenter();
    }

    /**
     * Changes current rotation in given direction by 90 degrees angle.
     *
     * Roataion is relative to current rotation angle.
     */
    public void updateRotationAndFitInBounds(RotationDirection rotationDirection) {
        mCurrentRotation += rotationDirection == RotationDirection.CLOCKWISE ? 90 : -90;
        if (mCurrentRotation < 0) {
            mCurrentRotation += 360;
        }
        mCurrentRotation %= 360;

        setScaleToFitInCenter();
    }

    private float getMatrixScale(Matrix matrix) {
        RectF currentRect = getRect();

        return currentRect.width() / mImageWidth;
    }

    private void setScaleInCenter(float scale) {
        float fitX = scale * (mCurrentRotation % 180 == 0 ? mViewPortWidth : mViewPortHeight);
        float fitY = scale * (mCurrentRotation % 180 == 0 ? mViewPortHeight : mViewPortWidth);

        RectF drawableRect = new RectF(0, 0, mImageWidth, mImageHeight);
        RectF viewRect = new RectF(0, 0, fitX, fitY);
        mCurrentMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

        mCurrentMatrix.postTranslate((mViewPortWidth - fitX) / 2, (mViewPortHeight - fitY) / 2);

        mCurrentMatrix.postRotate(mCurrentRotation, mViewPortWidth / 2.0f, mViewPortHeight / 2.0f);
    }

    private static void log(String message) {
        Log.d(PinchToZoomTransformation.class.getName(), message);
    }
}
