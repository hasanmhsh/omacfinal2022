package hasan.mohamed.shehata.myapplication.types;

import hasan.mohamed.shehata.myapplication.models.User;

public interface CallCenter {
    //establish call

    //Reject call

    //Respond to call

    //Send busy if busy

    //Receive call and ring

    //Terminate call

    //Give limited time to ring

    public void call(User user);
    public void terminateCurrentCall();
    public void respondCall(User user);
    public void sendTerminateMessage();
    public void setFreeStatus();

}
