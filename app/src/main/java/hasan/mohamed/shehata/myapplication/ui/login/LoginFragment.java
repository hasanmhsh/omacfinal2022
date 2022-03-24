package hasan.mohamed.shehata.myapplication.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.FragmentLoginBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.CountryPhoneCode;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.SMS;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.JSONResult;
import hasan.mohamed.shehata.myapplication.types.LoginResult;
import hasan.mohamed.shehata.myapplication.types.NavigationProvider;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.StartedACtivityResultsProvider;
import hasan.mohamed.shehata.myapplication.types.StartedActivityResultsListener;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements BindableItem, StartedActivityResultsListener {
    public static final int REQUEST_CODE_CAPTURE_CAMERA_PHOTO = 12;
    public static final int REQUEST_CODE_PICK_GALLERY_PHOTO = 32;
    public static final int REQUEST_CODE_PICK_ALL1 = 456;
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private boolean isLoginView = false;
    private Language selectedLanguage;
    private Closeable progressWindow;
    private LifecycleOwner lifeCycleOwner;
    private boolean isSignup = true;
    private Bitmap pickedPhoto;
    private File pickedPhotoFile;
    private String pickedPhotoPath;
    private String pickedPhotoContentUri;
    private Fragment thiz;
    private long myid;
    private String verifiedUserPhone;
    private Thread resendCounterThread;


    private boolean isCountrySelected = false;
    private String verificationCode;
    private boolean isVerificationGroupVisible = false;
    private boolean isResendCounterOn = false;
    private boolean isNowPhoneNumberVerificationView = true;
    private AtomicInteger resendCounter = new AtomicInteger(30);
    private boolean isToUpdateCurrentUserOnly = false;

    public static final String ME_PARAM = "ME_PARAM";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        isToUpdateCurrentUserOnly = Utils.getIsLoginForUserInfoUpdate(getContext());

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setUser(new User());
        if(isToUpdateCurrentUserOnly){
            binding.getUser().setUserid(Utils.getUserIdForLoginUpdate(getContext()));
            binding.getUser().setUsername(Utils.getUserNameForLoginUpdate(getContext()));
            binding.getUser().setUserlanguage(Utils.getUserLanguageForLoginUpdate(getContext()));
            isNowPhoneNumberVerificationView = false;

        }
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getIsLogin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean == null){
                    isLoginView = false;
                }
                else {
                    isLoginView = aBoolean;
                }
                isSignup = !isLoginView;
                if(!isSignup){
                    signInButtonPressed();
                }
                else{
                    signUpButtonPressed();
                }
            }
        });
        loginViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.userName.setText(s);
            }
        });
        loginViewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.userEmail.setText(s);
            }
        });
        loginViewModel.getPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.passwordEt.setText(s);
            }
        });
        loginViewModel.getRetypePassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.retypepasswordet.setText(s);
            }
        });
        loginViewModel.getLanguage().observe(getViewLifecycleOwner(), new Observer<Language>() {
            @Override
            public void onChanged(Language language) {
                if(language!=null) {
                    selectedLanguage = language;
                    binding.getUser().setUserlanguage(language);
                    binding.userLanguage.setText(selectedLanguage.getLanguageName());
                }
            }
        });
        binding.selectLanguageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeybaord(view);
                GeneralPopupWindow.makeLanguageSelectionWindow(getContext(),
                        getContext().getResources().getString(R.string.select_language),
                        new ResultReceiver() {
                            @Override
                            public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
                                selectedLanguage = (Language) bindableItemContentProvider;
                                binding.getUser().setUserlanguage((Language) bindableItemContentProvider);
                            }

                            @Override
                            public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {

                            }

                            @Override
                            public void deleteItem(ListItemBindableItemContentProvider item) {

                            }

                            @Override
                            public User getBuddy() {
                                return null;
                            }

                            @Override
                            public Group getGroup() {
                                return null;
                            }

                            @Override
                            public SpeakerProvider provideSpeaker() {
                                return null;
                            }

                            @Override
                            public boolean isReadOnly() {
                                return false;
                            }
                        }
                        ,false);
            }
        });









        binding.submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeybaord(view);
                ((PermissionRequestProvider)getActivity()).requireInternetPermission(new PermissionRequestCallbacks() {
                    @Override
                    public void granted() {
                        submitHandler();
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(getContext(), "Need internet permission!", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

        binding.signupBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpButtonPressed();
            }
        });

        binding.signinBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInButtonPressed();
            }
        });
        binding.selectPhotoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
//        ((StartedACtivityResultsProvider)getActivity()).registerStartedActivityResultsListener(this);
        thiz = this;



