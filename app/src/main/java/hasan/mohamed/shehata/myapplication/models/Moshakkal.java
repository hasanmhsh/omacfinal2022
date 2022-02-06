package hasan.mohamed.shehata.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class Moshakkal {
    @SerializedName("text")
    private String plainText;

    public Moshakkal(String plainText) {
        this.plainText = plainText;
    }

    public String getPlainText() {
        return plainText;
    }
}
