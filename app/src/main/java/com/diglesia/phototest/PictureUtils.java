package com.diglesia.phototest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

public class PictureUtils {

    // Generate a bitmap from the path, downsampled by a power of 2, so that the result remains larger width than destWidth and larger height than destHeight
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Read in dims from disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        //figure out how much to scale by
        //see https://developer.android.com/topic/performance/graphics/load-bitmap.html
        int inSampleSize = 1; // downsample
        if (srcHeight > destHeight || srcWidth > destWidth) {

            final int halfHeight = srcHeight / 2;
            final int halfWidth = srcWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= destHeight
                    && (halfWidth / inSampleSize) >= destWidth) {
                inSampleSize *= 2;
            }
        }

        Log.i("DEI", "Picture conversion, orig "+srcWidth+" "+srcHeight+" downsample by "+inSampleSize);
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    // Here's a way to get the dimensions of the screen and use that as the requested size.
    /*public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size); // write into size
        return getScaledBitmap(path, size.x, size.y);
    }*/
}