//        APIClient.getAPIInterface().downloadPhoto(30).enqueue(new Provider<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if(response.isSuccessful()) {
//                    Bitmap b = BitmapFactory.decodeStream(response.body().byteStream());
//                    Glide
//                            .with(getContext())
//                            .load(b)
//                            .circleCrop()
//                            .placeholder(R.drawable.user)
//                            .into(binding.selectPhotoBut);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });



        loginViewModel.getIsCountrySelected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null)
                    isCountrySelected = aBoolean;
            }
        });

        loginViewModel.getIsNowPhoneNumberVerificationView().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null)
                    isNowPhoneNumberVerificationView = aBoolean;
                if(isNowPhoneNumberVerificationView && !isToUpdateCurrentUserOnly) {
                    phoneVerficationViewInitialization();
                }
                else {
                    binding.loginFragmentPhoneNumberVerificationContainer.setVisibility(View.GONE);
                    binding.loginFragmentPersonalInformationContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        loginViewModel.getIsResendCounterOn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null)
                    isResendCounterOn = aBoolean;
                if (isResendCounterOn && !isToUpdateCurrentUserOnly) {
                    binding.countrySelectionTvPhoneNumberVerificationFragment.setEnabled(false);
                    binding.countryCodeEtPhoneVerificationLoginFragment.setEnabled(false);
                    binding.phonenumberEtPhoneNumberVerificationFragmnet.setEnabled(false);
                    binding.okButPhoneVerificationFragment.setEnabled(false);
                    binding.resendSmsButPhoneVerificationFragment.setEnabled(false);
                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.VISIBLE);
                    binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.VISIBLE);
                    binding.resendSmsButPhoneVerificationFragment.setVisibility(View.VISIBLE);
                    checkResendCounter(false);
                }
            }
        });

        loginViewModel.getIsVerificationGroupVisible().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null)
                    isVerificationGroupVisible = aBoolean;
                if (isVerificationGroupVisible && !isToUpdateCurrentUserOnly) {
                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.VISIBLE);
                    binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.VISIBLE);
                    binding.resendSmsButPhoneVerificationFragment.setVisibility(View.VISIBLE);
                }
            }
        });

        loginViewModel.getResendCounter().observe(getViewLifecycleOwner(), new Observer<AtomicInteger>() {
            @Override
            public void onChanged(AtomicInteger atomicInteger) {
                if(atomicInteger != null)
                    resendCounter = atomicInteger;
                else
                    resendCounter = new AtomicInteger(30);
            }
        });

        loginViewModel.getVerificationCode().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                verificationCode = s;
            }
        });

        loginViewModel.getVerifiedUserPhone().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                verifiedUserPhone = s;
            }
        });

        loginViewModel.getSelectedCountryCodeName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null){
                    if(binding != null && binding.countrySelectionTvPhoneNumberVerificationFragment!=null){
                        binding.countrySelectionTvPhoneNumberVerificationFragment.setText(s);
                    }
                }
            }
        });


        return binding.getRoot();

    }



    private void submitHandler(){
        // Todo : save user on db with binding status
        binding.userAlreadyExistsTV.setVisibility(View.GONE);
        if((binding.userName.getText().toString() == null || binding.userName.getText().toString().length() == 0)&&isSignup){
            binding.userAlreadyExistsTV.setText("Enter valid name!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }
//        if(binding.userEmail.getText().toString() == null || binding.userEmail.getText().toString().length() == 0 || !Utils.validateEmail(binding.userEmail.getText().toString())){
//            binding.userAlreadyExistsTV.setText("Enter valid email!");
//            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
//            return;
//        }
//        if(binding.passwordEt.getText().toString() == null || binding.passwordEt.getText().length() == 0 ){
//            binding.userAlreadyExistsTV.setText("Invalid password!");
//            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
//            return;
//        }
//        if(!binding.passwordEt.getText().toString().equals(binding.retypepasswordet.getText().toString()) && isSignup){
//            binding.userAlreadyExistsTV.setText("Password doesn't match!");
//            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
//            return;
//        }
        if((binding.userLanguage.getText().toString() == null || binding.userLanguage.getText().toString().length() == 0) && isSignup){
            binding.userAlreadyExistsTV.setText("Select your language!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }

        if (isSignup) {
            final User newUser = new User();
            newUser.setUsername(binding.userName.getText().toString());
            newUser.setUseremail(binding.userEmail.getText().toString());
            newUser.setUserphone(verifiedUserPhone);
            newUser.setUserlanguage(Language.valueOf(binding.userLanguage.getText().toString()));
            newUser.setUserstatus(StatusOfServerObject.Saved);
            newUser.setPassword(binding.passwordEt.getText().toString());



//                            long id = AppDatabase.getUserDao().insertUser(newUser);
//                            newUser.setUserid(id);
//                            List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug

            //Todo : save user on api
            Call<User> userCall;
            if(isToUpdateCurrentUserOnly){
                newUser.setUserid(binding.getUser().getUserid());
                userCall = APIClient.getAPIInterface(getContext()).updateExistingUser(binding.getUser().getUserid(),newUser);
            }
            else {
                userCall = APIClient.getAPIInterface(getContext()).createNewUser(newUser);
            }

            closeProgressWindow();
            progressWindow = GeneralPopupWindow.makeProgressWindow(getContext(), "Saving user...",false);


            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, final Response<User> response) {
                    if (response.isSuccessful()) {
                        myid = response.body().getUserid();
                        compressImage(pickedPhotoContentUri);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final User fetchedUser = response.body();
                                fetchedUser.setUserstatus(StatusOfServerObject.Saved);

                                long oldUserId = 0;
                                try {
                                    if (getContext() != null) {
                                        oldUserId = Utils.getUserID(getContext());
                                    }

                                }
                                catch (Exception e){e.printStackTrace();}

                                if(oldUserId != response.body().getUserid()){
                                    AppDatabase.getMessageDao().deleteAll();
                                }

                                final long id = AppDatabase.getUserDao().insertUser(fetchedUser);
//                                        newUser.setUserstatus(StatusOfServerObject.Saved);
//                                        AppDatabase.getUserDao().updateUser(newUser);
//                                        AppDatabase.getUserDao().updateUserIdAndStatus(newUser.getUserid(), abetchedUser.getUserid(),StatusOfServerObject.Saved);
//                                                List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                                        boolean before = Utils.isUserCreated(getContext());
                                        Utils.setUserCreated(getContext());
                                        Utils.setUserID(getContext(), id);
//                                                        boolean after = Utils.isUserCreated(getContext());
//                                                ((NavHeader) getActivity()).setNavHeaderData(fetchedUser.getUsername() + "(" + fetchedUser.getUserlanguage().getLanguageName() + ")", fetchedUser.getUseremail());
                                        ((NavigationProvider)getActivity()).navigateFromLoginToUsers();
                                        Utils.setIsLoginForUserInfoUpdate(getContext(),false);
                                        closeDialog();
                                    }
                                });
//                                        // This code to run queries in UI thread
//                                        Utils.runOnUIThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                LiveData<User> userLiveData = AppDatabase.getUserDao().loadUser(newUser.getUserid());
//                                                userLiveData.observe(getActivity(), new Observer<User>() {
//                                                    @Override
//                                                    public void onChanged(@Nullable User user) {
//
//                                                        user.setUserid(fetchedUser.getUserid());
//                                                        user.setUserstatus(StatusOfServerObject.Saved);
//                                                        AppDatabase.getUserDao().updateUser(user);
//                                                        List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug
//                                                        closeDialog();
//                                                    }
//                                                });
//                                            }
//                                        });
                            }
                        }).start();


                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    call.cancel();

//                                                Toast.makeText(getContext(),"Can't connect!" , Toast.LENGTH_SHORT).show();
                    call.cancel();
                    binding.userAlreadyExistsTV.setText("Can't connect!");
                    binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
                    closeProgressWindow();

//                                    closeDialog();
                }
            });
            // Todo : save user on db with Saved status if saving on api done successfully

            // Todo : Show operation result in toast

