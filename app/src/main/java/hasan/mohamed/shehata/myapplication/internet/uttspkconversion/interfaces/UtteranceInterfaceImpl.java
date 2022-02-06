package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.interfaces;


import java.io.IOException;
import java.util.Objects;


import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.RemoteAuthSettings;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ConnectionError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.errors.ResultFetchError;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.result.Utt2SPKResult;
import hasan.mohamed.shehata.myapplication.internet.uttspkconversion.general.JavaScriptObjectFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UtteranceInterfaceImpl implements UtteranceInterface {
    private static final int SUCCESSFULL_REQUEST = 200;
    private RemoteAuthSettings cloudCfg;

    public UtteranceInterfaceImpl(RemoteAuthSettings cfg) {
        cloudCfg = cfg;
    }

    private Response startCobnnection(RemoteAuthSettings cfg) throws IOException {
//        synchronized (Utils.currentGoogleCloudAccessToken){
            if(this.cloudCfg!=null)
                this.cloudCfg.setAuthCred(Utils.currentGoogleCloudAccessToken);
            if(cfg!=null)
                cfg.setAuthCred(Utils.currentGoogleCloudAccessToken);
//        }
        OkHttpClient connection = new OkHttpClient();
        Request overload = new Request.Builder()
                .url(cfg.getut2pkurls())
                .addHeader(cfg.getAuthCredParams(), Utils.currentGoogleCloudAccessToken)
                .build();

        return connection.newCall(overload).execute();
    }
    @Override
    public Utt2SPKResult fetch() {
        try {
            Response payload = startCobnnection(cloudCfg);
            String formattedPayload = Objects.requireNonNull(payload.body()).string();

            if (payload.code() != SUCCESSFULL_REQUEST) {
                throw new ResultFetchError(formattedPayload);
            }

            return JavaScriptObjectFactory.toObject(formattedPayload, Utt2SPKResult.class);

        } catch (Exception exception) {
            throw new ConnectionError(exception);
        }
    }


}
