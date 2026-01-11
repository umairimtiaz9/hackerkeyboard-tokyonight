# Hacker's Keyboard - Layout Files Analysis Report

## Executive Summary

**Total Layout Files Found:** 20
**Referenced in Java Code:** 16
**Unused/Legacy:** 4

This report categorizes all XML layout files in `/app/src/main/res/layout/` by their purpose and usage status.

---

## CATEGORY 1: KEYBOARD (Core Keyboard UI - MUST KEEP)

These layouts are essential for the keyboard's core functionality and rendering.

### 1. `input_tokyonight_dynamic.xml` (16 lines)
- **Purpose:** Main keyboard input view container
- **Used By:** `KeyboardSwitcher.java` (THEMES array, all 14 theme variants)
- **Status:** ACTIVE - Core keyboard layout
- **References:** Referenced in styles.xml as the primary keyboard layout
- **Dependency Chain:** 
  - Inflates `LatinKeyboardView` custom view
  - References `popup_tokyonight_dynamic` and `preview_tokyonight_dynamic` layouts
  - Used by `LatinIME.java` for keyboard rendering

### 2. `popup_tokyonight_dynamic.xml` (17 lines)
- **Purpose:** Container for long-press popup mini-keyboard
- **Used By:** `input_tokyonight_dynamic.xml` (via `popupLayout` attribute)
- **Status:** ACTIVE - Core keyboard feature
- **References:** Referenced in styles.xml and input_tokyonight_dynamic.xml
- **Dependency Chain:**
  - Contains `LatinKeyboardBaseView` for popup key rendering
  - Used when user long-presses a key to show alternate characters
  - Referenced in `LatinKeyboardView.java` (line 140, 147)

### 3. `preview_tokyonight_dynamic.xml` (17 lines)
- **Purpose:** Key preview/popup display when key is pressed
- **Used By:** `input_tokyonight_dynamic.xml` (via `keyPreviewLayout` attribute)
- **Status:** ACTIVE - Core keyboard feature
- **References:** Referenced in styles.xml and input_tokyonight_dynamic.xml
- **Dependency Chain:**
  - Simple TextView for displaying pressed key character
  - Uses `popup_tokyonight_dynamic` drawable as background
  - Referenced in `LatinKeyboardView.java` (line 139)

### 4. `candidates.xml` (38 lines)
- **Purpose:** Suggestion/candidate bar container
- **Used By:** `LatinIME.java` (inflated for candidate view)
- **Status:** ACTIVE - Core keyboard feature
- **References:** Contains custom `CandidateView` component
- **Dependency Chain:**
  - Displays word suggestions while typing
  - Part of main keyboard UI layout hierarchy

### 5. `candidate_preview.xml` (29 lines)
- **Purpose:** Individual candidate/suggestion item preview
- **Used By:** `CandidateView.java` (for popup preview of suggestions)
- **Status:** ACTIVE - Core keyboard feature
- **References:** Used by CandidateView for showing suggestion previews

### 6. `null_layout.xml` (2 lines)
- **Purpose:** Empty placeholder layout for optional UI elements
- **Used By:** `LatinKeyboardView.java` (lines 140, 147 - checked as null_layout)
- **Status:** ACTIVE - Utility layout
- **References:** Used as a sentinel value to disable optional layouts
- **Note:** This is a special marker layout, not meant to be inflated

---

## CATEGORY 2: MATERIAL3 (New Material 3 Settings UI - MUST KEEP)

These layouts implement the modern Material 3 design for the settings interface.

### 7. `activity_material_settings.xml` (40 lines)
- **Purpose:** Main settings activity container with TabLayout and ViewPager2
- **Used By:** `MaterialSettingsActivity.java` (line 66, setContentView)
- **Status:** ACTIVE - Material 3 Settings UI
- **References:** Main activity layout for tabbed settings interface
- **Dependency Chain:**
  - Contains AppBarLayout with MaterialToolbar
  - Contains TabLayout for 6 settings tabs
  - Contains ViewPager2 for fragment switching
  - Parent of all fragment layouts below

