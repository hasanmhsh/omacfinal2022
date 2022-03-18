package hasan.mohamed.shehata.myapplication.templates;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.async.AsyncPinger;
import hasan.mohamed.shehata.myapplication.databinding.CallPopupLayoutBinding;
import hasan.mohamed.shehata.myapplication.databinding.GeneralListViewLayoutBinding;
import hasan.mohamed.shehata.myapplication.databinding.GroupCreateUpdateFragmentBinding;
import hasan.mohamed.shehata.myapplication.databinding.LayoutDownloadWindowBinding;
import hasan.mohamed.shehata.myapplication.databinding.ProgressLayoutBinding;
import hasan.mohamed.shehata.myapplication.databinding.SignUpInLayoutBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.GroupUser;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.CallDialogCallbacks;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.GroupRole;
import hasan.mohamed.shehata.myapplication.types.ImageReady;
import hasan.mohamed.shehata.myapplication.types.JSONResult;
import hasan.mohamed.shehata.myapplication.types.LoginResult;
import hasan.mohamed.shehata.myapplication.types.NavHeader;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestCallbacks;
import hasan.mohamed.shehata.myapplication.types.PermissionRequestProvider;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.types.SearchCallbacks;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;
import hasan.mohamed.shehata.myapplication.types.StartedACtivityResultsProvider;
import hasan.mohamed.shehata.myapplication.types.StartedActivityResultsListener;
import hasan.mohamed.shehata.myapplication.types.StatusOfServerObject;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.views.DualTextRecyclerViewItemView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class GeneralPopupWindow extends DialogFragment {
    private static String WINDOW_TITLE_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.templates.GeneralListViewLayoutBinding.WINDOW_TITLE_EXTRA_PARAM";
    private static String WINDOW_LIST_ITEMS_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.templates.GeneralListViewLayoutBinding.WINDOW_LIST_ITEMS_EXTRA_PARAM";
    private static String WINDOW_RESULT_RECEIVER_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.templates.GeneralListViewLayoutBinding.WINDOW_RESULT_RECEIVER_EXTRA_PARAM";
    private static String PARCEL_EXTRA_PARAM = "hasan.mohamed.shehata.myapplication.templates.GeneralListViewLayoutBinding.PARCEL_EXTRA_PARAM";
    public static void makeSelectionWindow(Context context, String title, List<ListItemBindableItemContentProvider> listData , ResultReceiver selectionResultReceiver,boolean isRefreshFab,boolean isMultipleChoices){
        FragmentManager supportFragmentManager = Utils.getSupportFragmentManager(context);
        Bundle bundle = new Bundle();
        SerializedParcel serializedParcel = new SerializedParcel(title, listData, selectionResultReceiver,isMultipleChoices);
        serializedParcel.setItemWithImage(false);
        bundle.putSerializable(PARCEL_EXTRA_PARAM, serializedParcel);
        GSPWindow window = new GSPWindow(bundle);
        window.show(supportFragmentManager, title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
    }

    public static void makeLanguageSelectionWindow(Context context, String title , ResultReceiver selectionResultReceiver,boolean isRefreshFab){
        FragmentManager supportFragmentManager = Utils.getSupportFragmentManager(context);
        Bundle bundle = new Bundle();
        SerializedParcel serializedParcel = new SerializedParcel(title, Arrays.asList(Language.values()), selectionResultReceiver,isRefreshFab);
        serializedParcel.setItemWithImage(true);
        bundle.putSerializable(PARCEL_EXTRA_PARAM, serializedParcel);
        GSPWindow window = new GSPWindow(bundle);
        window.show(supportFragmentManager, title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
    }

    public static BindableItem makeDownloadWindow(Context context, String title,boolean isRefreshFab){
        DownloadWindow window = new DownloadWindow();
        window.show(Utils.getSupportFragmentManager(context), title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
        return window;
    }

    public static void makeGroupCreationWindow(Context context, String title, Group group,boolean isRefreshFab){
        FragmentManager supportFragmentManager = Utils.getSupportFragmentManager(context);
        Bundle bundle = new Bundle();
        SerializedParcel serializedParcel = new SerializedParcel();
        serializedParcel.setGroup(group);
        bundle.putSerializable(PARCEL_EXTRA_PARAM, serializedParcel);
        GroupWindow window = new GroupWindow(bundle);
        window.show(supportFragmentManager, title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
    }

    public static Closeable makeCallWindow(Context context, User buddy, CallDialogCallbacks callbacks, boolean isCallReceived, boolean isRefreshFab){
        Closeable returnResult = null;
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CallDialog.PARAM_BUDDY, buddy);
            bundle.putSerializable(CallDialog.PARAM_WINDOW_ACTIONS_CALLBACKS, callbacks);
            bundle.putBoolean(CallDialog.PARAM_IS_CALL_RECEIVED, isCallReceived);
            CallDialog window = new CallDialog();
            returnResult = window;
            window.setArguments(bundle);
            window.show(Utils.getSupportFragmentManager(context), "");
            if (isRefreshFab)
                ((FabSource) context).refreshFab();
            return window;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return returnResult;
    }

    public static BindableItem makeUserCreationWindow(Context context, String title,boolean isRefreshFab){
        UserCreation window = new UserCreation();
        window.show(Utils.getSupportFragmentManager(context), title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
        return window;
    }


    public static Closeable makeProgressWindow(Context context, String title,boolean isRefreshFab){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        ProgressWindow window = new ProgressWindow();
        window.setArguments(bundle);
        window.show(Utils.getSupportFragmentManager(context), title);
        if(isRefreshFab)
            ((FabSource)context).refreshFab();
        return window;
    }


    private static class SerializedParcel implements Serializable{
        private String title;
        private List<ListItemBindableItemContentProvider> listData;
        private ResultReceiver selectionResultReceiver;
        private boolean isItemWithImage = true;
        private boolean isMultipleChoices;
        private Group group;

        public SerializedParcel(String title, List<ListItemBindableItemContentProvider> listData, ResultReceiver selectionResultReceiver, boolean isMultipleChoices) {
            this.title = title;
            this.listData = listData;
            this.selectionResultReceiver = selectionResultReceiver;
            this.isMultipleChoices= isMultipleChoices;
        }

        public SerializedParcel() {
        }

        public boolean isMultipleChoices() {
            return isMultipleChoices;
        }

        public String getTitle() {
            return title;
        }

        public List<ListItemBindableItemContentProvider> getListData() {
            return listData;
        }

        public ResultReceiver getSelectionResultReceiver() {
            return selectionResultReceiver;
        }

        public boolean isItemWithImage() {
            return isItemWithImage;
        }

        public void setItemWithImage(boolean itemWithImage) {
            isItemWithImage = itemWithImage;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }
    }

    public static class ProgressWindow extends GeneralPopupWindow implements Closeable {
        private ProgressLayoutBinding binding;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = ProgressLayoutBinding.inflate(getLayoutInflater());
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
//            String title = getWindowTitle();
//            getDialog().setTitle(title);
            binding.title.setText(getArguments().getString("title",""));
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        }

        public void closeDialog() {
            dismiss();
        }

        @Override
        public void onResume() {
            super.onResume();
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }

        @Override
        public void close() throws IOException {
            closeDialog();
        }
    }

    public static class GSPWindow extends GeneralPopupWindow {
        private PopupViewModel viewModel;
        private SearchCallbacks searchCallbacks = new SearchCallbacks();
        public GSPWindow(Bundle bundle) {
            super();
            setArguments(bundle);
        }

        public GSPWindow(){
            super();
        }


        private String getWindowTitle() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.getTitle();
            else
                return null;
        }

        private boolean getIsViewItemImage() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.isItemWithImage();
            else
                return false;
        }

        private boolean getIsMultipleChoices() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.isMultipleChoices();
            else
                return false;
        }

        private List<ListItemBindableItemContentProvider> getListData() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.getListData();
            else
                return null;
        }

        private ResultReceiver getResultReceiver() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.getSelectionResultReceiver();
            else
                return null;
        }

        private SerializedParcel getSerializableParcel() {
            if(getArguments() != null)
                return ((SerializedParcel) getArguments().getSerializable(PARCEL_EXTRA_PARAM));
            return null;
        }

        private GeneralListViewLayoutBinding binding;
        private ResultReceiver resultReceiver = new ResultReceiver() {
            @Override
            public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
                //Return result to activity then closeDialog()
                ResultReceiver receiver = getResultReceiver();
                if(receiver != null)
                    receiver.receiveResult(bindableItemContentProvider);
                closeDialog();
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
        };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = GeneralListViewLayoutBinding.inflate(getLayoutInflater());
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
//            viewModel = new ViewModelProvider(this).get(PopupViewModel.class);
//            viewModel.getSerializedParcelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<SerializedParcel>() {
//                @Override
//                public void onChanged(SerializedParcel serializedParcel) {
//                    if(serializedParcel != null)
//                        initRV();
//                }
//            });
            if(getArguments() != null && getSerializableParcel() != null)
                initRV();
            else
                closeDialog();

        }


        private void initRV(){
            String title = getWindowTitle();
            getDialog().setTitle(title);
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            binding.generalFragmentRecyclerView.setHasFixedSize(false);
            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            binding.generalFragmentRecyclerView.setLayoutManager(layoutManager);
            boolean isMC = getIsMultipleChoices();
            List<ListItemBindableItemContentProvider> list = getListData();
            binding.generalFragmentRecyclerView.setAdapter(new GeneralRecyclerViewAdapter<DualTextRecyclerViewItemView>(getContext(), list, resultReceiver, DualTextRecyclerViewItemView.class, FabActionType.None, TranslatorCapabilities.NotApplicable, null,null,binding.generalFragmentRecyclerView,null, false, null, null, getIsMultipleChoices(),searchCallbacks));
            binding.gspSearchEtPopupId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(searchCallbacks.getSearchable() != null){
                        searchCallbacks.getSearchable().find(binding.gspSearchEtPopupId.getText().toString());
                    }
                }
            });
            if(getIsMultipleChoices()){
                binding.gspOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding != null &&
                                binding.generalFragmentRecyclerView != null &&
                                binding.generalFragmentRecyclerView.getAdapter() != null &&
                                resultReceiver != null) {
                            getResultReceiver().receiveMultipleChoices(((GeneralRecyclerViewAdapter) binding.generalFragmentRecyclerView.getAdapter()).getSelection());
                            closeDialog();
                        }
                    }
                });
            }
            else{
                binding.gspOkButton.setVisibility(View.GONE);
            }
        }

        public void closeDialog() {
            dismiss();
        }

        @Override
        public void onResume() {
            super.onResume();
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }

