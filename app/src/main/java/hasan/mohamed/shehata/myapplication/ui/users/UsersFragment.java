package hasan.mohamed.shehata.myapplication.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.FragmentUsersBinding;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralRecyclerViewAdapter;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.SearchCallbacks;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.types.UsersViewType;
import hasan.mohamed.shehata.myapplication.views.UserItemView;

public class UsersFragment extends Fragment implements TranslationMainActivity.MeListener, UserListConsumer {

    public static final String FRAGMENT_TYPE_ALL_USERS = "hasan.mohamed.shehata.myapplication.ui.users.FRAGMENT_TYPE_ALL_USERS";
    public static final String FRAGMENT_TYPE_CONTACTS = "hasan.mohamed.shehata.myapplication.ui.users.FRAGMENT_TYPE_CONTACTS";
    public static final String FRAGMENT_TYPE_GROUPS = "hasan.mohamed.shehata.myapplication.ui.users.FRAGMENT_TYPE_GROUPS";
    public static final String FRAGMENT_TYPE_CALLS = "hasan.mohamed.shehata.myapplication.ui.users.FRAGMENT_TYPE_CALLS";


    public static final int FRAGMENT_TYPE_ALL_USERS_ACTION = 12;
    public static final int FRAGMENT_TYPE_CONTACTS_ACTION = 13;
    public static final int FRAGMENT_TYPE_GROUPS_ACTION = 14;
    public static final int FRAGMENT_TYPE_CALLS_ACTION = 15;


    public static final String  USERS_FRAGMENT_TYPE_NAME = "hasan.mohamed.shehata.myapplication.ui.users.USERS_FRAGMENT_TYPE_NAME";
    private UsersViewModel usersViewModel;
    private FragmentUsersBinding binding;
    private User me;
    private boolean isMeReadyCalled  = false;

    private UsersViewType usersViewType;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        super.onCreateView(inflater,container,savedInstanceState);


        try{
            TranslationMainActivity activity = ((TranslationMainActivity) getActivity());
            if(activity != null){
                switch (activity.getCurrentDestenationId()){

                    case R.id.nav_contacts:
                        usersViewType = UsersViewType.contacts;
                        break;
                    case R.id.nav_groups:
                        usersViewType = UsersViewType.groups;
                        break;
                    case R.id.nav_calls:
                        usersViewType = UsersViewType.calls;
                        break;
                    default:
                    case R.id.nav_users:
                        usersViewType = UsersViewType.allusers;
                        break;
                }
            }
        }
        catch (Exception e){

        }

        usersViewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);
        Bundle args = getArguments();
        if(getArguments() != null && getContext() != null){
            if(getArguments().containsKey(USERS_FRAGMENT_TYPE_NAME)){
//                Toast.makeText(getContext(),(String)getArguments().getString(USERS_FRAGMENT_TYPE_NAME),Toast.LENGTH_LONG).show();
            }
        }

        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        binding.setUser(new User());

        final TextView textView = binding.usersTitle;
        usersViewModel.getMeUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(me == null) {
                    me = new User(user);
                    if (me != null && !isMeReadyCalled) {
                        meReady(me);
                        isMeReadyCalled = true;
//                        initList(null);
                    }
                }
            }
        });
        usersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        usersViewModel.getUserListLiveData().observe(getViewLifecycleOwner(), new Observer<List<ListItemBindableItemContentProvider>>() {
            @Override
            public void onChanged(List<ListItemBindableItemContentProvider> users) {
                initList(users);
            }
        });

        usersViewModel.getGroupList().observe(getViewLifecycleOwner(), new Observer<List<ListItemBindableItemContentProvider>>() {
            @Override
            public void onChanged(List<ListItemBindableItemContentProvider> groups) {
                initGroupList(groups);
            }
        });

        ((AsyncPingerProvider)getActivity()).registerUserConsumerAfterCreatingPinger(this);
        me = ((TranslationMainActivity)getActivity()).registerMeListener(this);
        if(me != null && !isMeReadyCalled){
            meReady(me);
            isMeReadyCalled = true;
        }
        return root;
    }

    private boolean isListInitialized = false;
    private void initList(List<ListItemBindableItemContentProvider> users) {
        if(usersViewType != UsersViewType.groups) {
            ArrayList<ListItemBindableItemContentProvider> items = null;
            if (users != null) {
                isListInitialized = true;
                items = new ArrayList<ListItemBindableItemContentProvider>(users);
            }
            SearchCallbacks searchCallbacks = null;
            if(this != null && this.getActivity() != null)
                searchCallbacks = ((TranslationMainActivity)getActivity()).getSearchableCallBacks();
            GeneralRecyclerViewAdapter<UserItemView> adapter = new GeneralRecyclerViewAdapter<UserItemView>(getContext(), items, null, UserItemView.class, FabActionType.None, null, null, null, binding.fragmentRecyclerView, null, false, null, usersViewType,false,searchCallbacks);

            binding.fragmentRecyclerView.setAdapter(adapter);
        }
    }

    private void initGroupList(List<ListItemBindableItemContentProvider> groups) {
        if(usersViewType == UsersViewType.groups) {
            ArrayList<ListItemBindableItemContentProvider> items = null;
            if (groups != null) {
                isListInitialized = true;
                items = new ArrayList<ListItemBindableItemContentProvider>(groups);
            }
            SearchCallbacks searchCallbacks = null;
            if(this != null && this.getActivity() != null)
                searchCallbacks = ((TranslationMainActivity)getActivity()).getSearchableCallBacks();
            GeneralRecyclerViewAdapter<UserItemView> adapter = new GeneralRecyclerViewAdapter<UserItemView>(getContext(), items, null, UserItemView.class, FabActionType.None, null, null, null, binding.fragmentRecyclerView, null, true, null, usersViewType ,false,searchCallbacks);
            binding.fragmentRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fragmentRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.fragmentRecyclerView.setLayoutManager(layoutManager);
//        if(binding != null && !isListInitialized) {
//            if (binding.loadingContainer != null && binding.loadingContainer.getVisibility() != View.VISIBLE)
//                binding.loadingContainer.setVisibility(View.VISIBLE);
//            if (binding.mainContainer != null && binding.mainContainer.getVisibility() != View.GONE)
//                binding.mainContainer.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());
            if (adapter != null) {
                adapter.release();
                if(usersViewType == UsersViewType.groups)
                    usersViewModel.getGroupList().setValue(adapter.getDataset());
                else
                    usersViewModel.getUserListLiveData().setValue(adapter.getDataset());
            }
        }


        catch (Exception e){e.printStackTrace();}
        GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());

        if (adapter != null)
            adapter.release();
        binding = null;

    }



    @Override
    public void onResume() {
        super.onResume();
        ((FabSource)getActivity()).disableFab();
//        ((FabSource)getActivity()).refreshFab();


        //Exit full screen mode
//        ((TranslationMainActivity)getActivity()).exitFullScreenMode();


        // This is to hide nav bar
        ((TranslationMainActivity)getActivity()).resetUIStateDelayed();
        ((TranslationMainActivity)getActivity()).displayUserListActionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void meReady(User me) {
        if(me!=null)
            if(binding!=null) {
                ((TranslationMainActivity)getActivity()).bindMeToNavHeader();
                binding.setUser(me);
                if(binding.headerMyUserImageView != null)
                    me.drawLogo(binding.headerMyUserImageView);
                if (binding.usersTitle != null) {
                    this.me = me;
                    binding.usersTitle.setText(me.getUsername());
                    usersViewModel.getText().setValue(me.getUsername());
                    usersViewModel.getMeUser().setValue(me);
                }
//                initList(null);
            }

    }
    @Override
    public void getUsersList(List<User> users, Fragment fragment) {
        try{Utils.executeKeysFetchRequest(getContext());}catch (Exception e){e.printStackTrace();}
        if(binding != null) {
            if (users != null && users.size() > 0) {
                if (binding.loadingContainer != null && binding.loadingContainer.getVisibility() != View.GONE)
                    binding.loadingContainer.setVisibility(View.GONE);
                if (binding.mainContainer != null && binding.mainContainer.getVisibility() != View.VISIBLE)
                    binding.mainContainer.setVisibility(View.VISIBLE);
            } else {
                if (binding.loadingProgress != null && binding.loadingProgress.getVisibility() != View.GONE)
                    binding.loadingProgress.setVisibility(View.GONE);
                if (binding.requireInternetConnection != null && binding.requireInternetConnection.getVisibility() != View.GONE)
                    binding.requireInternetConnection.setVisibility(View.GONE);
                if (binding.loadingTv != null && binding.loadingContainer.getVisibility() != View.VISIBLE)
                    binding.loadingTv.setVisibility(View.VISIBLE);
                if (binding.loadingTv != null)
                    binding.loadingTv.setText("No users");
                if (binding.loadingContainer != null && binding.loadingContainer.getVisibility() != View.VISIBLE)
                    binding.loadingContainer.setVisibility(View.VISIBLE);
                if (binding.mainContainer != null && binding.mainContainer.getVisibility() != View.GONE)
                    binding.mainContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getGroupList(List<Group> groups, Fragment fragment) {

    }


}