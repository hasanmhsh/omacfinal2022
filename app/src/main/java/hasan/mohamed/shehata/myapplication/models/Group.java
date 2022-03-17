package hasan.mohamed.shehata.myapplication.models;

import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import hasan.mohamed.shehata.myapplication.types.GroupRole;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;


public class Group implements ListItemBindableItemContentProvider, Serializable, Comparable<Group> {
    @SerializedName("groupid")
    private long groupid;

    @SerializedName("name")
    private String name;

    public GroupRole getDefaultgroupusersrole() {
        return defaultgroupusersrole;
    }

    public void setDefaultgroupusersrole(GroupRole defaultgroupusersrole) {
        this.defaultgroupusersrole = defaultgroupusersrole;
    }

    @SerializedName("defaultgroupusersrole")
    private GroupRole defaultgroupusersrole;





    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    @SerializedName("createddate")
    @PrimaryKey(autoGenerate = true)
    private String createddate;

    private transient int numberOfUnreadMessages;

    public int getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }

    public void setNumberOfUnreadMessages(int numberOfUnreadMessages) {
        this.numberOfUnreadMessages = numberOfUnreadMessages;
    }

    public Group() {
        this.defaultgroupusersrole = GroupRole.SEND_RECEIVE;
    }

    public Group(long groupid, String name, GroupRole defaultgroupusersrole) {
        this.groupid = groupid;
        this.name = name;
        this.defaultgroupusersrole = defaultgroupusersrole;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryText() {
        return name;
    }

    @Override
    public String getSecondaryText() {
        return "group";
    }

    @Override
    public long getID() {
        return groupid;
    }

    @Override
    public void drawLogo(ImageView view) {

    }

    private boolean isHighLighted;
    @Override
    public boolean getIsHighLighted() {
        return isHighLighted;
    }

    @Override
    public void setIsHighLighted(boolean isHighLighted) {
        this.isHighLighted=isHighLighted;
    }
    @Override
    public void toggleHighLight() {
        isHighLighted = !isHighLighted;
    }
    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @Override
    public void disposeResources() {

    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        if(item == null)
            return false;
        else {
            if (groupid == ((Group) item).groupid) {
                if (numberOfUnreadMessages == ((Group) item).numberOfUnreadMessages) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Group other = (Group) obj;
        return (this.groupid == other.groupid && this.numberOfUnreadMessages == other.numberOfUnreadMessages);
    }

    @Override
    public int compareTo(Group group) {
        if(getNumberOfUnreadMessages() > group.getNumberOfUnreadMessages())
            return -1;
        else if (getNumberOfUnreadMessages() < group.getNumberOfUnreadMessages())
            return 1;
        else {//if (getNumberOfUnreadMessages() == group.getNumberOfUnreadMessages()){
            if(groupid < group.groupid)
                return -1;
            else if(groupid > group.groupid)
                return 1;
            else
                return 0;
        }
    }
}
