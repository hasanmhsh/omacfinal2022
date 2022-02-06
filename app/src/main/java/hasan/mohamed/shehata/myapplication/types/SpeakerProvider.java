package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Message;

public interface SpeakerProvider {
    public void speak(Message message);
    public boolean isForCall();
}
