package hasan.mohamed.shehata.myapplication.models;

import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;

import hasan.mohamed.shehata.myapplication.types.GroupRole;
import kotlin.jvm.Transient;

public class GroupUser implements Serializable {



    @JsonIgnoreProperties(value = "groupusers")
    @JsonProperty("group")
    @SerializedName("group")
    private Group group;

    @JsonProperty("group")
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
    public GroupUser(Group group, User user, boolean isAdmin) {
        this.group = group;
        this.user = user;
        if(isAdmin)
            this.grouprole = GroupRole.ADMIN;
        else
            this.grouprole = GroupRole.SEND_RECEIVE;
    }

    @JsonProperty("grouprole")
    public GroupRole getGrouprole() {
        return grouprole;
    }

    @JsonProperty("grouprole")
    public void setGrouprole(GroupRole grouprole) {
        this.grouprole = grouprole;
    }

    @JsonProperty("grouprole")
    @SerializedName("grouprole")
    private GroupRole grouprole;

    @JsonProperty("createddate")
    public String getCreateddate() {
        return createddate;
    }

    @JsonProperty("createddate")
    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }


    @JsonProperty("createddate")
    @SerializedName("createddate")
    @PrimaryKey(autoGenerate = true)
    private String createddate;

    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

}
