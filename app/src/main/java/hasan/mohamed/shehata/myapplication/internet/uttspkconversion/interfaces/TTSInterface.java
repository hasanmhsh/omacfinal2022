package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces;


import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.overload.Utt2SpkOverload;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.TTSRSP;

public interface TTSInterface {
    TTSRSP fetch(Utt2SpkOverload request);
}
