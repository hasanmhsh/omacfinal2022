package hasan.mohamed.shehata.myapplication.ui.messages;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.dao.MessageDao;
import hasan.mohamed.shehata.myapplication.dao.UserDao;
import hasan.mohamed.shehata.myapplication.databinding.FragmentMessagesBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.languages.ASR_Enhanced;
import hasan.mohamed.shehata.myapplication.languages.HMSTransloator;
import hasan.mohamed.shehata.myapplication.languages.TRNSLG;
import hasan.mohamed.shehata.myapplication.languages.TTS;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.templates.GeneralRecyclerViewAdapter;
import hasan.mohamed.shehata.myapplication.types.AsrResultCallbacks;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.DownloadCallbacks;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.HighContrastObserver;
import hasan.mohamed.shehata.myapplication.types.MessageFragmentReverseCallbacks;
import hasan.mohamed.shehata.myapplication.types.NewMessagesConsumer;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.SearchCallbacks;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.TranslationReadyHandler;
import hasan.mohamed.shehata.myapplication.views.MessageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment implements SpeakerProvider, MessageFragmentReverseCallbacks, HighContrastObserver {


    private boolean isViewForGroupChat = false;
    private Group chatGroup;
    public static final String BUNDLE_KEY_FOR_ME_USER = "hasan.mohamed.shehata.myapplication.MeUser";
    public static final String BUNDLE_KEY_FOR_BUDDY_USER = "hasan.mohamed.shehata.myapplication.MyBuddyUser";
    public static final String BUNDLE_KEY_FOR_IS_FOR_CALL = "hasan.mohamed.shehata.myapplication.BUNDLE_KEY_FOR_IS_FOR_CALL";
    public static final String BUNDLE_KEY_FOR_IS_FOR_GROUP = "hasan.mohamed.shehata.myapplication.BUNDLE_KEY_FOR_IS_FOR_GROUP";
    public static final String BUNDLE_KEY_FOR_CHAT_GROUP = "hasan.mohamed.shehata.myapplication.BUNDLE_KEY_FOR_CHAT_GROUP";

    private MessageViewModel messagesViewModel;
    private FragmentMessagesBinding binding;
    private User buddy;
    private User me;
    private HMSTransloator translator;
    private TTS tts;
    private TTS translatedTTS;
    private Closeable progressWindow;
    private boolean isListeningNow = false;
    private AsyncPinger pinger;
    private boolean isForCall;
    private Thread asrThread;
    private AtomicBoolean inhibiteSendingTerminateMessageOnDestroyCallback = new AtomicBoolean(false);
    private ASR_Enhanced asr;
    private AsrResultCallbacks asrResultCallbacks = new AsrResultCallbacks() {
        @Override
        public void voiceRecognized(String result) {
            if(isForCall){
                if(binding!=null)
                    if(binding.sendingTextEt !=null){
                        binding.sendingTextEt.getText().clear();
                    }
                sendMessageFromString(result);
            }
        }

        @Override
        public void partialVoiceRecognized(String partialResult) {
            // Already shown bu ASR_Enhanced
        }
    };
    // Hypothis is the text which is recognized from speech


    public void say(Message message , boolean isTranslatedTextShown){
        String ttsText = "";
        boolean isToUseTranslatedTTS = false;
//        isToUseTranslatedTTS = isTranslatedTextShown;   //debug
        if(isTranslatedTextShown){
            if(message.getSenderid() == me.getUserid()) {
                isToUseTranslatedTTS = true;
                ttsText = message.getMessagetranslatedtext();
//                if (translatedTTS != null) {
//                    if (buddy.getUserlanguage() == Language.Arabic) {
//                        ttsText = message.getMessagemoshakkaltext();
//                    } else {
//                        ttsText = message.getMessagetranslatedtext();
//                    }
//                }
            }
            else{
                isToUseTranslatedTTS = false;
                ttsText = message.getMessagetranslatedtext();
//                if (translatedTTS != null) {
//                    if (me.getUserlanguage() == Language.Arabic) {
//                        ttsText = message.getMessagemoshakkaltext();
//                    } else {
//                        ttsText = message.getMessagetranslatedtext();
//                    }
//                }
            }
        }
        else{
            if(message.getSenderid() == me.getUserid()) {
                isToUseTranslatedTTS = false;
                ttsText = message.getMessagetext();
//                if (translatedTTS != null) {
//                    if (me.getUserlanguage() == Language.Arabic) {
//                        ttsText = message.getMessagemoshakkaltext();
//                    } else {
//                        ttsText = message.getMessagetext();
//                    }
//                }
            }
            else{
                isToUseTranslatedTTS = true;
                ttsText = message.getMessagetext();
//                if (translatedTTS != null) {
//                    if (buddy.getUserlanguage() == Language.Arabic) {
//                        ttsText = message.getMessagemoshakkaltext();
//                    } else {
//                        ttsText = message.getMessagetext();
//                    }
//                }
            }
        }

        if(isToUseTranslatedTTS){
            if(translatedTTS != null) {
                translatedTTS.speak(ttsText);
//                translatedTTS.speak(message.getMessagetranslatedtext());
            }
        }
        else{
            if(tts != null) {
                tts.speak(ttsText);
//                translatedTTS.speak(message.getMessagetext());
            }
        }
    }

    private void makeTTS(){
        if(tts != null)
            tts.release();
        tts = new TTS(getContext(), me.getUserlanguage());
        if(translatedTTS != null)
            translatedTTS.release();
        if(buddy!=null)
            translatedTTS = new TTS(getContext(), buddy.getUserlanguage());
    }


    private void openProgressWindow(String title){
        closeProgressWindow();
        progressWindow = GeneralPopupWindow.makeProgressWindow(getContext(), title, false);
    }
    private void closeProgressWindow(){
        if(progressWindow != null){
            try{
                progressWindow.close();
            }
            catch(Exception e){
            }
        }
    }

//    public MessageFragment(User buddy) {
//        super();
//        this.buddy = buddy;
//    }

    private float textSizeOfMessageET;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        messagesViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);
        if(getArguments().containsKey(BUNDLE_KEY_FOR_ME_USER)) {
            me = (User) getArguments().getSerializable(BUNDLE_KEY_FOR_ME_USER);
            messagesViewModel.getMeLiveData().setValue(me);
        }
        if(getArguments().containsKey(BUNDLE_KEY_FOR_BUDDY_USER)) {
            buddy = (User) getArguments().getSerializable(BUNDLE_KEY_FOR_BUDDY_USER);
            messagesViewModel.getBuddyLiveData().setValue(buddy);
        }
        if(getArguments().containsKey(BUNDLE_KEY_FOR_IS_FOR_CALL)){
            isForCall = getArguments().getBoolean(BUNDLE_KEY_FOR_IS_FOR_CALL);
            messagesViewModel.getIsForCall().setValue(isForCall);
        }

        if(getArguments().containsKey(BUNDLE_KEY_FOR_IS_FOR_GROUP)){
            isViewForGroupChat = getArguments().getBoolean(BUNDLE_KEY_FOR_IS_FOR_GROUP);
            messagesViewModel.getIsGroupChatView().setValue(isViewForGroupChat);
        }
        if(getArguments().containsKey(BUNDLE_KEY_FOR_CHAT_GROUP)) {
            chatGroup = (Group) getArguments().getSerializable(BUNDLE_KEY_FOR_CHAT_GROUP);
            messagesViewModel.getChatGroup().setValue(chatGroup);
        }


        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.setUser(new User());

