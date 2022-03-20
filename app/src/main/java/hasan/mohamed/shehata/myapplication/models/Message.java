package hasan.mohamed.shehata.myapplication.models;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.core.location.GnssStatusCompat;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.io.IOException;

import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.MessageDirection;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
public class Message extends BaseObservable implements ListItemBindableItemContentProvider {
    /*
    {
        "id": 2,
        "moshakkaltext": "moshakkaldftext452346456456456456456",
        "text": "لحمد لله يا منة انا بخير",
        "timedt": "Mon, 25 Oct 2021 07:43:06 GMT",
        "translatedtext": "iam fine menna"
    }
     */

  public Message() {
  }

  @JsonIgnore
  public void setContext(Context context) {
    this.context = context;
  }

  @JsonIgnore
  @Ignore
  private transient Context context;


  public Message(Context context) {
    this.context = context;
    setMessagestatusNotBinder(MessageStatus.notsent);
  }


  @JsonProperty("messageid")
  @SerializedName("messageid")
  @PrimaryKey(autoGenerate = true)
  private long messageid;

  @JsonProperty("createddate")
  public String getCreateddate() {
    return createddate;
  }

  @JsonProperty("createddate")
  public void setCreateddate(String  createddate) {
    this.createddate = createddate;
  }

  @JsonProperty("createddate")
  @SerializedName("createddate")
  private String createddate;

  @JsonProperty("text")
  @SerializedName("text")
  @ColumnInfo(name = "text")
  private String messagetext;

  @JsonProperty("translatedtext")
  @SerializedName("translatedtext")
  @ColumnInfo(name = "translatedtext")
  private String messagetranslatedtext;

  @JsonProperty("controltext")
  @SerializedName("controltext")
  @ColumnInfo(name = "controltext")
  private String messagemoshakkaltext;

  @JsonProperty("controltext")
  private String getControltext(){
    return messagemoshakkaltext;
  }

  @JsonProperty("controltext")
  private void setControltext(String controltext){
    this.messagemoshakkaltext = controltext;
  }

  @JsonProperty("controlnumber")
  public long getControlnumber() {
    return controlnumber;
  }

  @JsonProperty("controlnumber")
  public void setControlnumber(long controlnumber) {
    this.controlnumber = controlnumber;
  }

  @JsonProperty("controlnumber")
  @SerializedName("controlnumber")
  private long controlnumber;

  @JsonProperty("messagetimedt")
  @ColumnInfo(name = "messagetimedt")
  private transient String messagetimedt;


  @JsonProperty("messagestatus")
  @SerializedName("messagestatus")
  @ColumnInfo(name = "messagestatus")
  private MessageStatus messagestatus;

//    @ColumnInfo(name = "buddyid")
//    private int buddyid;

  @JsonIgnore
  @ColumnInfo(name = "messagedirection")
  private transient MessageDirection messageDirection;

  @JsonProperty("senderid")
  @ColumnInfo(name = "senderid")
  @SerializedName("senderid")
  private long senderid;

  @JsonProperty("receiverid")
  @ColumnInfo(name = "receiverid")
  @SerializedName("receiverid")
  private long receiverid;

  @JsonProperty("sendername")
  public String getSendername() {
    return sendername;
  }

  @JsonProperty("sendername")
  public void setSendername(String sendername) {
    this.sendername = sendername;
  }

  @JsonProperty("sendername")
  @SerializedName("sendername")
  @ColumnInfo(name = "sendername")
  private String sendername;


  @JsonProperty("senderlanguage")
  public void setSenderlanguage(Language senderlanguage) {
    this.senderlanguage = senderlanguage;
  }

  @JsonProperty("senderlanguage")
  @SerializedName("senderlanguage")
  @ColumnInfo(name = "senderlanguage")
  private Language senderlanguage;

  @JsonProperty("senderlanguage")
  public Language getSenderlanguage() {
    return senderlanguage;
  }

  @JsonProperty("groupid")
  public long getGroupid() {
    return groupid;
  }

  @JsonProperty("groupid")
  public void setGroupid(long groupid) {
    this.groupid = groupid;
  }

  @JsonProperty("groupid")
  @ColumnInfo(name = "groupid")
  @SerializedName("groupid")
  private long groupid;


  @JsonIgnore
  @ColumnInfo(name = "is_read")
  private transient boolean isRead;

  @JsonIgnore
  @Bindable
  public boolean getIsRead() {
    return isRead;
  }

  @JsonIgnore
  public void setIsRead(boolean isRead) {
    if(this.isRead != isRead) {
      this.isRead = isRead;
      notifyPropertyChanged(BR.isRead);
    }
  }

  @JsonProperty("messageid")
  @Bindable
  public long getMessageid() {
    return messageid;
  }

  @JsonProperty("messageid")
  public void setMessageid(long messageid) {
    if(this.messageid != messageid) {
      this.messageid = messageid;
      notifyPropertyChanged(BR.messageid);
    }
  }

