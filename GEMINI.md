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

## Future Roadmap: Modernization Phase II

### 1. Popup Refinement & Polish
- [x] **Geometry Compression**: Shrink popup dimensions to a "Pill" shape (approx. 60dp visual height) for a more professional, compact look.
- [x] **Physics & Curves**: Implement "Spring" or "Overshoot" interpolators for popup animations to eliminate the "clunky" feel.
- [x] **Latency Neutralization**: Eliminate the artificial popup delay (set to 0ms) and implement dynamic long-press triggers for instantaneous feedback.
- [x] **Transparency Fix**: Resolve the "Square/Triangle Background" rendering bug where the area outside the rounded corners becomes visible in certain applications.

### 2. Advanced Modifier States (Ctrl/Alt)
- [ ] **Hardware Aesthetic**: Transition from the "Underline" indicator to a "Mechanical LED" dot in the corner of the key or a "Neon Wireframe" stroke for active states.
- [ ] **Visual Hierarchy**: Differentiate between "Sticky" (single-tap) and "Locked" (double-tap) states using distinct color intensities or bloom effects.

### 3. Interaction Design
- [x] **Portal Transition**: Implement a visual link between the pressed key and the appearing popup (e.g., dimming the underlying key) to create a sense of continuous motion.
- [ ] **Contextual Scaling**: Dynamically adjust popup widths based on content (Letters vs. Symbols) to reduce visual noise.

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
    - **Safety Insets**: `6dp` transparent layers added to `popup_tokyonight_dynamic.xml` via `layer-list` to prevent window-boundary clipping.
    - `keyPreviewOffset`: `-8dp` (Balanced visual float).
    - `keyPreviewHeight`: `60dp` (Modern "Pill" geometry).
    - **Centering**: Asymmetric padding (`4dp` top, `8dp` bottom) used in `preview_tokyonight_dynamic.xml` for perfect optical centering.
    - **Unified Container**: `popup_container_tokyonight.xml` used for multi-key popups to eliminate notched borders.
    - `corners`: `12dp` radius for popups, `6dp` for keys.

### 5. Theme Mapping (Internal IDs)
- `10`: Storm (Default)
- `11`: Night
- `12`: Day
- `13`: Moon
