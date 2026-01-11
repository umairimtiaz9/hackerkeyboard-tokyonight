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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.pocketworkstation.pckeyboard.R;

import java.util.List;

/**
 * RecyclerView adapter for Tokyo Night theme selection.
 * Uses RadioButton with theme-specific accent colors for selected state.
 */
public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private final List<ThemeSelectionFragment.ThemeItem> themes;
    private String selectedThemeId;
    private final OnThemeSelectedListener listener;

    public interface OnThemeSelectedListener {
        void onThemeSelected(String themeId);
    }

    public ThemeAdapter(List<ThemeSelectionFragment.ThemeItem> themes,
                       String selectedThemeId,
                       OnThemeSelectedListener listener) {
        this.themes = themes;
        this.selectedThemeId = selectedThemeId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theme, parent, false);
        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        ThemeSelectionFragment.ThemeItem theme = themes.get(position);
        holder.bind(theme, theme.id.equals(selectedThemeId));
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private final RadioButton themeRadio;
        private final View colorBase;
        private final View colorAlpha;
        private final View colorMod;
        private final View colorHighlight;
        private final View colorText;
        private final View colorAccent;

        ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            nameTextView = itemView.findViewById(R.id.theme_name);
            descriptionTextView = itemView.findViewById(R.id.theme_description);
            themeRadio = itemView.findViewById(R.id.theme_radio);
            colorBase = itemView.findViewById(R.id.color_base);
            colorAlpha = itemView.findViewById(R.id.color_alpha);
            colorMod = itemView.findViewById(R.id.color_mod);
            colorHighlight = itemView.findViewById(R.id.color_highlight);
            colorText = itemView.findViewById(R.id.color_text);
            colorAccent = itemView.findViewById(R.id.color_accent);
        }

        void bind(ThemeSelectionFragment.ThemeItem theme, boolean isSelected) {
            nameTextView.setText(theme.name);
            descriptionTextView.setText(theme.description);

            // Set radio button state with theme-specific accent color
            themeRadio.setChecked(isSelected);

            // Get theme-specific accent color
            int accentColor = getThemeAccentColor(theme.id);

            if (isSelected) {
                // Apply theme-specific accent color to radio button
                themeRadio.setButtonTintList(ColorStateList.valueOf(accentColor));
            } else {
                // Default gray for unselected
                themeRadio.setButtonTintList(null);
            }

            // Set 6-color preview grid
            if (colorBase != null) {
                colorBase.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.baseColorRes)));
            }
            if (colorAlpha != null) {
                colorAlpha.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.alphaColorRes)));
            }
            if (colorMod != null) {
                colorMod.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.modColorRes)));
            }
            if (colorHighlight != null) {
                colorHighlight.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.highlightColorRes)));
            }
            if (colorText != null) {
                colorText.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.textColorRes)));
            }
            if (colorAccent != null) {
                colorAccent.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), theme.accentColorRes)));
            }

            // Set card selection state with theme-specific accent border
            if (isSelected) {
                cardView.setStrokeWidth(3);
                cardView.setStrokeColor(accentColor);
            } else {
                cardView.setStrokeWidth(1);
                // Get outline color from theme for unselected state
                TypedValue outlineValue = new TypedValue();
                itemView.getContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorOutline, outlineValue, true);
                cardView.setStrokeColor(outlineValue.data);
            }

            // Handle click
            itemView.setOnClickListener(v -> {
                if (!theme.id.equals(selectedThemeId)) {
                    String oldThemeId = selectedThemeId;
                    selectedThemeId = theme.id;

                    // Notify adapter to update UI
                    notifyItemChanged(getAdapterPosition());
                    for (int i = 0; i < themes.size(); i++) {
                        if (themes.get(i).id.equals(oldThemeId)) {
                            notifyItemChanged(i);
                            break;
                        }
                    }

                    // Notify listener
                    listener.onThemeSelected(theme.id);
                }
            });
        }

        /**
         * Get theme-specific accent color for the radio button and border.
         * Storm = Green, Night = Blue, Moon = Pink, Day = Purple
         */
        private int getThemeAccentColor(String themeId) {
            switch (themeId) {
                case "10": // Storm - Green
                    return ContextCompat.getColor(itemView.getContext(), R.color.tn_storm_green);
                case "11": // Night - Blue
                    return ContextCompat.getColor(itemView.getContext(), R.color.tn_night_blue);
                case "13": // Moon - Pink/Magenta
                    return ContextCompat.getColor(itemView.getContext(), R.color.tn_moon_purple);
                case "12": // Day - Purple
                    return ContextCompat.getColor(itemView.getContext(), R.color.tn_day_magenta);
                default:
                    return ContextCompat.getColor(itemView.getContext(), R.color.tn_storm_green);
            }
        }
    }
}
