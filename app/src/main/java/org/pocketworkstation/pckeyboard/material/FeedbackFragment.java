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

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

import org.pocketworkstation.pckeyboard.R;

/**
 * Fragment for configuring keyboard feedback settings (vibration and sound).
 */
public class FeedbackFragment extends Fragment {

    private SharedPreferences prefs;
    private Vibrator vibrator;
    private AudioManager audioManager;

    // Switches
    private MaterialSwitch vibrateSwitch;
    private MaterialSwitch soundSwitch;

    // Sliders
    private Slider vibrateLengthSlider;
    private TextView vibrateLengthValue;
    private Slider clickVolumeSlider;
    private TextView clickVolumeValue;

    // Click method buttons
    private MaterialButtonToggleGroup clickMethodGroup;
    private MaterialButton clickMethodStandard;
    private MaterialButton clickMethodAndroid;
    private MaterialButton clickMethodNone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Initialize vibrator and audio manager for feedback preview
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        try {
            // Initialize switches
            vibrateSwitch = view.findViewById(R.id.vibrate_switch);
            soundSwitch = view.findViewById(R.id.sound_switch);

            // Initialize sliders
            vibrateLengthSlider = view.findViewById(R.id.vibrate_length_slider);
            vibrateLengthValue = view.findViewById(R.id.vibrate_length_value);
            clickVolumeSlider = view.findViewById(R.id.click_volume_slider);
            clickVolumeValue = view.findViewById(R.id.click_volume_value);

            // Initialize click method buttons
            clickMethodGroup = view.findViewById(R.id.click_method_group);
            clickMethodStandard = view.findViewById(R.id.click_method_standard);
            clickMethodAndroid = view.findViewById(R.id.click_method_android);
            clickMethodNone = view.findViewById(R.id.click_method_none);

            // Load current values
            loadPreferences();

            // Setup listeners
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        if (vibrateSwitch != null) vibrateSwitch.setChecked(prefs.getBoolean("vibrate_on", true));
        if (soundSwitch != null) soundSwitch.setChecked(prefs.getBoolean("sound_on", false));

        if (vibrateLengthSlider != null && vibrateLengthValue != null) {
            try {
                int vibrateLength = 40; // default

                // Try to read as String first (VibratePreference stores as String)
                try {
                    String stringValue = prefs.getString("vibrate_len", null);
                    if (stringValue != null && !stringValue.isEmpty()) {
                        // Parse float and convert to int, handling suffixes like " ms"
                        String numericPart = stringValue.replaceAll("[^0-9.]", "");
                        vibrateLength = (int) Float.parseFloat(numericPart);
                    }
                } catch (NumberFormatException e) {
                    // Fall back to reading as int for backward compatibility
                    try {
                        vibrateLength = prefs.getInt("vibrate_len", 40);
                    } catch (ClassCastException ce) {
                        // If both fail, use default
                        vibrateLength = 40;
                    }
                }

                // Clamp value to slider range (5-200)
                vibrateLength = Math.max(5, Math.min(200, vibrateLength));
                vibrateLengthSlider.setValue(vibrateLength);
                vibrateLengthValue.setText(vibrateLength + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                vibrateLengthSlider.setValue(40f);
                vibrateLengthValue.setText("40 ms");
            }
        }

        // Load click method preference
        if (clickMethodGroup != null) {
            try {
                String clickMethod = prefs.getString("pref_click_method", "0");
                switch (clickMethod) {
                    case "0":
                        clickMethodGroup.check(R.id.click_method_standard);
                        break;
                    case "1":
                        clickMethodGroup.check(R.id.click_method_android);
                        break;
                    case "2":
                        clickMethodGroup.check(R.id.click_method_none);
                        break;
                    default:
                        clickMethodGroup.check(R.id.click_method_standard);
                }
            } catch (Exception e) {
                e.printStackTrace();
                clickMethodGroup.check(R.id.click_method_standard);
            }
        }

        // Load click volume preference (stored as float 0.0-1.0 in legacy)
        if (clickVolumeSlider != null && clickVolumeValue != null) {
            try {
                float clickVolume = 0.2f; // default 20%

                // Try to read as String first (legacy stores as float string like "0.2")
                try {
                    String stringValue = prefs.getString("pref_click_volume", null);
                    if (stringValue != null && !stringValue.isEmpty()) {
                        clickVolume = Float.parseFloat(stringValue);
                    }
                } catch (NumberFormatException e) {
                    clickVolume = 0.2f;
                }

                // Convert to percentage for display (0-100)
                int displayPercent = Math.round(clickVolume * 100);
                displayPercent = Math.max(0, Math.min(100, displayPercent));
                clickVolumeSlider.setValue(displayPercent);
                clickVolumeValue.setText(displayPercent + "%");
            } catch (Exception e) {
                e.printStackTrace();
                clickVolumeSlider.setValue(20f);
                clickVolumeValue.setText("20%");
            }
        }
    }

    private void setupListeners() {
        if (vibrateSwitch != null && vibrateLengthSlider != null) {
            vibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("vibrate_on", isChecked).apply();
                vibrateLengthSlider.setEnabled(isChecked);
                // Apply visual dimming when disabled
                vibrateLengthSlider.setAlpha(isChecked ? 1.0f : 0.4f);
                if (vibrateLengthValue != null) {
                    vibrateLengthValue.setAlpha(isChecked ? 1.0f : 0.4f);
                }
            });
        }

