package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.MessageItemLayoutBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.languages.TRNSLG;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.Group;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.HighContrastObserver;
import hasan.mohamed.shehata.myapplication.types.MessageDeletionResult;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageView  extends FrameLayout implements BindableItem, HighContrastObserver {
    private MessageItemLayoutBinding binding;
    private ResultReceiver selectionReceiver;
    private boolean isAsrEnabled = false;
    private User buddy;
    private long myID;
    private float originalMessageLableTextSize;
    private float originalMessageTextSize;
    public MessageView(Context context, ResultReceiver selectionReceiver) {
        super(context);
        isHighContrastEnabled = Utils.getIsHighContrastTheme(getContext());
        myID  = Utils.getUserID(context);
        this.selectionReceiver = selectionReceiver;
        this.buddy = this.selectionReceiver.getBuddy();
        setWillNotDraw(false);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)this.getContext().getSystemService(infService);
        this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //attach to parent must be true to be displayed
        binding = DataBindingUtil.inflate(li, R.layout.message_item_layout,this,true);
        View view = this.binding.getRoot();
//        this.setOnClickListener(this);
        binding.deleteMessageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete message
                binding.getMessage().setControlnumber(binding.getMessage().getMessageid());
                binding.getMessage().seControltext(Utils.MESSAGE_DELETE_COMMAND);
                final List<Message> messagesToDelete = new ArrayList<>();
                messagesToDelete.add(binding.getMessage());
                APIClient.getAPIInterface(getContext()).deleteMessage(messagesToDelete).enqueue(new Callback<MessageDeletionResult>() {
                    @Override
                    public void onResponse(Call<MessageDeletionResult> call, Response<MessageDeletionResult> response) {
                        if(response.isSuccessful()){
                            selectionReceiver.deleteItem(binding.getMessage());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                            for(Message msg : messagesToDelete){

                                                AppDatabase.getMessageDao().deleteMessageById(msg.getMessageid());
                                            }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDeletionResult> call, Throwable t) {
                        call.cancel();
                    }
                });
            }
        });

//        binding.messageTtsBut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Start tts
////                Toast.makeText(getContext() , binding.getMessage().getMessagetext(), Toast.LENGTH_SHORT).show();
//                selectionReceiver.provideSpeaker().speak(binding.getMessage());
//            }
//        });

        binding.messageViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start tts
//                Toast.makeText(getContext() , binding.getMessage().getMessagetext(), Toast.LENGTH_SHORT).show();
                selectionReceiver.provideSpeaker().speak(binding.getMessage());
            }
        });

        binding.isToShowTranslatedText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                binding.getMessage().setIsToShowTranslatedText(b);
            }
        });

        originalMessageLableTextSize = getResources().getDimension(R.dimen.message_item_sender_name_text_size);//binding.senderNameTv.getTextSize();
        originalMessageTextSize = getResources().getDimension(R.dimen.message_item_textofmessage_text_size);//binding.textOfMessageTv.getTextSize();

        this.binding.messageViewContainer.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(binding.getMessage() != null)
                    binding.getMessage().toggleHighLight();
                return true;
            }
        });
        Utils.registerHighContrastObserver(this);



    }



    @Override
    public void bind(ListItemBindableItemContentProvider bindableItemContentProvider) {
        bind((Message) bindableItemContentProvider);
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
    public void bind(Group group) {

    }

    private boolean checkIfControlMessage(Message message) {
        String control = message.getMessagemoshakkaltext();
        if(control != null){
            if(control.equals("Calling") ||control.equals("Accept") || control.equals("Busy") || control.equals("Terminate"))
                return true;
        }
        return false;
    }
    @Override
    public void bind(Message msg) {
        if(msg == null || binding == null || binding.textOfMessageTv == null || binding.senderNameTv == null
        || binding.buddyMsgLogo == null || binding.myMsgLogo==null)
            return;
        binding.setMessage(msg);

//        if(checkIfControlMessage(msg)){
//            binding.messageViewContainer.setVisibility(View.GONE);
//        }
//        else{
//            binding.messageViewContainer.setVisibility(View.VISIBLE);
//        }
        binding.isToShowTranslatedText.setChecked(binding.getMessage().getIsToShowTranslatedText());
        if(binding.getMessage().getSenderid() == myID){
            binding.senderNameTv.setText("You");
        }
        else{
            if(msg.getGroupid() !=0) {
                binding.senderNameTv.setText(msg.getSendername());
            }
            else {
                binding.senderNameTv.setText(buddy.getUsername());
            }
        }

//        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.messageHorizontalMargin);
//        int marginL = getContext().getResources().getDimensionPixelSize(R.dimen.margin_l);
//        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.general_text_padding);
//        LayoutParams params = new LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT
//        );
//        params.setMargins(left, top, right, bottom);
        if(Utils.getIsHighContrastTheme(getContext())){

        }


        if(msg.getSenderid() == myID){
            // Iam sender
            if(Utils.getIsHighContrastTheme(getContext())){

                binding.buddyMsgTriangle.setVisibility(GONE);
                binding.myMsgTriangle.setVisibility(GONE);
                binding.messageViewContainer.setBackgroundResource(R.drawable.my_message_pressable_background_high_contrast);
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
                binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            }
            else{

                binding.buddyMsgTriangle.setVisibility(GONE);
                binding.myMsgTriangle.setVisibility(VISIBLE);
                binding.messageViewContainer.setBackgroundResource(R.drawable.my_message_pressable_background);
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.my_message_text_color));
                binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.my_message_text_color));
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.my_message_label_text_color));
            }
            binding.buddyMsgLogo.setVisibility(GONE);
            binding.myMsgLogo.setVisibility(VISIBLE);
            final User me = new User();
            me.setUserid(myID);
            me.drawCircularLogo(binding.myMsgLogo);


            binding.getMessage().setIsToShowTranslatedText(false);
            binding.isToShowTranslatedText.setChecked(false);
