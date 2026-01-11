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
 * Settings item for boolean (switch/toggle) preferences.
 */
public class BooleanSettingsItem extends SettingsItem {

    private final boolean defaultValue;
    private final String summaryOn;
    private final String summaryOff;

    public BooleanSettingsItem(String key, String title, String summary, boolean defaultValue) {
        this(key, title, summary, defaultValue, null, null, null);
    }

    public BooleanSettingsItem(String key, String title, String summary, boolean defaultValue,
                               String summaryOn, String summaryOff) {
        this(key, title, summary, defaultValue, summaryOn, summaryOff, null);
    }

    public BooleanSettingsItem(String key, String title, String summary, boolean defaultValue,
                               String summaryOn, String summaryOff, String dependencyKey) {
        super(key, title, summary, Type.BOOLEAN, dependencyKey);
        this.defaultValue = defaultValue;
        this.summaryOn = summaryOn;
        this.summaryOff = summaryOff;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public String getSummaryOn() {
        return summaryOn;
    }

    public String getSummaryOff() {
        return summaryOff;
    }

    public boolean hasDynamicSummary() {
        return summaryOn != null || summaryOff != null;
    }

    public String getSummaryForState(boolean isChecked) {
        if (isChecked && summaryOn != null) {
            return summaryOn;
        } else if (!isChecked && summaryOff != null) {
            return summaryOff;
        }
        return getSummary();
    }
}
