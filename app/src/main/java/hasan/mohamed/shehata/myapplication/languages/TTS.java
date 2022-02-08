package hasan.mohamed.shehata.myapplication.languages;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.AllRemoteServicesOrganizer;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.Utt2SpkRemoteService;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.entities.UtterancePack;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.CompressionOfUtterance;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceOptions;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceSettings;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.types.LanguageToTTSLocaleMapper;

//import com.google.cloud.texttospeech.v1.UtteranceSettings;
//import com.google.cloud.texttospeech.v1.CompressionOfUtterance;
//import com.google.cloud.texttospeech.v1.SpeakerType;
//import com.google.cloud.texttospeech.v1.UtteranceResult;
//import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
//import com.google.cloud.texttospeech.v1.TextToSpeechClient;
//import com.google.cloud.texttospeech.v1.UtteranceOptions;
//import com.google.protobuf.ByteString;
//import com.google.cloud.texttospeech.v1.UtteranceSettings;
//import com.google.cloud.texttospeech.v1.CompressionOfUtterance;

import java.util.HashMap;

public class TTS {

    public static class LanguageCodeAndVoice{
        private String lang;
        private String voiceCode;

        public LanguageCodeAndVoice(String lang, String voiceCode) {
            this.lang = lang;
            this.voiceCode = voiceCode;
        }

        public String getLang() {
            return lang;
        }

        public String getVoiceCode() {
            return voiceCode;
        }
    }
    private static final HashMap<Language, LanguageCodeAndVoice> cloudModels = new HashMap(){{
//        put(Language.English, new LanguageCodeAndVoice("en-GB", "en-GB-Wavenet-B"));

        put(Language.English, new LanguageCodeAndVoice("en-GB", "en-US-Wavenet-B"));
        put(Language.Arabic, new LanguageCodeAndVoice("ar-XA", "ar-XA-Wavenet-C"));
        put(Language.Chinese, new LanguageCodeAndVoice("yue-HK", "yue-HK-Standard-B"));
        put(Language.German, new LanguageCodeAndVoice("de-DE", "de-DE-Wavenet-B"));
        put(Language.Spanish, new LanguageCodeAndVoice("es-ES", "es-ES-Wavenet-B"));
        put(Language.French, new LanguageCodeAndVoice("fr-FR", "fr-FR-Wavenet-B"));
        put(Language.Italian, new LanguageCodeAndVoice("it-IT", "it-IT-Wavenet-D"));
        put(Language.Portuguese, new LanguageCodeAndVoice("pt-PT", "pt-PT-Wavenet-C"));
        put(Language.Russian, new LanguageCodeAndVoice("ru-RU", "ru-RU-Wavenet-D"));
        put(Language.Turkish, new LanguageCodeAndVoice("tr-TR", "tr-TR-Wavenet-E"));
        put(Language.Dutch, new LanguageCodeAndVoice("nl-NL", "nl-NL-Wavenet-C"));
    }};


    private TextToSpeech textToSpeech;
    private Context context;
    private Language language;
    private Utt2SpkRemoteService utt2SpkRemoteService;

    public TTS(Context context, Language language) {
        this.context = context;
        this.language = language;
        createTTS();
    }



    private void ttsGoogleCloud(String text){

// Set languageCode and voiceName, Rate and pitch parameter.

// start speak
        utt2SpkRemoteService.start("you want speak something");

// stop speak
        utt2SpkRemoteService.stop();

// pause speak
        utt2SpkRemoteService.pause();

// resume speak
        utt2SpkRemoteService.resume();
    }



//    private static void arabicTTS(String moshakkalText, Context context) throws Exception {
//        // Instantiates a client
//        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
//            // Set the text input to be synthesized
//            UtteranceResult input = UtteranceResult.newBuilder().setText(moshakkalText).build();
//
//            // Build the voice request, select the language code ("en-US") and the ssml voice gender
//            // ("neutral")
//            UtteranceOptions voice =
//                    UtteranceOptions.newBuilder()
//                            .setLanguageCode("ar-XA")//voice code ->    ar-XA-Wavenet-C
//                            .setSsmlGender(SpeakerType.MALE)
//                            .build();
//
//            // Select the type of audio file you want returned
//            UtteranceSettings audioConfig =
//                    UtteranceSettings.newBuilder().setAudioEncoding(CompressionOfUtterance.MP3).build();
//
//            // Perform the text-to-speech request on the text input with the selected voice parameters and
//            // audio file type
//            SynthesizeSpeechResponse response =
//                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
//
//            // Get the audio contents from the response
//            ByteString audioContents = response.getAudioContent();
//
//            // Write the response to the output file.
//            try (FileOutputStream out = new FileOutputStream("output.mp3")) {
//                out.write(audioContents.toByteArray());
//                System.out.println("Audio content written to file \"output.mp3\"");
//                MediaPlayer mp = MediaPlayer.create(context, Uri.parse("./output.mp3"));
//                mp.start();
//            }
//        }
//    }
//


    private void createTTS() {

        if(language == Language.Arabic){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        // Set the ApiKey and create Utt2SpkRemoteService.
                        createTTSAsyncArabic();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(LanguageToTTSLocaleMapper.map(language));
                    }
                }
            });
        }
    }

    private void createTTSAsyncArabic() {
//        utt2SpkRemoteService = AllRemoteServicesOrganizer.create("AIzaSyDk-7P9RSXf6MH1uzcktKTYX0LcYspG0S8"); //debug key

        utt2SpkRemoteService = AllRemoteServicesOrganizer.create(Utils.getGoogleKey()); //release key

// Load google cloud UtterancePack and select the languageCode and voiceName with index (0 ~ N).
        UtterancePack utterancePack = utt2SpkRemoteService.load();
        String languageCode = utterancePack.listLNGS()[0];
        String voiceName = utterancePack.speakers(languageCode)[0];
        utt2SpkRemoteService.setVoiceSelectionParams(new UtteranceOptions(cloudModels.get(language).getLang(), cloudModels.get(language).voiceCode))
                .setAudioConfig(new UtteranceSettings(CompressionOfUtterance.MP3, 0.85f, 5f));
    }

    public void speak(final String text){
        if(cloudModels.containsKey(language)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(utt2SpkRemoteService == null)
                            createTTSAsyncArabic();
                        utt2SpkRemoteService.start(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        }
        else {
            if (textToSpeech == null) {
                createTTS();
            }
            if (!textToSpeech.isSpeaking())
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void release(){
        if(textToSpeech != null) {
            try{ textToSpeech.stop();}catch (Exception e){e.printStackTrace();}
            if(textToSpeech != null)
                try{ textToSpeech.shutdown();}catch (Exception e){e.printStackTrace();}
        }
        if(utt2SpkRemoteService != null){
            try{ utt2SpkRemoteService.stop();}catch (Exception e){e.printStackTrace();}
            if(utt2SpkRemoteService != null)
                try{ utt2SpkRemoteService.close();}catch (Exception e){e.printStackTrace();}

        }
        textToSpeech=null;
    }
}
