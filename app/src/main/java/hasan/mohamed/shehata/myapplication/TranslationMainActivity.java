package hasan.mohamed.shehata.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.mlsdk.common.MLApplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.InternetAvailabilityChecker;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.databinding.ActivityTranslationMainBinding;
import hasan.mohamed.shehata.myapplication.databinding.FragmentLoginBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.CallDialogCallbacks;
import hasan.mohamed.shehata.myapplication.types.CallDialogProvider;
import hasan.mohamed.shehata.myapplication.types.Callable;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.MessageFragmentProvider;
import hasan.mohamed.shehata.myapplication.types.NavHeader;
import hasan.mohamed.shehata.myapplication.types.NavigationProvider;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.StartedACtivityResultsProvider;
import hasan.mohamed.shehata.myapplication.types.StartedActivityResultsListener;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.ui.calling.CallingFragment;
import hasan.mohamed.shehata.myapplication.ui.login.LoginFragment;
import hasan.mohamed.shehata.myapplication.ui.messages.MessageFragment;
import hasan.mohamed.shehata.myapplication.ui.users.UsersFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslationMainActivity extends AppCompatActivity implements FabSource, NavHeader , AsyncPingerProvider, PermissionRequestProvider, MessageFragmentProvider, NavigationProvider, CallDialogProvider, StartedACtivityResultsProvider {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTranslationMainBinding binding;
    private TextView navtitleTV;
    private TextView navsubtitleTV;
    private AppCompatActivity thiz;
    private AsyncPinger pinger;
    private NavController navController;
    NavHostFragment navHostFragment;
//    private User me;
    private InternetAvailabilityChecker internetAvailabilityChecker;
    private Callable callable;
    private User currentUser;
    private int viewFlags;



    public static interface MeListener{
        public void meReady(User me);
    }

    public User registerMeListener(MeListener meListener){
        return currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thiz = this;
//        binding = ActivityTranslationMainBinding.inflate(getLayoutInflater());
        binding = ActivityTranslationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.appBarTranslationMain.toolbar);
//        binding.appBarTranslationMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_users, R.id.translationFragment2)
//                .setOpenableLayout(drawer)
//                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_translation_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_translation_main);
        MLApplication.getInstance().setAccessToken(Utils.getHMSApiKey());

//        navtitleTV = (TextView)binding.navView.getHeaderView(0).findViewById(R.id.nav_header_title_tv);
//        navsubtitleTV = (TextView)binding.navView.getHeaderView(0).findViewById(R.id.nav_header_sub_title_tv);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        AppDatabase.callInActivityOnCreate(this);
        disableFab();

//        binding.navView.setVisibility(View.GONE);
        binding.drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                continueAfterSplash();
                initInternetConnectionStatusNotifier();

                viewFlags =
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        |View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        ;
//                getWindow().getDecorView().setSystemUiVisibility(viewFlags);
                Window w = getWindow();
//                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            }
        }, 4000);
        if(getActionBar() !=null)
            getActionBar().hide();
        viewFlags =

//                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                         View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                 |View.SYSTEM_UI_FLAG_LOW_PROFILE
//                 |View.SYSTEM_UI_FLAG_FULLSCREEN
                 |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(viewFlags);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
//        final View decorView = getWindow().getDecorView();
//        decorView
//                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
//                {
//
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility)
//                    {
//                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
//                        {
//                            decorView.setSystemUiVisibility(viewFlags);
//                        }
//                    }
//                });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.registerPoster(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        APIClient.getAPIInterface(this).getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                call.cancel();
            }
        });

