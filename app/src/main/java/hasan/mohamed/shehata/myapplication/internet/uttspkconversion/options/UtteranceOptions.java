package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options;


import com.google.gson.annotations.SerializedName;

public class UtteranceOptions {
    @SerializedName("languageCode")
    private String nlp;
    @SerializedName("name")
    private String title;
    @SerializedName("ssmlGender")
    private SpeakerType typeOfSpkr;

    public UtteranceOptions(String nlp, String title) {
        this(nlp, title, SpeakerType.SSML_VOICE_GENDER_UNSPECIFIED);
    }

    public UtteranceOptions(String nlp, String title, SpeakerType typeSpkr) {
        this.nlp = nlp;
        this.title = title;
        this.typeOfSpkr = typeSpkr;
    }

    public String getNlp() {
        return nlp;
    }

    public String getTitle() {
        return title;
    }

    public SpeakerType getTypeOfSpkr() {
        return typeOfSpkr;
    }
}
