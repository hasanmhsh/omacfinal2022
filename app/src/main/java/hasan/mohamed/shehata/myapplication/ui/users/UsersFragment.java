package hasan.mohamed.shehata.myapplication.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.myapplication.TranslationMainActivity;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.FragmentUsersBinding;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.templates.GeneralRecyclerViewAdapter;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.UserListConsumer;
import hasan.mohamed.shehata.myapplication.views.UserItemView;

public class UsersFragment extends Fragment implements TranslationMainActivity.MeListener {

    private UsersViewModel usersViewModel;
    private FragmentUsersBinding binding;
    private User me;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usersViewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);



        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding.setUser(new User());

        final TextView textView = binding.usersTitle;
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

        ((AsyncPingerProvider)getActivity()).registerUserConsumerAfterCreatingPinger(new UserListConsumer() {
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
        });
        me = ((TranslationMainActivity)getActivity()).registerMeListener(this);
        meReady(me);
        return root;
    }

    private void initList(List<ListItemBindableItemContentProvider> users) {
        ArrayList<ListItemBindableItemContentProvider> items = null;
        if(users != null)
            new ArrayList<ListItemBindableItemContentProvider>(users);
        GeneralRecyclerViewAdapter<UserItemView> adapter = new GeneralRecyclerViewAdapter<UserItemView>(getContext(), items, null, UserItemView.class, FabActionType.None,null, null,null,binding.fragmentRecyclerView,null);
        binding.fragmentRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fragmentRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.fragmentRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());
            if (adapter != null) {
                adapter.release();
                usersViewModel.getUserListLiveData().setValue(adapter.getDataset());
            }
        }
        catch (Exception e){e.printStackTrace();}
        binding = null;

    }



    @Override
    public void onResume() {
        super.onResume();
        ((FabSource)getActivity()).disableFab();
//        ((FabSource)getActivity()).refreshFab();
        if(binding != null) {
            if (binding.loadingContainer != null && binding.loadingContainer.getVisibility() != View.VISIBLE)
                binding.loadingContainer.setVisibility(View.VISIBLE);
            if (binding.mainContainer != null && binding.mainContainer.getVisibility() != View.GONE)
                binding.mainContainer.setVisibility(View.GONE);
        }

        // This is to hide nav bar
        ((TranslationMainActivity)getActivity()).resetUIStateDelayed();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.fragmentRecyclerView.getAdapter());
            if (adapter != null) {
//                adapter.release();
                usersViewModel.getUserListLiveData().setValue(adapter.getDataset());
            }
        }
        catch (Exception e){e.printStackTrace();}


    }


    @Override
    public void meReady(User me) {
        if(me!=null)
            if(binding!=null)
                binding.setUser(me);
                me.drawLogo(binding.headerMyUserImageView);
                if(binding.usersTitle != null) {
                    this.me=me;
                    binding.usersTitle.setText(me.getUsername());
                    usersViewModel.getText().setValue(me.getUsername());
                }
    }
}