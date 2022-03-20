package hasan.mohamed.shehata.myapplication.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class MissedCall implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ColumnInfo(name = "callerid")
    private long callerid;

    public long getCallerid() {
        return callerid;
    }

    public void setCallerid(long callerid) {
        this.callerid = callerid;
    }
}
