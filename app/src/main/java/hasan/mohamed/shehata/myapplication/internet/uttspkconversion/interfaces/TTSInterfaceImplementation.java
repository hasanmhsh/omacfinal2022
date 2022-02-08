package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces;

import java.io.IOException;
import java.util.Objects;


import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.RemoteAuthSettings;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ConnectionError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ResultFetchError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.overload.Utt2SpkOverload;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.TTSRSP;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.general.JavaScriptObjectFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TTSInterfaceImplementation implements TTSInterface {

    private RemoteAuthSettings cfgOfCloud;

    public TTSInterfaceImplementation(RemoteAuthSettings cfg) {
        cfgOfCloud = cfg;
        cfg.setAuthCred(Utils.getGoogleKey());
    }
    private static final String MIME_T = "application/json; charset=utf-8";
    private Response startConnection(Utt2SpkOverload overload, RemoteAuthSettings cfg) throws IOException {
//        synchronized (Utils.currentGoogleCloudAccessToken){
            if(this.cfgOfCloud!=null)
                this.cfgOfCloud.setAuthCred(Utils.getGoogleKey());
            if(cfg!=null)
                cfg.setAuthCred(Utils.getGoogleKey());
//        }
        OkHttpClient connection = new OkHttpClient();
        RequestBody formatedOverload = RequestBody.create(MediaType.parse(MIME_T),
                JavaScriptObjectFactory.setData(overload));
        Request payload = new Request.Builder()
                .url(cfg.getuttURLS())
                .addHeader(cfg.getAuthCredParams(), Utils.getGoogleKey())
                .addHeader("Content-Type", MIME_T)
                .post(formatedOverload)
                .build();
        return connection.newCall(payload).execute();
    }
    @Override
    public TTSRSP fetch(Utt2SpkOverload payload) {
        try {
            Response result = startConnection(payload, cfgOfCloud);
            String formattedPayload = Objects.requireNonNull(result.body()).string();

            if (result.code() != 200) {
                throw new ResultFetchError(formattedPayload);
            }

            TTSRSP returned = JavaScriptObjectFactory.toObject(formattedPayload, TTSRSP.class);
            return  returned;
        } catch (Exception exception) {
            throw new ConnectionError(exception);
        }
    }


}