//        binding.drawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new
//                                           ViewTreeObserver.OnGlobalLayoutListener() {
//                                               @Override
//                                               public void onGlobalLayout() {
//                                                   Rect r = new Rect();
//                                                   binding.drawerLayout.getWindowVisibleDisplayFrame(r);
//                                                   int screenHeight = binding.drawerLayout.getRootView().getHeight();
//                                                   int keypadHeight = screenHeight - r.bottom;
//                                                   if (keypadHeight > screenHeight * 0.15) {
//                                                       Toast.makeText(thiz,"Keyboard is showing",Toast.LENGTH_LONG).show();
//                                                   } else {
////                                                       Toast.makeText(thiz,"keyboard closed",Toast.LENGTH_LONG).show();
//                                                   }
//                                               }
//                                           });
//         Internet availablity checker

    }

    private void initInternetConnectionStatusNotifier(){
        internetAvailabilityChecker  = new InternetAvailabilityChecker();
        callable = new Callable() {
            @Override
            public void call(boolean result) {
                if(result){
                    Snackbar.make(binding.drawerLayout, "Internet available", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
//                    readSectionsFromInternet();
//                    alterNewsContents(null,null);
                }
                else{
                    Snackbar.make(binding.drawerLayout, "Internet unavailable!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };
        internetAvailabilityChecker.registerSeeker(callable);
        this.registerReceiver(internetAvailabilityChecker,new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void continueAfterSplash(){
        requestMyPermissions();
        showUserDialog();
    }


    @Override
    public void setFabAction(Runnable runnable) {
        
    }

    @Override
    public void setFabActionType(FabActionType fabActionType){

    }

    @Override
    public void refreshFab() {

    }

    @Override
    public void disableFab() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.translation_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_translation_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private static final int PERMISSIONS_RESULT_TAG = 1668;
    private static final int REQUIRED_PERMISSIONS_COUNT = 12;
    private void requestMyPermissions() {
        List<String> requiredPermissions = new ArrayList<>();
        if(isPermissionsGranted){
            onPermissionGranted();
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_MEDIA_LOCATION);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.INTERNET);
            }


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.WAKE_LOCK);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.CAMERA);
            }


            if (requiredPermissions != null && requiredPermissions.size() > 0) {
                ActivityCompat.requestPermissions(this,
                        requiredPermissions.toArray(new String[requiredPermissions.size()]),
                        PERMISSIONS_RESULT_TAG);
            } else {
                onPermissionGranted();
            }
        }
    }


    private boolean isRequiringSinglePermission = false;
    private String requiredSinglePermission;
    private PermissionRequestCallbacks permissionRequestCallbacks;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_RESULT_TAG && permissions != null && grantResults != null) {
            StringBuilder result = new StringBuilder();
            int grantedPermissionsCount = 0;
            if (isRequiringSinglePermission) {
                boolean isSinglePermissionGranted = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED && permissions[i].equals(requiredSinglePermission)) {
                        isSinglePermissionGranted = true;
                        permissionRequestCallbacks.granted();
                        break;
                    }
                }
                if(!isSinglePermissionGranted)
                    permissionRequestCallbacks.denied();
            } else {

                boolean isAllPermissionsGranted = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        result.append(permissions[i] + " Granted \n");
                        grantedPermissionsCount++;
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        result.append(permissions[i] + " Denied \n");
                        isAllPermissionsGranted = false;
                    }

                }
                if(permissionRequestCallbacks != null) {
                    if (isAllPermissionsGranted) {
                        permissionRequestCallbacks.granted();
                    }
                    else{
                        permissionRequestCallbacks.denied();
                    }

                }

                if (grantedPermissionsCount == REQUIRED_PERMISSIONS_COUNT) {
                    onPermissionGranted();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    boolean isPermissionsGranted = false;
    private void onPermissionGranted() {
        isPermissionsGranted = true;
        //TODO Start your work here
    }

    @Override
    protected void onDestroy() {
        if(pinger!=null)
            pinger.dispose();
        AppDatabase.callInActivityOnDistroy();
        Utils.setBackgroundThreadFlag(getApplicationContext(),false);
        Utils.dispose(this);
        super.onDestroy();
    }

    @Override
    public void setNavHeaderData(String title, String subtitle) {
//        navtitleTV.setText(title);
//        navsubtitleTV.setText(subtitle);
    }




    public void showUserDialog(){
        if(!Utils.isUserCreated(this)) {
//            GeneralPopupWindow.makeUserCreationWindow(this, "", false);
            navigateFromSplashToLogin();
        }
        else{
            long id = Utils.getUserID(this);
            AppDatabase.getUserDao().loadUser(id).observe(thiz, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    currentUser = user;
                    createPingerIfNotCreated();
                    setNavHeaderData(user.getUsername()+"("+user.getUserlanguage().getLanguageName()+")", user.getUseremail());
                    navigateFromSplashToUsers();
                }
            });
        }
    }






    @Override
    public AsyncPinger getCurrentPinger() {
        return pinger;
    }

    @Override
    public void createPingerIfNotCreated() {
        if(Utils.isUserCreated(this)) {
            if (pinger == null) {
                Utils.setBackgroundThreadFlag(thiz, true);
                Utils.setGlobalPinger(pinger);
                pinger = new AsyncPinger(this);
            }
            pinger.checkThreadHealth();
        }
        if(pinger!=null) {
            for (UserListConsumer consumer : postUserListConsumers) {
                if (consumer != null) {
                    pinger.addUsersConsumer(consumer);
                }
            }
            postUserListConsumers.clear();
        }
    }

    private ArrayList<UserListConsumer> postUserListConsumers = new ArrayList<>();
    @Override
    public void registerUserConsumerAfterCreatingPinger(UserListConsumer userListConsumer) {
        if(pinger != null){
            pinger.addUsersConsumer(userListConsumer);
        }
        else {
            postUserListConsumers.add(userListConsumer);
        }
    }

    @Override
    public MessageFragment provideMessageFragment(User buddy, boolean isForCall) {
//        MessageFragment messageFragment = new MessageFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_container, messageFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//        return messageFragment;


        // ىييرتؤىءتؤىءبنرىؤىرﻻىءىرﻻن ىءورؤىﻻيتنسقافعهثص5اتﻻبنتلﻻينتبﻻىنتبلىﻻنتيبينتبلىﻻيبنمل
//        اي
//                بلا
//                يبل
//                        ايب
//                        لى
//                                بكاتكنلامنىتبلنىكمبيىبلﻻنلا
        // Hint for hasan > to create fragment file called mobile_navigation.xml then you can use nav controller like joy stick to open the fragment you want
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessageFragment.BUNDLE_KEY_FOR_ME_USER, currentUser);
        bundle.putSerializable(MessageFragment.BUNDLE_KEY_FOR_BUDDY_USER, buddy);
        bundle.putBoolean(MessageFragment.BUNDLE_KEY_FOR_IS_FOR_CALL,isForCall);
        navController.navigate(R.id.nav_messages, bundle);
        NavBackStackEntry nn = navController.getCurrentBackStackEntry();
        NavBackStackEntry pp = navController.getPreviousBackStackEntry();

        return null;
    }

    @Override
    public void endCallFragment(){
//        if(navController.getCurrentDestination().getId() == R.id.nav_messages)
//            navController.navigate(R.id.nav_users);
        navController.popBackStack();
    }