//                            Utils.runOnUIThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    close();
//                                }
//                            });


        }
        else{
            //Login
            final User newUser = new User();
            newUser.setUseremail(binding.userEmail.getText().toString());
            newUser.setPassword(binding.passwordEt.getText().toString());
            closeProgressWindow();
            progressWindow = GeneralPopupWindow.makeProgressWindow(getContext(), "Login...",false);
            APIClient.getAPIInterface(getContext()).login(newUser).enqueue(new Callback<LoginResult>() {
                @Override
                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                    if(response.isSuccessful()){
                        if(response.body().isSuccess()){
                            closeProgressWindow();
                            final User fetchedUser  = response.body().getUser();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long oldUserId = 0;
                                    try {
                                        if (getContext() != null) {
                                            oldUserId = Utils.getUserID(getContext());
                                        }

                                    }
                                    catch (Exception e){e.printStackTrace();}

                                    if(oldUserId != fetchedUser.getUserid()){
                                        AppDatabase.getMessageDao().deleteAll();
                                    }
                                    final long id = AppDatabase.getUserDao().insertUser(fetchedUser);
                                    Utils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                                        boolean before = Utils.isUserCreated(getContext());
                                            if(id > 0) {
                                                Utils.setUserCreated(getContext());
                                                Utils.setUserID(getContext(), id);
//                                                        boolean after = Utils.isUserCreated(getContext());
                                                ((NavigationProvider)getActivity()).navigateFromLoginToUsers();
                                                closeDialog();
                                            }
                                        }
                                    });
                                }
                            }).start();
                        }
                        else{
                            binding.userAlreadyExistsTV.setText("Wrong email or password!");
                            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
                            closeProgressWindow();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResult> call, Throwable t) {
                    //                                                Toast.makeText(getContext(),"Can't connect!" , Toast.LENGTH_SHORT).show();
                    call.cancel();
                    binding.userAlreadyExistsTV.setText("Can't connect!");
                    binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
                    closeProgressWindow();
                }
            });
        }
    }


    private void phoneVerficationViewInitialization() {
        final CountryPhoneCode [] countryPhoneCodes = Utils.getCountryPhoneCodes(getContext());
        String [] countryNames = new String[countryPhoneCodes.length];
        for(int i = 0 ; i < countryPhoneCodes.length ; i++){
            countryNames[i] = countryPhoneCodes[i].getName();
        }

        binding.verificationnumberEtPhoneNumberVerificationFragmnet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isVerificationGroupVisible && verificationCode != null && verificationCode.length() == 6 && binding.verificationnumberEtPhoneNumberVerificationFragmnet.getText().toString().equals(verificationCode)){
                    Utils.hideKeybaord(binding.loginFragmentPhoneNumberVerificationContainer);
                    binding.getUser().setUserphone(verifiedUserPhone);
//                    Toast.makeText(getContext(), verifiedUserPhone, Toast.LENGTH_SHORT).show();
//                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setEnabled(false);
                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setText("OK");
                    checkIfVerifiedPhoneNumberHasARegisteredUser();
                }
            }
        });

        binding.phonenumberEtPhoneNumberVerificationFragmnet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isVerificationGroupVisible){
                    isVerificationGroupVisible = false;
                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.GONE);
                    binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.GONE);
                    binding.resendSmsButPhoneVerificationFragment.setEnabled(true);
                    binding.resendSmsButPhoneVerificationFragment.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.countryCodeEtPhoneVerificationLoginFragment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isVerificationGroupVisible){
                    isVerificationGroupVisible = false;
                    binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.GONE);
                    binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.GONE);
                    binding.resendSmsButPhoneVerificationFragment.setEnabled(true);
                    binding.resendSmsButPhoneVerificationFragment.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.countrySelectionTvPhoneNumberVerificationFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeybaord(view);
                GeneralPopupWindow.makeSelectionWindow(getContext()
                        , "Select"
                        , new ArrayList<ListItemBindableItemContentProvider>(Arrays.asList(countryPhoneCodes))
                        , new ResultReceiver() {
                            @Override
                            public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
                                if(binding != null && binding.countrySelectionTvPhoneNumberVerificationFragment != null && binding.countryCodeEtPhoneVerificationLoginFragment != null) {
                                    isCountrySelected = true;
                                    binding.countrySelectionTvPhoneNumberVerificationFragment.setText(((CountryPhoneCode) bindableItemContentProvider).getName() + "...▼");
                                    binding.countryCodeEtPhoneVerificationLoginFragment.setText((((CountryPhoneCode) bindableItemContentProvider).getPhoneCode()).replace("+", ""));
                                }
                            }

                            @Override
                            public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {

                            }

                            @Override
                            public void deleteItem(ListItemBindableItemContentProvider item) {

                            }

                            @Override
                            public User getBuddy() {
                                return null;
                            }

                            @Override
                            public Group getGroup() {
                                return null;
                            }

                            @Override
                            public SpeakerProvider provideSpeaker() {
                                return null;
                            }

                            @Override
                            public boolean isReadOnly() {
                                return false;
                            }
                        }
                        ,true,false, false);
            }
        });

        View.OnClickListener sendVerCodeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.verificationnumberEtPhoneNumberVerificationFragmnet.setText("");
                binding.countrySelectionTvPhoneNumberVerificationFragment.setEnabled(true);
                binding.countryCodeEtPhoneVerificationLoginFragment.setEnabled(true);
                binding.phonenumberEtPhoneNumberVerificationFragmnet.setEnabled(true);
                binding.okButPhoneVerificationFragment.setEnabled(true);
                binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.GONE);
                binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.GONE);
                binding.resendSmsButPhoneVerificationFragment.setEnabled(true);
                binding.resendSmsButPhoneVerificationFragment.setVisibility(View.GONE);
                isVerificationGroupVisible = true;
                Utils.hideKeybaord(view);
                if(binding.phonenumberEtPhoneNumberVerificationFragmnet.getText().length() < 7){
                    Toast.makeText(getContext(),"Phone number is invalid!",Toast.LENGTH_LONG).show();
                    return;
                }
