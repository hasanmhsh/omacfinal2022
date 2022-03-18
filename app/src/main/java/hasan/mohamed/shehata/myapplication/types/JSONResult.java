package hasan.mohamed.shehata.myapplication.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class JSONResult {
    @JsonIgnore
//    @JsonProperty("result")
    @SerializedName("result")
    private String result;

    @JsonIgnore
//    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonIgnore
//    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }
}
