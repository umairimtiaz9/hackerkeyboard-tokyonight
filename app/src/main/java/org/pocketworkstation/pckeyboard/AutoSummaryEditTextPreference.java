package org.pocketworkstation.pckeyboard;

import android.content.Context;
import androidx.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Custom preference that extends {@link EditTextPreference} to automatically
 * update the summary text to match the entered value.
 * 
 * This preference synchronizes the display summary with the text input value,
 * providing real-time visual feedback of the currently stored preference value.
 * When {@link #setText(String)} is called, the summary is automatically updated
 * to display the new text.
 */
public class AutoSummaryEditTextPreference extends EditTextPreference {

    /**
     * Constructs an {@code AutoSummaryEditTextPreference} with the given context.
     *
     * @param context the {@link Context} used to access resources
     */
    public AutoSummaryEditTextPreference(Context context) {
        super(context);
    }

    /**
     * Constructs an {@code AutoSummaryEditTextPreference} with the given context and attributes.
     *
     * @param context the {@link Context} used to access resources
     * @param attrs   the attributes of the XML tag that is inflating the preference
     */
    public AutoSummaryEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructs an {@code AutoSummaryEditTextPreference} with the given context, attributes,
     * and default style attribute.
     *
     * @param context the {@link Context} used to access resources
     * @param attrs   the attributes of the XML tag that is inflating the preference
     * @param defStyle the default style attribute resource ID
     */
    public AutoSummaryEditTextPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructs an {@code AutoSummaryEditTextPreference} with the given context, attributes,
     * default style attribute, and default style resource ID.
     *
     * @param context the {@link Context} used to access resources
     * @param attrs   the attributes of the XML tag that is inflating the preference
     * @param defStyleAttr the default style attribute resource ID
     * @param defStyleRes  the default style resource ID
     */
    public AutoSummaryEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets the text value for this preference and automatically updates the summary.
     * 
     * This method overrides the parent {@link EditTextPreference#setText(String)} to
     * ensure that whenever the text value changes, the preference's summary is also
     * updated to display the new text. This provides visual feedback of the current
     * preference value in the preference screen.
     *
     * @param text the new text value to set for this preference
     */
    @Override
    public void setText(String text) {
        super.setText(text);
        setSummary(text);
    }
}
