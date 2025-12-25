package org.pocketworkstation.pckeyboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_XML = "xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) {
            setTitle(title);
        }

        if (savedInstanceState == null) {
            int xmlRes = getIntent().getIntExtra(EXTRA_XML, R.xml.prefs);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, GenericSettingsFragment.newInstance(xmlRes))
                    .commit();
        }
    }

    public static class GenericSettingsFragment extends PreferenceFragmentCompat {
        public static GenericSettingsFragment newInstance(int xmlRes) {
            GenericSettingsFragment fragment = new GenericSettingsFragment();
            Bundle args = new Bundle();
            args.putInt("xml", xmlRes);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            int xmlRes = getArguments().getInt("xml");
            setPreferencesFromResource(xmlRes, rootKey);
        }
    }
}
