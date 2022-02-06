package hasan.mohamed.shehata.myapplication.types;

public interface AsrResultCallbacks{
    public void voiceRecognized(String result);
    //Partial is during speaking
    public void partialVoiceRecognized(String partialResult);
}