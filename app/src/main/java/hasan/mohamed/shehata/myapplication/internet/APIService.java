package hasan.mohamed.shehata.myapplication.internet;

import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIService {


    String BASE_URL = "https://api.chucknorris.io/jokes/";

    @GET("users/ping/overloaded/{myid}")
    Observable<OverloadedPingResult> pingToKeepOnlineAndGetRequiredInfo2(@Path("myid") long myid);

}