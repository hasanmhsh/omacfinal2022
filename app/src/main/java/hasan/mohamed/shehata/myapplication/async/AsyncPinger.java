package hasan.mohamed.shehata.myapplication.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.languages.TRNSLG;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.MissedCall;
import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import hasan.mohamed.shehata.myapplication.models.UnreadReceivedMessage;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerCallbacks;
import hasan.mohamed.shehata.myapplication.types.CallCenter;
import hasan.mohamed.shehata.myapplication.types.CallDialogCallbacks;
import hasan.mohamed.shehata.myapplication.types.CallDialogProvider;
import hasan.mohamed.shehata.myapplication.types.CallDialogReverseCallbacks;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.MessageFragmentReverseCallbacks;
import hasan.mohamed.shehata.myapplication.types.NavigationProvider;
import hasan.mohamed.shehata.myapplication.types.NewGroupsMessagesConsumer;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.types.NewMessagesConsumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncPinger implements Serializable, CallCenter, AsyncPingerCallbacks {
    private static final int TIMER_SEGMENTS = 10; //11 seconds
    //establish call

    //Reject call

    //Respond to call

    //Send busy if busy

    //Receive call and ring

    //Terminate call

    //Give limited time to ring

    // Check calls
    private OverloadedPingResult overloadedPingResult;
    private Thread pingerDaemonThread;
    private Thread unreadMessagesWorker;
    private Context context;
    private List<UserListConsumer> usersConsumers;
    private List<UserListConsumer> groupsConsumers;
    private HashMap<Long , NewMessagesConsumer> newMessagesConsumers;
    private HashMap<Long , NewGroupsMessagesConsumer> newGroupMessagesConsumers;
    private long userid;
    private User me;
    private static final int NORMAL_RATE = 3333;
    private static final int HIGH_RATE = 1111;
    private List<UnreadReceivedMessage> lastUpdateOfUnreadReceivedMsgsNotifications;
    private AtomicBoolean isUnredItemsUpdated = new AtomicBoolean(true);
    private AtomicBoolean isTeminatingTimerRun = new AtomicBoolean(false);
    private AtomicInteger countedTimerSegments = new AtomicInteger(0);
    private final AtomicInteger millis = new AtomicInteger(NORMAL_RATE);
    private User caller;
    private long callerId;
    private MessageFragmentReverseCallbacks messageFragmentReverseCallbacks;

    private AtomicBoolean inhibitClosingDialog = new AtomicBoolean(false);

    private CallDialogReverseCallbacks callDialogReverseCallbacks;

    private Closeable callDialog;

    private HashMap<Long, Bitmap> userAvatars = new HashMap<Long, Bitmap>();
    private HashMap<Long, Bitmap> groupAvatars = new HashMap<Long, Bitmap>();




    @Override
    public void registerMessageFragmentReverseCallbacks(MessageFragmentReverseCallbacks messageFragmentReverseCallbacks) {
        this.messageFragmentReverseCallbacks = messageFragmentReverseCallbacks;
    }

    public void updateGroupImage(final long groupid) {
        try{
            synchronized (groupAvatars) {
                if (groupAvatars != null) {
                    groupAvatars.remove(groupid);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    enum PingerStatus{
        Free,
        Calling, // Ringing or sent in message moshakkaltext to notify that some one is calling (busy)
        ReceivingCall, // Ringing (busy)
        InCall, // Means call is established now and sent in moshakkaltext to notify acceptance of my call
    }

    private PingerStatus status;

    enum ReceivedCallValues{
        Busy,
        Accept,
        Calling,
        Terminate
    }

    private Runnable asynPingerBackgroundRunnable = new Runnable() {
        @Override
        public void run() {

            while (true) {
                if (context == null)
                    break;
                else if (Utils.getBackgroundThreadFlag(context)) {
                    handletCallTeminatingTimer();
                    try {
                        overloadedPingResult = APIClient.getAPIInterface(context).pingToKeepOnlineAndGetRequiredInfo(userid).execute().body();
//                        List<User> allUsers = APIClient.getAPIInterface(context).getAllUsers().execute().body();
//                        overloadedPingResult = new OverloadedPingResult();
//                        overloadedPingResult.setAllUsers(allUsers);
//                        overloadedPingResult.setAllUserReceivedMessages(new ArrayList<>());
                        Utils.setOverLoadedPingResult(overloadedPingResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() == null) {
                        try {
                            AppDatabase.callInActivityOnCreate(context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (lastUpdateOfUnreadReceivedMsgsNotifications == null && AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                        lastUpdateOfUnreadReceivedMsgsNotifications = AppDatabase.getUnreadReceivedMessageNotificationDao().getAll();
                    if (overloadedPingResult != null) {
                        boolean isPlayingNewSoundRing = false;
                        final List<Message> newReceivedMessages = overloadedPingResult.getMessages();
                        final AtomicBoolean isNonControlMsgReceived = new AtomicBoolean(false);
                        final List<RequiredNewMessage> requiredNewMessageList = new ArrayList<>();
                        final List<RequiredNewGroupMessage> requiredNewGroupMessageList = new ArrayList<>();
                        final List<Message> nonCallControlMsgsToSaveInDatabase = new ArrayList<>();
                        if (newReceivedMessages != null && newReceivedMessages.size() > 0) {
                            final List<UnreadReceivedMessage> unreadReceivedMessages = new ArrayList<>();
                            for (int i = 0; i < newReceivedMessages.size(); i++) {
                                Message newMessage = newReceivedMessages.get(i);
                                long senderUserId = newReceivedMessages.get(i).getSenderid();

                                if (newMessage.getMessagemoshakkaltext() == null) {
                                        isNonControlMsgReceived.set(true);
                                        UnreadReceivedMessage umsg = new UnreadReceivedMessage();
                                        umsg.setMessageid(newMessage.getMessageid());
                                        umsg.setSenderid(newMessage.getSenderid());
                                        umsg.setGroupid(newMessage.getGroupid());
                                        if (newMessage.getGroupid() == 0) {
                                            if (Utils.currentOpenedBuddyIdChatView != newMessage.getSenderid()) {
                                                unreadReceivedMessages.add(umsg);
                                                isPlayingNewSoundRing = true;
                                            }
                                        } else {
                                            if (Utils.currentOpenedGroupIdChatView != newMessage.getGroupid()) {
                                                unreadReceivedMessages.add(umsg);
                                                isPlayingNewSoundRing = true;
                                            }
                                        }
                                        nonCallControlMsgsToSaveInDatabase.add(newMessage);

//                                                    if(newMessage.getMessagemoshakkaltext().equals(ReceivedCallValues.Calling.name())){                                                       // TODO : Handle new received call request
//                                                            // TODO : Check if there is established call , send 'busy' to caller in moshakkal text
//                                                            // TODO : If there is no call , show received call dialog for user prompting him to respond or reject for a number of seconds
//                                                                // TODO : if he respond open message fragment for call
//                                                                // TODO : if he rejects send 'rejected' to caller and close dialog
//                                                                // TODO : IF time over send 'no response' to caller so he stop caller message
//                                                                // TODO : Log any unanswered call in missed calles table
//
//                                                    }
                                        if (newMessage.isGroupMessage()) {
                                            if (me == null) {
                                                me = AppDatabase.getUserDao().loadUserBlockable(userid);
                                            }
                                            Message translatedMessage = TRNSLG.translateBlockable(
                                                    newMessage,
                                                    newMessage.getSenderlanguage(),
                                                    me.getUserlanguage()
                                            );
                                            if (translatedMessage != null) {
                                                newMessage.setMessagetranslatedtext(
                                                        translatedMessage.getMessagetranslatedtext()
                                                );
                                            }
                                            synchronized (newGroupMessagesConsumers) {
                                                if (newGroupMessagesConsumers.containsKey(newMessage.getGroupid())) {
                                                    NewGroupsMessagesConsumer consumer = newGroupMessagesConsumers.get(newMessage.getGroupid());
                                                    if (consumer != null) {
                                                        requiredNewGroupMessageList.add(new RequiredNewGroupMessage(senderUserId, consumer, newReceivedMessages.get(i)));
                                                    } else {
                                                        newGroupMessagesConsumers.remove(newMessage.getGroupid());
                                                    }
                                                }
                                            }
                                        } else {
                                            synchronized (newMessagesConsumers) {
                                                if (newMessagesConsumers.containsKey(senderUserId)) {
                                                    NewMessagesConsumer consumer = newMessagesConsumers.get(senderUserId);
                                                    if (consumer != null) {
                                                        requiredNewMessageList.add(new RequiredNewMessage(senderUserId, consumer, newReceivedMessages.get(i)));
                                                    } else {
                                                        newMessagesConsumers.remove(senderUserId);
                                                    }
                                                }
                                            }
                                        }



                                }
                                else {
                                    if(newMessage.getControlText().equals(Utils.MESSAGE_DELETE_COMMAND) && context != null){
                                        if(AppDatabase.getMessageDao() == null){
                                            AppDatabase.callInActivityOnCreate(context);
                                        }
//                                        Message fetchedMsg = AppDatabase.getMessageDao().getMessageByIdBlocking(newMessage.getControlnumber());
//                                        fetchedMsg.setMessagestatus(MessageStatus.deleted);
//                                        AppDatabase.getMessageDao().updateMessage(fetchedMsg);
                                        AppDatabase.getMessageDao().deleteMessageById(newMessage.getControlnumber());

                                        if(newMessage.isGroupMessage()){
                                            synchronized (newGroupMessagesConsumers) {
                                                if (newGroupMessagesConsumers.containsKey(newMessage.getGroupid())) {
                                                    NewGroupsMessagesConsumer consumer = newGroupMessagesConsumers.get(newMessage.getGroupid());
                                                    if (consumer != null) {
                                                        requiredNewGroupMessageList.add(new RequiredNewGroupMessage(senderUserId, consumer, newReceivedMessages.get(i)));
                                                    } else {
                                                        newGroupMessagesConsumers.remove(newMessage.getGroupid());
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            synchronized (newMessagesConsumers) {
                                                if (newMessagesConsumers.containsKey(senderUserId)) {
                                                    NewMessagesConsumer consumer = newMessagesConsumers.get(senderUserId);
                                                    if (consumer != null) {
                                                        requiredNewMessageList.add(new RequiredNewMessage(senderUserId, consumer, newReceivedMessages.get(i)));
                                                    } else {
                                                        newMessagesConsumers.remove(senderUserId);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                        if(newMessage.getGroupid() != 0)
                                            handleVoiceCall(newReceivedMessages.get(i));
                                }

                            }
                            if (AppDatabase.getMessageDao() == null) {
                                try {
                                    AppDatabase.callInActivityOnCreate(context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                                AppDatabase.getUnreadReceivedMessageNotificationDao().insertAll(unreadReceivedMessages.toArray(new UnreadReceivedMessage[unreadReceivedMessages.size()]));
                            if ((status == PingerStatus.Free && isNonControlMsgReceived.get()) || isPlayingNewSoundRing) {
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.playMessageRing(context);
                                    }
                                });
                            }
                            try {
                                if (AppDatabase.getMessageDao() == null) {
                                    try {
                                        AppDatabase.callInActivityOnCreate(context);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                AppDatabase.getMessageDao().insertAll(nonCallControlMsgsToSaveInDatabase.toArray(new Message[nonCallControlMsgsToSaveInDatabase.size()]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isUnredItemsUpdated.set(true);
//                            for (Message msg : overloadedPingResult.getAllUserReceivedMessages())
//                                handleVoiceCall(msg);
                        }


                        if (isUnredItemsUpdated.get()) {
                            if (AppDatabase.getUnreadReceivedMessageNotificationDao() == null) {
                                try {
                                    AppDatabase.callInActivityOnCreate(context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                                lastUpdateOfUnreadReceivedMsgsNotifications = AppDatabase.getUnreadReceivedMessageNotificationDao().getAll();
                        }


                        if (lastUpdateOfUnreadReceivedMsgsNotifications != null && lastUpdateOfUnreadReceivedMsgsNotifications.size() > 0) {
                            List<User> users = overloadedPingResult.getUsers();
                            for (UnreadReceivedMessage umsg : lastUpdateOfUnreadReceivedMsgsNotifications) {
                                if(umsg.getGroupid() == 0) {
                                    for (User user : users) {
                                        if (user.getUserid() == umsg.getSenderid()) {
                                            user.setNumberOfUnreadMessages(user.getNumberOfUnreadMessages() + 1);
                                        }
                                    }
                                }
                                else{
                                    for (Group group : overloadedPingResult.getMyGroups()) {
                                        if (group.getGroupid() == umsg.getGroupid()) {
                                            group.setNumberOfUnreadMessages(group.getNumberOfUnreadMessages() + 1);
                                        }
                                    }
                                }
                            }
                        }


//                                            List<Message> debug = AppDatabase.getMessageDao().getAll();


                        final List<User> newUserList = new ArrayList<>();
                        for (User user : overloadedPingResult.getUsers()) {
                            if (user.getUserid() != userid)
                                newUserList.add(user);
                        }

                        Collections.sort(newUserList);

                        final List<Group> newGroupList = new ArrayList<>();
                        for (Group group : overloadedPingResult.getMyGroups()) {
                            newGroupList.add(group);
                        }
                        Collections.sort(newGroupList);

                        final Runnable consumersUpdater = new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < usersConsumers.size(); i++) {
                                    if (usersConsumers.get(i) != null) {
                                        usersConsumers.get(i).getUsersList(newUserList, null);
//                                        usersConsumers.get(i).getGroupList(newGroupList, null);
                                    }
                                    else
                                        usersConsumers.remove(i);
                                }

                                for (int i = 0; i < groupsConsumers.size(); i++) {
                                    if (groupsConsumers.get(i) != null) {
//                                        groupsConsumers.get(i).getUsersList(newUserList, null);
                                        groupsConsumers.get(i).getGroupList(newGroupList, null);
                                    }
                                    else
                                        groupsConsumers.remove(i);
                                }

                                for (RequiredNewMessage requiredNewMessage : requiredNewMessageList) {
                                    requiredNewMessage.notifyNewMessage();
                                }

                                for (RequiredNewGroupMessage requiredNewGroupMessage : requiredNewGroupMessageList) {
                                    requiredNewGroupMessage.notifyNewMessage();
                                }

                            }
                        };
                        Utils.runOnUIThread(consumersUpdater);


                        checkImagesOfAvatar();


//                    for (User user : overloadedPingResult.getAllUsers()) {
//                        synchronized (userAvatars) {
//                            if (!userAvatars.containsKey(user.getUserid())) {
//                                getUserAvatar(user.getUserid());
//                            }
//                            synchronized (avatarListeners){
//                                if(avatarListeners.containsKey(userid)){
//                                    final ImageReady imageReady = avatarListeners.get(userid);
//                                    final long idofthisuser = user.getUserid();
//                                    Utils.runOnUIThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            synchronized (userAvatars){
//                                                imageReady.imageReady(idofthisuser, userAvatars.get(idofthisuser));
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    }
                    }

                    try {
                        Thread.sleep(millis.get());
                    } catch (Exception e) { }
                }
            }
        }
    };

    private Runnable unreadMessagesTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (newGroupMessagesConsumers != null && context != null) {
                    HashMap<Long, NewMessagesConsumer> usersMsgConsumers = new HashMap<>();
                    synchronized (newMessagesConsumers) {
                        for (Long consumerid : newMessagesConsumers.keySet()) {
                            usersMsgConsumers.put(consumerid, newMessagesConsumers.get(consumerid));
                        }
                    }

                    for (Long consumerid : usersMsgConsumers.keySet()) {
                        try {
                            final NewMessagesConsumer consumer = usersMsgConsumers.get(consumerid);
                            if(consumer != null) {
                                final List<Message> unreadMsgs = APIClient.getAPIInterface(context).getUserToUserUnreadMessages(userid, consumerid).execute().body();
//                        Utils.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
                                if (consumer != null) {
                                    consumer.unreadMessages(unreadMsgs);
                                }
//                            }
//                        });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (newGroupMessagesConsumers != null && context != null) {
                    HashMap<Long, NewGroupsMessagesConsumer> groupsMsgConsumers = new HashMap<>();
                    synchronized (newGroupMessagesConsumers) {
                        for (Long consumerid : newGroupMessagesConsumers.keySet()) {
                            groupsMsgConsumers.put(consumerid, newGroupMessagesConsumers.get(consumerid));
                        }
                    }

                    for (Long consumerid : groupsMsgConsumers.keySet()) {
                        try {
                            final NewGroupsMessagesConsumer consumer = groupsMsgConsumers.get(consumerid);
                            if(consumer != null) {
                                final List<Message> unreadMsgs = APIClient.getAPIInterface(context).getGroupUnreadMessages(consumer.getGroupId()).execute().body();
//                        Utils.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
                                if (consumer != null) {
                                    consumer.unreadMessages(unreadMsgs);
                                }
//                            }
//                        });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void call(final User user) {
        // DONE : Establish call and send continuous 'call' to user for a certain number of second
        // If received 'busy' , 'rejected' , 'terminated' signals , terminate the call
        // Show dialog for user to notify him

        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Message response = new Message(context);
                response.setReceiverid(user.getUserid());
                response.setSenderid(userid);
                response.setMessagemoshakkaltext(ReceivedCallValues.Calling.name());
                Utils.sendMessage(context,response, null);
            }
        });
        openCallingDialog(false);
        Utils.playIamCallingRing(context);
        startCallTeminatingTimer();
        status = PingerStatus.Calling;
        caller = user;
        callerId = user.getID();

    }

    private void openCallingDialog(boolean isCallReceived) {
        this.inhibitClosingDialog.set(false);
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                CallDialogCallbacks callResultCallbacks = new CallDialogCallbacks() {
                    @Override
                    public void acceptCall() {
                        // this method is called only in case of receiving call dialog
//                        if(status == PingerStatus.ReceivingCall)
                        respondCall(caller);
                    }

                    @Override
                    public void rejectCall() {
                        rejectCallLocal();
                    }

                    @Override
                    public void registerReverseCallbacks(CallDialogReverseCallbacks callbacks) {
                        callDialogReverseCallbacks = callbacks;
                    }

                    @Override
                    public void setIsInhibitClosingDialogRequest(boolean value) {
                        inhibitClosingDialog.set(value);
                    }
                };
                if(isCallReceived)
                    ((CallDialogProvider)context).showCallDialogForCallReception(callResultCallbacks,caller);
                else
                    ((CallDialogProvider)context).showCallDialogForCallSourcing(callResultCallbacks,caller);
            }
        });

    }
    private void openCallingDialogPopup(boolean isCallReceived) {
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                callDialog = GeneralPopupWindow.makeCallWindow(context, caller, new CallDialogCallbacks() {
                    @Override
                    public void acceptCall() {
                        // this method is called only in case of receiving call dialog
//                        if(status == PingerStatus.ReceivingCall)
                            respondCall(caller);
                    }

                    @Override
                    public void rejectCall() {
                        rejectCallLocal();
                    }

                    @Override
                    public void registerReverseCallbacks(CallDialogReverseCallbacks callbacks) {

                    }

                    @Override
                    public void setIsInhibitClosingDialogRequest(boolean value) {

                    }
                },isCallReceived,false);
            }
        });

    }

    public void checkThreadHealth(){
        try{
            if(pingerDaemonThread == null){
                pingerDaemonThread = new Thread(asynPingerBackgroundRunnable);
                pingerDaemonThread.start();
            }

            if(unreadMessagesWorker == null){
                unreadMessagesWorker = new Thread(unreadMessagesTask);
                unreadMessagesWorker.start();
            }
        }
        catch (Exception e){

        }
    }

    private void sendOriginalTerminateMessage(){
        Message terminateMessage= new Message(context);
        terminateMessage.setMessagemoshakkaltext(ReceivedCallValues.Terminate.name());
        terminateMessage.setSenderid(callerId);//Note that this ids reversed in sendTerminateResponse
        terminateMessage.setReceiverid(userid);
        sendTerminateMessageResponse(terminateMessage);
    }

    @Override
    public void terminateCurrentCall() {
        // Called from outside pinger onlyyyyyyyyyyyyyyyyyyyyyyyy
        // Timer call terminate implemented by pinger with timer termination flag

        status = PingerStatus.Free;
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                sendOriginalTerminateMessage();
                closeCallRinger();
//                MessageFragmentProvider messageFragmentProvider = ((MessageFragmentProvider) context);
//                if (messageFragmentProvider != null) {
//                    messageFragmentProvider.endCallFragment();
//                }
            }
        });
    }

    public void releaseStatus(){
        status = PingerStatus.Free;
        stopTimer();
        sendOriginalTerminateMessage();
        if(status == PingerStatus.Calling || status == PingerStatus.ReceivingCall) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (callDialogReverseCallbacks != null)
                        callDialogReverseCallbacks.dontCallRejectCallbackOnDestroy_YouCanCallThisSyncOrAsync();
                    closeCallingDialog();
//                ((NavigationProvider)context).navigateToUsers();
                    Utils.stopRing();
                }
            });
        }
    }


    public void rejectCallLocal(){ isTeminatingTimerRun.set(false);countedTimerSegments.set(0);
            status = PingerStatus.Free;
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Utils.stopRing();
                    sendOriginalTerminateMessage();
                }
            });
    }

    private void closeCallRinger() {
        Utils.stopRing();
    }

    @Override
    public void respondCall(User user) {
        if(status == PingerStatus.ReceivingCall){
            Message m = new Message(context);
            m.setReceiverid(userid);
            m.setSenderid(callerId);
            m.setMessagemoshakkaltext(ReceivedCallValues.Accept.name());
            sendAcceptMessageResponse(m);
            establishCall(user,false);
        }
    }

    @Override
    public void sendTerminateMessage() {
        sendOriginalTerminateMessage();
    }

    @Override
    public void setFreeStatus(){
        status = PingerStatus.Free;
    }


    private void establishCall(User user,final boolean isOpenMsgs) {
        if(status == PingerStatus.ReceivingCall || status == PingerStatus.Calling) {
            stopTimer();
            status = PingerStatus.InCall;

            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Utils.stopRing();
                    if(callDialogReverseCallbacks!= null && isOpenMsgs)
                        callDialogReverseCallbacks.callEstablished(caller);
//                    MessageFragmentProvider messageFragmentProvider = ((MessageFragmentProvider) context);
//                    if (messageFragmentProvider != null) {
//                        // Already done in callingFragment
//                         messageFragmentProvider.provideMessageFragment(user, true);
//                    }
                }
            });
        }
        else{
            // If cere arraived know that you have broken the logic
        }
    }

    private void stopTimer() {
        isTeminatingTimerRun.set(false);
        countedTimerSegments.set(0);
    }


    private static class RequiredNewMessage {
        private long senderId;
        private NewMessagesConsumer consumer;
        private Message message;

        public RequiredNewMessage(long senderId, NewMessagesConsumer consumer, Message message) {
            this.senderId = senderId;
            this.consumer = consumer;
            this.message = message;
        }



        public void notifyNewMessage(){
            consumer.newMessage(senderId , message);
        }
    }

    private static class RequiredNewGroupMessage {
        private long groupid;
        private NewGroupsMessagesConsumer consumer;
        private Message message;

        public RequiredNewGroupMessage(long groupid,NewGroupsMessagesConsumer consumer, Message message) {
            this.groupid = groupid;
            this.consumer = consumer;
            this.message = message;
        }



        public void notifyNewMessage(){
            consumer.newMessage(groupid, message);
        }
    }

    private void startCallTeminatingTimer(){
        countedTimerSegments.set(0);
        isTeminatingTimerRun.set(true);

    }

    public AsyncPinger(Context context,User currentUser) {
        me = currentUser;
        this.context = context;status = PingerStatus.Free;
        Utils.setLastAsyncPinger(this);
        try {
            userid = Utils.getUserID(context);
            usersConsumers = new ArrayList<>();
            groupsConsumers = new ArrayList<>();
            newMessagesConsumers = new HashMap<>();
            newGroupMessagesConsumers = new HashMap<>();

            pingerDaemonThread = new Thread(asynPingerBackgroundRunnable);
            pingerDaemonThread.start();

            unreadMessagesWorker = new Thread(unreadMessagesTask);
            unreadMessagesWorker.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        startPingerDebug();
    }

    public void startPingerDebug(){
            new Thread(new Runnable(){
                @Override
                public void run() {

                    try {
                        Response<OverloadedPingResult> result = APIClient.getAPIInterface(context).pingToKeepOnlineAndGetRequiredInfo(userid).execute();
                        OverloadedPingResult result2 = result.body();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }}).start();

    }


    private void handleVoiceCall(Message newMessage) {
        // This method run in non ui thread

        if(newMessage.getMessagemoshakkaltext()!=null && newMessage.getMessagemoshakkaltext().equals(ReceivedCallValues.Calling.name())){
            // New incoming call
//            AppDatabase.getMessageDao().deleteMessageById(newMessage.getMessageid());
            switch(status){
                case Free:{
                    status = PingerStatus.ReceivingCall;
                    caller = getMessageSender(newMessage);
                    callerId = newMessage.getSenderid();
                    startCallTeminatingTimer();
                    openCallingDialog(true);
                    playReceiveCallRing();
                    break;
                }
                case Calling:
                case ReceivingCall:
                case InCall:{
                    if(newMessage.getSenderid() != callerId){
                        sendBusyMessageResponse(newMessage);
                        createMissedCall(callerId);
                    }
                    break;
                }
            }

        }



        else if(newMessage.getMessagemoshakkaltext() != null && newMessage.getMessagemoshakkaltext().equals(ReceivedCallValues.Accept.name())){
//            AppDatabase.getMessageDao().deleteMessageById(newMessage.getMessageid());
            switch(status){
                case Free:{
                    sendTerminateMessageResponse(newMessage);
                    break;
                }
                case Calling:{
                    if(callDialogReverseCallbacks != null)
                        callDialogReverseCallbacks.dontCallRejectCallbackOnDestroy_YouCanCallThisSyncOrAsync();
//                    closeCallingDialog();
                    if(newMessage.getSenderid() == callerId){
                        establishCall(caller,true);
                    }
                    else{
//                        status = PingerStatus.Free;
                        sendTerminateMessageResponse(newMessage);
                    }
                    break;
                }
                case ReceivingCall:{
                    if(newMessage.getSenderid() == callerId){

                    }
                    else {
                        sendTerminateMessageResponse(newMessage);
                        break;
                    }
                }
                case InCall:{
                    if(newMessage.getSenderid() != callerId){
                        sendTerminateMessageResponse(newMessage);
                    }
                    break;
                }
            }
        }



        else if((newMessage.getMessagemoshakkaltext() != null && newMessage.getMessagemoshakkaltext().equals(ReceivedCallValues.Busy.name())) || (newMessage.getMessagemoshakkaltext() !=null && newMessage.getMessagemoshakkaltext().equals(ReceivedCallValues.Terminate.name()))){
//            AppDatabase.getMessageDao().deleteMessageById(newMessage.getMessageid());
//            if(newMessage.getSenderid() == callerId && status != PingerStatus.Free)
//                releaseStatus();
//            return;
            switch(status){
                case Free:{

                    break;
                }
                case ReceivingCall:
                case Calling:{
                    if (newMessage.getSenderid() != callerId)
                        createMissedCall(callerId);
                    else{
                        stopTimer();
                        if(callDialogReverseCallbacks != null)
                            callDialogReverseCallbacks.dontCallRejectCallbackOnDestroy_YouCanCallThisSyncOrAsync();
                        closeCallingDialog();
                        status = PingerStatus.Free;
                    }
                    break;
                }
                case InCall:{
                    if(newMessage.getSenderid() == callerId) {
                        status = PingerStatus.Free;
                        stopTimer();
                        if (messageFragmentReverseCallbacks != null) {
                            try {
                                messageFragmentReverseCallbacks.pleaseMessageFragmentDontSendTerminateMessageAtOnDestroyCallbackBecauseIWillDoInAsyncPinger();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Utils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                ((NavigationProvider) context).returnToPreviousFragment();
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    private void closeCallingDialog() {
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Utils.stopRing();
            }
        });
        if(!inhibitClosingDialog.get() && (status == PingerStatus.Calling || status == PingerStatus.ReceivingCall)) {
            Utils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ((CallDialogProvider) context).hideCallDialog();
//                if(callDialog != null){
//                    try{
//                        callDialog.close();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
                }
            });
        }

    }

    private void playReceiveCallRing() {
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Utils.playIamReceivingCallRing(context);
            }
        });
    }

    private void sendBusyMessageResponse(final Message message) {
        sendCallControlMessage(message,ReceivedCallValues.Busy);

    }

    private void sendAcceptMessageResponse(final Message message) {
        sendCallControlMessage(message,ReceivedCallValues.Accept);

    }

    private void sendTerminateMessageResponse(final Message message) {
        sendCallControlMessage(message,ReceivedCallValues.Terminate);

    }

    private void sendCallingMessageResponse(final Message message) {
        sendCallControlMessage(message,ReceivedCallValues.Calling);

    }


    private void sendCallControlMessage(final Message message, ReceivedCallValues value){
        createMissedCall(message.getSenderid());
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Message response = new Message(context);
                response.setReceiverid(message.getSenderid());
                response.setSenderid(message.getReceiverid());
                response.setMessagemoshakkaltext(value.name());
                Utils.sendMessage(context,response, null);
            }
        });
    }



    private User getMessageSender(Message newMessage) {
        for(User user : overloadedPingResult.getUsers()){
            if(user.getUserid() == newMessage.getSenderid()){
                return user;
            }
        }
        return null;
    }

    private void handletCallTeminatingTimer() {
        // Run in non UI thread
        if(isTeminatingTimerRun.get()){
            if(countedTimerSegments.getAndIncrement() >= TIMER_SEGMENTS){
                isTeminatingTimerRun.set(false);
                if(status == PingerStatus.ReceivingCall){
                    createMissedCall(callerId);
                }
                status = PingerStatus.Free;
                stopTimer();
                closeCallingDialog();
            }
        }
    }

    private void stopRinging() {
        Utils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Utils.stopRing();
            }
        });

    }

    private void createMissedCall(long callerid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MissedCall newMissedcall = new MissedCall();
                newMissedcall.setCallerid(callerid);
                if(AppDatabase.getMissedCallDao() == null){
                    try{
                        AppDatabase.callInActivityOnCreate(context);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(AppDatabase.getMissedCallDao() != null)
                    AppDatabase.getMissedCallDao().insertAll(newMissedcall);
            }
        }).start();
    }

    public void addUsersConsumer(UserListConsumer consumer){
        if(consumer != null) {
            if (!usersConsumers.contains(consumer))
                this.usersConsumers.add(consumer);
            if(overloadedPingResult!=null) {
                    if (overloadedPingResult != null && overloadedPingResult.getUsers() != null) {
                        List<User> existingUsers = overloadedPingResult.getUsers();
                        List<User> newUsersList = new ArrayList<>();
                        for (User user : existingUsers) {
                            if(user.getUserid() != userid)
                                newUsersList.add(user);
                        }
                        Collections.sort(newUsersList);
                        consumer.getUsersList(newUsersList, null);
                    }
            }
        }
    }

    public void addGroupsConsumer(UserListConsumer consumer){
        if(consumer != null) {
            if (!groupsConsumers.contains(consumer))
                this.groupsConsumers.add(consumer);
            if(overloadedPingResult!=null) {
                    if (overloadedPingResult != null && overloadedPingResult.getMyGroups() != null) {
                        List<Group> existingGroups = overloadedPingResult.getMyGroups();
                        List<Group> newGroupList = new ArrayList<>();
                        for (Group group : existingGroups) {
                            newGroupList.add(group);
                        }
                        Collections.sort(newGroupList);
                        consumer.getGroupList(newGroupList, null);
                    }
            }
        }
    }

    public void addNewMessagesConsumer(long senderUseId , NewMessagesConsumer consumer){
        synchronized (newMessagesConsumers) {
            this.newMessagesConsumers.put(senderUseId, consumer);
        }
    }

    public void addNewGroupMessagesConsumer(long groupid , NewGroupsMessagesConsumer consumer){
        synchronized (newGroupMessagesConsumers) {
            this.newGroupMessagesConsumers.put(groupid, consumer);
        }
    }

    public void setNormalRate(){
        millis.set(NORMAL_RATE);
    }

    public void setFastRate(){
        millis.set(HIGH_RATE);
    }

    public void notifyUnreadItemsDatabaseUpdated(){
        isUnredItemsUpdated.set(true);
    }

    public void dispose(){
        if(status == PingerStatus.InCall){
            sendOriginalTerminateMessage();
        }
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}
        try{pingerDaemonThread.interrupt();}catch (Exception e){e.printStackTrace();}


        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
        try{unreadMessagesWorker.interrupt();}catch (Exception e){e.printStackTrace();}
    }












    private HashMap<Long, ArrayDeque<ImageReady>> avatarListeners = new HashMap<>();
    public void registerImageReadyListenerOrGetImageIfExist(long userid, ImageReady imageReady){
        synchronized (userAvatars) {
            if (userAvatars.containsKey(userid)) {
                imageReady.imageReady(userid, userAvatars.get(userid));
            } else {
                imageReady.imageReady(userid, null);
                if(avatarListeners.containsKey(userid) && avatarListeners.get(userid) != null){
                    avatarListeners.get(userid).offer(imageReady);
                }
                else {
                    ArrayDeque<ImageReady> userImageReadyObjects = new ArrayDeque<>();
                    userImageReadyObjects.offer(imageReady);
                    avatarListeners.put(userid, userImageReadyObjects);
                }
            }
        }
    }

    private HashMap<Long, ArrayDeque<ImageReady>> groupAvatarListeners = new HashMap<>();
    public void registerImageReadyListenerOrGetImageIfExistForGroups(long groupid, ImageReady imageReady){
        synchronized (groupAvatars) {
            if (groupAvatars.containsKey(groupid)) {
                imageReady.imageReady(groupid, groupAvatars.get(groupid));
            } else {
                imageReady.imageReady(userid, null);
                if(groupAvatarListeners.containsKey(groupid) && groupAvatarListeners.get(groupid) != null){
                    groupAvatarListeners.get(groupid).offer(imageReady);
                }
                else {
                    ArrayDeque<ImageReady> groupImageReadyObjects = new ArrayDeque<>();
                    groupImageReadyObjects.offer(imageReady);
                    groupAvatarListeners.put(groupid, groupImageReadyObjects);
                }
            }
        }
    }

    private void getUserAvatarOld(final long userid){
        synchronized (userAvatars) {
            if (!userAvatars.containsKey(userid)) {
                try {
                    Response<ResponseBody> response = APIClient.getAPIInterface(context).downloadPhoto(userid).execute();
                    final String path = context.getFilesDir().getPath() // /data/user/0/hasan.mohamed.shehata.myapplication/files/myphoto34532.png
//                Environment.getExternalStorageDirectory() //  /storage/o
                            + File.separator + "myphoto3453255.png";
                    File f = new File(path);
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        f.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //write the bytes in file
                    try {
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(response.body().bytes());
                        // remember close de FileOutput
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    synchronized (userAvatars) {
                        userAvatars.put(userid, bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }







//    private void getUserAvatar(final long userid){
//        Call<ResponseBody> c = APIClient.getAPIInterface(context).downloadPhoto(userid);
//        c.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if(response.isSuccessful()){
//                    Headers headers = response.headers();
//                    long currentId = Long.parseLong(headers.get("id"));
//                    byte [] imageBytes = null;
//                    try {
//                        imageBytes = response.body().bytes();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if(imageBytes != null) {
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                        userAvatars.put(currentId,bitmap);
//                        if (avatarListeners.containsKey(userid)) {
//                            avatarListeners.get(currentId).imageReady(currentId,bitmap);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                call.cancel();
//                call.request();
//                String [] parts = call.request().url().toString().split("/");
//                long id = Long.parseLong(parts[parts.length -1]);
//                getUserAvatar(id);
//            }
//        });
//
//    }
//
//
//    private void checkImagesOfAvatarAsyncOld() {
//        synchronized (avatarUsersList) {
//            boolean isCurrentUserAvatarExist = false;
//            for (User user : avatarUsersList) {
//                synchronized (userAvatars) {
//                    isCurrentUserAvatarExist = userAvatars.containsKey(user.getUserid());
//                }
//                if (!isCurrentUserAvatarExist) {
//                    try {
//                        byte[] bytes = APIClient.getAPIInterface(context).downloadPhoto(user.getUserid()).execute().body().bytes();
//                        Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        synchronized (userAvatars) {
//                            userAvatars.put(user.getUserid(), avatar);
//                        }
//                        final long i = user.getUserid();
//                        Utils.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                synchronized (userAvatars) {
//                                    if (avatarListeners.get(i) != null && userAvatars.containsKey(i)) {
//                                        avatarListeners.get(i).imageReady(i, userAvatars.get(i));
//                                        avatarListeners.remove(i);
//                                    }
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        }
//    }




    private void checkImagesOfAvatarAsync() {
        synchronized (avatarUsersAndGroupsList) {
            boolean isCurrentUserAvatarExist = false;
            for (Object userOrGroup : avatarUsersAndGroupsList) {

                if (userOrGroup instanceof User) {
                    synchronized (userAvatars) {
                        isCurrentUserAvatarExist = userAvatars.containsKey(((User) userOrGroup).getUserid());
                    }
                } else if (userOrGroup instanceof Group) {
                    synchronized (groupAvatars) {
                        isCurrentUserAvatarExist = groupAvatars.containsKey(((Group) userOrGroup).getGroupid());
                    }
                }
                final Object userOrGroup2 = userOrGroup;
                if (!isCurrentUserAvatarExist) {
                    if (userOrGroup2 instanceof User) {
                        APIClient.getAPIInterface(context).downloadPhoto(((User) userOrGroup2).getUserid()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        byte[] bytes = response.body().bytes();
                                        Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        synchronized (userAvatars) {
                                            userAvatars.put(((User) userOrGroup2).getUserid(), avatar);
                                            final long i = ((User) userOrGroup2).getUserid();
                                            if (avatarListeners.containsKey(i) && avatarListeners.get(i) != null && userAvatars.containsKey(i)) {
                                                ArrayDeque<ImageReady> userImageReadyObjs = avatarListeners.get(i);
                                                for (ImageReady imageReady : userImageReadyObjs) {
                                                    imageReady.imageReady(i, userAvatars.get(i));
                                                }
                                                avatarListeners.remove(i);
                                            }
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                    else if (userOrGroup2 instanceof Group) {
                        APIClient.getAPIInterface(context).downloadGroupPhoto(((Group) userOrGroup2).getGroupid()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        if(newGroupMessagesConsumers.containsKey(((Group) userOrGroup2).getGroupid())) {
                                            NewGroupsMessagesConsumer consumer = newGroupMessagesConsumers.get(((Group) userOrGroup2).getGroupid());
                                            if(consumer != null){
                                                consumer.updateGroupImage();
                                            }
                                        }
                                        byte[] bytes = response.body().bytes();
                                        Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        synchronized (groupAvatars) {
                                            groupAvatars.put(((Group) userOrGroup2).getGroupid(), avatar);
                                            final long i = ((Group) userOrGroup2).getGroupid();
                                            if (groupAvatarListeners.containsKey(i) && groupAvatarListeners.get(i) != null && groupAvatarListeners.containsKey(i)) {
                                                ArrayDeque<ImageReady> userImageReadyObjs = groupAvatarListeners.get(i);
                                                for (ImageReady imageReady : userImageReadyObjs) {
                                                    imageReady.imageReady(i, groupAvatars.get(i));
                                                }
//                                                groupAvatarListeners.remove(i);
                                            }
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Thread avatarWorker = null;
    private List avatarUsersAndGroupsList = null;
    private void checkImagesOfAvatar() {
        if (avatarWorker == null || !avatarWorker.isAlive()) {
            if (avatarUsersAndGroupsList == null)
                avatarUsersAndGroupsList = new ArrayList<>();
            avatarUsersAndGroupsList.clear();
            synchronized (avatarUsersAndGroupsList) {
                for (User user : overloadedPingResult.getUsers()) {
                    avatarUsersAndGroupsList.add(user);
                }

                for (Group group : overloadedPingResult.getMyGroups()) {
                    avatarUsersAndGroupsList.add(group);
                }
            }
            avatarWorker = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkImagesOfAvatarAsync();
                }
            });
            avatarWorker.start();
        }

    }


    public void release(){
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
        try{pingerDaemonThread.interrupt();}catch (Exception e){ }
    }


}
