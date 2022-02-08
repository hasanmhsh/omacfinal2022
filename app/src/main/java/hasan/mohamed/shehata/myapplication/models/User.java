package hasan.mohamed.shehata.myapplication.models;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.Gender;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;

@Entity
public class User extends BaseObservable implements ListItemBindableItemContentProvider, Serializable, ImageReady {
    /*
    {
        "country": "egypt",
        "email": "lily@gmail.com",
        "id": 4,
        "language": "arabic",
        "lastactivetimedt": "Sat, 23 Oct 2021 23:11:27 GMT",
        "name": "lily",
        "phone": "01102910007"
    }
     */
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private long userid;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String username;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String useremail;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    private String userphone;

    @SerializedName("language")
    @ColumnInfo(name = "language")
    private Language userlanguage;


    @SerializedName("country")
    @ColumnInfo(name = "country")
    private String usercountry;

    @ColumnInfo(name = "status")
    private StatusOfServerObject userstatus;

    @SerializedName("isactive")
    private boolean isOnline;

    @SerializedName("exist")
    private boolean isExist;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    @SerializedName("lastactivetimedt")
    private String lastactivetimedt;

    @SerializedName("password")
    @ColumnInfo(name = "password")
    private String password;

    @SerializedName("gender")
    @ColumnInfo(name = "gender")
    private Gender gender;

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(this.password == null){
            if(password != null){
                this.password = password;
                notifyPropertyChanged(BR.password);
            }
        }
        else if(!this.password.equals(password)) {
            this.password = password;
            notifyPropertyChanged(BR.password);
        }
    }

    @Bindable
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if(this.gender != gender) {
            this.gender = gender;
            notifyPropertyChanged(BR.gender);
        }
    }

    public String getLastactivetimedt() {
        return lastactivetimedt;
    }

    public void setLastactivetimedt(String lastactivetimedt) {
        this.lastactivetimedt = lastactivetimedt;
    }

    @Bindable
    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        if(this.userid != userid) {
            this.userid = userid;
            notifyPropertyChanged(BR.userid);
        }
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        if(this.username == null){
            if(value != null){
                this.username = value;
                notifyPropertyChanged(BR.username);
            }
        }
        else if(!this.username.equals(value)) {
            this.username = value;
            notifyPropertyChanged(BR.username);
        }
    }

    @Bindable
    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        if(this.useremail == null){
            if(useremail != null){
                this.useremail = useremail;
                notifyPropertyChanged(BR.useremail);
            }
        }
        else if(!this.useremail.equals(useremail)) {
            this.useremail = useremail;
            notifyPropertyChanged(BR.useremail);
        }
    }

    @Bindable
    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        if(this.userphone == null){
            if(userphone != null){
                this.userphone = userphone;
                notifyPropertyChanged(BR.userphone);
            }
        }
        else if(!this.userphone.equals(userphone)) {
            this.userphone = userphone;
            notifyPropertyChanged(BR.userphone);
        }
    }

    @Bindable
    public Language getUserlanguage() {
        return userlanguage;
    }

    public void setUserlanguage(Language userlanguage) {
        if(this.userlanguage != userlanguage) {
            this.userlanguage = userlanguage;
            notifyPropertyChanged(BR.userlanguage);
        }
    }

    @Bindable
    public String getUsercountry() {
        return usercountry;
    }

    @Bindable
    public StatusOfServerObject getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(StatusOfServerObject userstatus) {
        if(this.userstatus != userstatus) {
            this.userstatus = userstatus;
            notifyPropertyChanged(BR.userstatus);
        }
    }

    @Bindable
    public boolean getIsOnline() {
        return isOnline;
    }

    public void setUsercountry(String usercountry) {
        if(this.usercountry == null){
            if(usercountry != null){
                this.usercountry = usercountry;
                notifyPropertyChanged(BR.usercountry);
            }
        }
        else if(!this.usercountry.equals(usercountry)) {
            this.usercountry = usercountry;
            notifyPropertyChanged(BR.usercountry);
        }
    }

    public void setIsOnline(boolean online) {
        if(online != this.isOnline) {
            isOnline = online;
            notifyPropertyChanged(BR.isOnline);
            notifyPropertyChanged(BR.callButtonVisibility);
        }
    }

    @Override
    public String getPrimaryText() {
        return null;
    }

    @Override
    public String getSecondaryText() {
        return null;
    }

    @Override
    public long getID() {
        return userid;
    }


    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @Override
    public void disposeResources() {

    }

    private int numberOfUnreadMessages;

    public int getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }

    public void setNumberOfUnreadMessages(int numberOfUnreadMessages) {
        if(this.numberOfUnreadMessages != numberOfUnreadMessages){
            this.numberOfUnreadMessages = numberOfUnreadMessages;
            notifyPropertyChanged(BR.numberOfUnreadMessagesString);
            notifyPropertyChanged(BR.selectUnreadMessagesTVVisibility);
        }
    }

    @Bindable
    public String getNumberOfUnreadMessagesString() {
        return String.valueOf(numberOfUnreadMessages);
    }

    @Bindable
    public int getSelectUnreadMessagesTVVisibility(){
        switch(numberOfUnreadMessages){
            case 0:{
                return View.GONE;
            }
            default:{
                return View.VISIBLE;
            }
        }
    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        if(userid == ((User)item).userid){
            if(numberOfUnreadMessages == ((User)item).numberOfUnreadMessages){
                return true;
            }
        }
        return false;
    }

    @Bindable
    public int getCallButtonVisibility(){
        if(isOnline)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        User other = (User)obj;
        return (this.userid == other.userid && this.isOnline == other.isOnline);
    }


    @Override
    public void imageReady(long userid, Bitmap image) {
        if(lastInstanceOfImageView != null) {
            if (this.userid == userid) {
                RequestOptions requestOptions = new RequestOptions();

                if(isLogoCircular){
                    requestOptions = requestOptions.circleCrop();
                }
                else {
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(60));
                }
                Glide
                        .with(lastInstanceOfImageView.getContext())
                        .load(image)
                        .apply(requestOptions)
                        .into(lastInstanceOfImageView);
            }
        }
    }

    @Override
    public long getUserId() {
        return userid;
    }

    @Ignore
    private ImageView lastInstanceOfImageView;
    @Override
    public void drawLogo(ImageView view) {
        isLogoCircular = false;

//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(60));
//        Glide
//                .with(view.getContext())
//                .load(view.getResources().getDrawable(R.drawable.user))
//                .apply(requestOptions)
//                .into(view);
        lastInstanceOfImageView = view;
        AsyncPinger asyncPinger = ((AsyncPingerProvider)view.getContext()).getCurrentPinger();
        if(asyncPinger != null){
            asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid,this);
        }
    }

    @Ignore
    private boolean isLogoCircular = false;
    public void drawCircularLogo(ImageView view){
        isLogoCircular = true;
        lastInstanceOfImageView = view;
        AsyncPinger asyncPinger = ((AsyncPingerProvider)view.getContext()).getCurrentPinger();
        if(asyncPinger != null){
            asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid,this);
        }
    }

    public void drawCircularLogoWithActivity(ImageView view, TranslationMainActivity activity) {
        isLogoCircular = true;
        lastInstanceOfImageView = view;
        AsyncPinger asyncPinger = ((AsyncPingerProvider)activity).getCurrentPinger();
        if(asyncPinger != null){
            asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid,this);
        }
    }
}
