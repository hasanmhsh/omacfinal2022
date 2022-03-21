package hasan.mohamed.shehata.myapplication.ui.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.UsersViewType;

public class UsersViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<User> meUser;
    private MutableLiveData<List<ListItemBindableItemContentProvider>> userList;
    private MutableLiveData<List<ListItemBindableItemContentProvider>> groupList;






    public UsersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(null);
        userList = new MutableLiveData<List<ListItemBindableItemContentProvider>>();
        userList.setValue(null);
        groupList = new MutableLiveData<List<ListItemBindableItemContentProvider>>();
        groupList.setValue(null);
        meUser = new MutableLiveData<>();
        meUser.setValue(null);



    }

    public MutableLiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<List<ListItemBindableItemContentProvider>> getUserListLiveData() {
        return userList;
    }

    public MutableLiveData<List<ListItemBindableItemContentProvider>> getGroupList() {
        return groupList;
    }

    public MutableLiveData<User> getMeUser() {
        return meUser;
    }
}