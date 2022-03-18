package hasan.mohamed.shehata.myapplication.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverloadedPingResult {
    @JsonIgnore
    @SerializedName("id")
    private int id;

    @JsonIgnore
    @SerializedName("success")
    private boolean isSuccessfull;


    @JsonIgnore
    @SerializedName("users")
    private List<User> users;


    @JsonIgnore
    @SerializedName("messages")
    private List<Message> messages;

    @JsonIgnore
    public List<Group> getMyGroups() {
        return groups;
    }

    @JsonProperty("groups")
    public List<Group> getGroups() {
        return groups;
    }

    @JsonAnyGetter
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @JsonIgnore
    @SerializedName("groups")
    private List<Group> groups;

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonIgnore
    public boolean getIsSuccessfull() {
        return isSuccessfull;
    }

    @JsonProperty("users")
    public List<User> getUsers() {
        return users;
    }


    @JsonProperty("messages")
    public List<Message> getMessages() {
        return messages;
    }

    @JsonProperty("users")
    public void setUsers(List<User> users) {
        this.users = users;
    }


    @JsonProperty("messages")
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
