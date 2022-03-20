package hasan.mohamed.shehata.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        ((SwitchPreferenceCompat)  getPreferenceManager().findPreference("hasan.mohamed.shehata.myapplication.getIsContinuousSpeaking"))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        Toast.makeText(getContext(), "speaking", Toast.LENGTH_SHORT).show();
                        Utils.setIsContinuousSpeaking(getContext(), (Boolean) newValue);
                        ((TranslationMainActivity)getActivity()).setContinousSpeakingMenuCheckbox((Boolean)newValue);
                        return true;
                    }
                });
        ((SwitchPreferenceCompat)  getPreferenceManager().findPreference("hasan.mohamed.shehata.myapplication.getIsContinuousRecognition"))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        Toast.makeText(getContext(),"recognition", Toast.LENGTH_SHORT).show();
                        Utils.setIsContinuousRecognition(getContext(),(Boolean)newValue);
                        ((TranslationMainActivity)getActivity()).setContinousRecognitionMenuCheckbox((Boolean)newValue);
                        return true;
                    }
                });
        ((SwitchPreferenceCompat)  getPreferenceManager().findPreference("hasan.mohamed.shehata.myapplication.getIsHighContrastTheme"))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        Toast.makeText(getContext(),"contrast", Toast.LENGTH_SHORT).show();
                        Utils.setIsHighContrastTheme(getContext(),(Boolean)newValue);
                        return true;
                    }
                });


        ((Preference)  getPreferenceManager().findPreference("settings_user_profile"))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        ((TranslationMainActivity)getActivity()).updateUser();
                        return true;
                    }
                });

    }
}