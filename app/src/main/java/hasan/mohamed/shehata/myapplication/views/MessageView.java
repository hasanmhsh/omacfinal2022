package hasan.mohamed.shehata.myapplication.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import hasan.mohamed.shehata.myapplication.AppDatabase;
import hasan.mohamed.shehata.myapplication.R;
import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.MessageItemLayoutBinding;
import hasan.mohamed.shehata.myapplication.internet.APIClient;
import hasan.mohamed.shehata.myapplication.models.BindableItem;
import hasan.mohamed.shehata.myapplication.models.DownloadWindowContent;
import hasan.mohamed.shehata.myapplication.models.ListItemBindableItemContentProvider;
import hasan.mohamed.shehata.myapplication.models.Message;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.MessageDeletionResult;
import hasan.mohamed.shehata.myapplication.types.ResultReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageView  extends FrameLayout implements BindableItem {
    private MessageItemLayoutBinding binding;
    private ResultReceiver selectionReceiver;
    private boolean isAsrEnabled = false;
    private User buddy;
    private long myID;
    public MessageView(Context context, ResultReceiver selectionReceiver) {
        super(context);
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        APIClient.getAPIInterface(getContext()).deleteMessage(binding.getMessage().getMessageid()).enqueue(new Callback<MessageDeletionResult>() {
                            @Override
                            public void onResponse(Call<MessageDeletionResult> call, Response<MessageDeletionResult> response) {
                                if(response.isSuccessful()){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppDatabase.getMessageDao().deleteMessageById(binding.getMessage().getMessageid());
                                            Utils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    selectionReceiver.deleteItem(binding.getMessage());
                                                }
                                            });
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
                }).start();
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
            binding.senderNameTv.setText(buddy.getUsername());
        }

//        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.messageHorizontalMargin);
//        int marginL = getContext().getResources().getDimensionPixelSize(R.dimen.margin_l);
//        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.general_text_padding);
//        LayoutParams params = new LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT
//        );
//        params.setMargins(left, top, right, bottom);

        if(msg.getSenderid() == myID){
            // Iam sender
            binding.buddyMsgLogo.setVisibility(GONE);
            binding.buddyMsgTriangle.setVisibility(GONE);
            binding.myMsgLogo.setVisibility(VISIBLE);
            binding.myMsgTriangle.setVisibility(VISIBLE);
            binding.messageViewContainer.setBackgroundResource(R.drawable.my_message_pressable_background);
            binding.senderNameTv.setTextColor(getResources().getColor(R.color.my_message_text_color));
            binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.my_message_text_color));
            binding.senderNameTv.setTextColor(getResources().getColor(R.color.my_message_label_text_color));
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
            binding.getMessage().setIsToShowTranslatedText(true);
            binding.isToShowTranslatedText.setChecked(true);
            // Iam receiver
//            params.setMargins(marginL, marginL, margin, marginL);
//            binding.messageViewContainer.setBackgroundResource(R.drawable.received_message_background);
//            setmarg(binding.messageViewContainer,0,0,(int)densitytopixels(getContext(),70.0f),0);

            binding.buddyMsgLogo.setVisibility(VISIBLE);
            binding.buddyMsgTriangle.setVisibility(VISIBLE);
            binding.myMsgLogo.setVisibility(GONE);
            binding.myMsgTriangle.setVisibility(GONE);
            binding.senderNameTv.setTextColor(getResources().getColor(R.color.buddy_message_text_color));
            binding.textOfMessageTv.setTextColor(getResources().getColor(R.color.buddy_message_text_color));
            binding.messageViewContainer.setBackgroundResource(R.drawable.buddy_message_pressable_background);
            binding.senderNameTv.setTextColor(getResources().getColor(R.color.buddy_message_label_text_color));
            final User me = new User();
            me.setUserid(binding.getMessage().getSenderid());
            me.drawCircularLogo(binding.buddyMsgLogo);
        }

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

//    @BindingAdapter("translationItem")
//    public static void setTime(TranslationItemView view, TranslationItem newValue) {
//        // Important to break potential infinite loops.
//        if (view.binding.getTranslation() != newValue) {
//            view.binding.setTranslation(newValue);
//        }
//    }
}
