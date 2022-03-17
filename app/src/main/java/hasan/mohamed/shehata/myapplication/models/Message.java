package hasan.mohamed.shehata.myapplication.models;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.types.ListItemCallbacks;
import hasan.mohamed.shehata.myapplication.types.MessageDirection;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;

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

  public void setContext(Context context) {
    this.context = context;
  }

  @Ignore
  private transient Context context;


  public Message(Context context) {
    this.context = context;
    setMessagestatusNotBinder(MessageStatus.notsent);
  }


  @SerializedName("messageid")
  @PrimaryKey(autoGenerate = true)
  private long messageid;

  public String getCreateddate() {
    return createddate;
  }

  public void setCreateddate(String  createddate) {
    this.createddate = createddate;
  }

  @SerializedName("createddate")
  private String createddate;

  @SerializedName("text")
  @ColumnInfo(name = "text")
  private String messagetext;

  @SerializedName("translatedtext")
  @ColumnInfo(name = "translatedtext")
  private String messagetranslatedtext;

  @SerializedName("controltext")
  @ColumnInfo(name = "controltext")
  private String messagemoshakkaltext;

  private String getControltext(){
    return messagemoshakkaltext;
  }

  private void setControltext(String controltext){
    this.messagemoshakkaltext = controltext;
  }

  public long getControlnumber() {
    return controlnumber;
  }

  public void setControlnumber(long controlnumber) {
    this.controlnumber = controlnumber;
  }

  @SerializedName("controlnumber")
  private long controlnumber;

  @ColumnInfo(name = "messagetimedt")
  private transient String messagetimedt;


  @SerializedName("messagestatus")
  @ColumnInfo(name = "messagestatus")
  private MessageStatus messagestatus;

//    @ColumnInfo(name = "buddyid")
//    private int buddyid;

  @ColumnInfo(name = "messagedirection")
  private transient MessageDirection messageDirection;

  @ColumnInfo(name = "senderid")
  @SerializedName("senderid")
  private long senderid;

  @ColumnInfo(name = "receiverid")
  @SerializedName("receiverid")
  private long receiverid;

  public String getSendername() {
    return sendername;
  }

  public void setSendername(String sendername) {
    this.sendername = sendername;
  }

  @SerializedName("sendername")
  @ColumnInfo(name = "sendername")
  private String sendername;


  public void setSenderlanguage(Language senderlanguage) {
    this.senderlanguage = senderlanguage;
  }

  @SerializedName("senderlanguage")
  @ColumnInfo(name = "senderlanguage")
  private Language senderlanguage;

  public Language getSenderlanguage() {
    return senderlanguage;
  }

  public long getGroupid() {
    return groupid;
  }

  public void setGroupid(long groupid) {
    this.groupid = groupid;
  }

  @ColumnInfo(name = "groupid")
  @SerializedName("groupid")
  private long groupid;



  @ColumnInfo(name = "is_read")
  private transient boolean isRead;

  @Bindable
  public boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(boolean isRead) {
    if(this.isRead != isRead) {
      this.isRead = isRead;
      notifyPropertyChanged(BR.isRead);
    }
  }

  @Bindable
  public long getMessageid() {
    return messageid;
  }

  public void setMessageid(long messageid) {
    if(this.messageid != messageid) {
      this.messageid = messageid;
      notifyPropertyChanged(BR.messageid);
    }
  }

  @Bindable
  public String getMessagetext() {
    return messagetext;
  }


  public boolean isGroupMessage(){
    if(groupid > 0)
      return true;
    else
      return false;
  }

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

  @Bindable
  public String getMessagetranslatedtext() {
    return messagetranslatedtext;
  }

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

  @Bindable
  public String getMessagemoshakkaltext() {
    return messagemoshakkaltext;
  }

  @Bindable
  public String getControlText(){
    return messagemoshakkaltext;
  }

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


  @Bindable
  public String getMessagetimedt() {
    return messagetimedt;
  }

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

  @Bindable
  public MessageStatus getMessagestatus() {
    return messagestatus;
  }

  @Bindable
  public String getMessagestatusString() {
    if(messagestatus == null)
      return "";
    else
      return messagestatus.name();
  }

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



  @Bindable
  public int getMessageBoxVisibilityGoneIfDeleted() {
    if(messagestatus == MessageStatus.deleted)
      return View.GONE;
    else
      return View.VISIBLE;
  }

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

  private boolean isToShowTranslatedText;

  @Bindable
  public boolean getIsToShowTranslatedText() {
    return isToShowTranslatedText;
  }

  @Bindable
  public String getCurrentMessageText(){
    if(isToShowTranslatedText){
      return this.messagetranslatedtext;
    }
    else{
      return this.messagetext;
    }
  }

  public void setIsToShowTranslatedText(boolean isToShowTranslatedText) {
    if(this.isToShowTranslatedText != isToShowTranslatedText) {
      this.isToShowTranslatedText = isToShowTranslatedText;
      notifyPropertyChanged(BR.currentMessageText);
    }
  }

  @Override
  public String getPrimaryText() {
    return messagetext;
  }

  @Override
  public String getSecondaryText() {
    return messagetranslatedtext;
  }

  @Override
  public long getID() {
    return messageid;
  }

  @Override
  public void drawLogo(ImageView view) {

  }

  @Override
  public void setOnListItemCallbacks(ListItemCallbacks callbacks) {

  }

  @Override
  public void disposeResources() {

  }

  @Override
  public boolean isEqualTo(ListItemBindableItemContentProvider item) {
    if(messageid == ((Message)item).messageid)
      return true;
    return false;
  }

  public void seControltext(String messageDeleteCommand) {
    messagemoshakkaltext = messageDeleteCommand;
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
