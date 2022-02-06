package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.overload;


import com.google.gson.annotations.SerializedName;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceOptions;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceSettings;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceResult;

public class Utt2SpkOverload {
    @SerializedName("input")
    private UtteranceResult payload;

    @SerializedName("voice")
    private UtteranceOptions utteranceTalk;

    @SerializedName("audioConfig")
    private UtteranceSettings utteranceSettings;

    public Utt2SpkOverload(UtteranceResult payload, UtteranceOptions GCPVoice, UtteranceSettings utteranceSettings) {
        this.payload = payload;
        utteranceTalk = GCPVoice;
        this.utteranceSettings = utteranceSettings;
    }

    public UtteranceResult getPayload() {
        return payload;
    }

    public UtteranceOptions getUtteranceTalk() {
        return utteranceTalk;
    }

    public UtteranceSettings getAudioConfig() {
        return utteranceSettings;
    }

    public void setPayload(UtteranceResult payload) {
        this.payload = payload;
    }

    public void setUtteranceTalk(UtteranceOptions utteranceTalk) {
        this.utteranceTalk = utteranceTalk;
    }

    public void setAudioConfig(UtteranceSettings utteranceSettings) {
        this.utteranceSettings = utteranceSettings;
    }
}
