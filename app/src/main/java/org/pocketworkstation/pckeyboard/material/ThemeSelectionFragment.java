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

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.pocketworkstation.pckeyboard.KeyboardSwitcher;
import org.pocketworkstation.pckeyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for selecting Tokyo Night theme variants.
 * Shows a loading spinner during theme switch for smooth UX.
 */
public class ThemeSelectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private ThemeAdapter adapter;
    private SharedPreferences prefs;
    private Dialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        recyclerView = view.findViewById(R.id.theme_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Get current theme
        String currentTheme = prefs.getString(KeyboardSwitcher.PREF_KEYBOARD_LAYOUT, "10");

        // Create theme list with prominent Tokyo Night colors for each variant
        List<ThemeItem> themes = new ArrayList<>();
        themes.add(new ThemeItem("10", "Tokyo Night Storm",
                "Deep blue with vibrant accents",
                R.color.tn_storm_bg, R.color.tn_storm_bg_dark, R.color.tn_storm_terminal_black,
                R.color.tn_storm_blue, R.color.tn_storm_fg, R.color.tn_storm_green));
        themes.add(new ThemeItem("11", "Tokyo Night Night",
                "Pure dark with neon highlights",
                R.color.tn_night_bg, R.color.tn_night_bg_dark, R.color.tn_night_terminal_black,
                R.color.tn_night_cyan, R.color.tn_night_fg, R.color.tn_night_magenta));
        themes.add(new ThemeItem("13", "Tokyo Night Moon",
                "Moonlit blue with soft contrast",
                R.color.tn_moon_bg, R.color.tn_moon_bg_dark, R.color.tn_moon_terminal_black,
                R.color.tn_moon_purple, R.color.tn_moon_fg, R.color.tn_moon_green));
        themes.add(new ThemeItem("12", "Tokyo Night Day",
                "Light mode with vibrant colors",
                R.color.tn_day_bg, R.color.tn_day_bg_dark, R.color.tn_day_terminal_black,
                R.color.tn_day_blue, R.color.tn_day_fg, R.color.tn_day_green));

        adapter = new ThemeAdapter(themes, currentTheme, this::onThemeSelected);
        recyclerView.setAdapter(adapter);
    }

    private void onThemeSelected(String themeId) {
        // Show loading spinner - it will stay visible until activity recreates
        showLoadingDialog();

        // Save theme preference
        prefs.edit()
                .putString(KeyboardSwitcher.PREF_KEYBOARD_LAYOUT, themeId)
                .apply();

        // Post recreate to next frame to ensure spinner is visible
        // The dialog will be dismissed automatically when activity is destroyed
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });
    }

    /**
     * Show a modern circular loading spinner during theme switch.
     */
    private void showLoadingDialog() {
        if (getContext() == null) return;

        loadingDialog = new Dialog(requireContext());
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);

        // Create a circular progress indicator
        CircularProgressIndicator progressIndicator = new CircularProgressIndicator(requireContext());
        progressIndicator.setIndeterminate(true);
        // Use theme-aware colorPrimary instead of hardcoded color
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        progressIndicator.setIndicatorColor(typedValue.data);
        progressIndicator.setTrackThickness(8);
        progressIndicator.setIndicatorSize(64);

        // Wrap in a card for better appearance
        MaterialCardView cardView = new MaterialCardView(requireContext());
        cardView.setCardElevation(16);
        cardView.setRadius(24);
        cardView.setContentPadding(48, 48, 48, 48);
        cardView.addView(progressIndicator);

        loadingDialog.setContentView(cardView);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.show();
    }

    /**
     * Dismiss the loading dialog if showing.
     */
    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoadingDialog();
    }

    /**
     * Data class for theme items with 6-color preview.
     */
    public static class ThemeItem {
        public final String id;
        public final String name;
        public final String description;
        public final int baseColorRes;
        public final int alphaColorRes;
        public final int modColorRes;
        public final int highlightColorRes;
        public final int textColorRes;
        public final int accentColorRes;

        public ThemeItem(String id, String name, String description,
                        int baseColorRes, int alphaColorRes, int modColorRes,
                        int highlightColorRes, int textColorRes, int accentColorRes) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.baseColorRes = baseColorRes;
            this.alphaColorRes = alphaColorRes;
            this.modColorRes = modColorRes;
            this.highlightColorRes = highlightColorRes;
            this.textColorRes = textColorRes;
            this.accentColorRes = accentColorRes;
        }
    }
}