//                if(!isCountrySelected) {
//                    Toast.makeText(getContext(), "Select country!", Toast.LENGTH_LONG).show();
//                    return;
//                }
                verifiedUserPhone = "00" + binding.countryCodeEtPhoneVerificationLoginFragment.getText().toString()
                + binding.phonenumberEtPhoneNumberVerificationFragmnet.getText();

                final SMS sms = new SMS();
                sms.setDestinationnumber(verifiedUserPhone);
                int randomNumber = 100000 + new Random().nextInt(900000);
                verificationCode = String.valueOf(randomNumber).substring(0,6);
                Toast.makeText(getActivity(),verificationCode, Toast.LENGTH_LONG).show();
                sms.setMessage("Verification code \n " +  verificationCode);
                checkResendCounter(true);//debug

                APIClient.getAPIInterface(getContext()).sendSms(sms).enqueue(new Callback<JSONResult>() {
                    @Override
                    public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
                        if(response.isSuccessful()){

                            checkResendCounter(true);

//                            Toast.makeText(getContext() , verifiedUserPhone , Toast.LENGTH_LONG).show();
//
//                            binding.loginFragmentPersonalInformationContainer.setVisibility(View.VISIBLE);
//                            binding.loginFragmentPhoneNumberVerificationContainer.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(getContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResult> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(getContext(), "Verification failed!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        };

        binding.okButPhoneVerificationFragment.setOnClickListener(sendVerCodeListener);
        binding.resendSmsButPhoneVerificationFragment.setOnClickListener(sendVerCodeListener);
   }

    private void checkIfVerifiedPhoneNumberHasARegisteredUser() {

        User user = new User();
        user.setUserphone(verifiedUserPhone);
        APIClient.getAPIInterface(getContext()).getUserByPhoneNumber(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    final User fetchedUser = response.body();
                    if(fetchedUser!=null && fetchedUser.getUserid()!=0){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                long oldUserId = 0;
                                try {
                                    if (getContext() != null) {
                                        oldUserId = Utils.getUserID(getContext());
                                    }

                                }
                                catch (Exception e){e.printStackTrace();}

                                if(oldUserId != response.body().getUserid()){
                                    AppDatabase.getMessageDao().deleteAll();
                                }


                                fetchedUser.setUserstatus(StatusOfServerObject.Saved);
                                final long id = AppDatabase.getUserDao().insertUser(fetchedUser);
//                                        newUser.setUserstatus(StatusOfServerObject.Saved);
//                                        AppDatabase.getUserDao().updateUser(newUser);
//                                        AppDatabase.getUserDao().updateUserIdAndStatus(newUser.getUserid(), abetchedUser.getUserid(),StatusOfServerObject.Saved);
//                                                List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                                        boolean before = Utils.isUserCreated(getContext());
                                        Utils.setUserCreated(getContext());
                                        Utils.setUserID(getContext(), id);
//                                                        boolean after = Utils.isUserCreated(getContext());
//                                                ((NavHeader) getActivity()).setNavHeaderData(fetchedUser.getUsername() + "(" + fetchedUser.getUserlanguage().getLanguageName() + ")", fetchedUser.getUseremail());
                                        ((NavigationProvider)getActivity()).navigateFromLoginToUsers();
                                        closeDialog();
                                    }
                                });
//                                        // This code to run queries in UI thread
//                                        Utils.runOnUIThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                LiveData<User> userLiveData = AppDatabase.getUserDao().loadUser(newUser.getUserid());
//                                                userLiveData.observe(getActivity(), new Observer<User>() {
//                                                    @Override
//                                                    public void onChanged(@Nullable User user) {
//
//                                                        user.setUserid(fetchedUser.getUserid());
//                                                        user.setUserstatus(StatusOfServerObject.Saved);
//                                                        AppDatabase.getUserDao().updateUser(user);
//                                                        List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug
//                                                        closeDialog();
//                                                    }
//                                                });
//                                            }
//                                        });
                            }
                        }).start();
                    }
                    else{
                        isNowPhoneNumberVerificationView = false;
                        if(binding != null && binding.loginFragmentPersonalInformationContainer != null && binding.loginFragmentPhoneNumberVerificationContainer!=null){
                            binding.loginFragmentPersonalInformationContainer.setVisibility(View.VISIBLE);
                            binding.loginFragmentPhoneNumberVerificationContainer.setVisibility(View.GONE);
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
                Toast.makeText(getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkResendCounter(boolean isToStartResendCounterOrCheckToMaintainFragmentRestart) {
        if ((isToStartResendCounterOrCheckToMaintainFragmentRestart ||
                (!isToStartResendCounterOrCheckToMaintainFragmentRestart && isResendCounterOn))
        ) {
            if(isToStartResendCounterOrCheckToMaintainFragmentRestart) {
                isResendCounterOn = true;
                if(resendCounter == null)
                    resendCounter = new AtomicInteger();
                resendCounter.set(30);
            }
            binding.countrySelectionTvPhoneNumberVerificationFragment.setEnabled(false);
            binding.countryCodeEtPhoneVerificationLoginFragment.setEnabled(false);
            binding.phonenumberEtPhoneNumberVerificationFragmnet.setEnabled(false);
            binding.okButPhoneVerificationFragment.setEnabled(false);
            binding.verificationnumberEtPhoneNumberVerificationFragmnet.setVisibility(View.VISIBLE);
            binding.verificationNumberLabelTvVerificationFragment.setVisibility(View.VISIBLE);
            binding.resendSmsButPhoneVerificationFragment.setEnabled(false);
            binding.resendSmsButPhoneVerificationFragment.setVisibility(View.VISIBLE);
            isResendCounterOn = true;
            if(resendCounterThread == null || !resendCounterThread.isAlive()) {
                resendCounterThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (resendCounter != null && resendCounter.get() > 0) {
                            resendCounter.getAndDecrement();
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                            }
                            try {

                                binding.resendSmsButPhoneVerificationFragment.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (binding != null && binding.resendSmsButPhoneVerificationFragment != null) {
                                            binding.resendSmsButPhoneVerificationFragment.setText("Resend (" +
                                                    String.valueOf(resendCounter.get())
                                                    + ")");
                                        }
                                    }
                                });

                            } catch (Exception e) {
                            } finally {
                                if(resendCounter != null && resendCounter.get() <= 0)
                                    isResendCounterOn = false;
                            }

                        }

                        try {
                            if (binding != null &&
                                    binding.countryCodeEtPhoneVerificationLoginFragment != null &&
                                    binding.countrySelectionTvPhoneNumberVerificationFragment != null &&
                                    binding.phonenumberEtPhoneNumberVerificationFragmnet != null &&
                                    binding.okButPhoneVerificationFragment != null &&
                                    binding.resendSmsButPhoneVerificationFragment != null
                            ) {
                                binding.countrySelectionTvPhoneNumberVerificationFragment.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.countryCodeEtPhoneVerificationLoginFragment.setEnabled(true);
                                        binding.countrySelectionTvPhoneNumberVerificationFragment.setEnabled(true);
                                        binding.phonenumberEtPhoneNumberVerificationFragmnet.setEnabled(true);
                                        binding.okButPhoneVerificationFragment.setEnabled(true);
                                        binding.resendSmsButPhoneVerificationFragment.setEnabled(true);
                                        binding.resendSmsButPhoneVerificationFragment.setText("Resend");
                                    }
                                });
                            }
                        } catch (Exception e) {
                        }
                    }
                });
                resendCounterThread.start();
            }
        }
        else {

                binding.countrySelectionTvPhoneNumberVerificationFragment.post(new Runnable() {
                    @Override
                    public void run() {
                        if (binding != null &&
                                binding.countryCodeEtPhoneVerificationLoginFragment != null &&
                                binding.countrySelectionTvPhoneNumberVerificationFragment != null &&
                                binding.phonenumberEtPhoneNumberVerificationFragmnet != null &&
                                binding.okButPhoneVerificationFragment != null &&
                                binding.resendSmsButPhoneVerificationFragment != null
                        ) {
                            binding.countryCodeEtPhoneVerificationLoginFragment.setEnabled(true);
                            binding.countrySelectionTvPhoneNumberVerificationFragment.setEnabled(true);
                            binding.phonenumberEtPhoneNumberVerificationFragmnet.setEnabled(true);
                            binding.okButPhoneVerificationFragment.setEnabled(true);
                            binding.resendSmsButPhoneVerificationFragment.setEnabled(true);
                            binding.resendSmsButPhoneVerificationFragment.setText("Resend");
                        }
                    }
                });

        }
    }

    private void signUpButtonPressed(){
        isSignup = true;
        isLoginView = false;
//        binding.signupBut.setBackgroundResource(R.drawable.signin_signup_button_background_pressed);
//        binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.light_blue_600));
//        binding.signinBut.setBackgroundResource(R.drawable.signin_signup_button_background_released);
//        binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.selectPhotoBut.setVisibility(View.VISIBLE);
        binding.userAlreadyExistsTV.setVisibility(View.GONE);
        binding.userName.setVisibility(View.VISIBLE);
//        binding.passwordEt.setVisibility(View.VISIBLE);
//        binding.retypepasswordet.setVisibility(View.VISIBLE);
        binding.userLanguage.setVisibility(View.VISIBLE);
        binding.selectLanguageBut.setVisibility(View.VISIBLE);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.black));
