package hasan.mohamed.shehata.myapplication.internet.asrapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
//import android.support.annotation.NonNull;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import io.grpc.ManagedChannel;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;


public class CloudServiceInterface {




    private static final int TOKEN_TIME_PERIOD = 1800000;

    private static final int TOKEN_THRESHOLD_TIME = 60000;

    private static final String CLOUD_URL = "speech.googleapis.com";
    private static final int SERVICE_PORT_NUMBER = 443;
    private static Handler backgroundThreadHandler;

    public static final List<String> CLOUD_SERVICE_URL = Collections.singletonList("https://www.googleapis.com/auth/cloud-platform");
    public static final String TAG = "CloudServiceInterface";

    private static final String PARAM = "SpeechToTextService";
    private static final String PARAM_CRED = "hasan.mohamed.shehata.PARAM_CRED";
    private static final String PARAM_CRED_TIME = "hasan.mohamed.shehata.PARAM_CRED_TIME";

    private final ArrayList<RequestCallback> myObservers = new ArrayList<>();

    private final StreamObserver<StreamingRecognizeResponse> myRespListeners = new StreamObserver<StreamingRecognizeResponse>() {
        @Override
        public void onNext(StreamingRecognizeResponse response) {
            String text = null;
            boolean isFinal = false;
            if (response.getResultsCount() > 0) {
                final StreamingRecognitionResult result = response.getResults(0);
                isFinal = result.getIsFinal();
                if (result.getAlternativesCount() > 0) {
                    final SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                    text = alternative.getTranscript();
                }
            }
            if (text != null) {
                for (RequestCallback requestCallback : myObservers) {
                    requestCallback.fullTextReady(text, isFinal);
                }
            }
        }

        @Override
        public void onError(Throwable t) {
            Log.e(TAG, "Error calling the API.", t);
        }

        @Override
        public void onCompleted() {
            Log.i(TAG, "API completed.");
        }

    };
    private Context appcntxt;
    private volatile Credentials task;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCredentials();
        }
    };
    private SpeechGrpc.SpeechStub speechStub;
    private StreamObserver<StreamingRecognizeRequest> listenerStrm;

    private String lng;

    public CloudServiceInterface(Context cntxt, String lng) {
        this.appcntxt = cntxt;
        this.lng = lng;
        backgroundThreadHandler = new Handler();
        getCredentials();

    }

    public void destroy() {
        backgroundThreadHandler.removeCallbacks(runnable);
        backgroundThreadHandler = null;
        if (speechStub != null) {
            final ManagedChannel medium = (ManagedChannel) speechStub.getChannel();
            if (medium != null && !medium.isShutdown()) {
                try {
                    medium.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException interruptedException) {
                    Log.e(TAG, "Failed to fetch data", interruptedException);
                }
            }
            speechStub = null;
        }
    }

    private void getCredentials() {
        if (task != null) {
            return;
        }
        task = new Credentials();
        task.execute();
    }

    public void registerObserver(@NonNull RequestCallback observer) {
        myObservers.add(observer);
    }

    public void unregisterObserver(@NonNull RequestCallback observer) {
        myObservers.remove(observer);
    }

    public void convertSpeechToTextBegin(int pulsePerSecond) {
        if (speechStub == null) {
            return;
        }

        listenerStrm = speechStub.streamingRecognize(myRespListeners);

        StreamingRecognitionConfig build = StreamingRecognitionConfig.newBuilder()
                .setConfig(RecognitionConfig.newBuilder()
                        .setLanguageCode(lng)
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(pulsePerSecond)
                        .build()
                )
                .setInterimResults(true)
                .setSingleUtterance(true)
                .build();

        StreamingRecognizeRequest build1 = StreamingRecognizeRequest.newBuilder().setStreamingConfig(build).build();
        listenerStrm.onNext(build1);
    }

    public void convertS2T(byte[] bytes, int length) {
        if (listenerStrm == null) {
            return;
        }
        listenerStrm.onNext(StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(bytes, 0, length))
                .build());
    }


    public void endConversion() {
        if (listenerStrm == null) {
            return;
        }
        listenerStrm.onCompleted();
        listenerStrm = null;
    }

    public interface RequestCallback {
        void fullTextReady(String text, boolean isFinal);
    }

    private class Credentials extends AsyncTask<Void, Void, AccessToken> {
        @Override
        protected void onPostExecute(AccessToken result) {
            task = null;
            final ManagedChannel build = new OkHttpChannelProvider()
                    .builderForAddress(CLOUD_URL, SERVICE_PORT_NUMBER)
                    .nameResolverFactory(new DnsNameResolverProvider())
                    .intercept(new Authenticator(new GoogleCredentials(result)
                            .createScoped(CLOUD_SERVICE_URL)))
                    .build();
            speechStub = SpeechGrpc.newStub(build);


//            synchronized (Utils.currentGoogleCloudAccessToken){
//                Utils.currentGoogleCloudAccessToken = result.getTokenValue();
//            }

            if (backgroundThreadHandler != null && result != null) {
                backgroundThreadHandler.postDelayed(runnable,
                        Math.max(result.getExpirationTime().getTime() - System.currentTimeMillis() - TOKEN_THRESHOLD_TIME, TOKEN_TIME_PERIOD));
            }
        }

        @Override
        protected AccessToken doInBackground(Void... voids) {

            final SharedPreferences presistedData = appcntxt.getSharedPreferences(PARAM, Context.MODE_PRIVATE);
            String credData = presistedData.getString(PARAM_CRED, null);
            long credTime = presistedData.getLong(PARAM_CRED_TIME, -1);

            if (credData != null && credTime > 0) {
                if (credTime > System.currentTimeMillis() + TOKEN_TIME_PERIOD) {
                    return new AccessToken(credData, new Date(credTime));
                }
            }

//            final InputStream incomingData = appcntxt.getResources().openRawResource(R.raw.omacprojectcredentials);

            File f = new File(
                    appcntxt.getFilesDir().getPath() // /data/user/0/hasan.mohamed.shehata.myapplication/files/myphoto34532.png
//                Environment.getExternalStorageDirectory() //  /storage/o
                            + File.separator + "clientkey34546.json");

            InputStream incomingData = null;

            try {
                incomingData = new FileInputStream(f);
                final GoogleCredentials scoped = GoogleCredentials.fromStream(incomingData).createScoped(CLOUD_SERVICE_URL);
                final AccessToken cred23 = scoped.refreshAccessToken();
                presistedData.edit()
                        .putString(PARAM_CRED, cred23.getTokenValue())
                        .putLong(PARAM_CRED_TIME, cred23.getExpirationTime().getTime())
                        .apply();
                return cred23;
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(incomingData != null)
                    try{incomingData.close();}catch (Exception e){e.printStackTrace();}
            }
            return null;
        }

    }


}
