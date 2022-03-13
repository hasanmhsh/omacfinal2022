package hasan.mohamed.shehata.myapplication.internet;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
//    public static String base_url = "https://35.86.30.22:6000/";
    public static String base_url = "https://35.86.30.22:6000/";

    private static Retrofit retrofit = null;

    private static OkHttpClient.Builder httpsClientForAWS = null;

    public static Retrofit getClient(Context context) {

        if(retrofit==null) {

            httpsClientForAWS = getHttpsClientForAWS(context); // for java spring server

//        URL url = null;
//        try {
//            url = new URL("http", "35.85.30.28", 5000, "");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
            // createCertOfSsl(context); //for python flask server

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss") //Same as jackson in backend
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
    //                .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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


    private static OkHttpClient.Builder getHttpsClientForAWS(Context context){
        try {
            KeyStore ksTrust = KeyStore.getInstance("BKS");
            InputStream inputStream = context.getResources().openRawResource(R.raw.androidservercert);
            ksTrust.load(inputStream, "password123_".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(ksTrust);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(8, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
//                    if("35.85.30.28".equals(s))
//                        return true;
//                    else
//                        return false;
                }
            });

            OkHttpClient okHttpClient = new OkHttpClient();
            builder.sslSocketFactory(sslContext.getSocketFactory(), new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            });
            return builder;

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
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
