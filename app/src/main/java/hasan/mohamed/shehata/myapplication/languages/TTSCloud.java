package hasan.mohamed.shehata.myapplication.languages;// Imports the Google Cloud client library
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.texttospeech.v1.stub.GrpcTextToSpeechStub;
import com.google.cloud.texttospeech.v1.stub.TextToSpeechStubSettings;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import hasan.mohamed.shehata.myapplication.languages.TTS;
import hasan.mohamed.shehata.myapplication.models.Language;
import io.grpc.ManagedChannel;

/**
 * Google Cloud TextToSpeech API sample application. Example usage: mvn package exec:java
 * -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class TTSCloud {
    private Context context;
    private static Handler mHandler;










































    private static final HashMap<Language, TTS.LanguageCodeAndVoice> cloudModels = new HashMap(){{
//        put(Language.English, new LanguageCodeAndVoice("en-GB", "en-GB-Wavenet-B"));

        put(Language.English, new TTS.LanguageCodeAndVoice("en-GB", "en-US-Wavenet-B"));
        put(Language.Arabic, new TTS.LanguageCodeAndVoice("ar-XA", "ar-XA-Wavenet-C"));
        put(Language.Chinese, new TTS.LanguageCodeAndVoice("yue-HK", "yue-HK-Standard-B"));
        put(Language.German, new TTS.LanguageCodeAndVoice("de-DE", "de-DE-Wavenet-B"));
        put(Language.Spanish, new TTS.LanguageCodeAndVoice("es-ES", "es-ES-Wavenet-B"));
        put(Language.French, new TTS.LanguageCodeAndVoice("fr-FR", "fr-FR-Wavenet-B"));
        put(Language.Italian, new TTS.LanguageCodeAndVoice("it-IT", "it-IT-Wavenet-D"));
        put(Language.Portuguese, new TTS.LanguageCodeAndVoice("pt-PT", "pt-PT-Wavenet-C"));
        put(Language.Russian, new TTS.LanguageCodeAndVoice("ru-RU", "ru-RU-Wavenet-D"));
        put(Language.Turkish, new TTS.LanguageCodeAndVoice("tr-TR", "tr-TR-Wavenet-E"));
        put(Language.Dutch, new TTS.LanguageCodeAndVoice("nl-NL", "nl-NL-Wavenet-C"));
    }};

    /** Demonstrates using the Text-to-Speech API. */
    public static void synthize(String text, Language language, Context context) throws Exception {
        // Instantiates a client
        TextToSpeechClient textToSpeechClient = null;
        GrpcTextToSpeechStub grpcTextToSpeechStub = null;
        try{
            // Set the text input to be synthesized
            TextToSpeechStubSettings textToSpeechStubSettings = TextToSpeechStubSettings.newBuilder().build();
            grpcTextToSpeechStub = GrpcTextToSpeechStub.create(textToSpeechStubSettings);
            textToSpeechClient = TextToSpeechClient.create();
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode(language.symbol)
                            .setSsmlGender(SsmlVoiceGender.MALE)
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
//                System.out.println("Audio content written to file \"output.mp3\"");
                playAudio(context,"output.mp3");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{grpcTextToSpeechStub.close();}catch (Exception e){e.printStackTrace();}
            try{textToSpeechClient.close();}catch (Exception e){e.printStackTrace();}
        }
    }

    private static void playAudio(Context context, String filename){
        Uri uri = Uri.parse(filename);
        MediaPlayer mp;
        mp = MediaPlayer.create(context,uri);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        mp.setLooping(false);
        mp.start();
    }



}