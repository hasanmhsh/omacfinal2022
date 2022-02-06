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
}
