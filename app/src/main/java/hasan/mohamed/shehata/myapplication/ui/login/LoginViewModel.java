package hasan.mohamed.shehata.myapplication.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hasan.mohamed.shehata.myapplication.models.Language;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLogin;
    private MutableLiveData<String> userName;
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;
    private MutableLiveData<String> retypePassword;
    private MutableLiveData<Language> language;

    public LoginViewModel() {
        isLogin = new MutableLiveData<>();
        isLogin.setValue(null);

        userName = new MutableLiveData<>();
        userName.setValue(null);

        email = new MutableLiveData<>();
        email.setValue(null);

        retypePassword = new MutableLiveData<>();
        retypePassword.setValue(null);

        password = new MutableLiveData<>();
        password.setValue(null);

        language = new MutableLiveData<>();
        language.setValue(null);
    }

    public MutableLiveData<Boolean> getIsLogin() {
        return isLogin;
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<String> getRetypePassword() {
        return retypePassword;
    }

    public MutableLiveData<Language> getLanguage() {
        return language;
    }
}
