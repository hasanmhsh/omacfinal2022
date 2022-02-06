package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.User;

public interface CallDialogProvider {
    public void showCallDialogForCallReception(CallDialogCallbacks callDialogCallbacks, User caller);
    public void showCallDialogForCallSourcing(CallDialogCallbacks callDialogCallbacks, User caller);
    public void hideCallDialog();
}
