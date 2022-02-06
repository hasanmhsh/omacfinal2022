package hasan.mohamed.shehata.myapplication.types;

import java.io.Serializable;

public interface CallDialogCallbacks extends Serializable {
    public void acceptCall();
    public void rejectCall();
    public void registerReverseCallbacks(CallDialogReverseCallbacks callbacks);
    public void setIsInhibitClosingDialogRequest(boolean value);
}
