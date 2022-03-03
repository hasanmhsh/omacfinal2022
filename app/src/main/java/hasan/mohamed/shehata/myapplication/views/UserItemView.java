package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.TranslationItemLayoutBinding;
import hasan.mohamed.shehata.myapplication.databinding.UsersListItemLayoutBinding;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.TranslationItem;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.AsyncPingerProvider;
import hasan.mohamed.shehata.myapplication.types.HighContrastObserver;
import hasan.mohamed.shehata.myapplication.types.MessageFragmentProvider;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import hasan.mohamed.shehata.myapplication.ui.messages.MessageFragment;

public class UserItemView  extends FrameLayout implements BindableItem , View.OnClickListener, HighContrastObserver {
    private UsersListItemLayoutBinding binding;
    private ResultReceiver selectionReceiver;
    private boolean isAsrEnabled = false;
    private long userid;
    public UserItemView(Context context, ResultReceiver selectionReceiver) {
        super(context);
        userid = Utils.getUserID(context);
        this.selectionReceiver = selectionReceiver;
        setWillNotDraw(false);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)this.getContext().getSystemService(infService);
        this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //attach to parent must be true to be displayed
        binding = DataBindingUtil.inflate(li, R.layout.users_list_item_layout,this,true);
        View view = this.binding.getRoot();
        this.setOnClickListener(this);
        binding.messageBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start message acticity
            }
        });

        binding.callBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Establish call
                if(userid != binding.getUser().getID())
                    ((AsyncPingerProvider)getContext()).getCurrentPinger().call(binding.getUser());
            }
        });

        isHighContrastEnabled = Utils.getIsHighContrastTheme(getContext());
        Utils.registerHighContrastObserver(this);

    }



    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        bind((User)bindableItemContentProvider);
    }

    @Override
    public void bind(DownloadWindowContent downloadWindowContent) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use ListItemBindableContentProvider as argument.");
    }

    @Override
    public void bind(User user) {
        binding.setUser(user);
        refresh(isHighContrastEnabled);
        if(user.getIsOnline()){
//            binding.userImageView.setImageResource(R.drawable.ic_baseline_person_72_green);
            binding.callBut.setEnabled(true);
        }
        else{
//            binding.userImageView.setImageResource(R.drawable.ic_baseline_person_72);
            binding.callBut.setEnabled(false);
        }
        binding.userImageView.setImageDrawable(null);
        user.drawLogo(binding.userImageView);
    }

    @Override
    public void bind(Message msg) {
        throw new UnsupportedOperationException("This operation is not supported for this datatype please use DownloadWindowContent as argument.");
    }


    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClick(View view) {
        if(userid != binding.getUser().getID())
            ((MessageFragmentProvider)getContext()).provideMessageFragment(binding.getUser(),false);
    }

    private boolean isHighContrastEnabled = false;
    @Override
    public void refresh(boolean isHighContrast) {
        isHighContrastEnabled = isHighContrast;
        if(binding == null ||binding.userItemRootOfRootContainer==null|| binding.userListItemRootContainer == null || getContext() == null)
            return;
        if(isHighContrastEnabled){
            binding.userItemRootOfRootContainer.setBackgroundResource(R.color.high_contrast_background_color);
            binding.userListItemRootContainer.setBackgroundResource(R.drawable.pressable_background_high_contrast);
            binding.usernameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext()) * getResources().getDimension(R.dimen.user_list_item_name_text_size));
            binding.usernameTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            binding.userlanguageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.getHighContrastTextFactor(getContext()) * getResources().getDimension(R.dimen.user_list_item_language_text_size));
            binding.userlanguageTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
        }
        else{
            binding.userItemRootOfRootContainer.setBackgroundResource(R.color.transparent);
            binding.userListItemRootContainer.setBackgroundResource(R.drawable.pressable_background);
            binding.usernameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.user_list_item_name_text_size));
            binding.usernameTv.setTextColor(getResources().getColor(R.color.usernameTextColor));
            binding.userlanguageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.user_list_item_language_text_size));
            binding.userlanguageTv.setTextColor(getResources().getColor(R.color.userLanguageTextColor));
        }

    }

//    @BindingAdapter("translationItem")
//    public static void setTime(TranslationItemView view, TranslationItem newValue) {
//        // Important to break potential infinite loops.
//        if (view.binding.getTranslation() != newValue) {
//            view.binding.setTranslation(newValue);
//        }
//    }
}
