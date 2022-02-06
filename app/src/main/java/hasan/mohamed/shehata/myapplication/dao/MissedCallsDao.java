package hasan.mohamed.shehata.myapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.MissedCall;
import hasan.mohamed.shehata.myapplication.models.UnreadReceivedMessage;

@Dao
public interface MissedCallsDao {
  @Query("SELECT * FROM missedcall")
  List<MissedCall> getAll();

  @Delete
  void delete(MissedCall missedCall);

  @Insert
  void insertAll(MissedCall... missedCalls);

  @Query("DELETE FROM missedcall WHERE callerid = :callerid")
  void detleteCallerMissedCall(long callerid);

}
