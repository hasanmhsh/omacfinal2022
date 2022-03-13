package hasan.mohamed.shehata.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class SMS {
    @SerializedName("destinationnumber")
    private String destinationnumber;

    @SerializedName("message")
    private String message;

    public SMS() {
    }

    public SMS(String destinationnumber, String message) {
        this.destinationnumber = destinationnumber;
        this.message = message;
    }

    public String getDestinationnumber() {
        return destinationnumber;
    }

    public void setDestinationnumber(String destinationnumber) {
        this.destinationnumber = destinationnumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
