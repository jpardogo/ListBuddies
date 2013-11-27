package com.jpardogo.android.listbuddies.Utils;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class ScaleToFitWidhtHeigthTransform implements Transformation {

    private int mSize;
    private boolean isHeightScale;

    public ScaleToFitWidhtHeigthTransform(int size, boolean isHeightScale) {
        mSize = size;
        this.isHeightScale = isHeightScale;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        float scale;
        int newSize;
        Bitmap scaleBitmap;
        if (isHeightScale) {
            scale = (float) mSize / source.getHeight();
            newSize = Math.round(source.getWidth() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, newSize, mSize, true);
        } else {
            scale = (float) mSize / source.getWidth();
            newSize = Math.round(source.getHeight() * scale);
            scaleBitmap = Bitmap.createScaledBitmap(source, mSize, newSize, true);
        }

        if (scaleBitmap != source) {
            source.recycle();
        }

        return scaleBitmap;

    }

    @Override
    public String key() {
        return "scaleRespectRatio";
    }
}