//        @Override
//        public void onPause() {
//            super.onPause();
//            viewModel.getSerializedParcelMutableLiveData().setValue(getSerializableParcel());
//        }
    }



    public static class DownloadWindow extends GeneralPopupWindow implements BindableItem{
        private LayoutDownloadWindowBinding binding;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = LayoutDownloadWindowBinding.inflate(getLayoutInflater());
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }


        public void closeDialog() {
            dismiss();
        }

        @Override
        public void onResume() {
            super.onResume();
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }


        @Override
        public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
            throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
        }

        @Override
        public void bind(DownloadWindowContent downloadWindowContent) {
            if(binding != null)
                binding.setData(downloadWindowContent);
        }

        @Override
        public void bind(User user) {
            throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
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
    }

    public static class UserCreation extends GeneralPopupWindow implements BindableItem{
        private SignUpInLayoutBinding binding;
        private Closeable progressWindow;
        private Language selectedLanguage = null;
        private LifecycleOwner lifeCycleOwner;
        private boolean isSignup = true;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = SignUpInLayoutBinding.inflate(getLayoutInflater());
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            lifeCycleOwner = getViewLifecycleOwner();
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            binding.setUser(new User());


            binding.selectLanguageBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeLanguageSelectionWindow(getContext(),
                            getContext().getResources().getString(R.string.select_language),
                            new ResultReceiver() {
                                @Override
                                public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
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
                            }
                    ,false);
                }
            });

            binding.submitBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                        progressWindow = makeProgressWindow(getContext(), "Saving user...",false);


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
                                                            ((NavHeader) getActivity()).setNavHeaderData(fetchedUser.getUsername() + "(" + fetchedUser.getUserlanguage().getLanguageName() + ")", fetchedUser.getUseremail());
                                                            ((AsyncPingerProvider) getActivity()).createPingerIfNotCreated();
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
                        progressWindow = makeProgressWindow(getContext(), "Login...",false);
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
                                                final long id = AppDatabase.getUserDao().insertUser(fetchedUser);
                                                Utils.runOnUIThread(new Runnable() {
                                                    @Override
                                                    public void run() {
//                                                        boolean before = Utils.isUserCreated(getContext());
                                                        if(id > 0) {
                                                            Utils.setUserCreated(getContext());
                                                            Utils.setUserID(getContext(), id);
//                                                        boolean after = Utils.isUserCreated(getContext());
                                                            ((NavHeader) getActivity()).setNavHeaderData(fetchedUser.getUsername() + "(" + fetchedUser.getUserlanguage().getLanguageName() + ")", fetchedUser.getUseremail());
                                                            ((AsyncPingerProvider) getActivity()).createPingerIfNotCreated();
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
        }

        private void signUpButtonPressed(){
            isSignup = true;
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
            dismiss();
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
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
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
    }




























    public static class CallDialog extends GeneralPopupWindow implements BindableItem, Closeable{
        private CallPopupLayoutBinding binding;
        private Closeable progressWindow;
        private boolean isCallReceived = false;
        private User buddy;
        private LifecycleOwner lifeCycleOwner;
        private CallDialogCallbacks callbacks;
        public static final String PARAM_IS_CALL_RECEIVED = "PARAM_IS_CALL_RECEIVED";
        public static final String PARAM_BUDDY = "PARAM_BUDDY";
        public static final String PARAM_WINDOW_ACTIONS_CALLBACKS = "PARAM_WINDOW_ACTIONS_CALLBACKS";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            buddy = (User) getArguments().getSerializable(PARAM_BUDDY);
            isCallReceived = getArguments().getBoolean(PARAM_IS_CALL_RECEIVED);
            callbacks = (CallDialogCallbacks)getArguments().getSerializable(PARAM_WINDOW_ACTIONS_CALLBACKS);
            binding = CallPopupLayoutBinding.inflate(getLayoutInflater());
            binding.setUser(buddy);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            lifeCycleOwner = getViewLifecycleOwner();
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            binding.setUser(buddy);

            if(isCallReceived){
                binding.callWindowLabel.setText("Incomming call from");
            }
            else{

                binding.respondBut.setVisibility(View.GONE);
            }

            binding.respondBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callbacks.acceptCall();
                    closeDialog();
                }
            });

            binding.rejectBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callbacks.rejectCall();
                    closeDialog();
                }
            });




        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            if(callbacks!=null)
                callbacks.rejectCall();
        }

        public void closeDialog() {
            closeProgressWindow();
            dismiss();
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
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
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
    }


    public class PopupViewModel extends ViewModel {
        private MutableLiveData<SerializedParcel> serializedParcelMutableLiveData;

        public PopupViewModel() {
            serializedParcelMutableLiveData = new MutableLiveData<>();
            serializedParcelMutableLiveData.setValue(null);
        }

        public MutableLiveData<SerializedParcel> getSerializedParcelMutableLiveData() {
            return serializedParcelMutableLiveData;
        }
    }
























    ////////////////GROUP WINDOW
    public static class GroupWindow extends GeneralPopupWindow implements ImageReady , StartedActivityResultsListener {
        private PopupViewModel viewModel;
        private Group group;
        private boolean isFullGroupFetched = false;

        private GroupWindow thiz;
        public GroupWindow(Bundle bundle) {
            super();
            setArguments(bundle);
        }

        public GroupWindow(){
            super();
        }




        private ResultReceiver resultReceiver = new ResultReceiver() {
            @Override
            public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {

            }

            @Override
            public void receiveMultipleChoices(List<ListItemBindableItemContentProvider> list) {
                group.getGroupusers().clear();
                for(ListItemBindableItemContentProvider item : list){
                    group.getGroupusers().add(new GroupUser(group,(User)item,((User)item).getIsGroupAdmin()));
                }
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
        };




        private Group getMyGroup() {
            SerializedParcel serializedParcel = getSerializableParcel();
            if(serializedParcel !=null)
                return serializedParcel.getGroup();
            else
                return null;
        }


        private SerializedParcel getSerializableParcel() {
            if(getArguments() != null)
                return ((SerializedParcel) getArguments().getSerializable(PARCEL_EXTRA_PARAM));
            return null;
        }

        private GroupCreateUpdateFragmentBinding binding;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = GroupCreateUpdateFragmentBinding.inflate(getLayoutInflater());
            return binding.getRoot();
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
//            viewModel = new ViewModelProvider(this).get(PopupViewModel.class);
//            viewModel.getSerializedParcelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<SerializedParcel>() {
//                @Override
//                public void onChanged(SerializedParcel serializedParcel) {
//                    if(serializedParcel != null)
//                        initRV();
//                }
//            });
            if(getArguments() != null && getSerializableParcel() != null)
                group = getMyGroup();
            else
                closeDialog();

            thiz = this;


            if(group == null)
                group = new Group();


            AsyncPinger asyncPinger = Utils.getLastAsyncPinger();
            if(asyncPinger != null && group.getGroupid() != 0){
                asyncPinger.registerImageReadyListenerOrGetImageIfExistForGroups(group.getGroupid(), this);
            }

            if(getActivity() != null){
                ((StartedACtivityResultsProvider)getActivity()).registerStartedActivityResultsListener(this);
            }

            binding.groupImagePhotoBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPhoto();
                }
            });
            if(group.getGroupid() != 0){
                binding.saveGroupBut.setEnabled(false);
                binding.groupName.setEnabled(false);
                binding.groupImagePhotoBut.setEnabled(false);
                if(!isFullGroupFetched) {
                    APIClient.getAPIInterface(getContext()).getFullGroupById(group.getGroupid()).enqueue(new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> response) {
                            if (response.isSuccessful()) {
                                group = response.body();
                                isFullGroupFetched = true;
                                if(getContext()!=null) {
                                    long myid = Utils.getUserID(getContext());
                                    for(GroupUser groupUser : group.getGroupusers()){
                                        if(groupUser.getUser().getUserid() == myid){
                                            if(groupUser.getGrouprole() == GroupRole.ADMIN){
                                                if(binding != null && binding.saveGroupBut != null){
                                                    binding.saveGroupBut.setEnabled(true);

                                                    binding.groupName.setEnabled(true);
                                                    binding.groupImagePhotoBut.setEnabled(true);
                                                }

                                            }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                            call.cancel();
                            Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{

                }
            }
            else{
                group.setGroupusers(new ArrayList<>());
            }
            binding.addGroupMembersBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(group.getGroupid() != 0){
                        if(!isFullGroupFetched) {
                            APIClient.getAPIInterface(getContext()).getFullGroupById(group.getGroupid()).enqueue(new Callback<Group>() {
                                @Override
                                public void onResponse(Call<Group> call, Response<Group> response) {
                                    if (response.isSuccessful()) {
                                        group = response.body();
                                        isFullGroupFetched = true;
                                        if(getContext()!=null) {
                                            long myid = Utils.getUserID(getContext());
                                            for(GroupUser groupUser : group.getGroupusers()){
                                                if(groupUser.getUser().getUserid() == myid){
                                                    if(groupUser.getGrouprole() == GroupRole.ADMIN){
                                                        if(binding != null && binding.saveGroupBut != null){
                                                            binding.saveGroupBut.setEnabled(true);

                                                            binding.groupName.setEnabled(true);
                                                            binding.groupImagePhotoBut.setEnabled(true);
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                        GeneralPopupWindow.makeSelectionWindow(
                                                getContext(),
                                                "",
                                                prepareUserGroupList(group.getGroupusers()),
                                                resultReceiver,
                                                true,
                                                true);
                                    } else {
                                        Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Group> call, Throwable t) {
                                    call.cancel();
                                    Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        else{
                            GeneralPopupWindow.makeSelectionWindow(
                                    getContext(),
                                    "",
                                    prepareUserGroupList(group.getGroupusers()),
                                    resultReceiver,
                                    true,
                                    true);
                        }
                    }
                    else{
                        GeneralPopupWindow.makeSelectionWindow(
                                getContext(),
                                "",
                                prepareUserGroupList(group.getGroupusers()),
                                resultReceiver,
                                true,
                                true);
                    }
                }
            });

            binding.saveGroupBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gname = binding.groupName.getText().toString();
                    if(gname == null || gname.length() == 0){
                        Toast.makeText(getContext(), "Invalid name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    group.setName(gname);

                    GroupRole defaultRole;
                    if(binding.isGroupReadOnlyChkbx.isChecked()){
                        defaultRole = GroupRole.RECEIVE;
                    }
                    else {
                        defaultRole = GroupRole.SEND_RECEIVE;
                    }
                    group.setDefaultgroupusersrole(defaultRole);
                    if(group.getGroupusers() != null) {
                        for (GroupUser groupUser : group.getGroupusers()) {
                            if (groupUser.getGrouprole() != GroupRole.ADMIN)
                                groupUser.setGrouprole(defaultRole);
                        }
                    }

//                    ObjectMapper mapper = new ObjectMapper();
//                    String userWithAddressJson = null;
//                    try {
//                        userWithAddressJson = mapper.writeValueAsString(group);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    if(userWithAddressJson == null)
//                        return;



                    APIClient.getAPIInterface(getContext()).replaceOrSaveFullGroup(group).enqueue(new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> response) {
                            if (response.isSuccessful()) {
                                if (pickedPhotoContentUri != null) {
                                    compressImage(pickedPhotoContentUri);
                                }
                            } else {
                                Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                            call.cancel();
                            Toast.makeText(getContext(), "Connection error!", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });

            binding.cancelGroupBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeDialog();
                }
            });


            if(group.getName() != null)
                binding.groupName.setText(group.getName());
            if(group.getDefaultgroupusersrole() != GroupRole.SEND_RECEIVE)
                binding.isGroupReadOnlyChkbx.setChecked(true);
            else
                binding.isGroupReadOnlyChkbx.setChecked(false);
        }

        private List<ListItemBindableItemContentProvider> prepareUserGroupList(List<GroupUser> members){
            List<User> allUsers = Utils.getOverloadedPingResult().getUsers();
            List<ListItemBindableItemContentProvider> selectionList = new ArrayList<>();
            long myuserid = Utils.getUserID(getContext());
            for(User user : allUsers) {
                User newUser = new User();
                newUser.setUserid(user.getUserid());
                newUser.setUsername(user.getUsername());
                newUser.setUserphone(user.getUserphone());
                newUser.setUserlanguage(user.getUserlanguage());
                newUser.setIsGroupAdmin(false);
                newUser.setIsHighLighted(false);
                if(members!=null && members.size() != 0) {

                    for (GroupUser member : members) {
                        if (member.getUser().getUserid() == user.getUserid()) {
                            if (member.getUser().getIsGroupAdmin()) {
                                newUser.setIsGroupAdmin(true);
                            } else {
                                newUser.setIsGroupAdmin(false);
                            }
                            newUser.setIsHighLighted(true);
                            break;
                        } else {
                            newUser.setIsGroupAdmin(false);
                            newUser.setIsHighLighted(false);
                        }
                    }



                }

                if(group.getGroupid() == 0 && newUser.getUserid() == myuserid){
                    newUser.setIsGroupAdmin(true);
                    newUser.setIsHighLighted(true);
                }
                selectionList.add(newUser);
            }
            return selectionList;
        }



        public void closeDialog() {
            dismiss();
        }

        @Override
        public void onResume() {
            super.onResume();
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }

        @Override
        public void imageReady(long groupid, Bitmap image) {
            if(getContext() != null && binding != null && binding.groupImagePhotoBut != null){
                if(pickedPhotoContentUri == null){
                    Glide
                            .with(getContext())
                            .load(image)
                            .apply(new RequestOptions().circleCrop())
                            .into(binding.groupImagePhotoBut);
                }
            }
        }

        @Override
        public long getUserId() {
            return 0;
        }

//        @Override
//        public void onPause() {
//            super.onPause();
//            viewModel.getSerializedParcelMutableLiveData().setValue(getSerializableParcel());
//        }



















        private Bitmap pickedPhoto;
        private File pickedPhotoFile;
        private String pickedPhotoPath;
        private String pickedPhotoContentUri;


        private void selectPhoto(){

            if(getActivity() != null) {
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
                    if (binding.groupImagePhotoBut != null && photo != null) {
                        Glide
                                .with(this)
                                .load(photo)
                                .circleCrop()
                                .placeholder(R.drawable.ic_baseline_photo_camera_100)
                                .into(binding.groupImagePhotoBut);
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
                    if(getContext() == null)
                        return;
                    Glide
                            .with(getContext())
                            .load(uri)
                            .circleCrop()
                            .placeholder(R.drawable.ic_baseline_photo_camera_100)
                            .into(binding.groupImagePhotoBut);
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
            int origWidth = bitmap.getWidth();
            int origHeight = bitmap.getHeight();
            if(origWidth > destWidth) {
                Bitmap scaledBm = Bitmap.createScaledBitmap(bitmap, destWidth, destWidth * origHeight / origWidth, true);

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
                postPhoto(group.getGroupid());
                trialsOfPhotoPost = 4;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        int trialsOfPhotoPost = 4;
        private void postPhoto(final long myid){
            trialsOfPhotoPost--;
            if(trialsOfPhotoPost <= 0)
                closeDialog();
            if(pickedPhotoFile != null){
                RequestBody fbody = RequestBody.create(pickedPhotoFile, MediaType.parse("image/*"));
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", pickedPhotoFile.getName(), fbody);
                APIClient.getAPIInterface(getContext()).uploadGroupPhoto(myid,body).enqueue(new Callback<JSONResult>() {
                    @Override
                    public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
//                    Toast.makeText(getContext(), response.body().getResult(), Toast.LENGTH_LONG).show();
                        if(!response.isSuccessful()){
                            postPhoto(myid);
                            Utils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        closeDialog();
                                    }
                                    catch (Exception e){e.printStackTrace();
                                    }
                                }
                            });
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






























}
