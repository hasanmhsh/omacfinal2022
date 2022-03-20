package hasan.mohamed.shehata.myapplication.types;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.Message;

public interface NewGroupsMessagesConsumer {
    public void newMessage(long groupid, Message message);
    public void sendAndSaveThisMessage(Message message);
    public void deleteMessage(long messageid);
    public void unreadMessages(List<Message> unreadMessages);
    public long getGroupId();
    void updateGroupImage();
}