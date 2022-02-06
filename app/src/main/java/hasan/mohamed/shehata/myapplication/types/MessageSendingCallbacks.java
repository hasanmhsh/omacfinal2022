package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Message;

public interface MessageSendingCallbacks {
    public void onMessageSendingSuccess(Message sentMessage);
    public void onMessageSendingFailure();
}
