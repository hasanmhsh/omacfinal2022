package hasan.mohamed.shehata.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE userid=:id")
    LiveData<User> loadUser(long id);

    @Query("SELECT * FROM user WHERE userid=:id")
    User loadUserBlockable(long id);

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertUser(User user);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("UPDATE user SET userid=:newid,status=:status WHERE userid=:oldid")
    void updateUserIdAndStatus(long oldid, long newid, StatusOfServerObject status);


}
