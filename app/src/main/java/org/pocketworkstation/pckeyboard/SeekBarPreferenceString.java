package org.pocketworkstation.pckeyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Variant of SeekBarPreference that stores values as string preferences.
 * 
 * This is for compatibility with existing preferences, switching types
 * leads to runtime errors when upgrading or downgrading.
 */
public class SeekBarPreferenceString extends SeekBarPreference {

    private static Pattern FLOAT_RE = Pattern.compile("(\\d+\\.?\\d*).*");

    public SeekBarPreferenceString(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarPreferenceString(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekBarPreferenceString(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Some saved preferences from old versions have " ms" or "%" suffix, remove that.
    private float floatFromString(String pref) {
        if (pref == null) return 0.0f;
        Matcher num = FLOAT_RE.matcher(pref);
        if (!num.matches()) return 0.0f;
        return Float.valueOf(num.group(1));
    }

    @Override
    protected Object onGetDefaultValue(android.content.res.TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        String persisted = getPersistedString(null);
        if (persisted != null) {
            try {
                mVal = floatFromString(persisted);
            } catch (NumberFormatException e) {
                mVal = 0.0f;
            }
        } else if (defaultValue != null) {
            if (defaultValue instanceof Float) {
                mVal = (Float) defaultValue;
            } else {
                try {
                    mVal = floatFromString(defaultValue.toString());
                } catch (NumberFormatException e) {
                    mVal = 0.0f;
                }
            }
        } else {
            mVal = 0.0f;
        }
        mPrevVal = mVal;
    }

    @Override
    protected void persistValue(float val) {
        persistString(Float.toString(val));
    }
}