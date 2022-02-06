package hasan.mohamed.shehata.myapplication.ui.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import hasan.mohamed.shehata.myapplication.models.User;

public class MessageViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<ListItemBindableItemContentProvider>> mutableLiveData;
    private MutableLiveData<User> buddyLiveData;
    private MutableLiveData<User> meLiveData;
    private MutableLiveData<Boolean> isForCall;
    private MutableLiveData<String> textInSendBox;





    public MessageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
        mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(null);
        buddyLiveData = new MutableLiveData<>();
        buddyLiveData.setValue(null);
        meLiveData = new MutableLiveData<>();
        meLiveData.setValue(null);
        isForCall = new MutableLiveData<>();
        isForCall.setValue(null);
        textInSendBox = new MutableLiveData<>();
        textInSendBox.setValue(null);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<List<ListItemBindableItemContentProvider>> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<User> getBuddyLiveData() {
        return buddyLiveData;
    }

    public MutableLiveData<User> getMeLiveData() {
        return meLiveData;
    }

    public MutableLiveData<Boolean> getIsForCall() {
        return isForCall;
    }

    public MutableLiveData<String> getTextInSendBox() {
        return textInSendBox;
    }
}