        if (soundSwitch != null) {
            soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("sound_on", isChecked).apply();
                // Apply visual dimming to click method and volume controls when sound is disabled
                if (clickMethodGroup != null) {
                    clickMethodGroup.setEnabled(isChecked);
                    clickMethodGroup.setAlpha(isChecked ? 1.0f : 0.4f);
                }
                if (clickVolumeSlider != null) {
                    clickVolumeSlider.setEnabled(isChecked);
                    clickVolumeSlider.setAlpha(isChecked ? 1.0f : 0.4f);
                }
                if (clickVolumeValue != null) {
                    clickVolumeValue.setAlpha(isChecked ? 1.0f : 0.4f);
                }
            });
        }

        if (vibrateLengthSlider != null && vibrateLengthValue != null) {
            vibrateLengthSlider.addOnChangeListener((slider, value, fromUser) -> {
                int length = (int) value;
                vibrateLengthValue.setText(length + " ms");
                if (fromUser) {
                    prefs.edit().putString("vibrate_len", String.valueOf(length)).apply();
                    // Trigger vibration preview while sliding
                    triggerVibration(length);
                }
            });

            // Set initial enabled state and alpha for vibrate length slider
            if (vibrateSwitch != null) {
                boolean isEnabled = vibrateSwitch.isChecked();
                vibrateLengthSlider.setEnabled(isEnabled);
                vibrateLengthSlider.setAlpha(isEnabled ? 1.0f : 0.4f);
                vibrateLengthValue.setAlpha(isEnabled ? 1.0f : 0.4f);
            }
        }

        // Setup click method button group listener
        if (clickMethodGroup != null) {
            clickMethodGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    String value = "0"; // default to standard
                    if (checkedId == R.id.click_method_standard) {
                        value = "0";
                    } else if (checkedId == R.id.click_method_android) {
                        value = "1";
                    } else if (checkedId == R.id.click_method_none) {
                        value = "2";
                    }
                    prefs.edit().putString("pref_click_method", value).apply();
                }
            });

            // Set initial enabled state and alpha based on sound switch
            if (soundSwitch != null) {
                boolean isEnabled = soundSwitch.isChecked();
                clickMethodGroup.setEnabled(isEnabled);
                clickMethodGroup.setAlpha(isEnabled ? 1.0f : 0.4f);
            }
        }

        // Setup click volume slider listener
        if (clickVolumeSlider != null && clickVolumeValue != null) {
            clickVolumeSlider.addOnChangeListener((slider, value, fromUser) -> {
                int volume = (int) value;
                clickVolumeValue.setText(volume + "%");
                if (fromUser) {
                    // Store as float 0.0-1.0 to match legacy format
                    float floatValue = volume / 100.0f;
                    prefs.edit().putString("pref_click_volume", String.valueOf(floatValue)).apply();
                    // Play click sound preview at the new volume
                    playClickSound(floatValue);
                }
            });

            // Set initial enabled state and alpha based on sound switch
            if (soundSwitch != null) {
                boolean isEnabled = soundSwitch.isChecked();
                clickVolumeSlider.setEnabled(isEnabled);
                clickVolumeSlider.setAlpha(isEnabled ? 1.0f : 0.4f);
                clickVolumeValue.setAlpha(isEnabled ? 1.0f : 0.4f);
            }
        }
    }

    /**
     * Trigger a vibration with the specified duration for preview.
     */
    private void triggerVibration(int durationMs) {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(durationMs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Play a click sound at the specified volume for preview.
     */
    private void playClickSound(float volume) {
        if (audioManager == null) return;

        try {
            // Use system click sound effect
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
