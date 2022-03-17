package hasan.mohamed.shehata.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverloadedPingResult {
    @SerializedName("id")
    private int id;

    @SerializedName("success")
    private boolean isSuccessfull;

    @SerializedName("users")
    private List<User> allUsers;

    @SerializedName("messages")
    private List<Message> allUserReceivedMessages;

    public List<Group> getMyGroups() {
        return groups;
    }

    @SerializedName("groups")
    private List<Group> groups;

    public int getId() {
        return id;
    }

    public boolean getIsSuccessfull() {
        return isSuccessfull;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public List<Message> getAllUserReceivedMessages() {
        return allUserReceivedMessages;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public void setAllUserReceivedMessages(List<Message> allUserReceivedMessages) {
        this.allUserReceivedMessages = allUserReceivedMessages;
    }
}
