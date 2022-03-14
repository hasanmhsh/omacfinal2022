package hasan.mohamed.shehata.myapplication.internet;
import android.graphics.Bitmap;

import java.util.List;

import hasan.mohamed.shehata.myapplication.types.JSONKey;
import hasan.mohamed.shehata.myapplication.types.JSONResult;
import io.reactivex.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import hasan.mohamed.shehata.myapplication.models.*;

import hasan.mohamed.shehata.myapplication.types.LoginResult;
import hasan.mohamed.shehata.myapplication.types.MessageDeletionResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface APIInterface {

    //note that GET not allowed to have @Body

//    @HTTP(method = "GET", path = "shakkelha", hasBody = true)
    @POST("shakkelha")
    Call<MoshakkalResult> shakkel(@Body Moshakkal moshakkal);

    @POST("users/login")
    Call<LoginResult> login(@Body User user);

    @GET("users/users")
    Call<List<User>> getAllUsers();

    @POST("users/user")
    Call<User> createNewUser(@Body User user);

    @POST("users/user/phone")
    Call<User> getUserByPhoneNumber(@Body User user);

    @POST ("messages/smsajhhjasbcnksnxcvjhasgdjhsbdhfjvgadsy7td7styf78")
    Call<JSONResult> sendSms(@Body SMS sms);

    @POST("messages")
    Call<Message> createNewMessage(@Body Message message);

    @DELETE("messages/{messageid}")
    Call<MessageDeletionResult> deleteMessage(@Path("messageid") long messageid);

    @GET("messages/firstuserid/{id1}/seconduserid/{id2}")
    Call<List<Message>> getBuddyMessagesInDescendingOrderNewToOld(@Path("id1") int myid, @Path("id2") int buddyid);

    @GET("messages/receiver/{id}")
    Call<List<Message>> getAllMyReceivedMessages(@Path("id1") long myid);

    @GET("users/ping/{myid}")
    Call pingToKeepOnline(@Path("myid") long myid);

    @GET("users/ping/overloaded/{myid}")
    Call<OverloadedPingResult> pingToKeepOnlineAndGetRequiredInfo(@Path("myid") long myid);

    @GET("users/ping/overloaded/{myid}")
    Observable<OverloadedPingResult> pingToKeepOnlineAndGetRequiredInfo2(@Path("myid") long myid);

//    @Streaming
//    @Multipart
//    @POST("users/photo/upload/{id}")
//    Call<JSONResult> uploadPhoto(@Path("id") long id, @Part("file\"; filename=\"pp.png\" ") RequestBody file);//Request body import

    @Multipart
    @POST("uploadimagefile/{id}/userimage")
    Call<JSONResult> uploadPhoto(@Path("id") long id, @Part MultipartBody.Part file);//Request body import


    @GET("download/tts/key")
    Call<JSONKey> downloadGoogleKey();

    @GET("download/asr/clientkey")
    Call<ResponseBody> downloadGoogleClientKey();//Request body import

//    @Streaming
    @GET("downloadimagefile/{id}/userimage")
    Call<ResponseBody> downloadPhoto(@Path("id") long id);//Request body import
}
