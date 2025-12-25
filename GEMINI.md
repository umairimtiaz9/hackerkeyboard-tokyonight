# Hacker's Keyboard: Tokyo Night Edition

## Project Overview
We are pivoting from a generic "Modern IDE" theme to a high-fidelity **Tokyo Night** implementation. The goal is to replicate the exact layout precision of the original 5-row keyboard while applying the polished, professional aesthetic of the Tokyo Night color scheme (Storm, Night, Day, Moon).

## The "Tokyo Night" Design System

### 1. Visual Strategy
We will support 4 distinct variations based on `folke/tokyonight.nvim`:

| Theme | Background | Alpha Keys | Modifiers | Text/FG | Accent (Arrows) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Storm** | `#1F2335` | `#24283B` | `#1F2335` | `#C0CAF5` | `#BB9AF7` (Purple) |
| **Night** | `#16161E` | `#1A1B26` | `#16161E` | `#C0CAF5` | `#BB9AF7` (Purple) |
| **Day** | `#D0D5E3` | `#E1E2E7` | `#D0D5E3` | `#3760BF` | `#9854F1` (Purple) |
| **Moon** | `#1E2030` | `#222436` | `#1E2030` | `#C8D3F5` | `#C099FF` (Purple) |

### 2. Layout Architecture
*   **Exact Replication**: We will maintain the structure of `kbd_qwerty`, `kbd_full`, and `kbd_full_fn`.
*   **Inset Gutter System**: Instead of complex `gap` attributes, we will use a global `android:inset="1dp"` on key drawables. This creates a uniform **2dp gutter** between all keys, ensuring the "tiled" look matches the reference screenshots exactly.
*   **Monospace**: All text (labels, popups, candidates) will strictly use `Typeface.MONOSPACE`.

### 3. "Glamorous" Popups
*   **Shape**: Rounded corners (12dp radius) with vibrant strokes.
*   **Motion**: Scale-up, slide-up, and fade-in animation (`res/anim/popup_enter_modern.xml`).
*   **Positioning**: Negative offset (-8dp) to float above the key.

## Implementation Plan

### Phase 1: Foundation (Colors & Styles) [COMPLETE]
*   **Step 1.1**: Define the 4 palettes in `colors.xml`.
*   **Step 1.2**: Create the "Master Selector" logic in `drawables/`.

### Phase 2: The Cleanup (The Great Purge) [COMPLETE]
*   **Step 2.1**: Delete all legacy `input_*.xml` files.
*   **Step 2.2**: Create 4 unified layout files.
*   **Step 2.3**: Delete all obsolete bitmap assets and unused XML selectors.

### Phase 3: Layout Hardening [COMPLETE]
*   **Step 3.1**: Audit `kbd_full.xml` for `isModifier="true"`.
*   **Step 3.2**: Implement the "Glamorous" popup styles and animations.

### Phase 4: Final Polish [COMPLETE]
*   **Step 4.1**: Build and verify.
*   **Step 4.2**: Neutralize any remaining icons to match the theme's text color.
*   **Step 4.3**: Implement high-fidelity "Orgasmic" fluid popups and surgical coloring.

### Phase 5: Data-Oriented Refactor (The "Heart Transplant")
**Risk Level**: HIGH. This phase involves decoupling the UI from hardcoded values, which may cause crashes if a single attribute reference is missing.

*   **Step 5.1: Attribute Contract Definition**
    *   Create `res/values/attrs.xml` defining the semantic "Interface" for the keyboard theme (e.g., `kbdColorBase`, `kbdColorAlpha`, `kbdColorAccent`).
    *   **Goal**: Establish a vocabulary that describes *role* (What it is), not *color* (What it looks like).

*   **Step 5.2: The Color Dump**
    *   Populate `res/values/colors.xml` with the FULL authoritative palette from `tokyonight.nvim`.
    *   Naming convention: `tn_[theme]_[role]` (e.g., `tn_storm_key_mod`, `tn_night_popup_border`).
    *   **Goal**: Create the "Database" of raw hex values.

*   **Step 5.3: The "Storm" Pilot**
    *   Create a test Style in `res/values/styles.xml` specifically for Storm that maps the new Attributes -> New Colors.
    *   Refactor **only** the Storm drawable selectors (`btn_key_storm.xml`) to use `?attr/kbdColor...` instead of `@color/tn_storm...`.
    *   **Goal**: Prove that the attribute system works for one theme without breaking the others.

*   **Step 5.4: Java Logic Integration**
    *   Modify `LatinKeyboardBaseView.java` to fetch colors dynamically.
    *   Replace manual `Paint.setColor()` calls with attribute resolution logic (`context.obtainStyledAttributes`).
    *   **Goal**: Ensure symbols, arrows, and custom drawn elements respect the theme switch.

