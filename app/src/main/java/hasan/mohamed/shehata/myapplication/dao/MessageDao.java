package hasan.mohamed.shehata.myapplication.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.Message;







//////NOTE hasan that !important !important !important !important !important
/*
LiveData object is like promise in javascript which returned immediately to run these method in UI thread
then you can observe it and observer method is called after data is ready in UI thread also
So when using live data no need to make background threds

lkdbklsndvbknsc!!!!

1!
 */





@Dao
public interface MessageDao {
  @Query("SELECT * FROM message")
  List<Message> getAll();

  @Query("SELECT * FROM message WHERE (senderid = :id1 AND receiverid=:id2) OR (senderid = :id2 AND receiverid=:id1)")
  LiveData<List<Message>> getMyMessages(long id1, long id2);

  @Query("SELECT * FROM message WHERE groupid=:groupid")
  LiveData<List<Message>> getMyGroupMessages(long groupid);

  @Query("SELECT * FROM message WHERE (senderid = :id) OR (receiverid = :id)")
  LiveData<List<Message>> getAllUserMessages(long id);



  @Query("SELECT * FROM message WHERE messageid = :id")
  LiveData<Message> getMessageById(long id);
//    @Query("SELECT * FROM message WHERE buddyid = :id")
//    List<Message> getBuddyMessages(int id);

  @Query("SELECT * FROM message WHERE messageid = :id")
  Message getMessageByIdBlocking(long id);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void updateMessage(Message message);

  @Insert
  void insertAll(Message... messages);

  @Delete
  void delete(Message message);

  @Query("DELETE FROM message WHERE messageid = :id")
  void deleteMessageById(long id);


  @Query("DELETE FROM message WHERE 1=1")
  void deleteAll();
}