//                    binding.signupBut.setBackgroundResource(R.drawable.elegant_button_style1_pressed_night);
                break;
            }

            case Configuration.UI_MODE_NIGHT_NO: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.white));
//                    binding.signupBut.setBackgroundResource(R.drawable.elegant_button_style1_pressed_light);
                break;
            }

            case Configuration.UI_MODE_NIGHT_UNDEFINED: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.purple_200));
                break;
            }
        }
    }

    private void signUpButtonReleased(){
//            isSignup = true;
//            binding.userAlreadyExistsTV.setVisibility(View.GONE);
//            binding.userName.setVisibility(View.VISIBLE);
//            binding.passwordEt.setVisibility(View.VISIBLE);
//            binding.retypepasswordet.setVisibility(View.VISIBLE);
//            binding.userLanguage.setVisibility(View.VISIBLE);
//            binding.selectLanguageBut.setVisibility(View.VISIBLE);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.white));
//                    binding.signupBut.setBackgroundResource(R.drawable.elegant_button_style1_released_night);
                break;
            }

            case Configuration.UI_MODE_NIGHT_NO: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.purple_700));
//                    binding.signupBut.setBackgroundResource(R.drawable.elegant_button_style1_released_light);
                break;
            }

            case Configuration.UI_MODE_NIGHT_UNDEFINED: {
//                    binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.purple_200));
                break;
            }
        }
    }

    private void signInButtonPressed(){
        isSignup = false;
        isLoginView = true;
//        binding.signinBut.setBackgroundResource(R.drawable.signin_signup_button_background_pressed);
//        binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.light_blue_600));
//        binding.signupBut.setBackgroundResource(R.drawable.signin_signup_button_background_released);
//        binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.selectPhotoBut.setVisibility(View.GONE);
        binding.userAlreadyExistsTV.setVisibility(View.GONE);
        binding.userName.setVisibility(View.GONE);
//        binding.passwordEt.setVisibility(View.VISIBLE);
        binding.retypepasswordet.setVisibility(View.GONE);
        binding.userLanguage.setVisibility(View.GONE);
        binding.selectLanguageBut.setVisibility(View.GONE);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.black));
