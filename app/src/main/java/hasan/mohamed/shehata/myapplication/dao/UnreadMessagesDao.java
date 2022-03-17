package hasan.mohamed.shehata.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.UnreadReceivedMessage;

@Dao
public interface UnreadMessagesDao {
    @Query("SELECT * FROM unreadreceivedmessage")
    List<UnreadReceivedMessage> getAll();

    @Delete
    void delete(UnreadReceivedMessage unreadReceivedMessage);

    @Query("DELETE FROM unreadreceivedmessage WHERE messageid = :id")
    void deleteUnreadReceivedMessageNotificationById(long id);

    @Insert
    void insertAll(UnreadReceivedMessage... unreadReceivedMessages);

    @Query("DELETE FROM unreadreceivedmessage WHERE senderid = :senderid")
    void deleteSenderUnreadNotifications(long senderid);

    @Query("DELETE FROM unreadreceivedmessage WHERE groupid = :groupid")
    void deleteGroupUnreadNotifications(long groupid);

    @Query("SELECT * FROM unreadreceivedmessage WHERE senderid = :senderid")
    LiveData<List<UnreadReceivedMessage>> getSenderNotifications(long senderid);
}