### 8. `activity_material_main.xml` (408 lines)
- **Purpose:** Welcome/main activity with setup instructions and test area
- **Used By:** `MaterialMainActivity.java` (line 58, setContentView)
- **Status:** ACTIVE - Material 3 Main UI
- **References:** Main welcome screen for first-time setup
- **Dependency Chain:**
  - Contains setup cards with buttons
  - Contains test input field
  - Contains about/GitHub link section

### 9. `fragment_theme_selection.xml` (38 lines)
- **Purpose:** Theme selection settings fragment
- **Used By:** `ThemeSelectionFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 0 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains RecyclerView for theme list
  - Uses `item_theme.xml` for each theme item

### 10. `fragment_language_selection.xml` (38 lines)
- **Purpose:** Language selection settings fragment
- **Used By:** `LanguageSelectionFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 1 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains RecyclerView for language list
  - Uses `item_language.xml` for each language item

### 11. `fragment_input_behavior.xml` (1011 lines)
- **Purpose:** Input behavior settings (text correction, key behavior, modifiers)
- **Used By:** `InputBehaviorFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 2 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains multiple MaterialCardView sections
  - Contains switches, sliders, and dropdown menus
  - Largest fragment layout

### 12. `fragment_visual_appearance.xml` (683 lines)
- **Purpose:** Visual appearance settings (display options, keyboard layout, size)
- **Used By:** `VisualAppearanceFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 3 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains display options, layout modes, size sliders
  - Contains hint display mode options

