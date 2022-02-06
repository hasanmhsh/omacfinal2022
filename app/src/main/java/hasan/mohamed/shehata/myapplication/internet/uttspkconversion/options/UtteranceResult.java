package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.options;


import com.google.gson.annotations.SerializedName;

import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.NotExistError;

public class UtteranceResult {
    @SerializedName("text")
    private String str;

    public UtteranceResult(String str, String utt) {
        setStr(str);
    }


    public UtteranceResult(String str) {
        this(str, "");
    }

    public void setStr(String str) {
        if (str.length() > 5000) {
            throw  new NotExistError("The input size is limited to 5000 characters");
        }

        this.str = str;
    }

    public String getStr() {
        return str;
    }





}
