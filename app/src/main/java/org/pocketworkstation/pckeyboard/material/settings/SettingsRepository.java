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

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Repository for reading and writing keyboard settings.
 * Handles type conversion between Material UI and original preference system.
 */
public class SettingsRepository {

    private final SharedPreferences prefs;

    public SettingsRepository(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Boolean settings
    public boolean getBoolean(BooleanSettingsItem item) {
        return prefs.getBoolean(item.getKey(), item.getDefaultValue());
    }

    public void setBoolean(BooleanSettingsItem item, boolean value) {
        prefs.edit().putBoolean(item.getKey(), value).apply();
    }

    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    // Slider settings (stored as String to match SeekBarPreferenceString)
    public float getSliderValue(SliderSettingsItem item) {
        try {
            if (item.isStoreAsString()) {
                String value = prefs.getString(item.getKey(), String.valueOf((int) item.getDefaultValue()));
                return item.clamp(Float.parseFloat(value));
            } else {
                return item.clamp(prefs.getFloat(item.getKey(), item.getDefaultValue()));
            }
        } catch (Exception e) {
            return item.getDefaultValue();
        }
    }

    public void setSliderValue(SliderSettingsItem item, float value) {
        float clampedValue = item.clamp(value);
        if (item.isStoreAsString()) {
            prefs.edit().putString(item.getKey(), String.valueOf((int) clampedValue)).apply();
        } else {
            prefs.edit().putFloat(item.getKey(), clampedValue).apply();
        }
    }

    // List settings
    public String getListValue(ListSettingsItem item) {
        return prefs.getString(item.getKey(), item.getDefaultValue());
    }

    public void setListValue(ListSettingsItem item, String value) {
        prefs.edit().putString(item.getKey(), value).apply();
    }

    public void setString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    // Generic preference access
    public SharedPreferences getPreferences() {
        return prefs;
    }

    /**
     * Check if a dependency is satisfied (for dependent settings).
     */
    public boolean isDependencySatisfied(SettingsItem item) {
        if (!item.hasDependency()) {
            return true;
        }
        return prefs.getBoolean(item.getDependencyKey(), false);
    }
}
