package hasan.mohamed.shehata.myapplication.types;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.Message;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupPostRequest implements Serializable {
    private Group group;
    private Context context;
    private String groupImageContentUri;
    private SingleObjectReceiver groupReceiver;

    public GroupPostRequest(Context context, Group group, String groupImageContentUri, SingleObjectReceiver groupReceiver) {
        this.group = group;
        this.context = context;
        this.groupImageContentUri = groupImageContentUri;
        this.groupReceiver = groupReceiver;
    }

    public void startRequest(){
        APIClient.getAPIInterface(context).replaceOrSaveFullGroup(group).enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    group = response.body();
                    if(groupReceiver != null){
                        groupReceiver.receive(group);
                    }
                    if (groupImageContentUri != null) {
                        compressImage(groupImageContentUri);
                    }
                } else {
                    if(context != null)
                        Toast.makeText(context, "Connection error!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                call.cancel();
                if(context != null)
                    Toast.makeText(context, "Connection error!", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void compressImage(String url){
//        Bitmap b = BitmapFactory.decodeFile("Pass your file path");
// original measurements
        Uri uri = Uri.parse(url);
//        pickedPhotoPath = uri.getEncodedPath();
//        url=pickedPhotoPath;
        if(url==null){
            return;
        }
        final int destWidth = 130;//or the width you need
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
            if(bitmap == null){
                if(context != null){
                    Toast.makeText(context, "Unsupported image format!",Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
        catch(Exception e){
            return;
        }
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        if(origWidth > destWidth) {
            Bitmap scaledBm = Bitmap.createScaledBitmap(bitmap, destWidth, destWidth * origHeight / origWidth, true);
            if(scaledBm == null){
                if(context != null){
                    Toast.makeText(context, "Unsupported image format!",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            finalizeCompression(scaledBm);
        }
        else
            finalizeCompression(bitmap);
//        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        int origWidth = resource.getWidth();
//                        int origHeight = resource.getHeight();
//                        if(origWidth > destWidth) {
//
//                            RequestOptions myOptions = new RequestOptions()
//                                    .override(destWidth, destWidth * origHeight / origWidth);//.circleCrop();
//                            Glide.with(thiz.getContext())
//                                    .asBitmap()
////                                    .apply(myOptions)
//                                    .load(resource)
//                                    .override(destWidth, destWidth * origHeight / origWidth)
//                                    .centerCrop()
//                                    .into(new CustomTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(@NonNull Bitmap resource2, @Nullable Transition<? super Bitmap> transition) {
//                                            int origWidth = resource2.getWidth();
//                                            int origHeight = resource2.getHeight();
//                                            finalizeCompression(resource2);
//                                        }
//
//                                        @Override
//                                        public void onLoadCleared(@Nullable Drawable placeholder2) {
//                                        }
//                                    });
//                        }
//                        else{
//                            finalizeCompression(resource);
//                        }
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });

    }
    private Bitmap pickedPhoto;
    private File pickedPhotoFile;
    private String pickedPhotoPath;
    private void finalizeCompression(Bitmap bitmap){

//        Drawable resizedImage = null;
//        Bitmap resizedBitmap = null;
//        int origWidth = bitmap.getWidth();
//        int origHeight = bitmap.getHeight();
//        final int destWidth = 120;
//        try{
//            resizedImage = Glide
//                    .with(getActivity())
//                    .load(bitmap)
//                    .override(destWidth, destWidth * origHeight / origWidth)
//                    .submit()
//                    .get();
//
//            resizedBitmap = Bitmap.createBitmap(destWidth, destWidth * origHeight / origWidth, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(resizedBitmap);
//            resizedImage.setBounds(0, 0, destWidth, destWidth * origHeight / origWidth);
//            resizedImage.draw(canvas);
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
//        bitmap = resizedBitmap;



        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        if(bitmap.getWidth() != 130){
//            bitmap = Utils.AngleBitmapRotation(90.0D,bitmap);
//        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100 , outStream);
        if(bitmap == null){
            if(context != null){
                Toast.makeText(context, "Unsupported image format!",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        File f = new File(
                context.getFilesDir().getPath() // /data/user/0/hasan.mohamed.shehata.myapplication/files/myphoto34532.png
//                Environment.getExternalStorageDirectory() //  /storage/o
                        + File.separator + "myphoto34532.png");
        if(f.exists()){
            f.delete();
        }
        try{f.createNewFile();}
        catch (Exception e){
            e.printStackTrace();
        }
        //write the bytes in file
        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            // remember close de FileOutput
            fo.close();
            pickedPhotoFile = f;
            postPhoto(group.getGroupid());
            trialsOfPhotoPost = 4;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    int trialsOfPhotoPost = 4;
    private void postPhoto(final long myid){
        trialsOfPhotoPost--;
        if(trialsOfPhotoPost <= 0)
            return;
        if(pickedPhotoFile != null){
            RequestBody fbody = RequestBody.create(pickedPhotoFile, MediaType.parse("image/*"));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", pickedPhotoFile.getName(), fbody);
            APIClient.getAPIInterface(context).uploadGroupPhoto(myid,body).enqueue(new Callback<JSONResult>() {
                @Override
                public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
//                    Toast.makeText(getContext(), response.body().getResult(), Toast.LENGTH_LONG).show();
                    if(!response.isSuccessful()){
                        postPhoto(myid);
                    }
                    else{
//                        if(groupReceiver != null){
//                            groupReceiver.refreshGroupImage();

                            if(context != null){
                                AsyncPinger pinger =
                                        ((TranslationMainActivity)context).getCurrentPinger();
                                if(pinger != null){
                                    pinger.updateGroupImage(group.getGroupid());
                                    if(Utils.lastMessageFragmentImageUpdater != null){
                                        pinger.registerImageReadyListenerOrGetImageIfExistForGroups(group.getGroupid(),Utils.lastMessageFragmentImageUpdater);
                                    }
                                }
                            }
//                        }
                        if(context!=null){
                            Message message = new Message();
                            message.setControlnumber(Utils.MESSAGE_UPDATE_GROUP_IMAGE_CONTROL_CODE_CMD);
                            message.setSenderid(Utils.getUserID(context));
                            message.setGroupid(group.getGroupid());
                            APIClient.getAPIInterface(context).createNewMessage(message).enqueue(new Callback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    if(response.isSuccessful()){
                                        int i = 10;
                                    }
                                }

                                @Override
                                public void onFailure(Call<Message> call, Throwable t) {
                                    call.cancel();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<JSONResult> call, Throwable t) {
                    call.cancel();
                    postPhoto(myid);
                }
            });

        }
    }
}
