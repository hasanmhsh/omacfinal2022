package hasan.mohamed.shehata.myapplication.models;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import hasan.mohamed.shehata.myapplication.types.GroupRole;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;


public class Group extends BaseObservable implements ListItemBindableItemContentProvider, Comparable<Group> {

    @JsonProperty("groupid")
    @SerializedName("groupid")
    private long groupid;

    @JsonProperty("name")
    @SerializedName("name")
    private String name;

    @JsonProperty("defaultgroupusersrole")
    public GroupRole getDefaultgroupusersrole() {
        return defaultgroupusersrole;
    }

    @JsonProperty("defaultgroupusersrole")
    public void setDefaultgroupusersrole(GroupRole defaultgroupusersrole) {
        this.defaultgroupusersrole = defaultgroupusersrole;
    }

    @JsonProperty("defaultgroupusersrole")
    @SerializedName("defaultgroupusersrole")
    private GroupRole defaultgroupusersrole;


    @JsonIgnore
    @SerializedName("groupusers")
    private List<GroupUser> groupusers;

    @JsonProperty("groupusers")
    public List<GroupUser> getGroupusers() {
        return groupusers;
    }

    @JsonProperty("groupusers")
    public void setGroupusers(List<GroupUser> groupusers) {
        this.groupusers = groupusers;
    }

    @JsonIgnore
    public boolean isHighLighted() {
        return isHighLighted;
    }

    @JsonIgnore
    public void setHighLighted(boolean highLighted) {
        isHighLighted = highLighted;
    }

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

    @JsonIgnore
    private transient int numberOfUnreadMessages;

    @JsonIgnore
    public int getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }

    @JsonIgnore
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
    @JsonProperty("groupid")
    public long getGroupid() {
        return groupid;
    }

    @JsonProperty("groupid")
    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public String getPrimaryText() {
        return name;
    }

    @JsonIgnore
    @Override
    public String getSecondaryText() {
        return "group";
    }

    @JsonIgnore
    @Override
    public long getID() {
        return groupid;
    }

    @JsonIgnore
    @Override
    public void setIsGroupAdmin(boolean isGroupAdmin) {

    }

    @JsonIgnore
    @Bindable
    @Override
    public int getIsAdminCheckBoxVisibility() {
        return View.GONE;
    }
    @Override
    public boolean getIsGroupAdmin() {
        return false;
    }

    @JsonIgnore
    @Override
    public void drawLogo(ImageView view) {

    }

    @JsonIgnore
    private boolean isHighLighted;
    @JsonIgnore
    @Override
    public boolean getIsHighLighted() {
        return isHighLighted;
    }

    @JsonIgnore
    @Override
    public void setIsHighLighted(boolean isHighLighted) {
        this.isHighLighted=isHighLighted;
    }

    @JsonIgnore
    @Override
    public void toggleHighLight() {
        isHighLighted = !isHighLighted;
    }

    @JsonIgnore
    @Bindable
    public int getHighlightedFilterVisibility(){
        if(isHighLighted)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    @JsonIgnore
    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @JsonIgnore
    @Override
    public void disposeResources() {

    }

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public boolean equals(@Nullable Object obj) {
        Group other = (Group) obj;
        return (this.groupid == other.groupid && this.numberOfUnreadMessages == other.numberOfUnreadMessages);
    }

    @JsonIgnore
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
