package hasan.mohamed.shehata.myapplication.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class MessageDeletionResult {
//    @JsonProperty("isDeletionDoneSuccessfully")
    @JsonIgnore
//    @SerializedName("isDeletionDoneSuccessfully")
    private boolean isDeletionDoneSuccessfully;

    @JsonIgnore
//    @SerializedName("deletedMessageId")
    private int deletedMessageId;

    @JsonIgnore
//    @JsonProperty("isDeletionDoneSuccessfully")
    public boolean isDeletionDoneSuccessfully() {
        return isDeletionDoneSuccessfully;
    }

    @JsonIgnore
//    @JsonProperty("deletedMessageId")
    public int getDeletedMessageId() {
        return deletedMessageId;
    }
}
