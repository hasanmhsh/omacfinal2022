package hasan.mohamed.shehata.myapplication.types;

import com.google.gson.annotations.SerializedName;

public class MessageDeletionResult {
    @SerializedName("success")
    private boolean isDeletionDoneSuccessfully;

    @SerializedName("deleted")
    private int deletedMessageId;

    public boolean isDeletionDoneSuccessfully() {
        return isDeletionDoneSuccessfully;
    }

    public int getDeletedMessageId() {
        return deletedMessageId;
    }
}
