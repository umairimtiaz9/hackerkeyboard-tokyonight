package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.util.AttributeSet;

public class VibratePreference extends SeekBarPreferenceString {
    public VibratePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VibratePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VibratePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    @Override
    public void onChange(float val) {
        LatinIME ime = LatinIME.sInstance;
        if (ime != null) ime.vibrate((int) val);
    }
}