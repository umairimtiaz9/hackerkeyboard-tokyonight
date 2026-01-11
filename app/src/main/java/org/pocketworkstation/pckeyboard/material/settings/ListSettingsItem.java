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
 * Settings item for list/dropdown preferences.
 */
public class ListSettingsItem extends SettingsItem {

    private final String[] entries;      // Display labels
    private final String[] entryValues;  // Stored values
    private final String defaultValue;

    public ListSettingsItem(String key, String title, String summary,
                            String[] entries, String[] entryValues, String defaultValue) {
        this(key, title, summary, entries, entryValues, defaultValue, null);
    }

    public ListSettingsItem(String key, String title, String summary,
                            String[] entries, String[] entryValues, String defaultValue,
                            String dependencyKey) {
        super(key, title, summary, Type.LIST, dependencyKey);
        this.entries = entries;
        this.entryValues = entryValues;
        this.defaultValue = defaultValue;
    }

    public String[] getEntries() {
        return entries;
    }

    public String[] getEntryValues() {
        return entryValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the display label for a stored value.
     */
    public String getEntryForValue(String value) {
        if (entries == null || entryValues == null) return value;
        for (int i = 0; i < entryValues.length; i++) {
            if (entryValues[i].equals(value)) {
                return entries[i];
            }
        }
        return value;
    }

    /**
     * Get the index of a stored value.
     */
    public int getIndexForValue(String value) {
        if (entryValues == null) return -1;
        for (int i = 0; i < entryValues.length; i++) {
            if (entryValues[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
