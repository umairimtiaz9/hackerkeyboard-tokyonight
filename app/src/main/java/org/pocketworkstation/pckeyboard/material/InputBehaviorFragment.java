/*
 * Copyright (C) 2025 Hacker's Keyboard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pocketworkstation.pckeyboard.material;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.pocketworkstation.pckeyboard.R;

/**
 * Fragment for configuring keyboard input behavior settings.
 */
public class InputBehaviorFragment extends Fragment {

    private SharedPreferences prefs;

    // Switches
    private MaterialSwitch autoCapSwitch;
    private MaterialSwitch connectbotTabSwitch;
    private MaterialSwitch fullKeyboardPortraitSwitch;
    private MaterialSwitch capsLockSwitch;
    private MaterialSwitch shiftLockModifiersSwitch;
    private MaterialSwitch fullscreenOverrideSwitch;
    private MaterialSwitch forceKeyboardSwitch;
    private MaterialSwitch keyboardNotificationSwitch;

    // Dropdowns
    private MaterialAutoCompleteTextView ctrlAOverrideDropdown;
    private MaterialAutoCompleteTextView slideKeysDropdown;
    private MaterialAutoCompleteTextView popupContentDropdown;
    private MaterialAutoCompleteTextView ctrlKeyCodeDropdown;
    private MaterialAutoCompleteTextView altKeyCodeDropdown;
    private MaterialAutoCompleteTextView metaKeyCodeDropdown;

    // Slider
    private Slider longPressSlider;
    private TextView longPressValue;

    // Ctrl-A Override options
    private static final String[] CTRL_A_ENTRIES = {
            "Disable Ctrl-A, use Ctrl-Alt-A instead",
            "Disable Ctrl-A completely",
            "Use Ctrl-A (no override)"
    };
    private static final String[] CTRL_A_VALUES = {"0", "1", "2"};

    // Sliding key events options
    private static final String[] SLIDE_KEYS_ENTRIES = {
            "Ignore keys during sliding (Recommended)",
            "Send first key touched",
            "Send last key touched",
            "Send first and last key",
            "Send all keys touched"
    };
    private static final String[] SLIDE_KEYS_VALUES = {"0", "1", "2", "3", "4"};

    // Popup content options
    private static final String[] POPUP_CONTENT_ENTRIES = {
            "No popups",
            "No popups, use auto-repeat",
            "Unique only: 3 → ³, e → é",
            "Add shifted: 3 → #³, e → é",
            "Add upper: 3 → #³, e → Eé",
            "Add self: 3 → 3#³, e → eEé"
    };
    private static final String[] POPUP_CONTENT_VALUES = {"256", "768", "0", "1", "3", "7"};

    // Ctrl key code options
    private static final String[] CTRL_KEY_ENTRIES = {
            "None (ignored when not modifying)",
            "Left Ctrl",
            "Right Ctrl"
    };
    private static final String[] CTRL_KEY_VALUES = {"0", "113", "114"};

    // Alt key code options
    private static final String[] ALT_KEY_ENTRIES = {
            "None (ignored when not modifying)",
            "Left Alt",
            "Right Alt"
    };
    private static final String[] ALT_KEY_VALUES = {"0", "57", "58"};