//        messagesViewModel.getBuddyLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                buddy = user;
//                AppDatabase.getUserDao().loadUser(Utils.getUserID(getContext())).observe(getViewLifecycleOwner(), new Observer<User>() {
//                    @Override
//                    public void onChanged(User user) {
//                        me = user;
//                        binding.textMessages.setText(buddy.getUsername());
//                        messagesViewModel.getMeLiveData().setValue(me);
//                        makeASR();
//                        makeTTS();
//                        // Prepare Translation Model
//                        if(user.getUserlanguage() != null && buddy.getUserlanguage() != null) {
//                            if (user.getUserlanguage() != buddy.getUserlanguage()) {
//                                translator = new HMSTransloator(getContext(), me.getUserlanguage(), buddy.getUserlanguage(), null, null, null, null, new DownloadCallbacks() {
//                                    @Override
//                                    public void downloadCompleted() {
//                                        getDatasetFromDB();
//                                    }
//                                }, false);
//                            } else {
//                                getDatasetFromDB();
//                            }
//                        }
//                    }
//                });
//            }
//        });
//
//        if(isForCall && false){
//            binding.sendingTextEt.setVisibility(View.GONE);
//            binding.recognizeVoiceBut.setVisibility(View.GONE);
//            binding.sendBut.setVisibility(View.GONE);
//            binding.controlsContainer.setVisibility(View.GONE);
//        }
//
//        final TextView textView = binding.textMessages;
//        messagesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
////                textView.setText(s);
//            }
//        });
//
//        messagesViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ListItemBindableItemContentProvider>>() {
//            @Override
//            public void onChanged(List<ListItemBindableItemContentProvider> listItemBindableItemContentProviders) {
////                initList(listItemBindableItemContentProviders);
//            }
//        });
//
//        messagesViewModel.getTextInSendBox().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                binding.sendingTextEt.setText(s);
//            }
//        });
//
//        messagesViewModel.getIsForCall().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                isForCall = aBoolean;
//            }
//        });
//
//        binding.sendBut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMessageFromString(binding.sendingTextEt.getText().toString());
////                final Message newMessage = new Message();
////                newMessage.setSenderid(me.getUserid());
////                newMessage.setReceiverid(buddy.getUserid());
////                newMessage.setMessagetext(binding.sendingTextEt.getText().toString());
////                newMessage.setIsToShowTranslatedText(true);
////                if(buddy.getUserlanguage() != me.getUserlanguage()){
////                    translator.translateMessageAsync(newMessage, new NewMessagesConsumer() {
////                        @Override
////                        public void newMessage(long senderUserId, Message message) {
////                            // Not implemented
////                        }
////
////                        @Override
////                        public void sendAndSaveThisMessage(final Message message) {
////                            shakkelha(message);
////                        }
////                    });
////                }
////                else{
////                    newMessage.setMessagetranslatedtext(newMessage.getMessagetext());
////                    shakkelha(newMessage);
////                }
//            }
//        });
//
//
//        pinger = ((AsyncPingerProvider)getActivity()).getCurrentPinger();



        return root;
    }



    private void sendMessage(Message message) {
        final GeneralRecyclerViewAdapter<MessageView> adapter = (GeneralRecyclerViewAdapter<MessageView>) binding.fragmentRecyclerView.getAdapter();
        APIClient.getAPIInterface(getContext()).createNewMessage(message).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    final Message msg = response.body();
                    msg.setIsToShowTranslatedText(false);
                    if(adapter != null){
                        adapter.newMessage(me.getUserid(),msg);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!checkIfControlMessage(msg))
                                if(AppDatabase.getMessageDao() == null){
                                    AppDatabase.callInActivityOnCreate(getContext());
                                }
                                if(AppDatabase.getMessageDao() != null)
                                    AppDatabase.getMessageDao().insertAll(msg);
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                call.cancel();
            }
        });

    }

    private void sendMessageFromString(String string){
        final Message newMessage = new Message(getContext());
        newMessage.setSenderid(me.getUserid());
        if(buddy != null && !isViewForGroupChat)
            newMessage.setReceiverid(buddy.getUserid());
        if(isViewForGroupChat && chatGroup!= null)
            newMessage.setGroupid(chatGroup.getGroupid());
        newMessage.setMessagetext(string);
        newMessage.setIsToShowTranslatedText(false);
        if (buddy != null && !isViewForGroupChat) {
            if (buddy.getUserlanguage() != me.getUserlanguage()) {
                if (Utils.getIsToUseCloudTranslation()) {

                    // Using G
                    TRNSLG.translate(newMessage, me.getUserlanguage(), buddy.getUserlanguage(), new TranslationReadyHandler() {
                        @Override
                        public void translationDone(Message messageToBeSent) {
                            messageToBeSent.setIsToShowTranslatedText(false);
                            shakkelha(messageToBeSent);
                        }
                    });
                } else {
                    //// Using hms Translation
                    translator.translateMessageAsync(newMessage, new NewMessagesConsumer() {
                        @Override
                        public void newMessage(long senderUserId, Message message) {
                            // Not implemented
                        }

                        @Override
                        public void sendAndSaveThisMessage(final Message message) {
                            message.setIsToShowTranslatedText(false);
                            shakkelha(message);
                        }

                        @Override
                        public void deleteMessage(long messageid) {
                            if (binding != null && binding.fragmentRecyclerView != null && binding.fragmentRecyclerView.getAdapter() != null) {
                                GeneralRecyclerViewAdapter<MessageView> adapter =
                                        (GeneralRecyclerViewAdapter<MessageView>)
                                                binding.fragmentRecyclerView.getAdapter();
                                adapter.deleteMessage(messageid);
                            }
                        }

                        @Override
                        public void unreadMessages(List<Message> unreadMessages) {

                        }
                    });
                }
            } else {
                newMessage.setMessagetranslatedtext(newMessage.getMessagetext());
                shakkelha(newMessage);
            }
        }
        else{
            shakkelha(newMessage);
        }
    }

    private void shakkelha(final Message message){

        sendMessage(message);





//        String textToTashkeel = null;
//        if(me.getUserlanguage() == Language.Arabic){
//            textToTashkeel = message.getMessagetext();
//        }
//        else if(buddy.getUserlanguage() == Language.Arabic){
//            textToTashkeel = message.getMessagetranslatedtext();
//        }
//        if(textToTashkeel != null) {
//            APIClient.getAPIInterface().shakkel(new Moshakkal(textToTashkeel)).enqueue(new Provider<MoshakkalResult>() {
//                @Override
//                public void onResponse(Call<MoshakkalResult> call, Response<MoshakkalResult> response) {
//                    if(response.isSuccessful()){
//                        message.setMessagemoshakkaltext(response.body().getMoshakkalText());
//                        sendMessage(message);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MoshakkalResult> call, Throwable t) {
//                    call.cancel();
//                }
//            });
//        }
//        else{
//            sendMessage(message);
//        }
    }

    private boolean checkIfControlMessage(Message message) {
        String control = message.getMessagemoshakkaltext();
        if(control != null){
            if(control.equals("Calling") ||control.equals("Accept") || control.equals("Busy") || control.equals("Terminate"))
                return true;
        }
        return false;
    }

    private void getDatasetFromDB() {
//        if(getContext() != null) {
        MessageDao messageDao = AppDatabase.getMessageDao();
        if(messageDao == null){
            AppDatabase.callInActivityOnCreate(getContext());
        }
        if(messageDao != null){

            if(isViewForGroupChat){
                messageDao.getMyGroupMessages(chatGroup.getGroupid()).observe(getActivity(), new Observer<List<Message>>() {
                    @Override
                    public void onChanged(List<Message> messages) {
                        List<ListItemBindableItemContentProvider> list = new ArrayList<>();
                        if (messages != null) {

//                    list = new ArrayList<>(messages);
                            for (Message msg : messages) {
                                if (!checkIfControlMessage(msg)) {
                                    list.add(msg);
                                }
                            }
                        }
                        initList(list);

                    }
                });
            }
            else {
                messageDao.getMyMessages(Utils.getUserID(getContext()), buddy.getUserid()).observe(getActivity(), new Observer<List<Message>>() {
                    @Override
                    public void onChanged(List<Message> messages) {
                        List<ListItemBindableItemContentProvider> list = new ArrayList<>();
                        if (messages != null) {

//                    list = new ArrayList<>(messages);
                            for (Message msg : messages) {
                                if (!checkIfControlMessage(msg)) {
                                    list.add(msg);
                                }
                            }
                        }
                        initList(list);

                    }
                });
            }
        }
    }

    private void makeASR() {
        if(me != null) {
            asr = new ASR_Enhanced(
                    getActivity(),
                    (PermissionRequestProvider) getActivity(),
                    me.getUserlanguage(),
                    binding.recognizeVoiceBut,
                    R.drawable.ic_baseline_mic_50,
                    R.drawable.ic_baseline_mic_off_50,
                    binding.sendingTextEt,
                    asrResultCallbacks
            );
        }
    }

    private void initList(List<ListItemBindableItemContentProvider> listItemBindableItemContentProviders) {
        if(binding == null || binding.fragmentRecyclerView == null)
            return;
        SearchCallbacks searchCallbacks = null;
        if(this != null && this.getActivity() != null)
            searchCallbacks = ((TranslationMainActivity)getActivity()).getSearchableCallBacks();
        GeneralRecyclerViewAdapter<MessageView> adapter = new GeneralRecyclerViewAdapter<MessageView>(getContext(), listItemBindableItemContentProviders, null, MessageView.class, FabActionType.None,null, buddy,this,binding.fragmentRecyclerView,me , isViewForGroupChat , chatGroup, null,false,searchCallbacks);
        binding.fragmentRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isViewForGroupChat){
            Utils.currentOpenedGroupIdChatView = 0;
        }
        else{
            Utils.currentOpenedBuddyIdChatView = 0;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isViewForGroupChat){
            if(chatGroup != null){
                Utils.currentOpenedGroupIdChatView = chatGroup.getGroupid();
            }
        }
        else{
            if(buddy != null){
                Utils.currentOpenedBuddyIdChatView = buddy.getID();
            }
        }
        binding.fragmentRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.fragmentRecyclerView.setLayoutManager(layoutManager);
        clearUnreadFlags();

        messagesViewModel.getBuddyLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user == null)
                    return;;
                buddy = user;
                UserDao userDao = AppDatabase.getUserDao();
                if(userDao == null){
                    AppDatabase.callInActivityOnCreate(getContext());
                }
                if (userDao != null) {
                    userDao.loadUser(Utils.getUserID(getContext())).observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            me = user;
                            binding.setUser(buddy);
                            binding.getUser().drawLogo(binding.headerMyMsgImageView);
                            messagesViewModel.getMeLiveData().setValue(me);
                            makeASR();
                            makeTTS();
                            // Prepare Translation Model
                            if (user.getUserlanguage() != null && buddy.getUserlanguage() != null) {
                                if (user.getUserlanguage() != buddy.getUserlanguage()) {
                                    translator = new HMSTransloator(getContext(), me.getUserlanguage(), buddy.getUserlanguage(), null, null, null, null, new DownloadCallbacks() {
                                        @Override
                                        public void downloadCompleted() {
//                                        getDatasetFromDB();
                                        }
                                    }, false);
                                } else {
//                                getDatasetFromDB();
                                }
                            }
                        }
                    });
                }
            }
        });

        messagesViewModel.getIsGroupChatView().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null)
                    isViewForGroupChat = aBoolean;
            }
        });

        messagesViewModel.getChatGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                if(group == null)
                    return;
                chatGroup = group;
                UserDao userDao = AppDatabase.getUserDao();
                if(userDao == null){
                    AppDatabase.callInActivityOnCreate(getContext());
                }
                if (userDao != null) {
                    userDao.loadUser(Utils.getUserID(getContext())).observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            me = user;
                            User guser = new User();
                            guser.setGroup(chatGroup);
                            binding.setUser(guser);
                            binding.getUser().drawLogo(binding.headerMyMsgImageView);
                            messagesViewModel.getMeLiveData().setValue(me);
                            makeASR();
                            makeTTS();
                        }
                    });
                }
            }
        });

        if(isForCall && false){
            binding.sendingTextEt.setVisibility(View.GONE);
            binding.recognizeVoiceBut.setVisibility(View.GONE);
            binding.sendBut.setVisibility(View.GONE);
            binding.controlsContainer.setVisibility(View.GONE);
        }