//            setmarg(binding.messageViewContainer,(int)densitytopixels(getContext(),70.0f),0,0,0);



//            params.setMargins(margin, marginL, marginL, marginL);
//            binding.messageViewContainer.setBackgroundResource(R.drawable.sent_message_background);
        }
        else {
//            SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.android);
//            // Get a drawable from the parsed SVG and set it as the drawable for the ImageView
//            imageView.setImageDrawable(svg.createPictureDrawable());
            binding.getMessage().setIsToShowTranslatedText(true);
            binding.isToShowTranslatedText.setChecked(true);
            // Iam receiver
//            params.setMargins(marginL, marginL, margin, marginL);
//            binding.messageViewContainer.setBackgroundResource(R.drawable.received_message_background);
//            setmarg(binding.messageViewContainer,0,0,(int)densitytopixels(getContext(),70.0f),0);


            if (isHighContrastEnabled) {

                binding.buddyMsgTriangle.setVisibility(GONE);
                binding.myMsgTriangle.setVisibility(GONE);
                binding.messageViewContainer.setBackgroundResource(R.drawable.my_message_pressable_background_high_contrast);
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
                binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.high_contrast_text_color));
            } else {

                binding.buddyMsgTriangle.setVisibility(VISIBLE);
                binding.myMsgTriangle.setVisibility(GONE);
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.buddy_message_text_color));
                binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.buddy_message_text_color));
                binding.messageViewContainer.setBackgroundResource(R.drawable.buddy_message_pressable_background);
                binding.senderNameTv.setTextColor(getResources().getColor(R.color.buddy_message_label_text_color));
            }


            binding.buddyMsgLogo.setVisibility(VISIBLE);
            binding.myMsgLogo.setVisibility(GONE);
            final User me = new User();
            me.setUserid(binding.getMessage().getSenderid());
            me.drawCircularLogo(binding.buddyMsgLogo);
        }
        setTextSize();


//        binding.messageViewContainer.setLayoutParams(params);

    }

    public float densitytopixels(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void setmarg(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    boolean isHighContrastEnabled = false;
    @Override
    public void refresh(boolean isHighContrast) {
        isHighContrastEnabled = isHighContrast;
        bind(binding.getMessage());
        setTextSize();
    }

    private void setTextSize() {
        if(isHighContrastEnabled){
            //Enlarge text size
            binding.senderNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext()) * originalMessageLableTextSize);
            binding.textOfMessageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,Utils.getHighContrastTextFactor(getContext()) * originalMessageTextSize);
        }
        else{
            binding.senderNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,originalMessageLableTextSize);
            binding.textOfMessageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,originalMessageTextSize);
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
