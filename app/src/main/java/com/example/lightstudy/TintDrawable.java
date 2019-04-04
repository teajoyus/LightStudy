package com.example.lightstudy;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

/**
 * Author:mihon
 * Time: 2019\4\3 0003.14:54
 * Description:This is TintDrawable
 */
public class TintDrawable extends BitmapDrawable {
    private static final String TAG = "TintDrawable";
    private int tint;
    private Drawable drawable;

    public TintDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void setTint(int tint) {
        this.tint = tint;
        update(tint);
        Log.i(TAG, "setTint tint:" + Integer.toHexString(tint));
    }

    public int getTint() {
        Log.i(TAG, "getTint tint:" + Integer.toHexString(tint));
        return tint;
    }

    public void update(int tint) {
//        Log.i(TAG,"SDK:"+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(tint);
        }
    }
}
