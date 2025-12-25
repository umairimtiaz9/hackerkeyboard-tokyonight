/**
 *
 */
package org.pocketworkstation.pckeyboard;

import android.content.Context;
import androidx.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class AutoSummaryListPreference extends ListPreference {
    private static final String TAG = "HK/AutoSummaryListPreference";

    public AutoSummaryListPreference(Context context) {
        super(context);
    }

    public AutoSummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSummaryListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoSummaryListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void trySetSummary() {
        CharSequence entry = null;
        try {
            entry = getEntry();
        } catch (Exception e) {
            Log.i(TAG, "Malfunctioning ListPreference, can't get entry: " + e.getMessage());
        }
        if (entry != null) {
            //String percent = getResources().getString(R.string.percent);
            String percent = "percent";
            setSummary(entry.toString().replace("%", " " + percent));
        }
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        trySetSummary();
    }

    @Override
    public void setEntryValues(CharSequence[] entryValues) {
        super.setEntryValues(entryValues);
        trySetSummary();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        trySetSummary();
    }
}