  @JsonProperty("text")
  @Bindable
  public String getMessagetext() {
    return messagetext;
  }

  @JsonIgnore
  public boolean isGroupMessage(){
    if(groupid > 0)
      return true;
    else
      return false;
  }

  @JsonProperty("text")
  public void setMessagetext(String messagetext) {
    if(this.messagetext == null){
      if(messagetext != null){
        this.messagetext = messagetext;
        notifyPropertyChanged(BR.messagetext);
      }
    }
    else if(!this.messagetext.equals(messagetext)) {
      this.messagetext = messagetext;
      notifyPropertyChanged(BR.messagetext);
    }
  }

  @JsonProperty("translatedtext")
  @Bindable
  public String getMessagetranslatedtext() {
    return messagetranslatedtext;
  }

  @JsonProperty("translatedtext")
  public void setMessagetranslatedtext(String messagetranslatedtext) {
    if(this.messagetranslatedtext == null){
      if(messagetranslatedtext != null){
        this.messagetranslatedtext = messagetranslatedtext;
        notifyPropertyChanged(BR.messagetranslatedtext);
      }
    }
    else if(!this.messagetranslatedtext.equals(messagetranslatedtext)) {
      this.messagetranslatedtext = messagetranslatedtext;
      notifyPropertyChanged(BR.messagetranslatedtext);
    }
  }

  @JsonIgnore
  @Bindable
  public String getMessagemoshakkaltext() {
    return messagemoshakkaltext;
  }

  @JsonIgnore
  @Bindable
  public String getControlText(){
    return messagemoshakkaltext;
  }

  @JsonIgnore
  public void setMessagemoshakkaltext(String messagemoshakkaltext) {
    if(this.messagemoshakkaltext == null){
      if(messagemoshakkaltext != null){
        this.messagemoshakkaltext = messagemoshakkaltext;
        notifyPropertyChanged(BR.messagemoshakkaltext);
      }
    }
    else if(!this.messagemoshakkaltext.equals(messagemoshakkaltext)) {
      this.messagemoshakkaltext = messagemoshakkaltext;
      notifyPropertyChanged(BR.messagemoshakkaltext);
    }
  }


  @JsonIgnore
  @Bindable
  public String getMessagetimedt() {
    return messagetimedt;
  }

  @JsonIgnore
  public void setMessagetimedt(String messagetimedt) {
    if(this.messagetimedt == null){
      if(messagetimedt != null){
        this.messagetimedt = messagetimedt;
        notifyPropertyChanged(BR.messagetimedt);
      }
    }
    else if(!this.messagetimedt.equals(messagetimedt)) {
      this.messagetimedt = messagetimedt;
      notifyPropertyChanged(BR.messagetimedt);
    }
  }

  @JsonProperty("messagestatus")
  @Bindable
  public MessageStatus getMessagestatus() {
    return messagestatus;
  }

  @JsonIgnore
  @Bindable
  public String getMessagestatusString() {
    if(messagestatus == null)
      return "";
    else
      return messagestatus.name();
  }

  @JsonIgnore
  @Bindable
  public int getMessagestatusvisivility() {
    if(context != null){
      if(Utils.getUserID(context) == receiverid){
        return View.GONE;
      }
      else{
        return View.VISIBLE;
      }
    }
    return View.VISIBLE;
  }

  @JsonProperty("messagestatus")
  public void setMessagestatus(MessageStatus status) {
    if(this.messagestatus != status) {
      this.messagestatus = status;
      notifyPropertyChanged(BR.messagestatus);
      notifyPropertyChanged(BR.messageBoxVisibilityGoneIfDeleted);
      notifyPropertyChanged(BR.messagestatusvisivility);
      notifyPropertyChanged(BR.messagestatusString);
      notifyPropertyChanged(BR.readEyeVisibility);
    }
  }

  public void setMessagestatusNotBinder(MessageStatus status){
    if(status != null) {
      this.messagestatus = status;
      notifyPropertyChanged(BR.messagestatus);
      notifyPropertyChanged(BR.messageBoxVisibilityGoneIfDeleted);
      notifyPropertyChanged(BR.messagestatusvisivility);
      notifyPropertyChanged(BR.messagestatusString);
      notifyPropertyChanged(BR.readEyeVisibility);
    }
  }



  @JsonIgnore
  @Bindable
  public int getMessageBoxVisibilityGoneIfDeleted() {
    if(messagestatus == MessageStatus.deleted)
      return View.GONE;
    else
      return View.VISIBLE;
  }

  @JsonIgnore
  @Bindable
  public int getReadEyeVisibility() {
    if(messagestatus == MessageStatus.read && context!=null && Utils.getUserID(context) == senderid)
      return View.VISIBLE;
    else
      return View.GONE;
  }

//    @Bindable
//    public int getBuddyid() {
//        return buddyid;
//    }
//
//    public void setBuddyid(int buddyid) {
//        if(this.buddyid != buddyid) {
//            this.buddyid = buddyid;
//            notifyPropertyChanged(BR.buddyid);
//        }
//    }


