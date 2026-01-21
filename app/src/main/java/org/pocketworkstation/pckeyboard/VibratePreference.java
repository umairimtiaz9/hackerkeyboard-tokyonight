package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Preference class for configuring vibration intensity settings.
 * 
 * Extends SeekBarPreferenceString to allow users to adjust vibration intensity levels
 * through a slider dialog. Triggers haptic feedback on the keyboard input method when
 * the vibration value changes.
 */
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
    
    /**
     * Called when the vibration preference value changes.
     * 
     * Retrieves the current IME instance and triggers haptic feedback with the new
     * vibration intensity value. This allows users to preview the vibration effect
     * as they adjust the slider.
     * 
     * @param val the new vibration intensity value as a float
     */
    @Override
    public void onChange(float val) {
        LatinIME ime = LatinIME.sInstance;
        if (ime != null) ime.vibrate((int) val);
    }
}