### 13. `fragment_feedback.xml` (317 lines)
- **Purpose:** Feedback settings (haptic and audio feedback)
- **Used By:** `FeedbackFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 4 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains vibration settings and sliders
  - Contains sound settings and volume controls

### 14. `fragment_gestures.xml` (386 lines)
- **Purpose:** Gesture and hardware key settings
- **Used By:** `GesturesFragment.java` (onCreateView)
- **Status:** ACTIVE - Material 3 Settings Fragment
- **References:** Tab 5 in MaterialSettingsActivity
- **Dependency Chain:**
  - Contains swipe gesture dropdowns
  - Contains hardware key action dropdowns

### 15. `item_theme.xml` (122 lines)
- **Purpose:** Individual theme selection card item
- **Used By:** `ThemeAdapter.java` (onBindViewHolder)
- **Status:** ACTIVE - Material 3 RecyclerView Item
- **References:** Used in fragment_theme_selection.xml RecyclerView
- **Dependency Chain:**
  - Contains theme name, description, radio button
  - Contains 6-color preview grid

### 16. `item_language.xml` (55 lines)
- **Purpose:** Individual language selection card item
- **Used By:** `LanguageAdapter.java` (onBindViewHolder)
- **Status:** ACTIVE - Material 3 RecyclerView Item
- **References:** Used in fragment_language_selection.xml RecyclerView
- **Dependency Chain:**
  - Contains language name, code, and toggle switch

### 17. `item_dropdown.xml` (12 lines)
- **Purpose:** Dropdown menu item layout for Material3 AutoCompleteTextView
- **Used By:** Various fragment layouts (dropdown menus)
- **Status:** ACTIVE - Material 3 Utility Layout
- **References:** Used as dropdown item template in multiple fragments

---

## CATEGORY 3: LEGACY (Old UI Layouts - CANDIDATES FOR REMOVAL)

These layouts are from the old preference-based settings UI and are no longer actively used.

### 18. `seek_bar_dialog.xml` (42 lines)
- **Purpose:** Legacy SeekBar preference dialog layout
- **Used By:** `SeekBarPreference.java` (onCreateDialogView, line ~100+)
- **Status:** LEGACY - Old Preference System
- **References:** Used by deprecated SeekBarPreference class
- **Note:** Modern Material 3 UI uses Material Slider instead
- **Recommendation:** Can be removed if SeekBarPreference is deprecated

### 19. `key_preview.xml` (29 lines)
- **Purpose:** Legacy key preview layout (old style)
- **Used By:** Not found in current Java code
- **Status:** LEGACY - Unused
- **References:** No active references found
- **Note:** Replaced by `preview_tokyonight_dynamic.xml`
- **Recommendation:** SAFE TO REMOVE

### 20. `recognition_status.xml` (98 lines)
- **Purpose:** Voice recognition status UI (legacy voice input feature)
- **Used By:** Not found in current Java code
- **Status:** LEGACY - Unused
- **References:** No active references found
- **Note:** Voice input feature appears to be removed
- **Recommendation:** SAFE TO REMOVE

---

## SUMMARY TABLE

| Layout File | Category | Status | Lines | Removable |
|---|---|---|---|---|
| input_tokyonight_dynamic.xml | KEYBOARD | ACTIVE | 16 | NO |
| popup_tokyonight_dynamic.xml | KEYBOARD | ACTIVE | 17 | NO |
| preview_tokyonight_dynamic.xml | KEYBOARD | ACTIVE | 17 | NO |
| candidates.xml | KEYBOARD | ACTIVE | 38 | NO |
| candidate_preview.xml | KEYBOARD | ACTIVE | 29 | NO |
| null_layout.xml | KEYBOARD | ACTIVE | 2 | NO |
| activity_material_settings.xml | MATERIAL3 | ACTIVE | 40 | NO |
| activity_material_main.xml | MATERIAL3 | ACTIVE | 408 | NO |
| fragment_theme_selection.xml | MATERIAL3 | ACTIVE | 38 | NO |
| fragment_language_selection.xml | MATERIAL3 | ACTIVE | 38 | NO |
| fragment_input_behavior.xml | MATERIAL3 | ACTIVE | 1011 | NO |
| fragment_visual_appearance.xml | MATERIAL3 | ACTIVE | 683 | NO |
| fragment_feedback.xml | MATERIAL3 | ACTIVE | 317 | NO |
| fragment_gestures.xml | MATERIAL3 | ACTIVE | 386 | NO |
| item_theme.xml | MATERIAL3 | ACTIVE | 122 | NO |
| item_language.xml | MATERIAL3 | ACTIVE | 55 | NO |
| item_dropdown.xml | MATERIAL3 | ACTIVE | 12 | NO |
| seek_bar_dialog.xml | LEGACY | CONDITIONAL | 42 | MAYBE |
| key_preview.xml | LEGACY | UNUSED | 29 | YES |
| recognition_status.xml | LEGACY | UNUSED | 98 | YES |

---

## RECOMMENDATIONS

### SAFE TO REMOVE (No Active References)
1. **key_preview.xml** - Replaced by preview_tokyonight_dynamic.xml
2. **recognition_status.xml** - Voice input feature not in use

### CONDITIONAL REMOVAL
1. **seek_bar_dialog.xml** - Only if SeekBarPreference is deprecated and replaced with Material3 Slider

### MUST KEEP
- All 6 KEYBOARD category layouts (core functionality)
- All 11 MATERIAL3 category layouts (modern settings UI)
- null_layout.xml (utility marker)

---

## DEPENDENCY GRAPH

```
activity_material_settings.xml
├── fragment_theme_selection.xml
│   └── item_theme.xml
├── fragment_language_selection.xml
│   └── item_language.xml
├── fragment_input_behavior.xml
│   └── item_dropdown.xml (multiple)
├── fragment_visual_appearance.xml
│   └── item_dropdown.xml (multiple)
├── fragment_feedback.xml
│   └── (no sub-layouts)
└── fragment_gestures.xml
    └── item_dropdown.xml (multiple)

activity_material_main.xml
└── (no sub-layouts)

input_tokyonight_dynamic.xml (Main Keyboard)
├── popup_tokyonight_dynamic.xml
└── preview_tokyonight_dynamic.xml

candidates.xml
└── candidate_preview.xml
```

---

## CONCLUSION

The project has successfully migrated from a legacy preference-based settings system to a modern Material 3 tabbed interface. The keyboard UI uses a unified Tokyo Night theme system with dynamic layouts.

**Current State:** 17 active layouts, 3 legacy/unused layouts
**Recommended Action:** Remove key_preview.xml and recognition_status.xml to clean up the codebase
