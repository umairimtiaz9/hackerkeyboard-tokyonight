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

package org.pocketworkstation.pckeyboard.material.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Central definition of all keyboard settings.
 * Maps to the original prefs.xml settings with correct keys, types, and defaults.
 */
public final class SettingsDefinitions {

    private SettingsDefinitions() {} // Prevent instantiation

    // ==================== PREFERENCE KEYS ====================
    // These must match the keys used in GlobalKeyboardSettings and LatinIME

    // Input Behavior
    public static final String KEY_AUTO_CAP = "auto_cap";
    public static final String KEY_CONNECTBOT_TAB = "connectbot_tab_hack";
    public static final String KEY_LONG_PRESS_DURATION = "pref_long_press_duration";
    public static final String KEY_POPUP_CONTENT = "pref_popup_content";

    // Visual Appearance
    public static final String KEY_HEIGHT_PORTRAIT = "settings_height_portrait";
    public static final String KEY_HEIGHT_LANDSCAPE = "settings_height_landscape";
    public static final String KEY_POPUP_ON = "popup_on";
    public static final String KEY_FULLSCREEN_OVERRIDE = "fullscreen_override";
    public static final String KEY_FORCE_KEYBOARD_ON = "force_keyboard_on";
    public static final String KEY_KEYBOARD_NOTIFICATION = "keyboard_notification";
    public static final String KEY_KEYBOARD_MODE_PORTRAIT = "pref_keyboard_mode_portrait";
    public static final String KEY_KEYBOARD_MODE_LANDSCAPE = "pref_keyboard_mode_landscape";
    public static final String KEY_HINT_MODE = "pref_hint_mode";
    public static final String KEY_LABEL_SCALE = "pref_label_scale_v2";
    public static final String KEY_TOP_ROW_SCALE = "pref_top_row_scale";
    public static final String KEY_RENDER_MODE = "pref_render_mode";

    // Feedback
    public static final String KEY_VIBRATE_ON = "vibrate_on";
    public static final String KEY_VIBRATE_LEN = "vibrate_len";
    public static final String KEY_SOUND_ON = "sound_on";
    public static final String KEY_CLICK_METHOD = "pref_click_method";
    public static final String KEY_CLICK_VOLUME = "pref_click_volume";

    // Key Behavior
    public static final String KEY_CAPS_LOCK = "pref_caps_lock";
    public static final String KEY_SHIFT_LOCK_MODIFIERS = "pref_shift_lock_modifiers";
    public static final String KEY_CTRL_A_OVERRIDE = "pref_ctrl_a_override";
    public static final String KEY_CHORDING_CTRL = "pref_chording_ctrl_key";
    public static final String KEY_CHORDING_ALT = "pref_chording_alt_key";
    public static final String KEY_CHORDING_META = "pref_chording_meta_key";
    public static final String KEY_SLIDE_KEYS = "pref_slide_keys_int";

    // Gestures
    public static final String KEY_SWIPE_UP = "pref_swipe_up";
    public static final String KEY_SWIPE_DOWN = "pref_swipe_down";
    public static final String KEY_SWIPE_LEFT = "pref_swipe_left";
    public static final String KEY_SWIPE_RIGHT = "pref_swipe_right";
    public static final String KEY_VOL_UP = "pref_vol_up";
    public static final String KEY_VOL_DOWN = "pref_vol_down";

    // Theme
    public static final String KEY_KEYBOARD_LAYOUT = "pref_keyboard_layout";

    // Debug
    public static final String KEY_TOUCH_POS = "pref_touch_pos";

    // ==================== INPUT BEHAVIOR SETTINGS ====================
    public static List<SettingsSection> getInputBehaviorSections() {
        List<SettingsSection> sections = new ArrayList<>();

        // Text Correction Section
        sections.add(new SettingsSection.Builder("Text Correction")
                .addBoolean(KEY_AUTO_CAP, "Auto-capitalization",
                        "Capitalize first letter of sentences", true)
                .build());

        // Advanced Section
        sections.add(new SettingsSection.Builder("Advanced")
                .addBoolean(KEY_CONNECTBOT_TAB, "ConnectBot tab hack",
                        "Enable special tab handling for ConnectBot", true,
                        "Tab key sends ESC+TAB for ConnectBot",
                        "Tab key sends normal tab character")
                .addSlider(KEY_LONG_PRESS_DURATION, "Long press duration",
                        "Time to hold key for alternate characters",
                        100f, 1000f, 50f, 400f, true, "%.0f ms")
                .build());

        return sections;
    }

