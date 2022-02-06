package hasan.mohamed.shehata.myapplication.ui.translationfragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hasan.mohamed.shehata.myapplication.databinding.TranslationFragmentBinding;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.templates.GeneralRecyclerViewAdapter;
import hasan.mohamed.shehata.myapplication.types.FabActionType;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.TranslatorCapabilities;
import hasan.mohamed.shehata.myapplication.views.TranslationItemView;

public class TranslationFragment extends Fragment {

    private TranslationViewModel mViewModel;
    private TranslationFragmentBinding binding;

    public static TranslationFragment newInstance() {
        return new TranslationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(TranslationViewModel.class);
        binding = TranslationFragmentBinding.inflate(inflater, container, false);

        mViewModel.getLngList().observe(getViewLifecycleOwner(), new Observer<List<ListItemBindableItemContentProvider>>() {
            @Override
            public void onChanged(List<ListItemBindableItemContentProvider> listItemBindableItemContentProviders) {
                GeneralRecyclerViewAdapter<TranslationItemView> adapter = new GeneralRecyclerViewAdapter<TranslationItemView>(getActivity(), listItemBindableItemContentProviders, null, TranslationItemView.class, FabActionType.AddNewItem, TranslatorCapabilities.TextAndTTSAndASR, null,null,binding.generalFragmentRecyclerView,null);
                binding.generalFragmentRecyclerView.setAdapter(adapter);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.generalFragmentRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.generalFragmentRecyclerView.setLayoutManager(layoutManager);



//        listAdab = new GeneralRecyclerViewAdapter<TranslationItemView>(getContext(), null, null, TranslationItemView.class, FabActionType.AddNewItem, TranslatorCapabilities.TextAndTTSAndASR);
//        binding.generalFragmentRecyclerView.setAdapter(listAdab);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((FabSource)getActivity()).refreshFab();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.generalFragmentRecyclerView.getAdapter());
            if (adapter != null) {
                adapter.release();
//                mViewModel.getLngList().setValue(adapter.getTranslatorItemsLanguages());
            }
        }
        catch (Exception e){e.printStackTrace();}
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            GeneralRecyclerViewAdapter adapter = ((GeneralRecyclerViewAdapter) binding.generalFragmentRecyclerView.getAdapter());
            if (adapter != null) {
//                adapter.release();
                mViewModel.getLngList().setValue(adapter.getTranslatorItemsLanguages());
            }
        }
        catch (Exception e){e.printStackTrace();}
    }
}