*   **Step 5.5: Full Rollout**
    *   Once the Pilot passes, apply the logic to Night, Day, and Moon.
    *   Create the final `themes.xml` variants.

## Contextual History
*   **Past**: A "mess" of 14+ themes, inconsistent spacing, and mixed assets.
*   **Present**: Complete Tokyo Night overhaul with 4 variations. Fluid, high-performance popups with modern animations.
*   **Future**: A robust, data-driven architecture allowing instant theme switching and professional maintenance.

## TODO: Future Modernization Plans
- [x] **Pixel-Perfect Geometry**: Set `key_bottom_gap` and `key_horizontal_gap` to `0dp` and use strict `1dp` insets to guarantee a uniform `2dp` gutter across all keys.
- [x] **Soft-Rect Aesthetic**: Update corner radii to `6dp` for a more modern, premium mobile feel.
- [x] **Tonal Elevation**: Refine the color contrast so Alphas and Modifiers use "Surface Tones" for a more unified look.
- [x] **Typographic Hierarchy**: Force `Typeface.BOLD` for main labels and significantly reduce the opacity/size of "Hint" characters to reduce visual clutter.
- [x] **Interaction Depth**: Enhance the "pop-up" animations with scale-up transitions.
- [x] **Data-Oriented Colors**: Move from hardcoded XML colors to a centralized attribute-based system.
- [x] **Official Alignment**: Sync all hex values with the latest `tokyonight.nvim` releases.
- [ ] **More to come**

## Project Knowledge Base

### 1. Theme Architecture (Data-Oriented)
The project has been migrated from a legacy hardcoded color system to a modern, semantic attribute-driven architecture.

#### Semantic Attributes (`attrs.xml`)
We use roles to define colors, allowing themes to inject their own palettes:
- `kbdColorBase`: The main background of the keyboard tray.
- `kbdColorAlpha`: Background for standard character keys.
- `kbdColorMod`: Background for functional keys (Shift, Ctrl, Alt).
- `kbdColorHighlight`: Background for Pressed or Sticky (Locked) states.
- `kbdColorText`: Color for all labels and icons.
- `kbdColorAccent`: Specifically used for cursor/arrow keys.
- `kbdColorPopup`: Border/Stroke color for popups.

#### Style Inheritance (`styles.xml`)
- `LatinKeyboardBaseView`: The master style defining common dimensions and defaults.
- `Theme.TokyoNight.Storm`: The base Tokyo Night theme from which others inherit.
- `Theme.TokyoNight.[Night|Moon|Day]`: Variants that override the 7 core semantic attributes.

### 2. Unified Asset System
Instead of maintaining dozens of bitmap nine-patches, we use a single, high-fidelity XML drawable system.

#### Key Selection Logic (`btn_key_tokyonight.xml`)
Uses `inset` with `1dp` margins to create a consistent `2dp` gutter.
- `state_pressed="true"` / `state_checked="true"` -> `?attr/kbdColorHighlight`
- `state_single="true"` (Modifier role) -> `?attr/kbdColorMod`
- Default -> `?attr/kbdColorAlpha`

#### Vector Iconography (`sym_keyboard_*.xml`)
All keyboard icons are now tinted using `?attr/kbdColorText` to ensure they adapt to light (Day) and dark themes automatically.

### 3. Java Logic Integration
- **Theme Injection**: `KeyboardSwitcher.java` now uses `ContextThemeWrapper(context, STYLES[layoutId])` before inflating the keyboard layout. This ensures that `?attr` references are correctly resolved to the active theme's colors.
- **Dynamic Drawing**: `LatinKeyboardBaseView.java` has been updated to use `mKeyCursorColor` for keys marked with `isCursor="true"`, enabling the purple arrow aesthetic.
- **Monospace Enforcement**: The `LatinKeyboardBaseView` constructor forces `Typeface.MONOSPACE` and `Typeface.BOLD` based on the `keyTextStyle` attribute.

### 4. Layout & Geometry
- **Gutter System**: Strictly `0dp` gap in XML layouts, with `1dp` insets in the drawables.
- **Popup Physics**:
    - `keyPreviewOffset`: `-8dp` (Floats above the key).
    - `keyPreviewHeight`: `80dp` (Modern vertical tab look).
    - `corners`: `12dp` radius for popups, `6dp` for keys.

### 5. Theme Mapping (Internal IDs)
- `10`: Storm (Default)
- `11`: Night
- `12`: Day
- `13`: Moon
