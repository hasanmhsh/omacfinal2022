package hasan.mohamed.shehata.myapplication.languages;

import android.app.Activity;
import android.text.TextUtils;

import butterknife.ButterKnife;
import hasan.mohamed.shehata.myapplication.internet.asrapi.CloudServiceInterface;
import hasan.mohamed.shehata.myapplication.internet.asrapi.Transducer;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.types.AsrResultCallbacks;

public class GoogleCloudASR {


    private Activity owner;
    private AsrResultCallbacks asrResultCallbacks;
    private static final int RECORD_REQUEST_CODE = 101;
    private CloudServiceInterface cloudServiceInterface;
    private Transducer mTransducer;
    private String languageCode;

    public GoogleCloudASR(Activity owner, AsrResultCallbacks asrResultCallbacks, String languageCode) {
        this.owner = owner;
        this.asrResultCallbacks = asrResultCallbacks;
        this.languageCode = languageCode;

        initialize();
    }

    private void initialize(){
        ButterKnife.bind(owner);
        cloudServiceInterface = new CloudServiceInterface(owner,languageCode);
        cloudServiceInterface.registerObserver(mSpeechServiceRequestCallback);
    }

    public void release(){
        stopVoiceRecorder();

        // Stop Cloud Speech API
        cloudServiceInterface.unregisterObserver(mSpeechServiceRequestCallback);
        cloudServiceInterface.destroy();
        cloudServiceInterface = null;
    }


    private final Transducer.Provider mVoiceProvider = new Transducer.Provider() {

        @Override
        public void beginSpeaking() {
            if (cloudServiceInterface != null) {
                cloudServiceInterface.convertSpeechToTextBegin(mTransducer.getPcmPerSecond());
            }
        }

        @Override
        public void speaking(byte[] data, int size) {
            if (cloudServiceInterface != null) {
                cloudServiceInterface.convertS2T(data, size);
            }
        }

        @Override
        public void speakingStopped() {
            if (cloudServiceInterface != null) {
                cloudServiceInterface.endConversion();
            }
        }

    };
    private final CloudServiceInterface.RequestCallback mSpeechServiceRequestCallback =
            new CloudServiceInterface.RequestCallback() {
                @Override
                public void fullTextReady(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mTransducer.ignore();
                    }
                    if (asrResultCallbacks != null && !TextUtils.isEmpty(text)) {
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    asrResultCallbacks.voiceRecognized(text);
                                } else {
                                    asrResultCallbacks.partialVoiceRecognized(text);
                                }
                            }
                        });
                    }
                }
            };

    public void startVoiceRecorder() {
        if (mTransducer != null) {
            mTransducer.end();
        }
        mTransducer = new Transducer(mVoiceProvider);
        mTransducer.begin();
    }

    public void stopVoiceRecorder() {
        if (mTransducer != null) {
            mTransducer.end();
            mTransducer = null;
        }
    }
}
