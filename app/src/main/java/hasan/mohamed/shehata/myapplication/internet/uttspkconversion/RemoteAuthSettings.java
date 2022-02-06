package hasan.mohamed.shehata.myapplication.internet.uttspkconversion;


import com.google.gson.annotations.SerializedName;

public class RemoteAuthSettings {
    public void setAuthCred(String authCred) {
        this.authCred = authCred;
    }

    @SerializedName("mApiKey")
    private String authCred;
    @SerializedName("mApiKeyHeader")
    private String authCredParams = "X-Goog-Api-Key";
    @SerializedName("mSynthesizeEndpoint")
    private String uttURLS = "https://texttospeech.googleapis.com/v1/text:synthesize";
    @SerializedName("mVoicesEndpoint")
    private String ut2pkurls = "https://texttospeech.googleapis.com/v1/voices";

    public RemoteAuthSettings(String accessToken) {
        authCred = accessToken;
    }

    public String getAuthCred() {
        return authCred;
    }

    public String getAuthCredParams() {
        return authCredParams;
    }

    public String getuttURLS() {
        return uttURLS;
    }

    public String getut2pkurls() {
        return ut2pkurls;
    }
}