//                    binding.signinBut.setBackgroundResource(R.drawable.elegant_button_style1_pressed_night);
                break;
            }

            case Configuration.UI_MODE_NIGHT_NO: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.white));
//                    binding.signinBut.setBackgroundResource(R.drawable.elegant_button_style1_pressed_light);
                break;
            }

            case Configuration.UI_MODE_NIGHT_UNDEFINED: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.purple_200));
                break;
            }
        }
    }

    private void signInButtonReleased(){
//            isSignup = true;
//            binding.userAlreadyExistsTV.setVisibility(View.GONE);
//            binding.userName.setVisibility(View.VISIBLE);
//            binding.passwordEt.setVisibility(View.VISIBLE);
//            binding.retypepasswordet.setVisibility(View.VISIBLE);
//            binding.userLanguage.setVisibility(View.VISIBLE);
//            binding.selectLanguageBut.setVisibility(View.VISIBLE);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.white));
//                    binding.signinBut.setBackgroundResource(R.drawable.elegant_button_style1_released_night);
                break;
            }

            case Configuration.UI_MODE_NIGHT_NO: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.purple_700));
//                    binding.signinBut.setBackgroundResource(R.drawable.elegant_button_style1_released_light);
                break;
            }

            case Configuration.UI_MODE_NIGHT_UNDEFINED: {
//                    binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.purple_200));
                break;
            }
        }
    }

    public void closeDialog() {
        closeProgressWindow();
//        Toast.makeText(getActivity(),"Exited",Toast.LENGTH_SHORT).show();
    }

    private void closeProgressWindow() {
        if(progressWindow != null){
            try{
                progressWindow.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getActivity() != null && getActivity().getWindow() != null) {
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        // This is to hide nav bar
        ((TranslationMainActivity)getActivity()).resetUIStateDelayed();
    }

    @Override
    public void onPause() {
        super.onPause();
        loginViewModel.getIsLogin().setValue(isLoginView);
        loginViewModel.getUserName().setValue(binding.userName.getText().toString());
        loginViewModel.getEmail().setValue(binding.userEmail.getText().toString());
        loginViewModel.getPassword().setValue(binding.passwordEt.getText().toString());
        loginViewModel.getRetypePassword().setValue(binding.retypepasswordet.getText().toString());
        loginViewModel.getLanguage().setValue(binding.getUser().getUserlanguage());

        loginViewModel.getVerifiedUserPhone().setValue(verifiedUserPhone);
        loginViewModel.getVerificationCode().setValue(verificationCode);
        loginViewModel.getResendCounter().setValue(resendCounter);
        loginViewModel.getIsVerificationGroupVisible().setValue(isVerificationGroupVisible);
        loginViewModel.getIsResendCounterOn().setValue(isResendCounterOn);
        loginViewModel.getIsCountrySelected().setValue(isCountrySelected);
        loginViewModel.getIsNowPhoneNumberVerificationView().setValue(isNowPhoneNumberVerificationView);
        loginViewModel.getSelectedCountryCodeName().setValue(
                binding.countrySelectionTvPhoneNumberVerificationFragment.getText().toString()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            if(resendCounterThread != null){
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
                try{resendCounterThread.interrupt();}catch (Exception e){}
            }
        }
        catch (Exception e){}
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }

    @Override
    public void bind(DownloadWindowContent downloadWindowContent) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }

    @Override
    public void bind(User user) {
        if(binding != null)
            binding.setUser(user);
    }

    @Override
    public void bind(Group group) {

    }

    @Override
    public void bind(Message msg) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }

    @Override
    public void close() {
        closeDialog();
    }


    private void selectPhoto1(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent,takePicture});

        startActivityForResult(chooserIntent, REQUEST_CODE_PICK_ALL1);
    }

    private void selectPhoto2() {
        ((PermissionRequestProvider)getActivity()).requireStoragePermissions(new PermissionRequestCallbacks() {
            @Override
            public void granted() {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose your profile picture");

                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo")) {
                            ((PermissionRequestProvider)getActivity()).requireCameraPermission(new PermissionRequestCallbacks() {
                                @Override
                                public void granted() {
                                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    getActivity().startActivityForResult(takePicture, REQUEST_CODE_CAPTURE_CAMERA_PHOTO);
                                }

                                @Override
                                public void denied() {

                                }
                            });

                        } else if (options[item].equals("Choose from Gallery")) {
                            try {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                getActivity().startActivityForResult(pickPhoto, REQUEST_CODE_PICK_GALLERY_PHOTO);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }

            @Override
            public void denied() {

            }
        });

    }

    private void selectPhoto(){

        if(getActivity() != null) {
            ((StartedACtivityResultsProvider)getActivity()).registerStartedActivityResultsListener(this);
            ((PermissionRequestProvider) getActivity()).requireStoragePermissions(new PermissionRequestCallbacks() {
                @Override
                public void granted() {
                    if (getActivity() != null) {
                        ((StartedACtivityResultsProvider) getActivity()).pickImage();
                    }
                }

                @Override
                public void denied() {

                }
            });
        }
    }

    enum LastLoadedPhotoType{
        ContentURI,
        Bitmap,
        None
    };
    private LastLoadedPhotoType lastLoadedPhotoType = LastLoadedPhotoType.None;

    @Override
    public void photoPicked(Bitmap photo) {
        lastLoadedPhotoType = LastLoadedPhotoType.Bitmap;
        if(photo != null) {
            pickedPhoto = photo;
            if (binding != null) {
                if (binding.selectPhotoBut != null && photo != null) {
                    Glide
                            .with(this)
                            .load(photo)
                            .circleCrop()
                            .placeholder(R.drawable.ic_baseline_photo_camera_100)
                            .into(binding.selectPhotoBut);
                }
            }
        }
    }

    @Override
    public void photoPickedContentUri(String uri) {
//        Toast.makeText(getActivity(), "Content uri", Toast.LENGTH_LONG).show();
//        binding.userName.setText(uri);

        lastLoadedPhotoType = LastLoadedPhotoType.ContentURI;
        pickedPhotoContentUri = uri;
        Utils.runOnUIThreadPostDelayed(new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    Glide
                            .with(getContext())
                            .load(uri)
                            .circleCrop()
                            .placeholder(R.drawable.ic_baseline_photo_camera_100)
                            .into(binding.selectPhotoBut);
                }
            }
        });