    // ==================== VISUAL APPEARANCE SETTINGS ====================
    public static List<SettingsSection> getVisualAppearanceSections() {
        List<SettingsSection> sections = new ArrayList<>();

        // Display Options Section
        sections.add(new SettingsSection.Builder("Display Options")
                .addBoolean(KEY_POPUP_ON, "Show key popups",
                        "Display popup when key is pressed", true)
                .addBoolean(KEY_FULLSCREEN_OVERRIDE, "Fullscreen override",
                        "Force fullscreen mode in landscape", false,
                        "Fullscreen mode enabled in landscape",
                        "Using app's fullscreen setting")
                .addBoolean(KEY_FORCE_KEYBOARD_ON, "Force keyboard on",
                        "Keep keyboard visible at all times", false,
                        "Keyboard always visible",
                        "Keyboard hides when not needed")
                .addBoolean(KEY_KEYBOARD_NOTIFICATION, "Keyboard notification",
                        "Show persistent notification when keyboard is active", false,
                        "Notification shown when keyboard is active",
                        "No notification shown")
                .build());

        // Keyboard Size Section
        sections.add(new SettingsSection.Builder("Keyboard Size")
                .addSlider(KEY_HEIGHT_PORTRAIT, "Height in portrait mode",
                        "Adjust keyboard height when device is vertical",
                        15f, 75f, 1f, 50f, true, true, "%.0f%%", null)
                .addSlider(KEY_HEIGHT_LANDSCAPE, "Height in landscape mode",
                        "Adjust keyboard height when device is horizontal",
                        15f, 75f, 1f, 50f, true, true, "%.0f%%", null)
                .build());

        return sections;
    }

    // ==================== FEEDBACK SETTINGS ====================
    public static List<SettingsSection> getFeedbackSections() {
        List<SettingsSection> sections = new ArrayList<>();

        // Haptic Feedback Section
        sections.add(new SettingsSection.Builder("Haptic Feedback")
                .addBoolean(KEY_VIBRATE_ON, "Vibrate on keypress",
                        "Provide haptic feedback when typing", true)
                .addSlider(KEY_VIBRATE_LEN, "Vibration duration",
                        "Adjust the length of haptic feedback",
                        5f, 200f, 5f, 40f, false, "%.0f ms")
                .build());

        // Audio Feedback Section
        sections.add(new SettingsSection.Builder("Audio Feedback")
                .addBoolean(KEY_SOUND_ON, "Sound on keypress",
                        "Play sound effect when typing", false)
                .build());

        return sections;
    }

    // ==================== ADVANCED/KEY BEHAVIOR SETTINGS ====================
    public static List<SettingsSection> getAdvancedSections() {
        List<SettingsSection> sections = new ArrayList<>();

        // Key Behavior Section
        sections.add(new SettingsSection.Builder("Key Behavior")
                .addBoolean(KEY_CAPS_LOCK, "Caps lock",
                        "Double-tap shift for caps lock", true,
                        "Double-tap shift enables caps lock",
                        "Caps lock disabled")
                .addBoolean(KEY_SHIFT_LOCK_MODIFIERS, "Shift lock modifiers",
                        "Shift key locks modifier keys", false,
                        "Shift locks Ctrl/Alt/Meta",
                        "Modifiers work independently")
                .build());

        // Debugging Section
        sections.add(new SettingsSection.Builder("Debugging")
                .addBoolean(KEY_TOUCH_POS, "Show touch position",
                        "Display touch coordinates on screen", false,
                        "Touch position displayed",
                        "Touch position hidden")
                .build());

        return sections;
    }

    // ==================== SWIPE ACTION OPTIONS ====================
    public static String[] getSwipeActionEntries() {
        return new String[]{
                "None",
                "Close keyboard",
                "Shift",
                "Caps lock",
                "Ctrl",
                "Alt",
                "Meta",
                "Fn",
                "Compose",
                "Cursor left",
                "Cursor right",
                "Cursor up",
                "Cursor down",
                "Extension keyboard",
                "Full keyboard"
        };
    }

    public static String[] getSwipeActionValues() {
        return new String[]{
                "none",
                "close",
                "shift",
                "caps",
                "ctrl",
                "alt",
                "meta",
                "fn",
                "compose",
                "cursor_left",
                "cursor_right",
                "cursor_up",
                "cursor_down",
                "extension",
                "full"
        };
    }

    // ==================== CHORDING KEY OPTIONS ====================
    public static String[] getChordingKeyEntries() {
        return new String[]{
                "Disabled",
                "Bottom left",
                "Bottom right",
                "Both"
        };
    }

    public static String[] getChordingKeyValues() {
        return new String[]{"0", "1", "2", "3"};
    }

    // ==================== KEYBOARD MODE OPTIONS ====================
    public static String[] getKeyboardModeEntries() {
        return new String[]{
                "Auto",
                "4-row",
                "5-row"
        };
    }

    public static String[] getKeyboardModeValues() {
        return new String[]{"0", "1", "2"};
    }
}
