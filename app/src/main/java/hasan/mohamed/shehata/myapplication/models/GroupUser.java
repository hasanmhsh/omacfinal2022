package hasan.mohamed.shehata.myapplication.models;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


import hasan.mohamed.shehata.myapplication.types.GroupRole;
import kotlin.jvm.Transient;

public class GroupUser {



    
    @SerializedName("group")
    private Group group;

    @SerializedName("user")
    private User user;

    public GroupUser() {
        grouprole = GroupRole.SEND_RECEIVE;
    }

    public GroupUser(Group group, User user, GroupRole grouprole) {
        this.group = group;
        this.user = user;
        this.grouprole = grouprole;
    }

    public GroupRole getGrouprole() {
        return grouprole;
    }

    public void setGrouprole(GroupRole grouprole) {
        this.grouprole = grouprole;
    }

    @SerializedName("grouprole")
    private GroupRole grouprole;

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    @SerializedName("createddate")
    @PrimaryKey(autoGenerate = true)
    private String createddate;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
