package hasan.mohamed.shehata.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class MoshakkalResult {
    @SerializedName("text-result")
    private String moshakkalText;

    public String getMoshakkalText() {
        return moshakkalText;
    }
}
