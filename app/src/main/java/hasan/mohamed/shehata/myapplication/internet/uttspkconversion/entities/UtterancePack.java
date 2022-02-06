package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options.UtteranceOptions;


public class UtterancePack {
    private HashMap<String, List<UtteranceOptions>> pack = new HashMap<>();

    public void add(String lng, UtteranceOptions options) {
        List<UtteranceOptions> utterances = pack.get(lng);
        if (utterances == null) {
            utterances = new ArrayList<>();
            pack.put(lng, utterances);
        }

        utterances.add(options);
    }

    public String[] listLNGS() {
        if (pack.size() == 0) {
            throw new NullPointerException("Not supported lng");
        }

        List<String> supportedLngs = new ArrayList<>(pack.keySet());
        return supportedLngs.stream()
                .sorted(String::compareTo)
                .toArray(String[]::new);
    }



    public void update(UtterancePack utterancePack) {
        pack = new HashMap<>(utterancePack.pack);
    }

    public void reset() {
        for (HashMap.Entry<String, List<UtteranceOptions>> entry : pack.entrySet()) {
            List<UtteranceOptions> supportedUtterances = entry.getValue();
            supportedUtterances.clear();
        }

        pack.clear();
    }
    public UtteranceOptions getSpeceficSpeaker(String lng, String spkrTitle) {
        if (exists(lng)) {
            throw new NullPointerException("Not supported");
        }

        if (exists(spkrTitle)) {
            throw new NullPointerException("Not supported");
        }

        Optional<UtteranceOptions> remoteSpeaker = getSupportedRemoteSpeakers(lng).stream()
                .filter(m -> m.getTitle().equals(spkrTitle))
                .findFirst();

        if (!remoteSpeaker.isPresent()) {
            throw new NullPointerException("Not supported" + spkrTitle);
        }

        return remoteSpeaker.get();
    }


    public int length() {
        return pack.size();
    }

    private List<UtteranceOptions> getSupportedRemoteSpeakers(String lng) {
        List<UtteranceOptions> remoteSpeakers = pack.get(lng);
        if (remoteSpeakers == null) {
            throw new NullPointerException("Unsupported");
        }

        return remoteSpeakers;
    }

    private boolean exists(String str) {
        return str == null || str.length() == 0;
    }

    public String[] speakers(String lng) {
        if (exists(lng)) {
            throw new NullPointerException("Not supported operation");
        }

        return getSupportedRemoteSpeakers(lng).stream()
                .map(UtteranceOptions::getTitle)
                .sorted(String::compareTo)
                .toArray(String[]::new);
    }
}
