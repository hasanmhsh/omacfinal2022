package hasan.mohamed.shehata.myapplication.types;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface ImageReady extends Serializable {
    public void imageReady(long userid, Bitmap image);
    public long getUserId();
}
