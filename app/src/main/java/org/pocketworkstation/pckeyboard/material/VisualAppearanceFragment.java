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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

import org.pocketworkstation.pckeyboard.R;

/**
 * Fragment for configuring keyboard visual appearance settings.
 */
public class VisualAppearanceFragment extends Fragment {

    private SharedPreferences prefs;

    // Switches
    private MaterialSwitch popupSwitch;
    private MaterialSwitch fullscreenOverrideSwitch;
    private MaterialSwitch forceKeyboardOnSwitch;
    private MaterialSwitch keyboardNotificationSwitch;

    // Sliders
    private Slider heightPortraitSlider;
    private TextView heightPortraitValue;
    private Slider heightLandscapeSlider;
    private TextView heightLandscapeValue;
    private Slider labelScaleSlider;
    private TextView labelScaleValue;
    private Slider candidateScaleSlider;
    private TextView candidateScaleValue;
    private Slider topRowScaleSlider;
    private TextView topRowScaleValue;

    // Keyboard Mode Toggle Groups
    private MaterialButtonToggleGroup keyboardModePortraitGroup;
    private MaterialButtonToggleGroup keyboardModeLandscapeGroup;

    // Hint Mode Toggle Group
    private MaterialButtonToggleGroup hintModeGroup;

    // Font Toggle Group
    private MaterialButtonToggleGroup keyboardFontGroup;
    private TextView customFontStatus;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            copyCustomFont(uri);
                        }
                    } else {
                        // User cancelled, revert to stored preference
                        loadPreferences();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visual_appearance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        try {
            // Initialize switches
            popupSwitch = view.findViewById(R.id.popup_switch);
            fullscreenOverrideSwitch = view.findViewById(R.id.fullscreen_override_switch);
            forceKeyboardOnSwitch = view.findViewById(R.id.force_keyboard_on_switch);
            keyboardNotificationSwitch = view.findViewById(R.id.keyboard_notification_switch);

            // Initialize sliders
            heightPortraitSlider = view.findViewById(R.id.height_portrait_slider);
            heightPortraitValue = view.findViewById(R.id.height_portrait_value);
            heightLandscapeSlider = view.findViewById(R.id.height_landscape_slider);
            heightLandscapeValue = view.findViewById(R.id.height_landscape_value);
            labelScaleSlider = view.findViewById(R.id.label_scale_slider);
            labelScaleValue = view.findViewById(R.id.label_scale_value);
            candidateScaleSlider = view.findViewById(R.id.candidate_scale_slider);
            candidateScaleValue = view.findViewById(R.id.candidate_scale_value);
            topRowScaleSlider = view.findViewById(R.id.top_row_scale_slider);
            topRowScaleValue = view.findViewById(R.id.top_row_scale_value);

            // Initialize keyboard mode toggle groups
            keyboardModePortraitGroup = view.findViewById(R.id.keyboard_mode_portrait_group);
            keyboardModeLandscapeGroup = view.findViewById(R.id.keyboard_mode_landscape_group);

            // Initialize hint mode toggle group
            hintModeGroup = view.findViewById(R.id.hint_mode_group);

            // Initialize font toggle group
            keyboardFontGroup = view.findViewById(R.id.keyboard_font_group);
            customFontStatus = view.findViewById(R.id.custom_font_status);

            // Load current values
            loadPreferences();

            // Setup listeners
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        if (popupSwitch != null) popupSwitch.setChecked(prefs.getBoolean("popup_on", true));
        if (fullscreenOverrideSwitch != null) fullscreenOverrideSwitch.setChecked(prefs.getBoolean("fullscreen_override", false));
        if (forceKeyboardOnSwitch != null) forceKeyboardOnSwitch.setChecked(prefs.getBoolean("force_keyboard_on", false));
        if (keyboardNotificationSwitch != null) keyboardNotificationSwitch.setChecked(prefs.getBoolean("keyboard_notification", false));

        if (heightPortraitSlider != null && heightPortraitValue != null) {
            try {
                // settings_height_portrait is stored as String in original prefs
                String heightStr = prefs.getString("settings_height_portrait", "35");
                int heightPortrait = Integer.parseInt(heightStr);
                // Clamp to slider range (15-75)
                heightPortrait = Math.max(15, Math.min(75, heightPortrait));
                heightPortraitSlider.setValue(heightPortrait);
                heightPortraitValue.setText(heightPortrait + "%");
            } catch (Exception e) {
                e.printStackTrace();
                heightPortraitSlider.setValue(35f);
                heightPortraitValue.setText("35%");
            }
        }

        if (heightLandscapeSlider != null && heightLandscapeValue != null) {
            try {
                // settings_height_landscape is stored as String in original prefs
                String heightStr = prefs.getString("settings_height_landscape", "50");
                int heightLandscape = Integer.parseInt(heightStr);
                // Clamp to slider range (15-75)
                heightLandscape = Math.max(15, Math.min(75, heightLandscape));
                heightLandscapeSlider.setValue(heightLandscape);
                heightLandscapeValue.setText(heightLandscape + "%");
            } catch (Exception e) {
                e.printStackTrace();
                heightLandscapeSlider.setValue(50f);
                heightLandscapeValue.setText("50%");
            }
        }

        // Load label scale (stored as String, range 0.5-2.0)
        if (labelScaleSlider != null && labelScaleValue != null) {
            try {
                String scaleStr = prefs.getString("pref_label_scale_v2", "1.0");
                float scale = Float.parseFloat(scaleStr);
                // Clamp to slider range (0.5-2.0)
                scale = Math.max(0.5f, Math.min(2.0f, scale));
                labelScaleSlider.setValue(scale);
                labelScaleValue.setText((int)(scale * 100) + "%");
            } catch (Exception e) {
                e.printStackTrace();
                labelScaleSlider.setValue(1.0f);
                labelScaleValue.setText("100%");
            }
        }

        // Load candidate scale (stored as String, range 0.5-2.0)
        if (candidateScaleSlider != null && candidateScaleValue != null) {
            try {
                String scaleStr = prefs.getString("pref_candidate_scale_v2", "1.0");
                float scale = Float.parseFloat(scaleStr);
                // Clamp to slider range (0.5-2.0)
                scale = Math.max(0.5f, Math.min(2.0f, scale));
                candidateScaleSlider.setValue(scale);
                candidateScaleValue.setText((int)(scale * 100) + "%");
            } catch (Exception e) {
                e.printStackTrace();
                candidateScaleSlider.setValue(1.0f);
                candidateScaleValue.setText("100%");
            }
        }

        // Load top row scale (stored as String, range 0.5-2.0)
        if (topRowScaleSlider != null && topRowScaleValue != null) {
            try {
                String scaleStr = prefs.getString("pref_top_row_scale", "1.0");
                float scale = Float.parseFloat(scaleStr);
                // Clamp to slider range (0.5-2.0)
                scale = Math.max(0.5f, Math.min(2.0f, scale));
                topRowScaleSlider.setValue(scale);
                topRowScaleValue.setText((int)(scale * 100) + "%");
            } catch (Exception e) {
                e.printStackTrace();
                topRowScaleSlider.setValue(1.0f);
                topRowScaleValue.setText("100%");
            }
        }

        // Load keyboard mode portrait (0=Auto, 1=4-row, 2=5-row)
        if (keyboardModePortraitGroup != null) {
            try {
                String modeStr = prefs.getString("pref_keyboard_mode_portrait", "0");
                int mode = Integer.parseInt(modeStr);
                selectKeyboardModePortrait(mode);
            } catch (Exception e) {
                e.printStackTrace();
                selectKeyboardModePortrait(0); // Default to Auto
            }
        }

        // Load keyboard mode landscape (0=Auto, 1=4-row, 2=5-row)
        if (keyboardModeLandscapeGroup != null) {
            try {
                String modeStr = prefs.getString("pref_keyboard_mode_landscape", "0");
                int mode = Integer.parseInt(modeStr);
                selectKeyboardModeLandscape(mode);
            } catch (Exception e) {
                e.printStackTrace();
                selectKeyboardModeLandscape(0); // Default to Auto
            }
        }

        // Load hint mode (0=Off, 1=On, 2=Preview)
        if (hintModeGroup != null) {
            try {
                String hintStr = prefs.getString("pref_hint_mode", "1");
                int hintMode = Integer.parseInt(hintStr);
                selectHintMode(hintMode);
            } catch (Exception e) {
                e.printStackTrace();
                selectHintMode(1); // Default to On
            }
        }

        // Load font mode (0=Code, 1=Mono, 2=Sans, 3=Serif)
        if (keyboardFontGroup != null) {
            try {
                String fontStr = prefs.getString("pref_keyboard_font", "0");
                int fontMode = Integer.parseInt(fontStr);
                selectFontMode(fontMode);
            } catch (Exception e) {
                e.printStackTrace();
                selectFontMode(0); // Default to Code
            }
        }
    }

    private void selectKeyboardModePortrait(int mode) {
        if (keyboardModePortraitGroup == null) return;
        keyboardModePortraitGroup.clearChecked();
        switch (mode) {
            case 0: // Auto
                keyboardModePortraitGroup.check(R.id.keyboard_mode_portrait_auto);
                break;
            case 1: // 4-row
                keyboardModePortraitGroup.check(R.id.keyboard_mode_portrait_4row);
                break;
            case 2: // 5-row
                keyboardModePortraitGroup.check(R.id.keyboard_mode_portrait_5row);
                break;
        }
    }

    private void selectKeyboardModeLandscape(int mode) {
        if (keyboardModeLandscapeGroup == null) return;
        keyboardModeLandscapeGroup.clearChecked();
        switch (mode) {
            case 0: // Auto
                keyboardModeLandscapeGroup.check(R.id.keyboard_mode_landscape_auto);
                break;
            case 1: // 4-row
                keyboardModeLandscapeGroup.check(R.id.keyboard_mode_landscape_4row);
                break;
            case 2: // 5-row
                keyboardModeLandscapeGroup.check(R.id.keyboard_mode_landscape_5row);
                break;
        }
    }

    private void selectHintMode(int mode) {
        if (hintModeGroup == null) return;
        hintModeGroup.clearChecked();
        switch (mode) {
            case 0: // Off
                hintModeGroup.check(R.id.hint_mode_off);
                break;
            case 1: // On
                hintModeGroup.check(R.id.hint_mode_on);
                break;
            case 2: // Preview
                hintModeGroup.check(R.id.hint_mode_preview);
                break;
        }
    }

    private void selectFontMode(int mode) {
        if (keyboardFontGroup == null) return;
        keyboardFontGroup.clearChecked();
        switch (mode) {
            case 0: // Code (Default)
                keyboardFontGroup.check(R.id.font_code);
                break;
            case 4: // Custom
                keyboardFontGroup.check(R.id.font_custom);
                break;
            default: // Fallback for removed options or errors
                keyboardFontGroup.check(R.id.font_code);
                break;
        }
        
        if (mode == 4) {
            updateCustomFontStatus(true);
        } else {
            if (customFontStatus != null) customFontStatus.setVisibility(View.GONE);
        }
    }

    private void copyCustomFont(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            File outFile = new File(requireContext().getFilesDir(), "custom_font.ttf");
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            is.close();
            
            // Persist the custom selection only after successful copy
            prefs.edit().putString("pref_keyboard_font", "4").apply();
            updateCustomFontStatus(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            updateCustomFontStatus(false);
            prefs.edit().putString("pref_keyboard_font", "0").apply(); // Revert to Code
            loadPreferences();
        }
    }
    
    private void updateCustomFontStatus(boolean success) {
        if (customFontStatus == null) return;
        customFontStatus.setVisibility(View.VISIBLE);
        if (success) {
            File fontFile = new File(requireContext().getFilesDir(), "custom_font.ttf");
            if (fontFile.exists()) {
                customFontStatus.setText("Custom font loaded (" + (fontFile.length() / 1024) + " KB)");
                // We don't have easy access to theme colors here without context compat, so just use default text color
                // or try to set error color only on failure
                customFontStatus.setTextColor(customFontStatus.getTextColors().getDefaultColor()); 
            } else {
                customFontStatus.setText("Custom font selected but file missing");
            }
        } else {
            customFontStatus.setText("Failed to load font file");
            customFontStatus.setTextColor(0xFFFF0000); // Red
        }
    }

    private void setupListeners() {
        if (popupSwitch != null) {
            popupSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("popup_on", isChecked).apply());
        }

        if (fullscreenOverrideSwitch != null) {
            fullscreenOverrideSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("fullscreen_override", isChecked).apply());
        }

        if (forceKeyboardOnSwitch != null) {
            forceKeyboardOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("force_keyboard_on", isChecked).apply());
        }

        if (keyboardNotificationSwitch != null) {
            keyboardNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("keyboard_notification", isChecked).apply());
        }

        if (heightPortraitSlider != null && heightPortraitValue != null) {
            heightPortraitSlider.addOnChangeListener((slider, value, fromUser) -> {
                int height = (int) value;
                heightPortraitValue.setText(height + "%");
                if (fromUser) {
                    // Store as String to match original SeekBarPreferenceString
                    prefs.edit().putString("settings_height_portrait", String.valueOf(height)).apply();
                }
            });
        }

        if (heightLandscapeSlider != null && heightLandscapeValue != null) {
            heightLandscapeSlider.addOnChangeListener((slider, value, fromUser) -> {
                int height = (int) value;
                heightLandscapeValue.setText(height + "%");
                if (fromUser) {
                    // Store as String to match original SeekBarPreferenceString
                    prefs.edit().putString("settings_height_landscape", String.valueOf(height)).apply();
                }
            });
        }

        // Label scale slider listener
        if (labelScaleSlider != null && labelScaleValue != null) {
            labelScaleSlider.addOnChangeListener((slider, value, fromUser) -> {
                float scale = value;
                labelScaleValue.setText((int)(scale * 100) + "%");
                if (fromUser) {
                    // Store as String with one decimal place
                    prefs.edit().putString("pref_label_scale_v2", String.format("%.1f", scale)).apply();
                }
            });
        }

        // Candidate scale slider listener
        if (candidateScaleSlider != null && candidateScaleValue != null) {
            candidateScaleSlider.addOnChangeListener((slider, value, fromUser) -> {
                float scale = value;
                candidateScaleValue.setText((int)(scale * 100) + "%");
                if (fromUser) {
                    // Store as String with one decimal place
                    prefs.edit().putString("pref_candidate_scale_v2", String.format("%.1f", scale)).apply();
                }
            });
        }

        // Top row scale slider listener
        if (topRowScaleSlider != null && topRowScaleValue != null) {
            topRowScaleSlider.addOnChangeListener((slider, value, fromUser) -> {
                float scale = value;
                topRowScaleValue.setText((int)(scale * 100) + "%");
                if (fromUser) {
                    // Store as String with one decimal place
                    prefs.edit().putString("pref_top_row_scale", String.format("%.1f", scale)).apply();
                }
            });
        }

        // Keyboard mode portrait toggle group listener
        if (keyboardModePortraitGroup != null) {
            keyboardModePortraitGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    int mode = 0; // Default to Auto
                    if (checkedId == R.id.keyboard_mode_portrait_auto) {
                        mode = 0;
                    } else if (checkedId == R.id.keyboard_mode_portrait_4row) {
                        mode = 1;
                    } else if (checkedId == R.id.keyboard_mode_portrait_5row) {
                        mode = 2;
                    }
                    prefs.edit().putString("pref_keyboard_mode_portrait", String.valueOf(mode)).apply();
                }
            });
        }

        // Keyboard mode landscape toggle group listener
        if (keyboardModeLandscapeGroup != null) {
            keyboardModeLandscapeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    int mode = 0; // Default to Auto
                    if (checkedId == R.id.keyboard_mode_landscape_auto) {
                        mode = 0;
                    } else if (checkedId == R.id.keyboard_mode_landscape_4row) {
                        mode = 1;
                    } else if (checkedId == R.id.keyboard_mode_landscape_5row) {
                        mode = 2;
                    }
                    prefs.edit().putString("pref_keyboard_mode_landscape", String.valueOf(mode)).apply();
                }
            });
        }

        // Hint mode toggle group listener
        if (hintModeGroup != null) {
            hintModeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    int mode = 1; // Default to On
                    if (checkedId == R.id.hint_mode_off) {
                        mode = 0;
                    } else if (checkedId == R.id.hint_mode_on) {
                        mode = 1;
                    } else if (checkedId == R.id.hint_mode_preview) {
                        mode = 2;
                    }
                    prefs.edit().putString("pref_hint_mode", String.valueOf(mode)).apply();
                }
            });
        }

        // Font mode toggle group listener
        if (keyboardFontGroup != null) {
            keyboardFontGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    int mode = 0; // Default to Code
                    if (checkedId == R.id.font_code) {
                        mode = 0;
                    } else if (checkedId == R.id.font_custom) {
                        mode = 4;
                        // Launch file picker
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*"); // Accept all, user must pick ttf/otf
                        filePickerLauncher.launch(intent);
                        return; // Don't save pref yet, wait for result
                    }
                    prefs.edit().putString("pref_keyboard_font", String.valueOf(mode)).apply();
                    if (customFontStatus != null) customFontStatus.setVisibility(View.GONE);
                }
            });
        }
    }
}
