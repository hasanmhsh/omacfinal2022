package hasan.mohamed.shehata.myapplication.internet;


import android.content.Context;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import hasan.mohamed.shehata.myapplication.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

//    public static String base_url = "https://hasantranslator.herokuapp.com/";
    public static String base_url = "https://35.85.30.28:5000/";

    private static Retrofit retrofit = null;

    private static OkHttpClient.Builder httpsClientForAWS = null;

    public static Retrofit getClient(Context context) {

        if(retrofit==null) {

            httpsClientForAWS = new OkHttpClient.Builder().readTimeout(8, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    if("35.85.30.28".equals(s))
                        return true;
                    else
                        return false;
                }
            });
//        URL url = null;
//        try {
//            url = new URL("http", "35.85.30.28", 5000, "");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
            createCertOfSsl(context);

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
    //                .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpsClientForAWS.build())
                    .build();
        }


        return retrofit;
    }

    private static APIInterface apiInterface;
    public static APIInterface getAPIInterface(Context context) {
        try {
            if(apiInterface == null){
                apiInterface = APIClient.getClient(context).create(APIInterface.class);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return apiInterface;
    }





















    private static SSLContext makeCert(InputStream cert) throws Exception{

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate myCert;
        try {
            myCert = certificateFactory.generateCertificate(cert);
        } finally {
            cert.close();
        }

        // creating a KeyStore containing our trusted CAs
        String ksType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(ksType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", myCert);

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
        trustManagerFactory.init(keyStore);

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;

    }






    private static void createCertOfSsl(Context context) {

        SSLContext sslContext = null;
        try {
            sslContext = makeCert(context.getResources().openRawResource(R.raw.awsbackendcert));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(sslContext!=null){
            httpsClientForAWS.sslSocketFactory(sslContext.getSocketFactory(), getTrustManager());
        }

    }


    private static X509TrustManager getTrustManager() {

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }

    }


}
