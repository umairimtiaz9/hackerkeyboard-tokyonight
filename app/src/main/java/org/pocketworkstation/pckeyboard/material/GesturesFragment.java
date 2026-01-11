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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.pocketworkstation.pckeyboard.R;

/**
 * Fragment for configuring gesture and hardware key actions.
 * Handles swipe gestures (up, down, left, right) and hardware key actions (volume up/down).
 */
public class GesturesFragment extends Fragment {

    private SharedPreferences prefs;

    // Gesture action entries and values (matching legacy prefs_actions.xml)
    private static final String[] GESTURE_ENTRIES = {
            "(no action)", "Close keyboard", "Toggle extension row", "Launch Settings",
            "Toggle suggestions", "Voice input", "Switch keyboard layout",
            "Increase height", "Decrease height", "Previous language", "Next language"
    };

    private static final String[] GESTURE_VALUES = {
            "none", "close", "extension", "settings",
            "suggestions", "voice_input", "full_mode",
            "height_up", "height_down", "lang_prev", "lang_next"
    };

    // Swipe gesture dropdowns
    private MaterialAutoCompleteTextView swipeUpDropdown;
    private MaterialAutoCompleteTextView swipeDownDropdown;
    private MaterialAutoCompleteTextView swipeLeftDropdown;
    private MaterialAutoCompleteTextView swipeRightDropdown;

    // Hardware key dropdowns
    private MaterialAutoCompleteTextView volUpDropdown;
    private MaterialAutoCompleteTextView volDownDropdown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestures, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        try {
            // Initialize swipe gesture dropdowns
            swipeUpDropdown = view.findViewById(R.id.swipe_up_dropdown);
            swipeDownDropdown = view.findViewById(R.id.swipe_down_dropdown);
            swipeLeftDropdown = view.findViewById(R.id.swipe_left_dropdown);
            swipeRightDropdown = view.findViewById(R.id.swipe_right_dropdown);

            // Initialize hardware key dropdowns
            volUpDropdown = view.findViewById(R.id.vol_up_dropdown);
            volDownDropdown = view.findViewById(R.id.vol_down_dropdown);

            // Setup adapter for all dropdowns
            setupDropdownAdapters();

            // Load current values
            loadPreferences();

            // Setup listeners
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup ArrayAdapter for all dropdown menus with gesture options.
     * Each dropdown needs its own adapter instance.
     */
    private void setupDropdownAdapters() {
        try {
            if (swipeUpDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                swipeUpDropdown.setAdapter(adapter);
            }
            if (swipeDownDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                swipeDownDropdown.setAdapter(adapter);
            }
            if (swipeLeftDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                swipeLeftDropdown.setAdapter(adapter);
            }
            if (swipeRightDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                swipeRightDropdown.setAdapter(adapter);
            }
            if (volUpDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                volUpDropdown.setAdapter(adapter);
            }
            if (volDownDropdown != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.item_dropdown,
                        GESTURE_ENTRIES
                );
                volDownDropdown.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load gesture and hardware key preferences from SharedPreferences.
     */
    private void loadPreferences() {
        try {
            // Load swipe up (default: "extension" - matches legacy)
            if (swipeUpDropdown != null) {
                String value = prefs.getString("pref_swipe_up", "extension");
                String entry = valueToEntry(value);
                swipeUpDropdown.setText(entry, false);
            }

            // Load swipe down (default: "close" - matches legacy)
            if (swipeDownDropdown != null) {
                String value = prefs.getString("pref_swipe_down", "close");
                String entry = valueToEntry(value);
                swipeDownDropdown.setText(entry, false);
            }

            // Load swipe left (default: "none" - matches legacy)
            if (swipeLeftDropdown != null) {
                String value = prefs.getString("pref_swipe_left", "none");
                String entry = valueToEntry(value);
                swipeLeftDropdown.setText(entry, false);
            }

            // Load swipe right (default: "none" - matches legacy)
            if (swipeRightDropdown != null) {
                String value = prefs.getString("pref_swipe_right", "none");
                String entry = valueToEntry(value);
                swipeRightDropdown.setText(entry, false);
            }

            // Load volume up (default: "none")
            if (volUpDropdown != null) {
                String value = prefs.getString("pref_vol_up", "none");
                String entry = valueToEntry(value);
                volUpDropdown.setText(entry, false);
            }

            // Load volume down (default: "none")
            if (volDownDropdown != null) {
                String value = prefs.getString("pref_vol_down", "none");
                String entry = valueToEntry(value);
                volDownDropdown.setText(entry, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a preference value to its corresponding display entry.
     *
     * @param value The preference value (e.g., "shift", "close")
     * @return The display entry (e.g., "Shift", "Close keyboard")
     */
    private String valueToEntry(String value) {
        for (int i = 0; i < GESTURE_VALUES.length; i++) {
            if (GESTURE_VALUES[i].equals(value)) {
                return GESTURE_ENTRIES[i];
            }
        }
        // Default to first entry if not found
        return GESTURE_ENTRIES[0];
    }

    /**
     * Convert a display entry to its corresponding preference value.
     *
     * @param entry The display entry (e.g., "Shift", "Close keyboard")
     * @return The preference value (e.g., "shift", "close")
     */
    private String entryToValue(String entry) {
        for (int i = 0; i < GESTURE_ENTRIES.length; i++) {
            if (GESTURE_ENTRIES[i].equals(entry)) {
                return GESTURE_VALUES[i];
            }
        }
        // Default to first value if not found
        return GESTURE_VALUES[0];
    }

    /**
     * Setup listeners for all dropdown menus to save preferences when changed.
     */
    private void setupListeners() {
        try {
            // Swipe up listener
            if (swipeUpDropdown != null) {
                swipeUpDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_swipe_up", value).apply();
                });
            }

            // Swipe down listener
            if (swipeDownDropdown != null) {
                swipeDownDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_swipe_down", value).apply();
                });
            }

            // Swipe left listener
            if (swipeLeftDropdown != null) {
                swipeLeftDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_swipe_left", value).apply();
                });
            }

            // Swipe right listener
            if (swipeRightDropdown != null) {
                swipeRightDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_swipe_right", value).apply();
                });
            }

            // Volume up listener
            if (volUpDropdown != null) {
                volUpDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_vol_up", value).apply();
                });
            }

            // Volume down listener
            if (volDownDropdown != null) {
                volDownDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    String entry = (String) parent.getItemAtPosition(position);
                    String value = entryToValue(entry);
                    prefs.edit().putString("pref_vol_down", value).apply();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
