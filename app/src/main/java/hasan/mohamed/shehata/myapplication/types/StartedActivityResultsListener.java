package hasan.mohamed.shehata.myapplication.types;

import android.graphics.Bitmap;

public interface StartedActivityResultsListener {
    public void photoPicked(Bitmap photo);
    public void photoPickedContentUri(String uri);
    public void photoPickedFilePath(String path);
}