    // Meta key code options
    private static final String[] META_KEY_ENTRIES = {
            "None (ignored when not modifying)",
            "Left Meta",
            "Right Meta"
    };
    private static final String[] META_KEY_VALUES = {"0", "117", "118"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_behavior, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        try {
            // Initialize switches
            autoCapSwitch = view.findViewById(R.id.auto_cap_switch);
            connectbotTabSwitch = view.findViewById(R.id.connectbot_tab_switch);
            fullKeyboardPortraitSwitch = view.findViewById(R.id.full_keyboard_portrait_switch);
            capsLockSwitch = view.findViewById(R.id.caps_lock_switch);
            shiftLockModifiersSwitch = view.findViewById(R.id.shift_lock_modifiers_switch);
            fullscreenOverrideSwitch = view.findViewById(R.id.fullscreen_override_switch);
            forceKeyboardSwitch = view.findViewById(R.id.force_keyboard_switch);
            keyboardNotificationSwitch = view.findViewById(R.id.keyboard_notification_switch);

            // Initialize dropdowns
            ctrlAOverrideDropdown = view.findViewById(R.id.ctrl_a_override_dropdown);
            slideKeysDropdown = view.findViewById(R.id.slide_keys_dropdown);
            popupContentDropdown = view.findViewById(R.id.popup_content_dropdown);
            ctrlKeyCodeDropdown = view.findViewById(R.id.ctrl_key_code_dropdown);
            altKeyCodeDropdown = view.findViewById(R.id.alt_key_code_dropdown);
            metaKeyCodeDropdown = view.findViewById(R.id.meta_key_code_dropdown);

            // Initialize slider
            longPressSlider = view.findViewById(R.id.long_press_slider);
            longPressValue = view.findViewById(R.id.long_press_value);

            // Setup dropdown adapters
            setupDropdownAdapters();

            // Load current values
            loadPreferences();

            // Setup listeners
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        if (autoCapSwitch != null) autoCapSwitch.setChecked(prefs.getBoolean("auto_cap", true));
        if (connectbotTabSwitch != null) connectbotTabSwitch.setChecked(prefs.getBoolean("connectbot_tab_hack", false));
        if (fullKeyboardPortraitSwitch != null) fullKeyboardPortraitSwitch.setChecked(prefs.getBoolean("full_keyboard_in_portrait", false));
        if (capsLockSwitch != null) capsLockSwitch.setChecked(prefs.getBoolean("pref_caps_lock", true));
        if (shiftLockModifiersSwitch != null) shiftLockModifiersSwitch.setChecked(prefs.getBoolean("pref_shift_lock_modifiers", false));
        if (fullscreenOverrideSwitch != null) fullscreenOverrideSwitch.setChecked(prefs.getBoolean("fullscreen_override", false));
        if (forceKeyboardSwitch != null) forceKeyboardSwitch.setChecked(prefs.getBoolean("force_keyboard_on", false));
        if (keyboardNotificationSwitch != null) keyboardNotificationSwitch.setChecked(prefs.getBoolean("keyboard_notification", false));

        // Load dropdown values
        if (ctrlAOverrideDropdown != null) {
            String value = prefs.getString("pref_ctrl_a_override", "0");
            ctrlAOverrideDropdown.setText(valueToEntry(value, CTRL_A_VALUES, CTRL_A_ENTRIES), false);
        }
        if (slideKeysDropdown != null) {
            String value = prefs.getString("pref_slide_keys_int", "0");
            slideKeysDropdown.setText(valueToEntry(value, SLIDE_KEYS_VALUES, SLIDE_KEYS_ENTRIES), false);
        }
        if (popupContentDropdown != null) {
            String value = prefs.getString("pref_popup_content", "1");
            popupContentDropdown.setText(valueToEntry(value, POPUP_CONTENT_VALUES, POPUP_CONTENT_ENTRIES), false);
        }

        // Load key code dropdown values
        if (ctrlKeyCodeDropdown != null) {
            String value = prefs.getString("pref_chording_ctrl_key", "0");
            ctrlKeyCodeDropdown.setText(valueToEntry(value, CTRL_KEY_VALUES, CTRL_KEY_ENTRIES), false);
        }
        if (altKeyCodeDropdown != null) {
            String value = prefs.getString("pref_chording_alt_key", "0");
            altKeyCodeDropdown.setText(valueToEntry(value, ALT_KEY_VALUES, ALT_KEY_ENTRIES), false);
        }
        if (metaKeyCodeDropdown != null) {
            String value = prefs.getString("pref_chording_meta_key", "0");
            metaKeyCodeDropdown.setText(valueToEntry(value, META_KEY_VALUES, META_KEY_ENTRIES), false);
        }

        if (longPressSlider != null && longPressValue != null) {
            try {
                // pref_long_press_duration is stored as String in original prefs
                String longPressStr = prefs.getString("pref_long_press_duration", "400");
                int longPressDuration = Integer.parseInt(longPressStr);
                // Clamp value to slider range (100-1000)
                longPressDuration = Math.max(100, Math.min(1000, longPressDuration));
                longPressSlider.setValue((float) longPressDuration);
                longPressValue.setText(longPressDuration + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                // Set default value on error
                longPressSlider.setValue(400f);
                longPressValue.setText("400 ms");
            }
        }
    }

    /**
     * Setup ArrayAdapter for all dropdown menus.
     */
    private void setupDropdownAdapters() {
        if (ctrlAOverrideDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    CTRL_A_ENTRIES
            );
            ctrlAOverrideDropdown.setAdapter(adapter);
        }
        if (slideKeysDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    SLIDE_KEYS_ENTRIES
            );
            slideKeysDropdown.setAdapter(adapter);
        }
        if (popupContentDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    POPUP_CONTENT_ENTRIES
            );
            popupContentDropdown.setAdapter(adapter);
        }
        if (ctrlKeyCodeDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    CTRL_KEY_ENTRIES
            );
            ctrlKeyCodeDropdown.setAdapter(adapter);
        }
        if (altKeyCodeDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    ALT_KEY_ENTRIES
            );
            altKeyCodeDropdown.setAdapter(adapter);
        }
        if (metaKeyCodeDropdown != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.item_dropdown,
                    META_KEY_ENTRIES
            );
            metaKeyCodeDropdown.setAdapter(adapter);
        }
    }

    /**
     * Convert a preference value to its corresponding display entry.
     */
    private String valueToEntry(String value, String[] values, String[] entries) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return entries[i];
            }
        }
        return entries[0];
    }

    /**
     * Convert a display entry to its corresponding preference value.
     */
    private String entryToValue(String entry, String[] entries, String[] values) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].equals(entry)) {
                return values[i];
            }
        }
        return values[0];
    }

    private void setupListeners() {
        if (autoCapSwitch != null) {
            autoCapSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("auto_cap", isChecked).apply());
        }

        if (connectbotTabSwitch != null) {
            connectbotTabSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("connectbot_tab_hack", isChecked).apply());
        }

        if (fullKeyboardPortraitSwitch != null) {
            fullKeyboardPortraitSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("full_keyboard_in_portrait", isChecked).apply());
        }

        if (capsLockSwitch != null) {
            capsLockSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("pref_caps_lock", isChecked).apply());
        }

        if (shiftLockModifiersSwitch != null) {
            shiftLockModifiersSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("pref_shift_lock_modifiers", isChecked).apply());
        }

        if (fullscreenOverrideSwitch != null) {
            fullscreenOverrideSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("fullscreen_override", isChecked).apply());
        }

        if (forceKeyboardSwitch != null) {
            forceKeyboardSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("force_keyboard_on", isChecked).apply());
        }

        if (keyboardNotificationSwitch != null) {
            keyboardNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("keyboard_notification", isChecked).apply());
        }

        // Dropdown listeners
        if (ctrlAOverrideDropdown != null) {
            ctrlAOverrideDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, CTRL_A_ENTRIES, CTRL_A_VALUES);
                prefs.edit().putString("pref_ctrl_a_override", value).apply();
            });
        }

        if (slideKeysDropdown != null) {
            slideKeysDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, SLIDE_KEYS_ENTRIES, SLIDE_KEYS_VALUES);
                prefs.edit().putString("pref_slide_keys_int", value).apply();
            });
        }

        if (popupContentDropdown != null) {
            popupContentDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, POPUP_CONTENT_ENTRIES, POPUP_CONTENT_VALUES);
                prefs.edit().putString("pref_popup_content", value).apply();
            });
        }

        // Key code dropdown listeners
        if (ctrlKeyCodeDropdown != null) {
            ctrlKeyCodeDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, CTRL_KEY_ENTRIES, CTRL_KEY_VALUES);
                prefs.edit().putString("pref_chording_ctrl_key", value).apply();
            });
        }

        if (altKeyCodeDropdown != null) {
            altKeyCodeDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, ALT_KEY_ENTRIES, ALT_KEY_VALUES);
                prefs.edit().putString("pref_chording_alt_key", value).apply();
            });
        }

        if (metaKeyCodeDropdown != null) {
            metaKeyCodeDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String entry = (String) parent.getItemAtPosition(position);
                String value = entryToValue(entry, META_KEY_ENTRIES, META_KEY_VALUES);
                prefs.edit().putString("pref_chording_meta_key", value).apply();
            });
        }

        if (longPressSlider != null && longPressValue != null) {
            longPressSlider.addOnChangeListener((slider, value, fromUser) -> {
                int duration = (int) value;
                longPressValue.setText(duration + " ms");
                if (fromUser) {
                    // Store as String to match original SeekBarPreferenceString
                    prefs.edit().putString("pref_long_press_duration", String.valueOf(duration)).apply();
                }
            });
        }
    }
}
