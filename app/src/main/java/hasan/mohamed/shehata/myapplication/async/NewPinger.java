package hasan.mohamed.shehata.myapplication.async;


import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.internet.APIService;

import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPinger {
    private Context context;
    private Retrofit retrofit;
    private APIService apiService;
    private Disposable disposable;
    private long myId;


    NewPinger(Context context,long myId){
        this.context = context;
        this.myId = myId;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.base_url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(APIService.class);


        disposable = Observable.interval(1000, 2000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callJokesEndpoint, this::onError);


    }


    public void calledOnResume(){
        if (disposable.isDisposed()) {
            disposable = Observable.interval(1000, 5000,
                    TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::callJokesEndpoint, this::onError);
        }
    }
    private void callJokesEndpoint(Long aLong) {


        Observable<OverloadedPingResult> observable = apiService.pingToKeepOnlineAndGetRequiredInfo2(myId);
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
//                .map(result -> result.value)
                .subscribe(this::handleResults, this::handleError);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(context, "OnError in Observable Timer", Toast.LENGTH_LONG).show();
    }


    private void handleResults(OverloadedPingResult result) {


        if (result == null) {



        } else {
            Toast.makeText(context, "NO RESULTS FOUND", Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(Throwable t) {

        //Add your error here.
    }

    public void calledOnPause() {
        disposable.dispose();
    }

}
