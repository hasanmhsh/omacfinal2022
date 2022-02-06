package hasan.mohamed.shehata.myapplication.internet.uttspkconversion;

import android.media.MediaPlayer;
import java.io.IOException;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.entities.UtterancePack;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.TTSInterface;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.UtteranceInterface;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ConnectionError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceOptions;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceResult;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceSettings;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.overload.Utt2SpkOverload;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.TTSRSP;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.Utt2SPKResult;


public class Utt2SpkRemoteService implements AutoCloseable {
    private TTSInterface mTTSInterface;
    private UtteranceInterface mUtteranceInterface;

    private UtteranceOptions mUtteranceOptions;
    private UtteranceSettings mUtteranceSettings;

    private MediaPlayer mp;

    private int uttSize = -1;

    public Utt2SpkRemoteService(TTSInterface TTSInterface, UtteranceInterface utteranceInterface) {
        mTTSInterface = TTSInterface;
        mUtteranceInterface = utteranceInterface;
    }

    public Utt2SpkRemoteService setVoiceSelectionParams(UtteranceOptions utteranceOptions) {
        mUtteranceOptions = utteranceOptions;
        return this;
    }

    public Utt2SpkRemoteService setAudioConfig(UtteranceSettings utteranceSettings) {
        mUtteranceSettings = utteranceSettings;
        return this;
    }
    public void stop() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.reset();
            uttSize = -1;
        }
    }

    public void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            uttSize = mp.getCurrentPosition();
        }
    }

    public void resume() {
        if (mp != null && !mp.isPlaying() && uttSize != -1) {
            mp.seekTo(uttSize);
            mp.start();
        }
    }

    private void playAudio(String base64EncodedString) throws IOException {
        stop();

        String path = "data:audio/mp3;base64," + base64EncodedString;
        mp = new MediaPlayer();
        mp.setDataSource(path);
        mp.prepare();
        mp.start();
    }

    public void close() {
        stop();
        mp.release();
        mp = null;
    }
    public void start(String str) {
        if (mUtteranceOptions == null) {
            throw new NullPointerException("You forget to setVoiceSelectionParams()");
        }

        if (mUtteranceSettings == null) {
            throw new NullPointerException("You forget to setAudioConfig()");
        }

        Utt2SpkOverload overload = new Utt2SpkOverload(new UtteranceResult(str), mUtteranceOptions, mUtteranceSettings);

        try {
            TTSRSP result = mTTSInterface.fetch(overload);
            playAudio(result.getAudioContent());
        } catch (Exception e) {
            throw new ConnectionError(e);
        }
    }
    public UtterancePack load() {
        Utt2SPKResult response = mUtteranceInterface.fetch();
        UtterancePack utterancePack = new UtterancePack();

        for (Utt2SPKResult.Speakers speakers : response.getSpeakers()) {
            String languageCode = speakers.getLng().get(0);
            UtteranceOptions params = new UtteranceOptions(
                    languageCode,
                    speakers.getTitle(),
                    speakers.getSpkrType()
            );
            utterancePack.add(languageCode, params);
        }

        return utterancePack;
    }




}
