package hasan.mohamed.shehata.myapplication.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by h on 11/14/2017.
 */

public class PreferenceItem<T> {
    //public static final int INTEGER_TYPE = 33;
    //public static final int STRING_TYPE = 65;
    //public static final int BOOLEAN_TYPE = 90;
    private PreferenceKey key ;
    private Context context=null;
    private SharedPreferences sp;
    private SharedPreferences.Editor speditor;
    public PreferenceItem (Context context_,  PreferenceKey key_){
        context = context_;
        key = key_;
        if(context!=null){
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            speditor = sp.edit();
        }
    }

    public T get(){
        if(context == null){
            return (T)key.getDefaultValue();
        }
        try {
            if (key.getDefaultValue() instanceof Integer) {
                return (T)(Integer)Integer.parseInt(sp.getString(key.getKey(), String.valueOf(((Integer)key.getDefaultValue()).intValue())));

            }
            else{
                return (T)sp.getString(key.getKey(), (String)key.getDefaultValue());
            }
        }
        catch(Exception ex){
            return (T)key.getDefaultValue();
        }
    }

    public void set(String value){
        if(speditor == null){
            return;
        }
        try {
            speditor.putString(key.getKey(),value);
            speditor.commit();
        }
        catch(Exception ex){
        }
    }

    public boolean isExist(){
        return sp.contains(key.getKey());
    }
}