  @JsonIgnore
  @Bindable
  public MessageDirection getMessageDirection() {
    return messageDirection;
  }
  public void setMessageDirection(MessageDirection messageDirection) {
    if(this.messageDirection != messageDirection) {
      this.messageDirection = messageDirection;
      notifyPropertyChanged(BR.messageDirection);
    }
  }

  @JsonProperty("senderid")
  @Bindable
  public long getSenderid() {
    return senderid;
  }
  public void setSenderid(long senderid) {
    if(this.senderid != senderid) {
      this.senderid = senderid;
      notifyPropertyChanged(BR.senderid);
    }
  }


  @JsonProperty("receiverid")
  @Bindable
  public long getReceiverid() {
    return receiverid;
  }
  public void setReceiverid(long receiverid) {
    if(this.receiverid != receiverid) {
      this.receiverid = receiverid;
      notifyPropertyChanged(BR.receiverid);
    }
  }

  @JsonIgnore
  private boolean isToShowTranslatedText;

  @JsonIgnore
  @Bindable
  public boolean getIsToShowTranslatedText() {
    return isToShowTranslatedText;
  }

  @JsonIgnore
  @Bindable
  public String getCurrentMessageText(){
    if(isToShowTranslatedText){
      return this.messagetranslatedtext;
    }
    else{
      return this.messagetext;
    }
  }

  @JsonIgnore
  public void setIsToShowTranslatedText(boolean isToShowTranslatedText) {
    if(this.isToShowTranslatedText != isToShowTranslatedText) {
      this.isToShowTranslatedText = isToShowTranslatedText;
      notifyPropertyChanged(BR.currentMessageText);
    }
  }

  @JsonIgnore
  @Override
  public String getPrimaryText() {
    return messagetext;
  }

  @JsonIgnore
  @Override
  public String getSecondaryText() {
    return messagetranslatedtext;
  }

  @JsonIgnore
  @Override
  public long getID() {
    return messageid;
  }



  @JsonIgnore
  @Override
  public void drawLogo(ImageView view) {

  }

  @JsonIgnore
  @Override
  public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

  }

  @JsonIgnore
  @Override
  public void disposeResources() {

  }

  @JsonIgnore
  @Override
  public boolean isEqualTo(ListItemBindableItemContentProvider item) {
    if(messageid == ((Message)item).messageid)
      return true;
    return false;
  }

  @JsonProperty("controltext")
  public void seControltext(String messageDeleteCommand) {
    messagemoshakkaltext = messageDeleteCommand;
  }


  @JsonIgnore
  @Ignore
  private transient boolean isHighLighted;
  @Override
  public boolean getIsHighLighted() {
    return isHighLighted;
  }

  @JsonIgnore
  @Override
  public void setIsHighLighted(boolean isHighLighted) {
    if(this.isHighLighted != isHighLighted) {
      notifyPropertyChanged(BR.highlightedFilterVisibility);
      this.isHighLighted = isHighLighted;
    }
  }

  @JsonIgnore
  @Override
  public void toggleHighLight() {
    isHighLighted = !isHighLighted;
    notifyPropertyChanged(BR.highlightedFilterVisibility);
  }

  @JsonIgnore
  @Bindable
  public int getHighlightedFilterVisibility(){
    if(isHighLighted)
      return View.VISIBLE;
    else
      return View.GONE;
  }

  @JsonIgnore
  @Bindable
  @Override
  public int getIsAdminCheckBoxVisibility() {
    return View.GONE;
  }


  @JsonIgnore
  @Override
  public void setIsGroupAdmin(boolean isGroupAdmin) {

  }

  @JsonIgnore
  @Override
  public boolean getIsGroupAdmin() {
    return false;
  }

  @JsonProperty("image")
  @JsonIgnore
  public byte [] getImage() {
    return image;
  }

  @JsonProperty("image")
  @JsonIgnore
  public void setImage(byte [] image) {
    this.image = image;
  }

  @JsonProperty("image")
  @SerializedName("image")
  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  private byte [] image;


  @JsonIgnore
  @Ignore
  private transient ImageView currentImageview;
  @JsonIgnore
  public void bindImageView(ImageView imageView) {
    currentImageview = imageView;
    if (imageView == null || imageView.getContext() == null)
      return;

    byte[] bytes = image;
    try {
      RequestOptions requestOptions = new RequestOptions();
      boolean isLogoCircular = false;
      if (isLogoCircular) {
        requestOptions = requestOptions.circleCrop();
      } else {
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(5));
      }
      Glide
              .with(imageView.getContext())
              .load(image)
              .apply(requestOptions)
              .into(imageView);
    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  @JsonIgnore
  @Bindable
  public int getMessageTextVisibility(){
    if(image != null){
      return View.GONE;
    }
    else {
      return View.VISIBLE;
    }
  }

  @JsonIgnore
  @Bindable
  public int getMessageImageViewVisibility(){
    if(image != null){
      return View.VISIBLE;
    }
    else {
      return View.GONE;
    }
  }

}
