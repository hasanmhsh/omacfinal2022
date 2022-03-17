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
public class User extends BaseObservable implements ListItemBindableItemContentProvider, Serializable, ImageReady,Comparable<User> {
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

    public User(User user) {
        this.userid = user.userid;
        this.username = user.username;
        this.userlanguage = user.userlanguage;
        this.isOnline = user.isOnline;
        this.isExist = user.isExist;
    }



    @SerializedName("userid")
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

    @SerializedName("language") //Example English
    @ColumnInfo(name = "language")
    private Language userlanguage;


    @SerializedName("country")
    @ColumnInfo(name = "country")
    private String usercountry;

    @ColumnInfo(name = "status")
    private StatusOfServerObject userstatus;

    @SerializedName("isOnline")
    private boolean isOnline;

    @SerializedName("exist")
    private boolean isExist;

    public User() {

    }

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

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    @SerializedName("createddate")
    private String createddate;

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
        if(isThisAGroup()){
            return group.getName();
        }
        else {
            return username;
        }
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
        return username;
    }

    @Override
    public String getSecondaryText() {
        return userphone;
    }

    @Override
    public long getID() {
        if(isThisAGroup()){
            return group.getID();
        }
        else{
            return userid;
        }
    }


    @Override
    public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

    }

    @Override
    public void disposeResources() {

    }

    private int numberOfUnreadMessages;

    public int getNumberOfUnreadMessages() {
        if(isThisAGroup()){
            return group.getNumberOfUnreadMessages();
        }
        else{
            return numberOfUnreadMessages;
        }
    }

    public void setNumberOfUnreadMessages(int numberOfUnreadMessages) {
        if(isThisAGroup()){
            if(this.group.getNumberOfUnreadMessages() != numberOfUnreadMessages){
                this.group.setNumberOfUnreadMessages(numberOfUnreadMessages);
                notifyPropertyChanged(BR.numberOfUnreadMessagesString);
                notifyPropertyChanged(BR.selectUnreadMessagesTVVisibility);
            }
        }
        else{
            if(this.numberOfUnreadMessages != numberOfUnreadMessages){
                this.numberOfUnreadMessages = numberOfUnreadMessages;
                notifyPropertyChanged(BR.numberOfUnreadMessagesString);
                notifyPropertyChanged(BR.selectUnreadMessagesTVVisibility);
            }
        }
    }

    @Bindable
    public String getNumberOfUnreadMessagesString() {
        if(isThisAGroup()){
            return String.valueOf(group.getNumberOfUnreadMessages());
        }
        else {
            return String.valueOf(numberOfUnreadMessages);
        }
    }

    @Bindable
    public int getSelectUnreadMessagesTVVisibility(){
        if(isThisAGroup()){
            switch(group.getNumberOfUnreadMessages()){
                case 0:{
                    return View.GONE;
                }
                default:{
                    return View.VISIBLE;
                }
            }
        }
        else {
            switch (numberOfUnreadMessages) {
                case 0: {
                    return View.GONE;
                }
                default: {
                    return View.VISIBLE;
                }
            }
        }
    }

    @Override
    public boolean isEqualTo(ListItemBindableItemContentProvider item) {
        if(item == null)
            return false;
        if(isThisAGroup() && ((User)item).group != null){
            if (group.getGroupid() == ((User) item).group.getGroupid()) {
                if (group.getNumberOfUnreadMessages() == ((User) item).group.getNumberOfUnreadMessages()) {
                    return true;
                }
            }
        }
        else {
            if (userid == ((User) item).userid) {
                if (numberOfUnreadMessages == ((User) item).numberOfUnreadMessages) {
                    return true;
                }
            }
        }
        return false;
    }

    @Bindable
    public int getCallButtonVisibility(){
        if(isThisAGroup()){
            return View.GONE;
        }
        else {
            if (isOnline)
                return View.VISIBLE;
            else
                return View.GONE;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        User other = (User)obj;
        if(isThisAGroup() && other.isThisAGroup()){
            return (this.group.getGroupid() == other.group.getGroupid());
        }
        else {
            return (this.userid == other.userid && this.isOnline == other.isOnline);
        }
    }


    @Override
    public void imageReady(long userid, Bitmap image) {
        if(lastInstanceOfImageView != null) {
            if ((!isThisAGroup() && this.userid == userid) || (isThisAGroup() && this.group.getGroupid() == userid)) {
                RequestOptions requestOptions = new RequestOptions();

                if(isLogoCircular){
                    requestOptions = requestOptions.circleCrop();
                }
                else {
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(60));
                }
                if(lastInstanceOfImageView == null)
                    return;
                try {
                    Glide
                            .with(lastInstanceOfImageView.getContext())
                            .load(image)
                            .apply(requestOptions)
                            .into(lastInstanceOfImageView);
                }
                catch(Exception e){e.printStackTrace();}
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
        if(view == null)
            return;
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
//            asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid,this);
            if(isThisAGroup()){
                asyncPinger.registerImageReadyListenerOrGetImageIfExistForGroups(group.getGroupid(), this);
            }
            else {
                asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid, this);
            }
        }
    }

    @Ignore
    private boolean isLogoCircular = false;
    public void drawCircularLogo(ImageView view){
        if(view == null)
            return;
        isLogoCircular = true;
        lastInstanceOfImageView = view;
        AsyncPinger asyncPinger = ((AsyncPingerProvider)view.getContext()).getCurrentPinger();
        if(asyncPinger != null){
            if(isThisAGroup()){
                asyncPinger.registerImageReadyListenerOrGetImageIfExistForGroups(group.getGroupid(), this);
            }
            else {
                asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid, this);
            }
        }
    }

    public void drawCircularLogoWithActivity(ImageView view, TranslationMainActivity activity) {
        if(view == null || activity == null)
            return;
        isLogoCircular = true;
        lastInstanceOfImageView = view;
        AsyncPinger asyncPinger = ((AsyncPingerProvider)activity).getCurrentPinger();
        if(asyncPinger != null){
            if(isThisAGroup()){
                asyncPinger.registerImageReadyListenerOrGetImageIfExistForGroups(group.getGroupid(),this);
            }
            else {
                asyncPinger.registerImageReadyListenerOrGetImageIfExist(userid,this);
            }
        }
    }

    private transient Group group;// thisIsNotUserObjectItAnInstanceUsedByGroupToUseItsUiView;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if(this.group != group){
            this.group = group;
            notifyPropertyChanged(BR.numberOfUnreadMessagesString);
            notifyPropertyChanged(BR.selectUnreadMessagesTVVisibility);

            notifyPropertyChanged(BR.languageBoxVisibility);
            notifyPropertyChanged(BR.username);
            notifyPropertyChanged(BR.callButtonVisibility);
        }
    }

    public boolean isThisAGroup(){
        return group==null?false:true;
    }

    @Bindable
    public int getLanguageBoxVisibility(){
        if(isThisAGroup())
            return View.GONE;
        else
            return View.VISIBLE;
    }

    @Override
    public int compareTo(User user) {
        if(getNumberOfUnreadMessages() > user.getNumberOfUnreadMessages())
            return -1;
        else if (getNumberOfUnreadMessages() < user.getNumberOfUnreadMessages())
            return 1;
        else {//if (getNumberOfUnreadMessages() == group.getNumberOfUnreadMessages()){
            if(userid < user.userid)
                return -1;
            else if(userid > user.userid)
                return 1;
            else
                return 0;
        }
    }


    @Ignore
    private transient boolean isHighLighted;
    @Override
    public boolean getIsHighLighted() {
        return isHighLighted;
    }

    @Override
    public void setIsHighLighted(boolean isHighLighted) {
        if(this.isHighLighted != isHighLighted) {
            notifyPropertyChanged(BR.highlightedFilterVisibility);
            this.isHighLighted = isHighLighted;
        }
    }
    @Override
    public void toggleHighLight() {
        isHighLighted = !isHighLighted;
        notifyPropertyChanged(BR.highlightedFilterVisibility);
    }

    @Bindable
    public int getHighlightedFilterVisibility(){
        if(isHighLighted)
            return View.VISIBLE;
        else
            return View.GONE;
    }
}