//        if(getActivity() != null && binding != null && binding.selectPhotoBut!= null) {
//
////        compressImage(uri);   //Worked
//        }
    }

    @Override
    public void photoPickedFilePath(String path) {
//        Toast.makeText(getActivity(), "path", Toast.LENGTH_LONG).show();
//        binding.userEmail.setText(path);
        pickedPhotoPath = path;
//        compressImage(path);  // Didnt work
    }




    private void compressImage(String url){
//        Bitmap b = BitmapFactory.decodeFile("Pass your file path");
// original measurements
        if(url == null || url.length()==0)
            return;
        Uri uri = Uri.parse(url);
//        pickedPhotoPath = uri.getEncodedPath();
//        url=pickedPhotoPath;
        if(url==null){
            return;
        }
        final int destWidth = 130;//or the width you need
        thiz = this;
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);

        }
        catch(Exception e){
            return;
        }
        if(bitmap ==null){
            if(getContext() != null){
                Toast.makeText(getContext(), "Unsupported image format!",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        if(origWidth > destWidth) {
            Bitmap scaledBm = Bitmap.createScaledBitmap(bitmap, destWidth, destWidth * origHeight / origWidth, true);
            if(scaledBm ==null){
                if(getContext() != null){
                    Toast.makeText(getContext(), "Unsupported image format!",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            finalizeCompression(scaledBm);
        }
        else
            finalizeCompression(bitmap);
//        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        int origWidth = resource.getWidth();
//                        int origHeight = resource.getHeight();
//                        if(origWidth > destWidth) {
//
//                            RequestOptions myOptions = new RequestOptions()
//                                    .override(destWidth, destWidth * origHeight / origWidth);//.circleCrop();
//                            Glide.with(thiz.getContext())
//                                    .asBitmap()
////                                    .apply(myOptions)
//                                    .load(resource)
//                                    .override(destWidth, destWidth * origHeight / origWidth)
//                                    .centerCrop()
//                                    .into(new CustomTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(@NonNull Bitmap resource2, @Nullable Transition<? super Bitmap> transition) {
//                                            int origWidth = resource2.getWidth();
//                                            int origHeight = resource2.getHeight();
//                                            finalizeCompression(resource2);
//                                        }
//
//                                        @Override
//                                        public void onLoadCleared(@Nullable Drawable placeholder2) {
//                                        }
//                                    });
//                        }
//                        else{
//                            finalizeCompression(resource);
//                        }
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });

    }

    private void finalizeCompression(Bitmap bitmap){

//        Drawable resizedImage = null;
//        Bitmap resizedBitmap = null;
//        int origWidth = bitmap.getWidth();
//        int origHeight = bitmap.getHeight();
//        final int destWidth = 120;
//        try{
//            resizedImage = Glide
//                    .with(getActivity())
//                    .load(bitmap)
//                    .override(destWidth, destWidth * origHeight / origWidth)
//                    .submit()
//                    .get();
//
//            resizedBitmap = Bitmap.createBitmap(destWidth, destWidth * origHeight / origWidth, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(resizedBitmap);
//            resizedImage.setBounds(0, 0, destWidth, destWidth * origHeight / origWidth);
//            resizedImage.draw(canvas);
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
//        bitmap = resizedBitmap;



        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        if(bitmap.getWidth() != 130){
//            bitmap = Utils.AngleBitmapRotation(90.0D,bitmap);
//        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100 , outStream);
        if(bitmap == null){
            if(getContext() != null){
                Toast.makeText(getContext(), "Unsupported image format!",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        File f = new File(
                getContext().getFilesDir().getPath() // /data/user/0/hasan.mohamed.shehata.myapplication/files/myphoto34532.png
//                Environment.getExternalStorageDirectory() //  /storage/o
                + File.separator + "myphoto34532.png");
        if(f.exists()){
            f.delete();
        }
        try{f.createNewFile();}
        catch (Exception e){
            e.printStackTrace();
        }
        //write the bytes in file
        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            // remember close de FileOutput
            fo.close();
            pickedPhotoFile = f;
            postPhoto(myid);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postPhoto(final long myid){
        if(pickedPhotoFile != null){
            RequestBody fbody = RequestBody.create(pickedPhotoFile, MediaType.parse("image/*"));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", pickedPhotoFile.getName(), fbody);
            APIClient.getAPIInterface(getContext()).uploadPhoto(myid,body).enqueue(new Callback<JSONResult>() {
                @Override
                public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
//                    Toast.makeText(getContext(), response.body().getResult(), Toast.LENGTH_LONG).show();
                    if(!response.isSuccessful()){
                        postPhoto(myid);
                    }
                }

                @Override
                public void onFailure(Call<JSONResult> call, Throwable t) {
                    call.cancel();
                    postPhoto(myid);
//                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isToUpdateCurrentUserOnly){
            APIClient.getAPIInterface(getContext()).downloadPhoto(binding.getUser().getUserId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        if(binding!=null && binding.selectPhotoBut!=null){
                            try {
                                byte[] image = response.body().bytes();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
//                                binding.selectPhotoBut.setImageBitmap(bitmap);
                                RequestOptions requestOptions = new RequestOptions();
                                    requestOptions = requestOptions.circleCrop();
                                Glide
                                        .with(getContext())
                                        .load(bitmap)
                                        .apply(requestOptions)
                                        .into(binding.selectPhotoBut);
                            }
                            catch (IOException e){e.printStackTrace();}
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
            binding.loginFragmentPhoneNumberVerificationContainer.setVisibility(View.GONE);
            binding.loginFragmentPersonalInformationContainer.setVisibility(View.VISIBLE);
            Utils.runOnUIThreadPostDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.getUser().refreshLanguageBindingUi();
                }
            });
        }
    }
}