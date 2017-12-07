package cc.pchospital.app;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import cc.pchospital.app.util.ChangeLocale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        initSummery(getPreferenceScreen());

        // 切换用户按钮监听
        Preference preference = findPreference(getString(R.string.settings_items_change_user));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent it = new Intent(getActivity(), LoginActivity.class);
                it.putExtra(getString(R.string.app_intent_extra_settings),
                        getString(R.string.app_intent_extra_settings_no_change_language_button));
                startActivityForResult(it, 1);
                return true;
            }
        });

        // 切换语言按钮监听
        Preference switchLanguage = findPreference(getString(R.string.settings_items_language));
        switchLanguage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ChangeLocale.Change(getActivity());
                restart();
                return true;
            }
        });
        switchLanguage.setSummary(getString(R.string.app_locale));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.app_db_push_enabled))) {
            return;
        }
        Preference preference = findPreference(key);
        preference.setSummary(sharedPreferences.getString(key, "default"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initSummery(getPreferenceScreen());
    }

    private void initSummery(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup group = (PreferenceGroup) p;
            for (int i = 0; i < group.getPreferenceCount(); i++) {
                initSummery(group.getPreference(i));
            }
        } else {
            String key = p.getKey();
            if (key != null) {
                if (!key.equals(getString(R.string.app_db_push_enabled))) {
                    String summery = sharedPreferences.getString(key, null);
                    if (summery != null)
                        p.setSummary(summery);
                }

            }
        }
    }

    private void restart() {
        getActivity().finish();
        Intent it = new Intent(getActivity(), TicketActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }

}
