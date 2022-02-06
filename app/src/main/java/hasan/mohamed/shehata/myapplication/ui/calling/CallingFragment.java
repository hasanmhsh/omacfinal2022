package hasan.mohamed.shehata.myapplication.ui.calling;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicBoolean;

import hasan.mohamed.shehata.myapplication.Utils;
import hasan.mohamed.shehata.myapplication.databinding.FragmentCallingBinding;
import hasan.mohamed.shehata.myapplication.models.User;
import hasan.mohamed.shehata.myapplication.types.CallDialogCallbacks;
import hasan.mohamed.shehata.myapplication.types.CallDialogReverseCallbacks;
import hasan.mohamed.shehata.myapplication.types.NavigationProvider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CallingFragment extends Fragment implements CallDialogReverseCallbacks {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    public static final String PARAM_CALLING_DIALOG_CALLBACKS = "hasan.mohamed.shehata.myapplication.PARAM_CALLING_CALLBACKS";
    private CallDialogCallbacks callDialogCallbacks;
    public static final String PARAM_CALLING_USER = "hasan.mohamed.shehata.myapplication.PARAM_CALLING_USER";
    public static final String PARAM_ME_USER = "hasan.mohamed.shehata.myapplication.PARAM_ME_USER";
    private User caller;
    private User me;
    private boolean isResponded = false;

    public static final String PARAM_IS_RECEIVE_CALL = "hasan.mohamed.shehata.myapplication.PARAM_IS_RECEIVE_CALL";
    private boolean isReceivingCall;
    private AtomicBoolean isToCallRejectInDestroy = new AtomicBoolean(true);



    private FragmentCallingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if(getArguments().containsKey(PARAM_CALLING_DIALOG_CALLBACKS)) {
            callDialogCallbacks = (CallDialogCallbacks) getArguments().getSerializable(PARAM_CALLING_DIALOG_CALLBACKS);
            callDialogCallbacks.registerReverseCallbacks(this);
            callDialogCallbacks.setIsInhibitClosingDialogRequest(false);
        }
        if(getArguments().containsKey(PARAM_CALLING_USER))
            caller = (User) getArguments().getSerializable(PARAM_CALLING_USER);
        if(getArguments().containsKey(PARAM_ME_USER))
            me = (User) getArguments().getSerializable(PARAM_ME_USER);
        if(getArguments().containsKey(PARAM_IS_RECEIVE_CALL))
            isReceivingCall = getArguments().getBoolean(PARAM_IS_RECEIVE_CALL);

        binding = FragmentCallingBinding.inflate(inflater, container, false);

        if(caller != null)
            binding.setUser(caller);
        else
            binding.setUser(new User());

        isToCallRejectInDestroy.set(true);

        Utils.setIsToCallRejectCallBackInDestroyOfCallingFragment(getContext(),true);
        binding.respondBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isResponded = true;
                isToCallRejectInDestroy.set(false);
                if(callDialogCallbacks != null){
                    callDialogCallbacks.setIsInhibitClosingDialogRequest(true);
                    callDialogCallbacks.acceptCall();
                }
                ((NavigationProvider)getActivity()).navigateFromCallingDialogToMessages(caller,true);
            }
        });
        binding.rejectBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isToCallRejectInDestroy.set(false);
                if(callDialogCallbacks != null){
                    callDialogCallbacks.setIsInhibitClosingDialogRequest(true);
                    // isReject is already called in on destroy
                    callDialogCallbacks.rejectCall();
                }
                ((NavigationProvider)getActivity()).navigateFromCallingDialogToUsers();
            }
        });
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareViewForCallReceptionOrCalling();
    }

    private void prepareViewForCallReceptionOrCalling(){
        if(isReceivingCall){
            binding.callWindowLabel.setText("Received call from");
            binding.respondBut.setVisibility(View.VISIBLE);
        }
        else{

            binding.callWindowLabel.setText("Calling.....");
            binding.respondBut.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        if(callDialogCallbacks != null){
            callDialogCallbacks.setIsInhibitClosingDialogRequest(true);
            if(isReceivingCall) {
                if(isToCallRejectInDestroy.get()) {
                    callDialogCallbacks.rejectCall();
                }
            }
            else{
                if(isToCallRejectInDestroy.get()) {
                    callDialogCallbacks.rejectCall();
                }
            }
        }
        if(isResponded){

        }
        else{


        }
        super.onDestroy();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void dontCallRejectCallbackOnDestroy_YouCanCallThisSyncOrAsync() {
        isToCallRejectInDestroy.set(false);
    }

    @Override
    public void callEstablished(User caller) {
        ((NavigationProvider)getActivity()).navigateFromCallingDialogToMessages(caller,true);
    }
}