package hasan.mohamed.shehata.myapplication;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import hasan.mohamed.shehata.myapplication.dao.MessageDao;
import hasan.mohamed.shehata.myapplication.dao.MissedCallsDao;
import hasan.mohamed.shehata.myapplication.dao.UnreadMessagesDao;
import hasan.mohamed.shehata.myapplication.dao.UserDao;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.MissedCall;
import hasan.mohamed.shehata.myapplication.models.UnreadReceivedMessage;
import hasan.mohamed.shehata.myapplication.models.User;


@Database(entities = {User.class , Message.class , UnreadReceivedMessage.class , MissedCall.class
}, version = 24, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    public abstract UnreadMessagesDao unreadMessagesDao();
    public abstract MissedCallsDao missedCallsDao();

    private static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "quizdatabase.db")
//Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                            // To simplify the exercise, allow queries on the main thread.
                            // Don't do this on a real app!
                            ////////.allowMainThreadQueries()//----hasan uncomment this------//
                            // recreate the database if necessary
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    private static void destroyInstance() {
        INSTANCE = null;
    }

    public static void callInActivityOnCreate(Context context){
        getDatabase(context);
    }

    public static void callInActivityOnDistroy(){
        destroyInstance();
    }

    public static UserDao getUserDao(){
        return INSTANCE.userDao();
    }

    public static MessageDao getMessageDao(){
        return INSTANCE.messageDao();
    }
    public static UnreadMessagesDao getUnreadReceivedMessageNotificationDao(){
        return INSTANCE.unreadMessagesDao();
    }

    public static MissedCallsDao getMissedCallDao(){
        return INSTANCE.missedCallsDao();
    }
}

