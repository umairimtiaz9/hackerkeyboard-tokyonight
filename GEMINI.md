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
    - **Safety Insets**: `6dp` transparent insets added to `popup_tokyonight_dynamic.xml` to prevent window-boundary clipping (squaring) in restricted layouts (e.g., browsers).
    - `keyPreviewOffset`: `-14dp` (Effective `-8dp` visual float + `6dp` compensation).
    - `keyPreviewHeight`: `92dp` (Effective `80dp` visual height + `12dp` vertical inset compensation).
    - `corners`: `12dp` radius for popups, `6dp` for keys.

### 5. Theme Mapping (Internal IDs)
- `10`: Storm (Default)
- `11`: Night
- `12`: Day
- `13`: Moon
