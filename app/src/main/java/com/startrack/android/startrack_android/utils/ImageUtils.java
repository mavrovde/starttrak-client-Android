package com.startrack.android.startrack_android.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.io.File;

/**
 * Created by andrii on 15.02.16.
 */
public class ImageUtils {

    //aquery utils
    public static void setImage(String url, ImageView imageView) {
        setImage(url, imageView, -1);
    }

    public static void setImage(String url, ImageView imageView, int placeHolderId) {
        setImage(url, imageView, imageView.getWidth(), placeHolderId);
    }

    public static void setImage(String url, ImageView imageView, int targetWidth, int placeHolderId) {
        AQuery aq = new AQuery(imageView);
        if (!TextUtils.isEmpty(url)) {
            File file = aq.getCachedFile(url);
            if (file != null) {
                aq.id(imageView).image(file, targetWidth);
            } else {
                aq.id(imageView).image(url, false, true, targetWidth, 0);
            }
        } else {
            if (placeHolderId != -1)
                aq.id(imageView).image(placeHolderId);
        }
    }
}
