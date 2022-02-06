package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options;


import com.google.gson.annotations.SerializedName;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.NotExistError;

public class UtteranceSettings {
    @SerializedName("audioEncoding")
    private CompressionOfUtterance compressionTech;
    @SerializedName("speakingRate")
    private float samplePerSec;
    @SerializedName("pitch")
    private float thickness;
    @SerializedName("volumeGainDb")
    private int power;
    @SerializedName("sampleRateHertz")
    private int frequency;
    @SerializedName("effectsProfileId")
    private String[] karaoke;

    public UtteranceSettings(CompressionOfUtterance compressionTech, float samplePerSec, float velocity) {
        setCompressionTech(compressionTech);
        setSamplePerSec(samplePerSec);
        setThickness(velocity);
    }

    public UtteranceSettings() {
        compressionTech = CompressionOfUtterance.LINEAR16;
        samplePerSec = 1.0f;
        thickness = 0.0f;
        power = 0;
        frequency = 0;
    }

    public CompressionOfUtterance getCompressionTech() {
        return compressionTech;
    }

    public float getSamplePerSec() {
        return samplePerSec;
    }

    public float getThickness() {
        return thickness;
    }

    public int getPower() {
        return power;
    }

    public int getFrequency() {
        return frequency;
    }

    public String[] getKaraoke() {
        return karaoke;
    }

    public void setCompressionTech(CompressionOfUtterance compressionTech) {
        this.compressionTech = compressionTech;
    }

    public void setSamplePerSec(float samplePerSec) {
        if (samplePerSec < 0.25 || samplePerSec > 4.0) {
            throw new NotExistError("Unsupported");
        }

        this.samplePerSec = samplePerSec;
    }

    public void setThickness(float thickness) {
        if (samplePerSec <= -20 || samplePerSec >= 20) {
            throw new NotExistError("The pitch range is -20 ~ 20, your pitch is " + thickness);
        }
        this.thickness = thickness;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setKaraoke(String[] karaoke) {
        this.karaoke = karaoke;
    }
}
