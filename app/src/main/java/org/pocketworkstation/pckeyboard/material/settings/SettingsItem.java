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
 * Base class for all settings items in the Material Settings UI.
 * Uses a data-oriented design for easy configuration and maintenance.
 */
public abstract class SettingsItem {

    public enum Type {
        BOOLEAN,    // Switch/Toggle
        SLIDER,     // SeekBar/Slider
        LIST,       // Dropdown/List selection
        TEXT        // Text input
    }

    private final String key;
    private final String title;
    private final String summary;
    private final Type type;
    private final String dependencyKey;

    protected SettingsItem(String key, String title, String summary, Type type, String dependencyKey) {
        this.key = key;
        this.title = title;
        this.summary = summary;
        this.type = type;
        this.dependencyKey = dependencyKey;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public Type getType() {
        return type;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public boolean hasDependency() {
        return dependencyKey != null && !dependencyKey.isEmpty();
    }
}
