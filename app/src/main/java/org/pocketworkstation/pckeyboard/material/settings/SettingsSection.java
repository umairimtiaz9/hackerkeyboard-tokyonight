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
import java.util.Collections;
import java.util.List;

/**
 * Represents a section/category of settings in the Material Settings UI.
 * Groups related settings items together with a title.
 */
public class SettingsSection {

    private final String title;
    private final List<SettingsItem> items;

    public SettingsSection(String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }

    public SettingsSection(String title, List<SettingsItem> items) {
        this.title = title;
        this.items = new ArrayList<>(items);
    }

    public String getTitle() {
        return title;
    }

    public List<SettingsItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(SettingsItem item) {
        items.add(item);
    }

    public int getItemCount() {
        return items.size();
    }

    /**
     * Builder for creating SettingsSection instances.
     */
    public static class Builder {
        private final String title;
        private final List<SettingsItem> items = new ArrayList<>();

        public Builder(String title) {
            this.title = title;
        }

        public Builder addBoolean(String key, String title, String summary, boolean defaultValue) {
            items.add(new BooleanSettingsItem(key, title, summary, defaultValue));
            return this;
        }

        public Builder addBoolean(String key, String title, String summary, boolean defaultValue,
                                  String summaryOn, String summaryOff) {
            items.add(new BooleanSettingsItem(key, title, summary, defaultValue, summaryOn, summaryOff));
            return this;
        }

        public Builder addBoolean(String key, String title, String summary, boolean defaultValue,
                                  String summaryOn, String summaryOff, String dependencyKey) {
            items.add(new BooleanSettingsItem(key, title, summary, defaultValue, summaryOn, summaryOff, dependencyKey));
            return this;
        }

        public Builder addSlider(String key, String title, String summary,
                                 float min, float max, float step, float defaultValue,
                                 boolean storeAsString, String displayFormat) {
            items.add(new SliderSettingsItem(key, title, summary, min, max, step, defaultValue,
                    storeAsString, displayFormat));
            return this;
        }

        public Builder addSlider(String key, String title, String summary,
                                 float min, float max, float step, float defaultValue,
                                 boolean storeAsString, boolean displayAsPercent, String displayFormat,
                                 String dependencyKey) {
            items.add(new SliderSettingsItem(key, title, summary, min, max, step, defaultValue,
                    storeAsString, displayAsPercent, displayFormat, dependencyKey));
            return this;
        }

        public Builder addList(String key, String title, String summary,
                               String[] entries, String[] entryValues, String defaultValue) {
            items.add(new ListSettingsItem(key, title, summary, entries, entryValues, defaultValue));
            return this;
        }

        public Builder addItem(SettingsItem item) {
            items.add(item);
            return this;
        }

        public SettingsSection build() {
            return new SettingsSection(title, items);
        }
    }
}
