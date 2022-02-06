package hasan.mohamed.shehata.myapplication.ui.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.User;

public class UsersViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<ListItemBindableItemContentProvider>> userList;



    public UsersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(null);
        userList = new MutableLiveData<List<ListItemBindableItemContentProvider>>();
        userList.setValue(null);

    }

    public MutableLiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<List<ListItemBindableItemContentProvider>> getUserListLiveData() {
        return userList;
    }


}