//    @Override
//    public void setTitleOfCurrentDestination(String title) {
//        String ll = navController.getCurrentDestination().getLabel().toString();
////        navController.getCurrentDestination().setLabel(title);
//    }

    @Override
    public void navigateFromLoginToUsers() {
        long id = Utils.getUserID(this);
        createPingerIfNotCreated();
        AppDatabase.getUserDao().loadUser(id).observe(thiz, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setNavHeaderData(user.getUsername()+"("+user.getUserlanguage().getLanguageName()+")", user.getUseremail());
                currentUser = user;

                if(navController.getCurrentDestination().getId() == R.id.loginFragment) {
//                    navController.popBackStack();
                    navController.navigate(R.id.action_from_login_to_users);
                }
            }
        });

    }


    @Override
    public void navigateFromSplashToLogin() {
        if(navController.getCurrentDestination().getId() == R.id.splashFragment)
            navController.navigate(R.id.action_splash_toLogin);
    }

    @Override
    public void navigateFromSplashToUsers() {
        if(navController.getCurrentDestination().getId() == R.id.splashFragment)
            navController.navigate(R.id.action_splash_toUsers);
    }

//    @Override
//    public void navigateFromUsersToMessages(User buddy, boolean iseCall) {
//        if(navController.getCurrentDestination().getId() == R.id.nav_users)
//            navController.navigate(R.id.action_fromUsers_toMessages);
//    }
//
//    @Override
//    public void navigateFromUsersToModelDownload(Language source, Language target, User buddy, boolean isCall) {
//        if(navController.getCurrentDestination().getId() == R.id.nav_users)
//            navController.navigate(R.id.action_fromUsers_toDownload);
//    }
//
//    @Override
//    public void navigateFromModelDownloadToMessages(User buddy, boolean iseCall) {
//        if(navController.getCurrentDestination().getId() == R.id.downloadFragment)
//            navController.navigate(R.id.action_fromDownload_toMessages);
//    }

