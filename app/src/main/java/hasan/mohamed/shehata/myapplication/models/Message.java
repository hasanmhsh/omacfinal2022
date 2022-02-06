package hasan.mohamed.shehata.myapplication.models;
import android.widget.ImageView;

import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

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

  @SerializedName("id")
  @PrimaryKey(autoGenerate = true)
  private long messageid;

  @SerializedName("text")
  @ColumnInfo(name = "messagetext")
  private String messagetext;

  @SerializedName("translatedtext")
  @ColumnInfo(name = "messagetranslatedtext")
  private String messagetranslatedtext;

  @SerializedName("moshakkaltext")
  @ColumnInfo(name = "messagemoshakkaltext")
  private String messagemoshakkaltext;

  @SerializedName("timedt")
  @ColumnInfo(name = "messagetimedt")
  private String messagetimedt;

  @ColumnInfo(name = "messagestatus")
  private StatusOfServerObject messagestatus;

//    @ColumnInfo(name = "buddyid")
//    private int buddyid;

  @ColumnInfo(name = "messagedirection")
  MessageDirection messageDirection;

  @ColumnInfo(name = "sender_id")
  @SerializedName("sender_id")
  private long senderid;

  @ColumnInfo(name = "receiver_id")
  @SerializedName("receiver_id")
  private long receiverid;

  @ColumnInfo(name = "is_read")
  private boolean isRead;

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
  public StatusOfServerObject getMessagestatus() {
    return messagestatus;
  }

  public void setMessagestatus(StatusOfServerObject status) {
    if(this.messagestatus != status) {
      this.messagestatus = status;
      notifyPropertyChanged(BR.messagestatus);
    }
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
    return null;
  }

  @Override
  public String getSecondaryText() {
    return null;
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
}
