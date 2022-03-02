package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.User;

public interface NavigationProvider {
//    public void setTitleOfCurrentDestination(String title);
    public void navigateFromLoginToUsers();
    public void navigateFromUsersToLogin();
    public void navigateFromMessagesToLogin();
    public void navigateFromSplashToLogin();
    public void navigateFromSplashToUsers();
//    public void navigateFromUsersToMessages(User buddy,boolean iseCall);
//    public void navigateFromUsersToModelDownload(Language source, Language target, User buddy , boolean isCall);
//    public void navigateFromModelDownloadToMessages(User buddy,boolean iseCall);
//    public void navigateFromMessagesToUsers();
    public void navigateFromCallingDialogToUsers();
    public void navigateFromCallingDialogToMessages(User buddy, boolean isCall);
    public void returnToPreviousFragment();
//    public void navigateToUsers();
}
