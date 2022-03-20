package hasan.mohamed.shehata.myapplication;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.CountryPhoneCode;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.ModelType;
import hasan.mohamed.shehata.myapplication.servicesandnotifications.RemainingTime;
import hasan.mohamed.shehata.myapplication.storage.PreferenceItem;
import hasan.mohamed.shehata.myapplication.storage.PreferenceKey;
import hasan.mohamed.shehata.myapplication.storage.ModelStatus;
import hasan.mohamed.shehata.myapplication.types.ContinuousRecognitionObserver;
import hasan.mohamed.shehata.myapplication.types.HighContrastObserver;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.JSONKey;
import hasan.mohamed.shehata.myapplication.types.MessageSendingCallbacks;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.UsersViewType;
import hasan.mohamed.shehata.myapplication.ui.messages.MessageFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {


    private static AsyncPinger lastAsyncPinger;
    public static AsyncPinger getLastAsyncPinger(){
        return lastAsyncPinger;
    }

    public static List<ResultReceiver> deletedMessagesResultReceivers = new ArrayList<>();
    public  static List<Message> messagesToDelete = new ArrayList<>();
    public static ImageReady lastMessageFragmentImageUpdater = null;
    public static MessageFragment lastMessageFragment;

    public static Context myCurrentContext = null;
    public static void setLastAsyncPinger(AsyncPinger asyncPinger){
        lastAsyncPinger = asyncPinger;
    }

    private static OverloadedPingResult overloadedPingResult;
    public static void setOverLoadedPingResult(OverloadedPingResult result){
        if(overloadedPingResult != null){
            synchronized (overloadedPingResult){
                overloadedPingResult = result;
            }
        }
        else{
            overloadedPingResult = result;
        }
    }

    public static OverloadedPingResult getOverloadedPingResult(){
        if(overloadedPingResult != null){
            synchronized (overloadedPingResult){
                return overloadedPingResult;
            }
        }
        else{
            return overloadedPingResult;
        }
    }

    public final transient static String MESSAGE_DELETE_COMMAND = "deletemessage";
    public final transient static  String MESSAGE_IMAGE_CMD = "image";
    public final transient static  long MESSAGE_UPDATE_GROUP_IMAGE_CONTROL_CODE_CMD = 400;
    public final transient static  long MESSAGE_IMAGE_CONTROL_CODE_CMD = 500;

    public static long currentOpenedBuddyIdChatView = 0;
    public static long currentOpenedGroupIdChatView = 0;
    public static UsersViewType currentUserViewType = UsersViewType.allusers;




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

    public static void unsetUserCreated(Context context, User loggedOutUser,Runnable runnable){
        String key = "hasan.mohamed.shehata.myapplication.IS_USER_CREATED";
        PreferenceKey preferenceKey = new PreferenceKey(key, context.getResources().getString(R.string.unset_shared_preference));
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        preferenceItem.set("no");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    AppDatabase.getUserDao().delete(loggedOutUser);
                }
                catch(Exception e){
                    e.printStackTrace();
                }


                Utils.runOnUIThread(runnable);
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

    public static void runOnUIThreadPostDelayedSpeceific(Runnable runnable, int delay){
        if(poster!=null)
            poster.postDelayed(runnable,delay);
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

    public static Bitmap AngleBitmapRotation(double ang, Bitmap bm)
    {
        Matrix m = new Matrix();
        m.postRotate((float) ang);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        return newbm;
    }


    public static void changeUser(Context context){

    }



    // This key for Text To Speech Service API only
    private static String currentGoogleCloudAccessToken = "AIzaSyCSp1FCBvdUzvcoNXd3urnu9uOOVtS4ezI";//AIzaSyCSp1FCBvdUzvcoNXd3urnu9uOOVtS4ezI

    public static void setGoogleKey(String key){
        if(key != null && key.length() >0)
            currentGoogleCloudAccessToken = key;

    }

    public static String getGoogleKey(){
        return currentGoogleCloudAccessToken;
    }


    private static boolean isTTSKeyFetched = false;
    private static boolean isASRClientKeyFetched = false;
    public static void executeKeysFetchRequest(final Context context){
        if(!isTTSKeyFetched){
            APIClient.getAPIInterface(context).downloadGoogleKey().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        try {
                            Utils.setGoogleKey(response.body().string());
                            isTTSKeyFetched = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }

        if(!isASRClientKeyFetched){
            APIClient.getAPIInterface(context).downloadGoogleClientKey().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        File f = new File(
                                context.getFilesDir().getPath() // /data/user/0/hasan.mohamed.shehata.myapplication/files/myphoto34532.png
//                Environment.getExternalStorageDirectory() //  /storage/o
                                        + File.separator + "clientkey34546.json");
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
                            fo.write(response.body().bytes());
                            // remember close de FileOutput
                            fo.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        isASRClientKeyFetched = true;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }


    }

    public static boolean getIsContinuousRecognition(Context context){
        String key = "hasan.mohamed.shehata.myapplication.getIsContinuousRecognition";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get().equals("true")){
            isContinuousRecognition = true;
            return true;
        }
        else{
            isContinuousRecognition = false;
            return false;
        }
    }



    public static boolean isContinuousRecognition = false;
    private static List<ContinuousRecognitionObserver> continuousRecognitionObservers = new ArrayList<>();
    public static void registerContinuousRecognitionObserver(ContinuousRecognitionObserver observer){
        if(continuousRecognitionObservers == null){
            continuousRecognitionObservers = new ArrayList<>();
        }
        if(observer != null)
            continuousRecognitionObservers.add(observer);
    }
    public static void setIsContinuousRecognition(Context context, boolean isContinuousRecognitionEnabled){
        String key = "hasan.mohamed.shehata.myapplication.getIsContinuousRecognition";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(isContinuousRecognitionEnabled){
            preferenceItem.set("true");
            isContinuousRecognition = true;
        }
        else{
            preferenceItem.set("false");
            isContinuousRecognition = false;
        }
        if(continuousRecognitionObservers != null){
            for(ContinuousRecognitionObserver observer : continuousRecognitionObservers){
                if(observer != null && context != null)
                    observer.refresh(getIsContinuousRecognition(context));
            }
        }
    }

    public static boolean getIsHighContrastTheme(Context context){
        String key = "hasan.mohamed.shehata.myapplication.getIsHighContrastTheme";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get().equals("true")){
            return true;
        }
        else{
            return false;
        }
    }




    private static List<HighContrastObserver> highContrastObservers = new ArrayList<>();
    public static void registerHighContrastObserver(HighContrastObserver observer){
        if(highContrastObservers == null){
            highContrastObservers = new ArrayList<>();
        }
        if(observer != null)
            highContrastObservers.add(observer);
    }
    public static void setIsHighContrastTheme(Context context, boolean isContinuousRecognitionEnabled){
        String key = "hasan.mohamed.shehata.myapplication.getIsHighContrastTheme";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(isContinuousRecognitionEnabled){
            preferenceItem.set("true");
        }
        else{
            preferenceItem.set("false");
        }
        if(highContrastObservers != null){
            for(HighContrastObserver observer : highContrastObservers){
                if(observer != null && context != null)
                    observer.refresh(getIsHighContrastTheme(context));
            }
        }
    }
    public static float getHighContrastTextFactor(Context context){
        return 1.5F;
    }


    public static <T> T selectAccordingToLightOrDark(Context context,T lightObject , T darkObject, T darkHighContrastObject , T lightHighContrastObject){
        if(context==null)
            return lightObject;
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO: {
                if(getIsHighContrastTheme(context))
                    return lightHighContrastObject;
                else
                    return lightObject;
            }
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active on device
                if(getIsHighContrastTheme(context))
                    return darkHighContrastObject;
                else
                    return darkObject;
        }
        return null;
    }


    private static boolean isToUseCloudTranslation = true;
    public static boolean getIsToUseCloudTranslation(){
        return isToUseCloudTranslation;
    }

    private static CountryPhoneCode[] countryPhoneCodes = null;
    public static synchronized CountryPhoneCode [] getCountryPhoneCodes(Context context){
        if(countryPhoneCodes == null) {
            InputStream incomingData = context.getResources().openRawResource(R.raw.phonecodes);
//        InputStreamReader inputStreamReader = new InputStreamReader(incomingData);
            StringBuilder json = new StringBuilder();
            try {
                int charCode = incomingData.read();
                while (charCode != -1) {
                    json.append((char) charCode);
                    charCode = incomingData.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(incomingData != null){
                    try{incomingData.close();}catch (Exception e){}
                }
            }

            countryPhoneCodes = new Gson().fromJson(json.toString(), CountryPhoneCode[].class);
        }
        return countryPhoneCodes;
    }

    private static ArrayList<String> contactsnameList = null;
    private static ArrayList<String> contactsphoneNumberList = null;

    public static synchronized List<String> getPhoneNumberList(Context context){
        if(context == null && contactsphoneNumberList == null)
            return new ArrayList<>();
        if(contactsphoneNumberList == null)
            getAllContacts(context);
        return contactsphoneNumberList;
    }


    @SuppressLint("Range")
    private static void getAllContacts(Context context) {
        contactsnameList = new ArrayList<>();
        contactsphoneNumberList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                @SuppressLint("Range") String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactsnameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo.replace("+", "00").replace(" ","").replace("-","");
                        contactsphoneNumberList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }


    public static void hideKeybaord(View v) {
        if(v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }



    public static boolean isContinuousSpeaking = false;
    public static boolean getIsContinuousSpeaking(Context context){
        String key = "hasan.mohamed.shehata.myapplication.getIsContinuousSpeaking";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(preferenceItem.get().equals("true")){
            isContinuousSpeaking = true;
            return true;
        }
        else{
            isContinuousSpeaking = false;
            return false;
        }
    }
    public static void setIsContinuousSpeaking(Context context, boolean isContinuousSpeakingEnabled){
        String key = "hasan.mohamed.shehata.myapplication.getIsContinuousSpeaking";
        PreferenceKey preferenceKey = new PreferenceKey(key, "false");
        PreferenceItem<String> preferenceItem = new PreferenceItem<String>(context, preferenceKey);
        if(isContinuousSpeakingEnabled){
            preferenceItem.set("true");
            isContinuousSpeaking = true;
        }
        else{
            preferenceItem.set("false");
            isContinuousSpeaking = false;
        }
    }
}
