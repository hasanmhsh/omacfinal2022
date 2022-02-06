package hasan.mohamed.shehata.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.ModelType;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.RemainingTime;
import hasan.mohamed.shehata.myapplication.storage.PreferenceItem;
import hasan.mohamed.shehata.myapplication.storage.PreferenceKey;
import hasan.mohamed.shehata.myapplication.storage.ModelStatus;
import hasan.mohamed.shehata.myapplication.types.MessageSendingCallbacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {
    public static int getDownloadedPercent(long downLength, long totalLength) {
        return (int) (Math.round( (double) ((double)((double)downLength) / ((double)totalLength)) * 100.0D));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getDownLinkSpeedKBps(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps()/1000 ;
//        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
        return downSpeed;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static RemainingTime getRemainingDownloadTime(Context context, long alreadyDownLength, long totalLength){
        long remainingKBs = totalLength/1024 - alreadyDownLength/1024;
        long remainingSeconds = remainingKBs/getDownLinkSpeedKBps(context);
        int seconds = (int) remainingSeconds % 60;
        int minutes = (int) (remainingSeconds/60)%60;
        int hours = (int) (remainingSeconds/60)/60;
        return new RemainingTime(hours,minutes,seconds);
    }

    public static boolean isInternetConnected(Context context){
        return false;
    }



    private static ModelStatus setGetLanguageModelAvailabiliy(Context context, Language src, Language trgt, ModelStatus value, ModelType modelType){
        String key =
                "hasan.mohamed.shehata.myapplication." + modelType.name() +  "."+ src.symbol + "." + trgt.symbol;

        PreferenceKey preferenceKey = new PreferenceKey
                (key, Integer.parseInt(context.getResources().getString(R.string.unset_shared_preference)));

        PreferenceItem<String> preferenceItem =
                new PreferenceItem<String>(context, preferenceKey);
        if(value != null) {
            preferenceItem.set(value.name());
        }

        if(preferenceItem.get().equals(ModelStatus.Exist.name()))
            return ModelStatus.Exist;
        else if(preferenceItem.get().equals(ModelStatus.Downloading.name()))
            return ModelStatus.Downloading;
        else
            return ModelStatus.NotExist;

    }

    public static void setUserCreated(Context context){
        String key = "hasan.mohamed.shehata.myapplication.IS_USER_CREATED";
        PreferenceKey preferenceKey = new PreferenceKey(key, context.getResources().getString(R.string.unset_shared_preference));
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        preferenceItem.set("yes");
    }

    public static void unsetUserCreated(Context context, User loggedOutUser){
        String key = "hasan.mohamed.shehata.myapplication.IS_USER_CREATED";
        PreferenceKey preferenceKey = new PreferenceKey(key, context.getResources().getString(R.string.unset_shared_preference));
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        preferenceItem.set("no");
        new Thread(new Runnable() {
            @Override
            public void run() {

                AppDatabase.getUserDao().delete(loggedOutUser);
            }
        }).start();
    }

    public static boolean isUserCreated(Context context){
        String key =
                "hasan.mohamed.shehata.myapplication.IS_USER_CREATED";
        PreferenceKey preferenceKey = new PreferenceKey(key, context.getResources().getString(R.string.unset_shared_preference));
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get() != null && preferenceItem.get().equals("yes"))
            return true;
        return false;
    }

    public static void setUserID(Context context, long id){
        String key = "hasan.mohamed.shehata.myapplication.MY_USER_ID";
        PreferenceKey preferenceKey = new PreferenceKey(key, context.getResources().getString(R.string.unset_shared_preference));
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        preferenceItem.set(String.valueOf(id));
    }

    public static long getUserID(Context context){
            String key = "hasan.mohamed.shehata.myapplication.MY_USER_ID";
            PreferenceKey preferenceKey = new PreferenceKey(key, "0");
            PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
            return Long.parseLong(preferenceItem.get());

    }



    public static void setTranslateLanguageModelStatus(Context context, Language src, Language trgt, ModelStatus value , ModelType modelType){
        setGetLanguageModelAvailabiliy(context,src,trgt,value, modelType);

    }


    public static ModelStatus getTranslateLanguageModelStatus(Context context, Language src, Language trgt, ModelType modelType){
        return setGetLanguageModelAvailabiliy(context,src,trgt,null, modelType);
    }

    public static void runOnUIThread(Runnable runnable){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }

    private static View poster;
    public static void registerPoster(View view){
        poster = view;
    }

//    public static void runOnUIThread(Runnable runnable){
//        if(poster!=null)
//            poster.post(runnable);
//    }

    public static void runOnUIThreadPostDelayed(Runnable runnable){
        if(poster!=null)
            poster.postDelayed(runnable,1000);
    }

    public static FragmentManager getSupportFragmentManager(Context context){
        return ((FragmentActivity)context).getSupportFragmentManager();
    }

    public static String getHMSApiKey(){
        return "CwEAAAAAmWUy4srP4AuJGxFzut4ADvKArlZ0N4TWrm1ND037bABSAzlkXwFiEDN0m44xchZZ9Ad+YKHob1x76AwbSDZ/mjD15r0=";
    }

    public static String extractPartialVoskText(String partialJson){
        try {
            final JSONObject obj = new JSONObject(partialJson);
            return obj.getString("partial");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setBackgroundThreadFlag(Context context, boolean isToRun){
        String key = "hasan.mohamed.shehata.myapplication.BACKGROUND_THREAD_FLAG";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(isToRun){
            preferenceItem.set("true");
        }
        else{
            preferenceItem.set("false");
        }
    }

    public static boolean getBackgroundThreadFlag(Context context){
        String key = "hasan.mohamed.shehata.myapplication.BACKGROUND_THREAD_FLAG";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get().equals("true")){
            return true;
        }
        else{
            return false;
        }
    }

    public static void setIsToCallRejectCallBackInDestroyOfCallingFragment(Context context, boolean isToRun){
        String key = "hasan.mohamed.shehata.myapplication.IsToCallRejectCallBackInDestroyOfCallingFragment";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(isToRun){
            preferenceItem.set("true");
        }
        else{
            preferenceItem.set("false");
        }
    }

    public static boolean getIsToCallRejectCallBackInDestroyOfCallingFragment(Context context){
        String key = "hasan.mohamed.shehata.myapplication.IsToCallRejectCallBackInDestroyOfCallingFragment";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get().equals("true")){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean validateEmail(String email){
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static MediaPlayer mp = null;
    public static void playMessageRing(Context context) {
        playRing(context, R.raw.ring ,false);
    }


    public static void playIamCallingRing(Context context){
        playRing(context, R.raw.iamcalling ,true);
    }

    public static void playIamReceivingCallRing(Context context){
        playRing(context, R.raw.callreceived ,true);
    }

    public static void playStartRecordSound(Context context){
        playRing(context, R.raw.startrecord ,false);
    }

    public static void playStopRecordSound(Context context){
        playRing(context, R.raw.stoprecord ,false);
    }

    public static void dispose(Context context){
        if(mp != null) {
            mp.release();
        }
    }

    public static void stopRing(){
        if(mp != null) {
            try{mp.stop();}catch (Exception e){e.printStackTrace();}
        }
    }

    private static void playRing(Context context ,int res ,boolean isLooping) {
        if(mp != null) {
            try{ mp.stop();}catch (Exception e){e.printStackTrace();}
            if(mp != null)
                try{ mp.release();}catch (Exception e){e.printStackTrace();}
        }
        mp = MediaPlayer.create(context, res);
        mp.setLooping(isLooping);
        mp.start();
    }

    private static AsyncPinger globalPinger;
    public static void setGlobalPinger(AsyncPinger pinger){
        globalPinger = pinger;
    }
    public static AsyncPinger getGlobalPinger(){
        return globalPinger;
    }
    private static boolean  checkIfControlMessage(Message message) {
        String control = message.getMessagemoshakkaltext();
        if(control != null){
            if(control.equals("Calling") ||control.equals("Accept") || control.equals("Busy") || control.equals("Terminate"))
                return true;
        }
        return false;
    }
    public static void sendMessage(Context context,Message message, final MessageSendingCallbacks callbacks){
        APIClient.getAPIInterface(context).createNewMessage(message).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    final Message msg = response.body();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!checkIfControlMessage(msg))
                                AppDatabase.getMessageDao().insertAll(msg);
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(callbacks != null)
                                        callbacks.onMessageSendingSuccess(msg);
                                }
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                call.cancel();
                if(callbacks != null)
                    callbacks.onMessageSendingFailure();
            }
        });
    }

    public static void changeUser(Context context){

    }



    // This key for Text To Speech Service API only
    public static final String currentGoogleCloudAccessToken = "AIzaSyCSp1FCBvdUzvcoNXd3urnu9uOOVtS4ezI";

}
