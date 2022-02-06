package hasan.mohamed.shehata.myapplication.types;

import androidx.fragment.app.Fragment;

import java.util.List;

import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.OverloadedPingResult;
import hasan.mohamed.shehata.myapplication.models.User;

public interface UserListConsumer {
    public void getUsersList(List<User> users, Fragment fragment);
}
