package hasan.mohamed.shehata.myapplication.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.atomic.AtomicInteger;

import hasan.mohamed.shehata.myapplication.models.Language;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLogin;
    private MutableLiveData<String> userName;
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;
    private MutableLiveData<String> retypePassword;
    private MutableLiveData<Language> language;
    private MutableLiveData<String> verifiedUserPhone;


    private MutableLiveData<Boolean> isCountrySelected;
    private MutableLiveData<String> verificationCode;
    private MutableLiveData<Boolean> isVerificationGroupVisible;
    private MutableLiveData<Boolean> isResendCounterOn;
    private MutableLiveData<Boolean> isNowPhoneNumberVerificationView;
    private MutableLiveData<AtomicInteger> resendCounter;

    public LoginViewModel() {
        verifiedUserPhone = new MutableLiveData<>();
        verifiedUserPhone.setValue(null);

        resendCounter = new MutableLiveData<>();
        resendCounter.setValue(null);

        isNowPhoneNumberVerificationView = new MutableLiveData<>();
        isNowPhoneNumberVerificationView.setValue(null);

        isResendCounterOn = new MutableLiveData<>();
        isResendCounterOn.setValue(null);

        isVerificationGroupVisible = new MutableLiveData<>();
        isVerificationGroupVisible.setValue(null);

        verificationCode = new MutableLiveData<>();
        verificationCode.setValue(null);

        isCountrySelected = new MutableLiveData<>();
        isCountrySelected.setValue(null);

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


    public MutableLiveData<Boolean> getIsCountrySelected() {
        return isCountrySelected;
    }

    public MutableLiveData<String> getVerificationCode() {
        return verificationCode;
    }

    public MutableLiveData<Boolean> getIsVerificationGroupVisible() {
        return isVerificationGroupVisible;
    }

    public MutableLiveData<Boolean> getIsResendCounterOn() {
        return isResendCounterOn;
    }

    public MutableLiveData<Boolean> getIsNowPhoneNumberVerificationView() {
        return isNowPhoneNumberVerificationView;
    }

    public MutableLiveData<AtomicInteger> getResendCounter() {
        return resendCounter;
    }

    public MutableLiveData<String> getVerifiedUserPhone() {
        return verifiedUserPhone;
    }
}
