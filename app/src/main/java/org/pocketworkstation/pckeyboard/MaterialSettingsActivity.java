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

package org.pocketworkstation.pckeyboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.pocketworkstation.pckeyboard.material.ThemeSelectionFragment;
import org.pocketworkstation.pckeyboard.material.LanguageSelectionFragment;
import org.pocketworkstation.pckeyboard.material.InputBehaviorFragment;
import org.pocketworkstation.pckeyboard.material.VisualAppearanceFragment;
import org.pocketworkstation.pckeyboard.material.FeedbackFragment;
import org.pocketworkstation.pckeyboard.material.GesturesFragment;

/**
 * Material 3 Settings Activity with Tokyo Night theme support.
 * Provides a modern, tabbed interface for keyboard configuration.
 * Optimized for smooth scrolling and instant theme changes.
 */
public class MaterialSettingsActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MaterialSettings";
    private static final int NUM_PAGES = 6;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SettingsPagerAdapter pagerAdapter;
    private int currentThemeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply Tokyo Night theme based on user preference
        currentThemeId = getThemeIdFromPrefs();
        setTheme(currentThemeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_settings);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Keyboard Settings");
        }

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Setup adapter
        pagerAdapter = new SettingsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Performance optimization: Keep all fragments in memory to prevent flickering
        viewPager.setOffscreenPageLimit(NUM_PAGES);

        // Add smooth fade crossfade transformer for professional transitions
        viewPager.setPageTransformer(new FadeCrossfadeTransformer());

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Theme");
                    tab.setIcon(R.drawable.ic_palette);
                    break;
                case 1:
                    tab.setText("Language");
                    tab.setIcon(R.drawable.ic_language);
                    break;
                case 2:
                    tab.setText("Input");
                    tab.setIcon(R.drawable.ic_keyboard);
                    break;
                case 3:
                    tab.setText("Visual");
                    tab.setIcon(R.drawable.ic_visibility);
                    break;
                case 4:
                    tab.setText("Feedback");
                    tab.setIcon(R.drawable.ic_vibration);
                    break;
                case 5:
                    tab.setText("Gestures");
                    tab.setIcon(R.drawable.ic_gesture);
                    break;
            }
        }).attach();

        // Register preference change listener for instant theme updates
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister preference change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Instant theme change when user selects a new theme
        if (KeyboardSwitcher.PREF_KEYBOARD_LAYOUT.equals(key)) {
            int newThemeId = getThemeIdFromPrefs();
            if (newThemeId != currentThemeId) {
                currentThemeId = newThemeId;
                // Recreate activity to apply new theme instantly
                recreate();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the theme resource ID based on user preference.
     */
    private int getThemeIdFromPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String layoutPref = prefs.getString(KeyboardSwitcher.PREF_KEYBOARD_LAYOUT, "10");

        try {
            int layoutId = Integer.parseInt(layoutPref);
            switch (layoutId) {
                case 11: // Night
                    return R.style.Theme_HackerKeyboard_Material3_Night;
                case 12: // Day
                    return R.style.Theme_HackerKeyboard_Material3_Day;
                case 13: // Moon
                    return R.style.Theme_HackerKeyboard_Material3_Moon;
                case 10: // Storm (default)
                default:
                    return R.style.Theme_HackerKeyboard_Material3_Storm;
            }
        } catch (NumberFormatException e) {
            return R.style.Theme_HackerKeyboard_Material3_Storm;
        }
    }

    /**
     * Smooth fade crossfade page transformer for ViewPager2.
     * Creates a professional fade transition between pages.
     */
    private static class FadeCrossfadeTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(@NonNull View page, float position) {
            if (position < -1 || position > 1) {
                // Page is off-screen
                page.setAlpha(0f);
            } else if (position <= 0) {
                // Page is moving out to the left or is the current page
                page.setAlpha(1 + position);
                page.setTranslationX(0f);
            } else {
                // Page is moving in from the right
                page.setAlpha(1 - position);
                page.setTranslationX(0f);
            }
        }
    }

    /**
     * FragmentStateAdapter for managing the 6 settings screens.
     */
    private static class SettingsPagerAdapter extends FragmentStateAdapter {

        public SettingsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ThemeSelectionFragment();
                case 1:
                    return new LanguageSelectionFragment();
                case 2:
                    return new InputBehaviorFragment();
                case 3:
                    return new VisualAppearanceFragment();
                case 4:
                    return new FeedbackFragment();
                case 5:
                    return new GesturesFragment();
                default:
                    return new ThemeSelectionFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
