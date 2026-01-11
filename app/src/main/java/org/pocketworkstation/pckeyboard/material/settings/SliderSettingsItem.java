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

/**
 * Settings item for slider/seekbar preferences.
 * Supports both integer and float values, with optional percentage display.
 */
public class SliderSettingsItem extends SettingsItem {

    private final float minValue;
    private final float maxValue;
    private final float stepSize;
    private final float defaultValue;
    private final boolean storeAsString;  // Original prefs use SeekBarPreferenceString
    private final boolean displayAsPercent;
    private final String displayFormat;  // e.g., "%.0f ms" or "%.0f%%"

    public SliderSettingsItem(String key, String title, String summary,
                              float minValue, float maxValue, float stepSize, float defaultValue,
                              boolean storeAsString, String displayFormat) {
        this(key, title, summary, minValue, maxValue, stepSize, defaultValue,
             storeAsString, false, displayFormat, null);
    }

    public SliderSettingsItem(String key, String title, String summary,
                              float minValue, float maxValue, float stepSize, float defaultValue,
                              boolean storeAsString, boolean displayAsPercent, String displayFormat,
                              String dependencyKey) {
        super(key, title, summary, Type.SLIDER, dependencyKey);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.defaultValue = defaultValue;
        this.storeAsString = storeAsString;
        this.displayAsPercent = displayAsPercent;
        this.displayFormat = displayFormat;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getStepSize() {
        return stepSize;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public boolean isStoreAsString() {
        return storeAsString;
    }

    public boolean isDisplayAsPercent() {
        return displayAsPercent;
    }

    public String getDisplayFormat() {
        return displayFormat;
    }

    /**
     * Clamp a value to the valid range.
     */
    public float clamp(float value) {
        return Math.max(minValue, Math.min(maxValue, value));
    }

    /**
     * Format a value for display.
     */
    public String formatValue(float value) {
        if (displayFormat != null) {
            return String.format(displayFormat, value);
        } else if (displayAsPercent) {
            return String.format("%.0f%%", value);
        } else {
            return String.valueOf((int) value);
        }
    }
}
