package hasan.mohamed.shehata.myapplication.types;

import com.google.gson.annotations.SerializedName;

public class JSONResult {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }
}
