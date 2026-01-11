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

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.pocketworkstation.pckeyboard.LatinIME;
import org.pocketworkstation.pckeyboard.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Fragment for selecting keyboard input languages.
 */
public class LanguageSelectionFragment extends Fragment {

    private static final String[] KBD_LOCALIZATIONS = {
        "ar", "bg", "bg_ST", "ca", "cs", "cs_QY", "da", "de", "de_NE",
        "el", "en", "en_CX", "en_DV", "en_GB", "es", "es_LA", "es_US",
        "fa", "fi", "fr", "fr_CA", "he", "hr", "hu", "hu_QY", "hy", "in",
        "it", "iw", "ja", "ka", "ko", "lo", "lt", "lv", "nb", "nl", "pl",
        "pt", "pt_PT", "rm", "ro", "ru", "ru_PH", "si", "sk", "sk_QY", "sl",
        "sr", "sv", "ta", "th", "tl", "tr", "uk", "vi", "zh_CN", "zh_TW"
    };

    private static final String[] BLACKLIST_LANGUAGES = {"ko", "ja", "zh"};

    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private SharedPreferences prefs;
    private List<LanguageItem> languages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        recyclerView = view.findViewById(R.id.language_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load languages
        languages = getAvailableLanguages();

        // Get selected languages
        String selectedLanguagePref = prefs.getString(LatinIME.PREF_SELECTED_LANGUAGES, "");
        Set<String> selectedLanguages = new HashSet<>(Arrays.asList(selectedLanguagePref.split(",")));

        adapter = new LanguageAdapter(languages, selectedLanguages, this::onLanguageToggled);
        recyclerView.setAdapter(adapter);
    }

    private void onLanguageToggled(String languageCode, boolean isSelected) {
        String selectedLanguagePref = prefs.getString(LatinIME.PREF_SELECTED_LANGUAGES, "");
        Set<String> selectedLanguages = new HashSet<>(Arrays.asList(selectedLanguagePref.split(",")));

        if (isSelected) {
            selectedLanguages.add(languageCode);
        } else {
            selectedLanguages.remove(languageCode);
        }

        // Remove empty strings
        selectedLanguages.remove("");

        // Save to preferences
        StringBuilder sb = new StringBuilder();
        for (String lang : selectedLanguages) {
            if (sb.length() > 0) sb.append(",");
            sb.append(lang);
        }

        prefs.edit()
                .putString(LatinIME.PREF_SELECTED_LANGUAGES, sb.toString())
                .apply();
    }

    private List<LanguageItem> getAvailableLanguages() {
        List<LanguageItem> result = new ArrayList<>();
        List<Loc> uniqueLocales = getUniqueLocales();

        for (Loc loc : uniqueLocales) {
            String code = get5Code(loc.locale);
            result.add(new LanguageItem(code, loc.label, loc.locale.toString()));
        }

        return result;
    }

    private List<Loc> getUniqueLocales() {
        String[] locales = KBD_LOCALIZATIONS;
        Arrays.sort(locales);

        List<Loc> uniqueLocaleList = new ArrayList<>();
        Set<String> blacklist = new HashSet<>(Arrays.asList(BLACKLIST_LANGUAGES));

        for (String localeStr : locales) {
            Locale locale = stringToLocale(localeStr);
            if (locale == null || blacklist.contains(locale.getLanguage())) continue;

            String displayName = getDisplayName(locale);
            if (displayName != null) {
                uniqueLocaleList.add(new Loc(displayName, locale));
            }
        }

        uniqueLocaleList.sort((a, b) -> Collator.getInstance().compare(a.label, b.label));
        return uniqueLocaleList;
    }

    private static Locale stringToLocale(String localeString) {
        String[] parts = localeString.split("_", 3);
        if (parts.length == 1) {
            return new Locale(parts[0]);
        } else if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new Locale(parts[0], parts[1], parts[2]);
        }
        return null;
    }

    private String getDisplayName(Locale locale) {
        Resources res = requireContext().getResources();
        Configuration conf = res.getConfiguration();
        Locale saveLocale = conf.locale;
        conf.locale = locale;
        res.updateConfiguration(conf, null);

        String displayName = locale.getDisplayName(locale);

        conf.locale = saveLocale;
        res.updateConfiguration(conf, null);

        return displayName;
    }

    private static String get5Code(Locale locale) {
        String country = locale.getCountry();
        return locale.getLanguage() + (country.isEmpty() ? "" : "_" + country);
    }

    private static class Loc {
        public final String label;
        public final Locale locale;

        Loc(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }
    }

    public static class LanguageItem {
        public final String code;
        public final String displayName;
        public final String localeString;

        public LanguageItem(String code, String displayName, String localeString) {
            this.code = code;
            this.displayName = displayName;
            this.localeString = localeString;
        }
    }
}