//    @Override
//    public void navigateFromMessagesToUsers() {
////        if(navController.getCurrentDestination().getId() == R.id.nav_users)
////            navController.navigate(R.id.action_fromMessages_toUsers);
//        navController.popBackStack();
//    }

    @Override
    public void navigateFromCallingDialogToUsers() {
//        if(navController.getCurrentDestination().getId() == R.id.callingFragment)
//            navController.navigate(R.id.action_fromCalling_toUsers);
        navController.popBackStack();

    }

    @Override
    public void navigateFromCallingDialogToMessages(User buddy, boolean isCall) {
        navController.popBackStack();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessageFragment.BUNDLE_KEY_FOR_ME_USER, currentUser);
        bundle.putSerializable(MessageFragment.BUNDLE_KEY_FOR_BUDDY_USER, buddy);
        bundle.putBoolean(MessageFragment.BUNDLE_KEY_FOR_IS_FOR_CALL,true);
        provideMessageFragment(buddy,true);
//        navController.getBackStack().removeLast();

        binding.drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
//                navController.getBackStack().removeLast();
            }
        }, 600);
//        if(navController.getCurrentDestination().getId() == R.id.callingFragment)
//            navController.navigate(R.id.action_fromCalling_toMessages,bundle);
    }


    @Override
    public void returnToPreviousFragment() {
        navController.popBackStack();
    }

