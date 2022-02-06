package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;

public interface NewMessagesConsumer {
    public void newMessage(long senderUserId, Message message);
    public void sendAndSaveThisMessage(Message message);
}
