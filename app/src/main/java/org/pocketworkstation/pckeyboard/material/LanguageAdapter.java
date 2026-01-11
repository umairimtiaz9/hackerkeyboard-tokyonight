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

import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.pocketworkstation.pckeyboard.R;

import java.util.List;
import java.util.Set;

/**
 * RecyclerView adapter for language selection.
 * Uses MaterialSwitch with green accent for selected state.
 */
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private final List<LanguageSelectionFragment.LanguageItem> languages;
    private final Set<String> selectedLanguages;
    private final OnLanguageToggleListener listener;

    public interface OnLanguageToggleListener {
        void onLanguageToggled(String languageCode, boolean isSelected);
    }

    public LanguageAdapter(List<LanguageSelectionFragment.LanguageItem> languages,
                          Set<String> selectedLanguages,
                          OnLanguageToggleListener listener) {
        this.languages = languages;
        this.selectedLanguages = selectedLanguages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageSelectionFragment.LanguageItem language = languages.get(position);
        holder.bind(language, selectedLanguages.contains(language.code));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameTextView;
        private final TextView codeTextView;
        private final MaterialSwitch languageSwitch;

        LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            nameTextView = itemView.findViewById(R.id.language_name);
            codeTextView = itemView.findViewById(R.id.language_code);
            languageSwitch = itemView.findViewById(R.id.language_switch);
        }

        void bind(LanguageSelectionFragment.LanguageItem language, boolean isSelected) {
            nameTextView.setText(language.displayName);
            codeTextView.setText(language.localeString);
            languageSwitch.setChecked(isSelected);

            // Get theme-aware primary color
            TypedValue typedValue = new TypedValue();
            itemView.getContext().getTheme().resolveAttribute(
                    com.google.android.material.R.attr.colorPrimary, typedValue, true);
            int primaryColor = typedValue.data;

            // Apply theme-aware accent color when selected, reset to default when not
            updateSwitchColors(isSelected, primaryColor);

            // Handle click
            itemView.setOnClickListener(v -> {
                boolean newState = !languageSwitch.isChecked();
                languageSwitch.setChecked(newState);
                updateSwitchColors(newState, primaryColor);

                if (newState) {
                    selectedLanguages.add(language.code);
                } else {
                    selectedLanguages.remove(language.code);
                }

                listener.onLanguageToggled(language.code, newState);
            });

            // Handle switch click
            languageSwitch.setOnClickListener(v -> {
                boolean newState = languageSwitch.isChecked();
                updateSwitchColors(newState, primaryColor);

                if (newState) {
                    selectedLanguages.add(language.code);
                } else {
                    selectedLanguages.remove(language.code);
                }

                listener.onLanguageToggled(language.code, newState);
            });
        }

        private void updateSwitchColors(boolean isSelected, int primaryColor) {
            if (isSelected) {
                languageSwitch.setThumbTintList(ColorStateList.valueOf(primaryColor));
                languageSwitch.setTrackTintList(ColorStateList.valueOf(primaryColor).withAlpha(128));
                cardView.setStrokeWidth(2);
                cardView.setStrokeColor(primaryColor);
            } else {
                // Reset to default Material colors - get colorOnSurface for thumb and colorSurfaceVariant for track
                TypedValue thumbValue = new TypedValue();
                TypedValue trackValue = new TypedValue();
                itemView.getContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorOnSurfaceVariant, thumbValue, true);
                itemView.getContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorSurfaceVariant, trackValue, true);

                // Create proper color state lists for unchecked state
                int[][] states = new int[][] {
                    new int[] { android.R.attr.state_checked },
                    new int[] { -android.R.attr.state_checked }
                };
                int[] thumbColors = new int[] { primaryColor, thumbValue.data };
                int[] trackColors = new int[] { (primaryColor & 0x00FFFFFF) | 0x80000000, trackValue.data };

                languageSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
                languageSwitch.setTrackTintList(new ColorStateList(states, trackColors));

                cardView.setStrokeWidth(1);
                // Get outline color from theme for unselected state
                TypedValue outlineValue = new TypedValue();
                itemView.getContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorOutline, outlineValue, true);
                cardView.setStrokeColor(outlineValue.data);
            }
        }
    }
}
