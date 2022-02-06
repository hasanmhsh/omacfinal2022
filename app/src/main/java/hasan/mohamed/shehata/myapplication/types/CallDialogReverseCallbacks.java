package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.User;

public interface CallDialogReverseCallbacks {
    public void dontCallRejectCallbackOnDestroy_YouCanCallThisSyncOrAsync();

    void callEstablished(User caller);
}
