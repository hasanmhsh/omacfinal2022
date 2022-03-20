package hasan.mohamed.shehata.myapplication.types;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostImageRequest implements StartedActivityResultsListener  {

    private Context context;
    private long messageId;
    private ByteaCallback imagePickedRunnable;

    public ImageReady getImageUpdater() {
        return imageUpdater;
    }

    public void setImageUpdater(ImageReady imageUpdater) {
        this.imageUpdater = imageUpdater;
    }

    private ImageReady imageUpdater;

    public PostImageRequest(Context context, ByteaCallback imagePickedRunnable) {
        this.context = context;
        this.imagePickedRunnable = imagePickedRunnable;
        if(context != null){
            ((StartedACtivityResultsProvider)context).registerStartedActivityResultsListener(this);
        }
    }

    public void pickPhoto(){
        selectPhoto();
    }

    private Bitmap pickedPhoto;
    private File pickedPhotoFile;
    private String pickedPhotoPath;

    private String pickedPhotoContentUri;

    private byte [] compressImage(String url){
        Uri uri = Uri.parse(url);
        final int destWidth = 200;//or the width you need
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);

        }
        catch(Exception e){

        }
        if(bitmap == null){
            return null;
        }
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        if(origWidth > destWidth) {
            Bitmap scaledBm = Bitmap.createScaledBitmap(bitmap, destWidth, destWidth * origHeight / origWidth, true);
            if(scaledBm == null){
                return null;
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            scaledBm.compress(Bitmap.CompressFormat.PNG,100 , outStream);
            return outStream.toByteArray();
        }
        else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            return outStream.toByteArray();
        }


    }



    int trialsOfPhotoPost = 4;


    private void selectPhoto(){

        if(context != null) {
            ((PermissionRequestProvider) context).requireStoragePermissions(new PermissionRequestCallbacks() {
                @Override
                public void granted() {
                    if (context != null) {
                        ((StartedACtivityResultsProvider) context).pickImage();
                    }
                }

                @Override
                public void denied() {

                }
            });
        }
    }


    @Override
    public void photoPicked(Bitmap photo) {
        pickedPhoto = photo;
    }

    @Override
    public void photoPickedContentUri(String uri) {
        pickedPhotoContentUri = uri;
//        imagePickedRunnable.ready(compressImage(uri));
        byte [] bytes = compressImage(uri);
        if(Utils.lastMessageFragment != null){
            Utils.lastMessageFragment.sendImageMessage(bytes);
        }
    }

    @Override
    public void photoPickedFilePath(String path) {
        pickedPhotoPath = path;
//        compressImage(path);  // Didnt work
    }


}