//        final TextView textView = binding.textMessages;
        messagesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        messagesViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ListItemBindableItemContentProvider>>() {
            @Override
            public void onChanged(List<ListItemBindableItemContentProvider> listItemBindableItemContentProviders) {
//                initList(listItemBindableItemContentProviders);
            }
        });

        messagesViewModel.getTextInSendBox().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sendingTextEt.setText(s);
            }
        });

        messagesViewModel.getIsForCall().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isForCall = aBoolean;
            }
        });

        binding.sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeybaord(view);
                sendMessageFromString(binding.sendingTextEt.getText().toString());
                binding.sendingTextEt.getText().clear();
//                final Message newMessage = new Message(getContext());
//                newMessage.setSenderid(me.getUserid());
//                newMessage.setReceiverid(buddy.getUserid());
//                newMessage.setMessagetext(binding.sendingTextEt.getText().toString());
//                newMessage.setIsToShowTranslatedText(true);
//                if(buddy.getUserlanguage() != me.getUserlanguage()){
//                    translator.translateMessageAsync(newMessage, new NewMessagesConsumer() {
//                        @Override
//                        public void newMessage(long senderUserId, Message message) {
//                            // Not implemented
//                        }
//
//                        @Override
//                        public void sendAndSaveThisMessage(final Message message) {
//                            shakkelha(message);
//                        }
//                    });
//                }
//                else{
//                    newMessage.setMessagetranslatedtext(newMessage.getMessagetext());
//                    shakkelha(newMessage);
//                }
            }
        });

        getDatasetFromDB();

        pinger = ((AsyncPingerProvider)getActivity()).getCurrentPinger();

        binding.continuousRecognitionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                asr.setContinuousRecognition(b);
            }
        });
        if(isForCall){
//            binding.continousRecognitionContainer.setVisibility(View.VISIBLE);
        }
        isHighContrastEnabled = Utils.getIsHighContrastTheme(getContext());
        textSizeOfMessageET = getResources().getDimension(R.dimen.message_fragment_message_et_box_text_size);binding.sendingTextEt.getTextSize();
        refresh(isHighContrastEnabled);
        Utils.registerHighContrastObserver(this);


        if(isViewForGroupChat){
            binding.headerMyMsgImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TranslationMainActivity)getActivity()).openGroupCreationOrUpdateDialog(chatGroup);
                }
            });
        }
    }

    private void clearUnreadFlags() {
        if(isViewForGroupChat){
            final long id = chatGroup.getGroupid();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() == null) {
                        AppDatabase.callInActivityOnCreate(getContext());
                    }
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                        AppDatabase.getUnreadReceivedMessageNotificationDao().deleteGroupUnreadNotifications(id);
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                        AppDatabase.getUnreadReceivedMessageNotificationDao().getAll();
                    if (pinger != null)
                        pinger.notifyUnreadItemsDatabaseUpdated();
                    if (Utils.getGlobalPinger() != null)
                        Utils.getGlobalPinger().notifyUnreadItemsDatabaseUpdated();
                }
            }).start();
        }
        else {
            final long id = buddy.getID();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() == null) {
                        AppDatabase.callInActivityOnCreate(getContext());
                    }
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                        AppDatabase.getUnreadReceivedMessageNotificationDao().deleteSenderUnreadNotifications(id);
                    if (AppDatabase.getUnreadReceivedMessageNotificationDao() != null)
                        AppDatabase.getUnreadReceivedMessageNotificationDao().getAll();
                    if (pinger != null)
                        pinger.notifyUnreadItemsDatabaseUpdated();
                    if (Utils.getGlobalPinger() != null)
                        Utils.getGlobalPinger().notifyUnreadItemsDatabaseUpdated();
                }
            }).start();
        }
    }

    @Override
    public void onDestroyView() {

//        ((AsyncPingerProvider)getActivity()).getCurrentPinger().releaseStatus();
        super.onDestroyView();
        if(isForCall && !inhibiteSendingTerminateMessageOnDestroyCallback.get()) {
            pinger.sendTerminateMessage();
            pinger.setFreeStatus();
        }
        messagesViewModel.getTextInSendBox().setValue(binding.sendingTextEt.getText().toString());
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());
            if (adapter != null) {
                adapter.release();
                messagesViewModel.getMutableLiveData().setValue(adapter.getDataset());
            }
        }
        catch (Exception e){e.printStackTrace();}
        binding = null;
        AsyncPinger currentPinger = ((AsyncPingerProvider)getActivity()).getCurrentPinger();
        if(currentPinger != null)
            currentPinger.setNormalRate();
        if(asr != null)
            asr.release();
        if(tts != null)
            tts.release();
        if(translatedTTS != null)
            translatedTTS.release();
        clearUnreadFlags();
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}
        try{asrThread.interrupt();}catch(Exception e){e.printStackTrace();}

    }

    @Override
    public void onResume() {
        super.onResume();
//        ((FabSource)getActivity()).refreshFab();
        if(isViewForGroupChat){
            if (chatGroup != null) {
                ((Activity) getActivity()).setTitle(chatGroup.getName());
            }
        }
        else {
            if (buddy != null) {
                ((Activity) getActivity()).setTitle(buddy.getUsername());
            }
        }
        ((FabSource)getActivity()).disableFab();


        // This is to hide nav bar
        ((TranslationMainActivity)getActivity()).resetUIStateDelayed();

        if(binding != null){
            if(binding.fragmentRecyclerView!= null){
                if(binding.fragmentRecyclerView.getAdapter() == null){
                    getDatasetFromDB();
                }
            }
        }

    }



    @Override
    public void onPause() {
        super.onPause();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());
            if (adapter != null) {
//                adapter.release();
                messagesViewModel.getMutableLiveData().setValue(adapter.getDataset());
            }
        }
        catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void speak(Message message) {
        say(message, message.getIsToShowTranslatedText());
    }

    @Override
    public boolean isForCall() {
        return isForCall;
    }

    @Override
    public void pleaseMessageFragmentDontSendTerminateMessageAtOnDestroyCallbackBecauseIWillDoInAsyncPinger() {
        inhibiteSendingTerminateMessageOnDestroyCallback.set(true);
    }

    private boolean isHighContrastEnabled;
    @Override
    public void refresh(boolean isHighContrast) {
        isHighContrastEnabled = isHighContrast;
        if(binding == null || binding.messageFragmentRootContainer == null || binding.sendingTextEt == null
        || binding.sendBut == null || binding.recognizeVoiceBut == null)
            return;
        if (isHighContrast) {
            binding.messageFragmentRootContainer.setBackgroundColor(getResources().getColor(R.color.high_contrast_background_color));
            binding.sendingTextEt.setBackgroundResource(R.drawable.buddy_message_unpressable_background1_high_contrast);
            binding.sendingTextEt.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext()) * textSizeOfMessageET);
            binding.sendingTextEt.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            binding.sendBut.setBackgroundResource(R.drawable.round_button_light_high_contrast);
            binding.sendBut.setImageResource(Utils.selectAccordingToLightOrDark(getContext(),R.drawable.ic_baseline_send_50,R.drawable.ic_baseline_send_50,R.drawable.ic_baseline_send_50,R.drawable.ic_baseline_send_50_black));
            binding.recognizeVoiceBut.setImageResource(Utils.selectAccordingToLightOrDark(getContext(),R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50,R.drawable.ic_baseline_mic_50_black));
            binding.recognizeVoiceBut.setBackgroundResource(R.drawable.round_button_light_high_contrast);

            binding.messageFragmentBuddyHeaderContainer.setBackgroundResource(R.drawable.buddy_message_unpressable_background2_high_contrast);
            binding.msgListHeaderNameBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext())*getResources().getDimension(R.dimen.message_fragment_header_buddy_name_text_size));

            binding.msgListHeaderLngBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext())*getResources().getDimension(R.dimen.message_fragment_header_buddy_language_text_size));

            binding.msgListHeaderNameBox.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            binding.msgListHeaderLngBox.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
        }
        else{
            binding.messageFragmentRootContainer.setBackgroundResource(R.drawable.chat_background_tiles);
            binding.sendingTextEt.setBackgroundResource(R.drawable.textboxbackground);
            binding.sendingTextEt.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeOfMessageET);
            binding.sendingTextEt.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            binding.sendBut.setBackgroundResource(R.drawable.round_button_light);
            binding.recognizeVoiceBut.setBackgroundResource(R.drawable.round_button_light);

            binding.messageFragmentBuddyHeaderContainer.setBackgroundResource(R.drawable.my_user_header_background);
            binding.msgListHeaderNameBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.message_fragment_header_buddy_name_text_size));

            binding.msgListHeaderLngBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.message_fragment_header_buddy_language_text_size));
            binding.msgListHeaderNameBox.setTextColor(getResources().getColor(R.color.white));
            binding.msgListHeaderLngBox.setTextColor(getResources().getColor(R.color.white));
        }

    }
}