//    @Override
//    public void navigateToUsers() {
//        navController.navigate(R.id.nav_users);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            /** CAMERA **/
            case R.id.action_settings:
                //openCamera();
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                return true;

            /** SEARCH **/
            case R.id.action_change_user:
                //openSearch();
                changeUser();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        resetUIState();
    }

    private void resetUIState() {
        viewFlags =

//                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        |View.SYSTEM_UI_FLAG_LOW_PROFILE
//                 |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(viewFlags);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void resetUIStateDelayed(){
        Utils.runOnUIThreadPostDelayed(new Runnable() {
            @Override
            public void run() {
                resetUIState();
            }
        });
    }

    private void changeUser() {
        Utils.unsetUserCreated(this,currentUser);
        showUserDialog();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Utils.isUserCreated(this))
            createPingerIfNotCreated();
    }


    @Override
    public void requireInternetPermission(PermissionRequestCallbacks permissionRequestCallbacks) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            isRequiringSinglePermission = true;
            this.permissionRequestCallbacks = permissionRequestCallbacks;
            this.requiredSinglePermission = Manifest.permission.INTERNET;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, PERMISSIONS_RESULT_TAG);

        }
        else{
            permissionRequestCallbacks.granted();
        }

    }

    @Override
    public void requireRecordPermission(PermissionRequestCallbacks permissionRequestCallbacks) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            isRequiringSinglePermission = true;
            this.permissionRequestCallbacks = permissionRequestCallbacks;
            this.requiredSinglePermission = Manifest.permission.RECORD_AUDIO;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_RESULT_TAG);
        }
        else{
            permissionRequestCallbacks.granted();
        }
    }


    @Override
    public void requireStoragePermissions(PermissionRequestCallbacks permissionRequestCallbacks) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            isRequiringSinglePermission = false;
            this.permissionRequestCallbacks = permissionRequestCallbacks;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_RESULT_TAG);
        }
        else{
            permissionRequestCallbacks.granted();
        }
    }

    @Override
    public void requireCameraPermission(PermissionRequestCallbacks permissionRequestCallbacks) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            isRequiringSinglePermission = true;
            this.permissionRequestCallbacks = permissionRequestCallbacks;
            this.requiredSinglePermission = Manifest.permission.CAMERA;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, PERMISSIONS_RESULT_TAG);
        }
        else{
            permissionRequestCallbacks.granted();
        }
    }


    @Override
    public void showCallDialogForCallReception(CallDialogCallbacks callDialogCallbacks, User caller) {
        showCallingDialog(callDialogCallbacks,caller,true);
    }

    @Override
    public void showCallDialogForCallSourcing(CallDialogCallbacks callDialogCallbacks, User caller) {
        showCallingDialog(callDialogCallbacks,caller,false);
    }

    private void showCallingDialog(final CallDialogCallbacks callbacks, final User caller,final boolean isCallReceived){
        binding.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putSerializable(CallingFragment.PARAM_ME_USER, currentUser);
                bundle.putSerializable(CallingFragment.PARAM_CALLING_USER, caller);
                bundle.putSerializable(CallingFragment.PARAM_CALLING_DIALOG_CALLBACKS, callbacks);
                bundle.putBoolean(CallingFragment.PARAM_IS_RECEIVE_CALL,isCallReceived);
//        Navigation.findNavController(this, R.id.nav_host_fragment_content_translation_main).navigate(R.id.callingFragment,bundle);
//        Navigation.findNavController(binding.).navigate(R.id.callingFragment,bundle);

//        navController.getCurrentDestination().
                navController.navigate(R.id.callingFragment,bundle);

//                CallingFragment callingFragment = new CallingFragment();
//                callingFragment.setArguments(bundle);
////                UsersFragment usersFragment = new UsersFragment();
//
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.drawer_layout, callingFragment, "callingFragment")
//                        .addToBackStack(null)
//                        .commit();
            }
        });

    }

    @Override
    public void hideCallDialog() {
        if(navController.getCurrentDestination().getId() == R.id.callingFragment)
            navController.popBackStack();
//        navigateToUsers();
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            Bitmap selectedImageBitmap = null;
            String selectedImageContentURI = null;
            String selectedImagePath = null;
            switch (requestCode) {
                case LoginFragment.REQUEST_CODE_PICK_ALL1:{
                    if (resultCode == RESULT_OK && data != null) {
                        Toast.makeText(this,"Req All",Toast.LENGTH_LONG).show();
                        selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                    }


                    break;
                }
                case LoginFragment
                        .REQUEST_CODE_CAPTURE_CAMERA_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                    }

                    break;
                case LoginFragment
                        .REQUEST_CODE_PICK_GALLERY_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        selectedImageContentURI = selectedImage.toString();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                selectedImagePath = picturePath;
                                selectedImageBitmap = BitmapFactory.decodeFile(picturePath);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
            for(StartedActivityResultsListener listener : startedActivityResultsListeners){
                if(listener!=null){
                    if(selectedImageBitmap != null)
                        listener.photoPicked(selectedImageBitmap);
                    if(selectedImageContentURI != null && selectedImageContentURI.length() > 0)
                        listener.photoPickedContentUri(selectedImageContentURI);
                    if(selectedImagePath != null && selectedImagePath.length() > 0)
                        listener.photoPickedFilePath(selectedImagePath);

                }
                else{
                    startedActivityResultsListeners.remove(listener);
                }
            }
        }
    }

    private ArrayList<StartedActivityResultsListener> startedActivityResultsListeners = new ArrayList<>();
    @Override
    public void registerStartedActivityResultsListener(StartedActivityResultsListener startedActivityResultsListener) {
        startedActivityResultsListeners.add(startedActivityResultsListener);
    }


    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    for(StartedActivityResultsListener listener: startedActivityResultsListeners){
                        if(listener != null && uri != null){
                            listener.photoPickedContentUri(uri.toString());
                        }
                    }
                }
            });

    @Override
    public void pickImage() {
        mGetContent.launch("image/*");

//        mThumbnailLiveData.observe(this, new Observer<Bitmap>() {
//            @Override
//            public void onChanged(Bitmap bitmap) {
//                for(StartedActivityResultsListener listener: startedActivityResultsListeners){
//                    if(listener != null){
//                        listener.photoPicked(bitmap);
//                    }
//                    else{
//                        startedActivityResultsListeners.remove(listener);
//                    }
//                }
//            }
//        });
//        mTakePicture.launch(null);
    }



    private final ActivityResultRegistry mRegistry = getActivityResultRegistry();
    private final MutableLiveData<Bitmap> mThumbnailLiveData = new MutableLiveData();
    private final ActivityResultLauncher<Void> mTakePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), mRegistry, new ActivityResultCallback<Bitmap>() {
                @Override
                public void onActivityResult(Bitmap thumbnail) {
                    mThumbnailLiveData.setValue(thumbnail);
                }
            });

}