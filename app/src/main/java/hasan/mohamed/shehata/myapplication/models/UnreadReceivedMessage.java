package hasan.mohamed.shehata.myapplication.models;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UnreadReceivedMessage {

    @PrimaryKey(autoGenerate = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ColumnInfo(name = "messageid")
    private long messageid;

    @ColumnInfo(name = "senderid")
    private long senderid;

    public long getMessageid() {
        return messageid;
    }

    public void setMessageid(long messageid) {
        this.messageid = messageid;
    }

    public long getSenderid() {
        return senderid;
    }

    public void setSenderid(long senderid) {
        this.senderid = senderid;
    }
}
