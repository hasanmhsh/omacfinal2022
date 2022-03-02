package hasan.mohamed.shehata.myapplication.languages;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.types.ActionResultCallback;
import hasan.mohamed.shehata.myapplication.types.AsrResultCallbacks;
import hasan.mohamed.shehata.myapplication.types.ContinuousRecognitionObserver;
import hasan.mohamed.shehata.myapplication.types.ModelSource;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.TextReceiver;

public class ASR_Enhanced implements ContinuousRecognitionObserver {


//    private HashMap<Language, String> languageAsrCodes = new HashMap<Language,String>(){{
//        put(Language.Arabic,"ar-JO");
//        put(Language.Danish,"da-DK");
//        put(Language.German,"de-DE");
//        put(Language.English,"en-AE");
//        put(Language.Spanish,"es-ES");
//        put(Language.Finnish,"fi-FL");
//        put(Language.French,"fr-FR");
//        put(Language.Italian,"it-IT");
//        put(Language.Japanese,"ja-JP");
//        put(Language.Korean,"ko-KR");
//
//        put(Language.Polish,"pl-PL");
//        put(Language.Portuguese,"pt-PT");
//        put(Language.Russian,"ru-RU");
//        put(Language.Swedish,"sv-SE");
//        put(Language.Thai,"th-TH");
//        put(Language.Turkish,"tr-TR");
//        put(Language.Chinese,"zh-HANS");
//        put(Language.Malay,"ms-MY");
//        put(Language.Norwegian,"nn-NO");
//        put(Language.Vietnamese,"vi-VN");
//        put(Language.Indonesian,"id-ID");
//
//        put(Language.Czech,"cs-CZ");
//        put(Language.Hebrew,"he-IL");
//        put(Language.Greek,"el-GR");
//        put(Language.Hindi,"hi-IN");
//        put(Language.Tagalog,"tl");
//        put(Language.Serbian,"sr");
//        put(Language.Romanian,"ro-RO");
//        put(Language.Traditional_Chinese,"zh");
//        put(Language.Tamil,"ta");
//        put(Language.Hungarian,"hu-HU");
//        put(Language.Dutch,"nl-NL");
//
//        put(Language.Persian,"fa-IR");
//        put(Language.Slovak,"sk-SK");
//        put(Language.Estonian,"et");
//        put(Language.Latvian,"lv-LV");
//        put(Language.Central_Khmer,"km-KH");
//    }};

    private HashMap<Language, String> languageAsrCodes = new HashMap<Language,String>(){{
        put(Language.Arabic,"ar");
        put(Language.Danish,"da");
        put(Language.German,"de");
        put(Language.English,"en");
        put(Language.Spanish,"es");
        put(Language.Finnish,"fi");
        put(Language.French,"fr");
        put(Language.Italian,"it");
        put(Language.Japanese,"ja");
        put(Language.Korean,"ko");

        put(Language.Polish,"pl");
        put(Language.Portuguese,"pt");
        put(Language.Russian,"ru");
        put(Language.Swedish,"sv");
        put(Language.Thai,"th");
        put(Language.Turkish,"tr");
        put(Language.Chinese,"zh");
        put(Language.Malay,"ms");
        put(Language.Norwegian,"nn");
        put(Language.Vietnamese,"vi");
        put(Language.Indonesian,"id");

        put(Language.Czech,"cs");
        put(Language.Hebrew,"he");
        put(Language.Greek,"el");
        put(Language.Hindi,"hi");
        put(Language.Tagalog,"tl");
        put(Language.Serbian,"sr");
        put(Language.Romanian,"ro");
        put(Language.Traditional_Chinese,"zh");
        put(Language.Tamil,"ta");
        put(Language.Hungarian,"hu");
        put(Language.Dutch,"nl");

        put(Language.Persian,"fa");
        put(Language.Slovak,"sk");
        put(Language.Estonian,"et");
        put(Language.Latvian,"lv");
        put(Language.Central_Khmer,"km");
    }};


    private Runnable resetButton = new Runnable() {
        @Override
        public void run() {

        }
    };

