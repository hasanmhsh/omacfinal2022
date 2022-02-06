package hasan.mohamed.shehata.myapplication.ui.login;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.FragmentLoginBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.JSONResult;
import hasan.mohamed.shehata.myapplication.types.LoginResult;
import hasan.mohamed.shehata.myapplication.types.NavHeader;
import hasan.mohamed.shehata.myapplication.types.NavigationProvider;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.StartedACtivityResultsProvider;
import hasan.mohamed.shehata.myapplication.types.StartedActivityResultsListener;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;
import okhttp3.MediaType;
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
    private boolean isLoginView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setUser(new User());
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
                selectedLanguage = language;
                binding.getUser().setUserlanguage(language);
                if(language!=null)
                    binding.userLanguage.setText(selectedLanguage.getLanguageName());
            }
        });
        binding.selectLanguageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralPopupWindow.makeLanguageSelectionWindow(getContext(),
                        getContext().getResources().getString(R.string.select_language),
                        new ResultReceiver() {
                            @Override
                            public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
                                selectedLanguage = (Language) bindableItemContentProvider;
                                binding.getUser().setUserlanguage((Language) bindableItemContentProvider);
                            }

                            @Override
                            public void deleteItem(ListItemBindableItemContentProvider item) {

                            }

                            @Override
                            public User getBuddy() {
                                return null;
                            }

                            @Override
                            public SpeakerProvider provideSpeaker() {
                                return null;
                            }
                        }
                        ,false);
            }
        });









        binding.submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        ((StartedACtivityResultsProvider)getActivity()).registerStartedActivityResultsListener(this);
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
        if(binding.userEmail.getText().toString() == null || binding.userEmail.getText().toString().length() == 0 || !Utils.validateEmail(binding.userEmail.getText().toString())){
            binding.userAlreadyExistsTV.setText("Enter valid email!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }
        if(binding.passwordEt.getText().toString() == null || binding.passwordEt.getText().length() == 0 ){
            binding.userAlreadyExistsTV.setText("Invalid password!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }
        if(!binding.passwordEt.getText().toString().equals(binding.retypepasswordet.getText().toString()) && isSignup){
            binding.userAlreadyExistsTV.setText("Password doesn't match!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }
        if((binding.userLanguage.getText().toString() == null || binding.userLanguage.getText().toString().length() == 0) && isSignup){
            binding.userAlreadyExistsTV.setText("Select your language!");
            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
            return;
        }

        if (isSignup) {
            final User newUser = new User();
            newUser.setUsername(binding.userName.getText().toString());
            newUser.setUseremail(binding.userEmail.getText().toString());
            newUser.setUserphone(binding.userPhone.getText().toString());
            newUser.setUserlanguage(Language.valueOf(binding.userLanguage.getText().toString()));
            newUser.setUserstatus(StatusOfServerObject.Saved);
            newUser.setPassword(binding.passwordEt.getText().toString());

//                            long id = AppDatabase.getUserDao().insertUser(newUser);
//                            newUser.setUserid(id);
//                            List<User> allUsers = AppDatabase.getUserDao().getAll();  // for debug

            //Todo : save user on api
            Call<User> userCall = APIClient.getAPIInterface(getContext()).createNewUser(newUser);

            closeProgressWindow();
            progressWindow = GeneralPopupWindow.makeProgressWindow(getContext(), "Saving user...",false);


            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, final Response<User> response) {
                    if (response.isSuccessful()) {

                        if(response.body().isExist()){
                            closeProgressWindow();
                            binding.userAlreadyExistsTV.setText("User already exist try login!");
                            binding.userAlreadyExistsTV.setVisibility(View.VISIBLE);
                            return;
                        }
                        myid = response.body().getID();
                        compressImage(pickedPhotoContentUri);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final User fetchedUser = response.body();
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
                        if(response.body().isLoginSuccessfully()){
                            closeProgressWindow();
                            final User fetchedUser  = response.body().getUser();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
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
    private void signUpButtonPressed(){
        isSignup = true;
//        binding.signupBut.setBackgroundResource(R.drawable.signin_signup_button_background_pressed);
//        binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.light_blue_600));
//        binding.signinBut.setBackgroundResource(R.drawable.signin_signup_button_background_released);
//        binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.selectPhotoBut.setVisibility(View.VISIBLE);
        binding.userAlreadyExistsTV.setVisibility(View.GONE);
        binding.userName.setVisibility(View.VISIBLE);
        binding.passwordEt.setVisibility(View.VISIBLE);
        binding.retypepasswordet.setVisibility(View.VISIBLE);
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
//        binding.signinBut.setBackgroundResource(R.drawable.signin_signup_button_background_pressed);
//        binding.signinBut.setTextColor(getContext().getResources().getColor(R.color.light_blue_600));
//        binding.signupBut.setBackgroundResource(R.drawable.signin_signup_button_background_released);
//        binding.signupBut.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.selectPhotoBut.setVisibility(View.GONE);
        binding.userAlreadyExistsTV.setVisibility(View.GONE);
        binding.userName.setVisibility(View.GONE);
        binding.passwordEt.setVisibility(View.VISIBLE);
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
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        ((PermissionRequestProvider)getActivity()).requireStoragePermissions(new PermissionRequestCallbacks() {
            @Override
            public void granted() {
                ((StartedACtivityResultsProvider)getActivity()).pickImage();
            }

            @Override
            public void denied() {

            }
        });
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
        Glide
                .with(this)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_photo_camera_100)
                .into(binding.selectPhotoBut);
//        compressImage(uri);   //Worked
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
        if(url==null){
            return;
        }
        final int destWidth = 300;//or the width you need
        thiz = this;
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int origWidth = resource.getWidth();
                        int origHeight = resource.getHeight();
                        if(origWidth > destWidth) {
                            RequestOptions myOptions = new RequestOptions()
                                    .override(destWidth, destWidth * origHeight / origWidth);//.circleCrop();
                            Glide.with(thiz.getContext())
                                    .asBitmap()
//                                    .apply(myOptions)
                                    .load(resource)
                                    .override(destWidth, destWidth * origHeight / origWidth)
                                    .centerCrop()
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource2, @Nullable Transition<? super Bitmap> transition) {
                                            int origWidth = resource2.getWidth();
                                            int origHeight = resource2.getHeight();
                                            finalizeCompression(resource2);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder2) {
                                        }
                                    });
                        }
                        else{
                            finalizeCompression(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

    }

    private void finalizeCompression(Bitmap bitmap){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100 , outStream);
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
            APIClient.getAPIInterface(getContext()).uploadPhoto(myid,fbody).enqueue(new Callback<JSONResult>() {
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
}