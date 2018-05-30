package cc.kenai.projectX.childmode;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import cc.kenai.projectX.R;


public class ChildModeTextActivity extends PreferenceActivity {

    SwitchPreference switchPreference;
    Preference applistPreference;
    ListPreference limittimePreference;
    ListPreference limittimeWeekendPreference;
    SwitchPreference forcestopPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.child_mode_config);

        switchPreference = (SwitchPreference) findPreference("switch");
        applistPreference = findPreference("app_list");
        limittimePreference = (ListPreference) findPreference("limit_time");
        limittimeWeekendPreference = (ListPreference) findPreference("limit_time_weekend");
        forcestopPreference = (SwitchPreference) findPreference("force_stop");
        initConfig();

        limittimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(Long.valueOf((String) newValue)/60000+"分钟");
                return true;
            }
        });

        limittimeWeekendPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(Long.valueOf((String) newValue)/60000+"分钟");
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isModeOn = switchPreference.isChecked();
        Long limitTime = Long.valueOf(limittimePreference.getValue());
        Long limitTimeWeekend = Long.valueOf(limittimeWeekendPreference.getValue());
        boolean isForceStop = forcestopPreference.isChecked();


    }

    private void initConfig(){
        switchPreference.setChecked(true);

        limittimePreference.setEntries(new CharSequence[]{"1分钟","3分钟","5分钟"});
        limittimePreference.setEntryValues(new CharSequence[]{"60000","180000","300000"});

        limittimeWeekendPreference.setEntries(new CharSequence[]{"1分钟","3分钟","5分钟"});
        limittimeWeekendPreference.setEntryValues(new CharSequence[]{"60000","180000","300000"});
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