    private Activity owner;
    private PermissionRequestProvider permissionRequestProvider;
    private Language language;
    private ImageButton recordButton;
    private int noRecordResId;
    private int recordResId;
    private TextView recognizedTextConsumer;
    private AsrResultCallbacks asrResultCallbacks;
    private boolean isListening = false;
    private Intent speechRecognizerIntent;
    private boolean isResultDelivered = false;
    private boolean isError = false;
    private boolean isEnd = false;
    private boolean isContinuousRecognition = false;

    public void setContinuousRecognition(boolean continuousRecognition) {
        isContinuousRecognition = continuousRecognition;
    }

    private final static boolean IS_USE_GOOGLE_CLOUD_ASR = true;

    public static final Integer RecordAudioRequestCode = 1776;
    private SpeechRecognizer speechRecognizer;

    private GoogleCloudASR googleCloudASR;


    public ASR_Enhanced(Activity owner,
                        PermissionRequestProvider permissionRequestProvider,
                        Language language,
                        ImageButton recordButton,
                        int recordResId,
                        int noRecordResId,
                        TextView recognizedTextConsumer,
                        AsrResultCallbacks asrResultCallbacks
    ) {
        this.owner = owner;
        this.permissionRequestProvider = permissionRequestProvider;
        this.language = language;
        this.recordButton = recordButton;
        this.recordResId = recordResId;
        this.noRecordResId = noRecordResId;
        this.recognizedTextConsumer = recognizedTextConsumer;
        this.asrResultCallbacks = asrResultCallbacks;
        isContinuousRecognition = Utils.getIsContinuousRecognition(owner);
        if(recordButton != null)
            changeRecordButton(recordButton);
        if(IS_USE_GOOGLE_CLOUD_ASR){
            googleCloudASR = new GoogleCloudASR(owner, new AsrResultCallbacks() {
                @Override
                public void voiceRecognized(String result) {
                    if(recognizedTextConsumer != null)
                        recognizedTextConsumer.setText(result);
                    if(asrResultCallbacks != null)
                        asrResultCallbacks.voiceRecognized(result);

                    if( !isContinuousRecognition) {
                        isListening = false;
                        if (googleCloudASR != null) {
                            googleCloudASR.stopVoiceRecorder();
                        }
//                    speechRecognizer.stopListening();
                        if (recordButton != null) {
                            recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50_black));
                            recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_button_light,R.drawable.round_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                            Utils.playStopRecordSound(owner);
                        }
                    }
                }

                @Override
                public void partialVoiceRecognized(String partialResult) {
                    if(recognizedTextConsumer != null)
                        recognizedTextConsumer.setText(partialResult);
                }
            }, languageAsrCodes.get(language));
        }
        else {
            initASR();
        }
        Utils.registerContinuousRecognitionObserver(this);
    }

    private void initGoogleCloudAsr(){

    }

    private void initASR() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(owner);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,languageAsrCodes.get(language));
        if(language == Language.Arabic)
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"tk.oryx.voice");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000); // value to wait
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 4000);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);  // 1 is the maximum number of results to be returned.
        speechRecognizer.setRecognitionListener(new RecognitionListener() {


            @Override
            public void onReadyForSpeech(Bundle bundle) {
                int kmbvf=546;
            }

            @Override
            public void onBeginningOfSpeech() {
//                editText.setText("");
//                editText.setHint("Listening...");
//                Utils.playStartRecordSound(owner);
                isResultDelivered = false;
            }

            @Override
            public void onRmsChanged(float v) {

                int jhf = 8;
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                int jdsf = 98;
            }

            @Override
            public void onEndOfSpeech() {
                int kmbvf=546;
//                isListening = false;
//                if(recordButton != null) {
//                    recordButton.setImageResource(recordResId);
//                    Utils.playStopRecordSound(owner);
//                }
//                speechRecognizer.stopListening();
//                if(!isResultDelivered){
//                    isResultDelivered = false;
//                    if(recordButton != null) {
//                        recordButton.setImageResource(recordResId);
//                        Utils.playStopRecordSound(owner);
//                    }
//                }
                if(!isResultDelivered ){//&& !isError){
                    isEnd = true;
                    isListening = false;
                    isResultDelivered = false;
//                    speechRecognizer.stopListening();
                    if(recordButton != null) {
                        recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50_black));
                        recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_button_light,R.drawable.round_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                        Utils.playStopRecordSound(owner);
                    }
                }
            }

            @Override
            public void onError(int i) {
                int kmbvf=546;
//                if(!isResultDelivered && !isEnd){
//                    isError = true;
//                    isListening = false;
//                    isResultDelivered = false;
////                    speechRecognizer.stopListening();
//                    if(recordButton != null) {
//                        recordButton.setImageResource(recordResId);
//                        Utils.playStopRecordSound(owner);
//                    }
//                }
            }

            @Override
            public void onResults(Bundle bundle) {
                isListening = false;
                isError = true;
                isEnd = false;
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(recognizedTextConsumer != null)
                    recognizedTextConsumer.setText(data.get(0));
                if(asrResultCallbacks != null)
                    asrResultCallbacks.voiceRecognized(data.get(0));
                if(recordButton != null) {
                    recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50_black));
                    recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_button_light,R.drawable.round_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                    Utils.playStopRecordSound(owner);
                }
