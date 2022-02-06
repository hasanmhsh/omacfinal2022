package hasan.mohamed.shehata.myapplication.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by h on 11/13/2017.
 */

public class PreferenceFlag {
    private boolean defaultValue;
    private boolean value;
    private String preferenceKey = "";
    static private Context context=null;
    private CheckBox flagUI;
    public PreferenceFlag(Context context_, String key, boolean defaultValue_, CheckBox flagUI_){
        if(context==null)
            context = context_;
        preferenceKey = key;
        defaultValue = defaultValue_;
        flagUI = flagUI_;
        initialize();
    }
    public PreferenceFlag(Context context_, int keyRes, int defaultValueRes_, CheckBox flagUI_){
        //String key = context_.getResources().getString(keyRes);
        this(context_,context_.getResources().getString(keyRes),context_.getResources().getBoolean(defaultValueRes_),flagUI_);
    }

    private void initialize() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor speditor = sp.edit();
        if(sp.contains(preferenceKey)){
            value = sp.getBoolean(preferenceKey,defaultValue);
        }
        else {
            value = defaultValue;
        }
        if (flagUI != null) {
            flagUI.setChecked(value);
            flagUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setValue(isChecked);
                    speditor.putBoolean(preferenceKey,value);
                    speditor.commit();
                }
            });
        }
    }

    public void setValue(boolean value_) {
        this.value = value_;
        if(context==null)
            return;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor speditor=sp.edit();
        speditor.putBoolean(preferenceKey,value);
        speditor.commit();
        if(flagUI!=null){
            flagUI.setChecked(value);
        }
    }

    public void reload(){
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        value = sp.getBoolean(preferenceKey,defaultValue);
    }
    public boolean getValue(){
        return value;
    }
}
