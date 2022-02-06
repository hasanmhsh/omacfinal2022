package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.databinding.TranslationItemLayoutBinding;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;

public class TranslationItemView extends FrameLayout implements View.OnClickListener, BindableItem {
    private TranslationItemLayoutBinding binding;
    private ResultReceiver selectionReceiver;
    private boolean isAsrEnabled = false;
    public TranslationItemView(Context context, ResultReceiver selectionReceiver) {
        super(context);
        this.selectionReceiver = selectionReceiver;
        setWillNotDraw(false);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)this.getContext().getSystemService(infService);
        this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //attach to parent must be true to be displayed
        binding = DataBindingUtil.inflate(li,R.layout.translation_item_layout,this,true);
        View view = this.binding.getRoot();
//        this.setOnClickListener(this);
        binding.selectLanguageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getTranslation().changeSourceLanguage();
            }
        });

        binding.deleteItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getTranslation().deleteItem();
            }
        });

        binding.ttsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getTranslation().say();
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        binding.setTranslation((TranslationItem) bindableItemContentProvider);
        binding.getTranslation().bindASRViews(binding.asrButton , binding.textTranslationTranslateFromTextEt);
    }

    @Override
    public void bind(DownloadWindowContent downloadWindowContent) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use ListItemBindableContentProvider as argument.");
    }

    @Override
    public void bind(User user) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }

    @Override
    public void bind(Message msg) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }


    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

//    @BindingAdapter("translationItem")
//    public static void setTime(TranslationItemView view, TranslationItem newValue) {
//        // Important to break potential infinite loops.
//        if (view.binding.getTranslation() != newValue) {
//            view.binding.setTranslation(newValue);
//        }
//    }
}
