package hasan.mohamed.shehata.myapplication.internet.uttspkconversion;


import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.TTSInterface;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.TTSInterfaceImplementation;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.UtteranceInterface;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces.UtteranceInterfaceImpl;

public class AllRemoteServicesOrganizer {

    public static Utt2SpkRemoteService create(String token) {
        RemoteAuthSettings settings = new RemoteAuthSettings(token);
        return create(settings);
    }

    public static Utt2SpkRemoteService create(RemoteAuthSettings settings) {
        TTSInterface TTSInterface = new TTSInterfaceImplementation(settings);
        UtteranceInterface utteranceInterface = new UtteranceInterfaceImpl(settings);
        return new Utt2SpkRemoteService(TTSInterface, utteranceInterface);
    }
}
