package hasan.mohamed.shehata.myapplication.ui.texttranslation;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.databinding.TextTranslationFragmentBinding;
import hasan.mohamed.shehata.myapplication.languages.HMSTransloator;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.FabSource;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.models.Language;
import hasan.mohamed.shehata.myapplication.templates.GeneralPopupWindow;
import hasan.mohamed.shehata.myapplication.types.SpeakerProvider;

public class TextTranslationFragment extends Fragment {

    private TextTranslationViewModel mViewModel;
    private TextTranslationFragmentBinding binding;
    private HMSTransloator translator;
    private ResultReceiver fromLanguageResultReceiver = new ResultReceiver() {
        @Override
        public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
            mViewModel.setFromLanguage((Language) bindableItemContentProvider);
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

    private ResultReceiver toLanguageResultReceiver = new ResultReceiver() {
        @Override
        public void receiveResult(ListItemBindableItemContentProvider bindableItemContentProvider) {
            mViewModel.setToLanguage((Language) bindableItemContentProvider);
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

    public static TextTranslationFragment newInstance() {
        return new TextTranslationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(TextTranslationViewModel.class);
        binding = TextTranslationFragmentBinding.inflate(inflater, container, false);

        final TextView textView = binding.textTranslation;
        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mViewModel.getFromLanguage().observe(getViewLifecycleOwner(), new Observer<Language>() {
            @Override
            public void onChanged(@Nullable Language lng) {
                binding.setFromLanguage(lng);
            }
        });

        mViewModel.getToLanguage().observe(getViewLifecycleOwner(), new Observer<Language>() {
            @Override
            public void onChanged(@Nullable Language lng) {
                binding.setToLanguage(lng);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textTranslationTranslateFromChooseLanguageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralPopupWindow.makeSelectionWindow(getContext(), getResources().getString(R.string.select_language), Arrays.asList(Language.values()), fromLanguageResultReceiver , true,false);
//                DownloadStickyNotification.make(getContext(), new Canceler() {
//                }, "filename");
            }
        });
        binding.textTranslationTranslateToChooseLanguageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralPopupWindow.makeSelectionWindow(getContext(), getResources().getString(R.string.select_language), Arrays.asList(Language.values()), toLanguageResultReceiver, true,false );
            }
        });
        binding.checkModelExistanceDownloadIfNotExistBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewModel.getFromLanguage().getValue() == null){
                    Toast.makeText(getContext(), R.string.please_select_language, Toast.LENGTH_SHORT).show();
                }
                else if(mViewModel.getToLanguage().getValue() == null){
                    Toast.makeText(getContext(), R.string.please_select_language, Toast.LENGTH_SHORT).show();
                }
                else{
                    if (translator == null) {
                        translator = new HMSTransloator(getContext(), mViewModel.getFromLanguage().getValue() , mViewModel.getToLanguage().getValue(),binding.textTranslationTranslateFromTextEt,binding.textTranslationTranslateToTextEt,null,"",null,true);
                    }
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(translator != null)
            translator.release();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((FabSource)getActivity()).refreshFab();
    }

}