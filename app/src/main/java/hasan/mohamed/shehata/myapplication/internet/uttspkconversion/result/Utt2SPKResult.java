package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.SpeakerType;


public class Utt2SPKResult implements Serializable {
    @SerializedName("voices")
    private List<Speakers> speakers;

    public List<Speakers> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speakers> speakers) {
        this.speakers = speakers;
    }

    public static class Speakers {
        @SerializedName("languageCodes")
        private List<String> lng;
        @SerializedName("name")
        private String title;
        @SerializedName("ssmlGender")
        private SpeakerType spkrType;
        @SerializedName("naturalSampleRateHertz")
        private Integer frequency;

        public List<String> getLng() {
            return lng;
        }

        public String getTitle() {
            return title;
        }

        public SpeakerType getSpkrType() {
            return spkrType;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void setLng(List<String> lng) {
            this.lng = lng;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSpkrType(SpeakerType spkrType) {
            this.spkrType = spkrType;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }
    }
}
