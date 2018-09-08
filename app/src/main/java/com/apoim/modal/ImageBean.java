package com.apoim.modal;

import android.graphics.Bitmap;

/**
 * Created by mindiii on 23/3/18.
 */

public class ImageBean {
    public Bitmap bitmap;
    public String url;
    public String imageId;

    public ImageBean(String url, Bitmap bitmap,String imageId){
        this.imageId = imageId;
        this.url = url;
        this.bitmap = bitmap;
    }
}
