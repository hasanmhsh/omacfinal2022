package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Message;

public interface TranslationReadyHandler {
    public void translationDone(Message messageToBeSent);
}
