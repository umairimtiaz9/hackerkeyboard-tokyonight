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

package org.pocketworkstation.pckeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Material 3 styled main/welcome activity for Hacker's Keyboard.
 * Provides setup buttons for enabling keyboard, setting input method, and accessing settings.
 * Supports Tokyo Night theme variants with instant theme switching.
 */
public class MaterialMainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int currentThemeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply Tokyo Night theme based on user preference BEFORE super.onCreate()
        currentThemeId = getThemeIdFromPrefs();
        setTheme(currentThemeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_main);

        // Register preference change listener for instant theme switching
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        setupUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Instant theme change when user selects a new theme
        if (KeyboardSwitcher.PREF_KEYBOARD_LAYOUT.equals(key)) {
            int newThemeId = getThemeIdFromPrefs();
            if (newThemeId != currentThemeId) {
                currentThemeId = newThemeId;
                recreate();
            }
        }
    }

    /**
     * Get the Material 3 theme resource ID based on user's keyboard layout preference.
     */
    private int getThemeIdFromPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String layoutPref = prefs.getString(KeyboardSwitcher.PREF_KEYBOARD_LAYOUT, "10");

        try {
            int layoutId = Integer.parseInt(layoutPref);
            switch (layoutId) {
                case 11: // Night
                    return R.style.Theme_HackerKeyboard_Material3_Night;
                case 12: // Day
                    return R.style.Theme_HackerKeyboard_Material3_Day;
                case 13: // Moon
                    return R.style.Theme_HackerKeyboard_Material3_Moon;
                case 10: // Storm (default)
                default:
                    return R.style.Theme_HackerKeyboard_Material3_Storm;
            }
        } catch (NumberFormatException e) {
            return R.style.Theme_HackerKeyboard_Material3_Storm;
        }
    }

    /**
     * Setup all UI components and click listeners.
     */
    private void setupUI() {
        // Setup version text
        TextView versionText = findViewById(R.id.version_text);
        if (versionText != null) {
            versionText.setText("Version " + getString(R.string.auto_version));
        }

        // Setup Enable Keyboard button
        MaterialButton enableKeyboardBtn = findViewById(R.id.btn_enable_keyboard);
        if (enableKeyboardBtn != null) {
            enableKeyboardBtn.setOnClickListener(v -> {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            });
        }

        // Setup Set Input Method button
        MaterialButton setInputMethodBtn = findViewById(R.id.btn_set_input_method);
        if (setInputMethodBtn != null) {
            setInputMethodBtn.setOnClickListener(v -> {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (mgr != null) {
                    mgr.showInputMethodPicker();
                }
            });
        }

        // Setup Settings button
        MaterialButton settingsBtn = findViewById(R.id.btn_settings);
        if (settingsBtn != null) {
            settingsBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MaterialSettingsActivity.class));
            });
        }

        // Setup GitHub link
        LinearLayout githubLink = findViewById(R.id.github_link);
        if (githubLink != null) {
            githubLink.setOnClickListener(v -> {
                String url = getString(R.string.about_github_url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
        }
    }
}