//                speechRecognizer.stopListening();
                isResultDelivered = true;
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                int  fd = 9;
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                int jk = 9;
            }
        });


//        recordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isListening){
//                    if(recordButton != null) {
//                        recordButton.setImageResource(recordResId);
//                        Utils.playStopRecordSound(owner);
//                    }
//                    speechRecognizer.stopListening();
//                    isListening = false;
//                }
//                else{
//                    if(recordButton != null) {
//                        recordButton.setImageResource(noRecordResId);
//                        Utils.playStartRecordSound(owner);
//                    }
//                    speechRecognizer.startListening(speechRecognizerIntent);
//                    isListening = true;
//                }
//            }
//        });


    }

    public void release(){
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
        if(googleCloudASR != null)
            googleCloudASR.release();
    }


    private View.OnClickListener onRecordButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((PermissionRequestProvider)owner).requireRecordPermission(new PermissionRequestCallbacks() {
                @Override
                public void granted() {
                    if(!IS_USE_GOOGLE_CLOUD_ASR) {
                        if (!isListening) {
                            speechRecognizer.stopListening();
                            isListening = true;
                        }
                        isResultDelivered = false;
                        isError = false;
                        isEnd = false;
                        if (recordButton != null) {
                            recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50_black));
                            recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_red_button_light,R.drawable.round_red_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                            Utils.playStartRecordSound(owner);
                        }
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }
                    else{
                        if (!isListening) {
                            isListening = true;
                            if (recordButton != null) {

                                recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50,R.drawable.ic_baseline_mic_off_50_black));
                                recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_red_button_light,R.drawable.round_red_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                                Utils.playStartRecordSound(owner);
                            }
                            if (googleCloudASR != null) {
                                googleCloudASR.startVoiceRecorder();
                            }
                        }
                        else{
                            isListening = false;
//                    speechRecognizer.stopListening();
                            if(recordButton != null) {
                                recordButton.setImageResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50_black));
                                recordButton.setBackgroundResource(Utils.selectAccordingToLightOrDark(owner,R.drawable.round_button_light,R.drawable.round_button_light,R.drawable.round_button_light_high_contrast,R.drawable.round_button_light_high_contrast));
                                Utils.playStopRecordSound(owner);
                            }
                            if (googleCloudASR != null) {
                                googleCloudASR.stopVoiceRecorder();
                            }
                        }
                    }
                }

                @Override
                public void denied() {
                    Toast.makeText(owner, "Accept record permission to use this!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    public void changeRecordButton(ImageButton button){
//        if(recordButton != null){
//            recordButton.setOnClickListener(null);
//        }
        button.setOnClickListener(onRecordButtonClicked);
    }

    public void changeRecognizedTextConsumer(TextView textView){
        recognizedTextConsumer = textView;
    }

    @Override
    public void refresh(boolean isContinuousRecognitionEnabled) {
        setContinuousRecognition(isContinuousRecognitionEnabled);
    }
}
