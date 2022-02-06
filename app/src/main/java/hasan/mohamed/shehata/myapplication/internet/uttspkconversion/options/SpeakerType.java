package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options;


public enum SpeakerType {
    MALE,
    FEMALE,
    SSML_VOICE_GENDER_UNSPECIFIED,
    NEUTRAL,
    NONE;


    public static SpeakerType utt2spk(String typeOfSpkr) {
        if (typeOfSpkr.compareTo(SSML_VOICE_GENDER_UNSPECIFIED.toString()) == 0) {
            return SSML_VOICE_GENDER_UNSPECIFIED;
        }
        if (typeOfSpkr.compareTo(MALE.toString()) == 0) {
            return MALE;
        }
        if (typeOfSpkr.compareTo(FEMALE.toString()) == 0) {
            return FEMALE;
        } else if (typeOfSpkr.compareTo(NEUTRAL.toString()) == 0) {
            return NEUTRAL;
        }

        throw new NullPointerException("Speaker not exist");
    }
}
