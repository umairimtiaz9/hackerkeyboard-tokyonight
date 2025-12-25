# Hacker's Keyboard: Tokyo Night Edition

## Project Overview
We are pivoting from a generic "Modern IDE" theme to a high-fidelity **Tokyo Night** implementation. The goal is to replicate the exact layout precision of the original 5-row keyboard while applying the polished, professional aesthetic of the Tokyo Night color scheme (Storm, Night, Day, Moon).

## The "Tokyo Night" Design System

### 1. Visual Strategy
We will support 4 distinct variations based on `folke/tokyonight.nvim`:

| Theme | Background | Alpha Keys | Modifiers | Text/FG | Accent |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Storm** | `#24283B` | `#1F2335` | `#1A1B26` | `#C0CAF5` | `#7AA2F7` (Blue) |
| **Night** | `#1A1B26` | `#292E42` | `#16161E` | `#C0CAF5` | `#BB9AF7` (Purple) |
| **Day** | `#E1E2E7` | `#FFFFFF` | `#D0D5E3` | `#3760BF` | `#3760BF` (Blue) |
| **Moon** | `#222436` | `#444B6A` | `#2F334D` | `#C0CAF5` | `#82AAFF` (Cyan) |

### 2. Layout Architecture
*   **Exact Replication**: We will maintain the structure of `kbd_qwerty`, `kbd_full`, and `kbd_full_fn`.
*   **Inset Gutter System**: Instead of complex `gap` attributes, we will use a global `android:inset="1dp"` on key drawables. This creates a uniform **2dp gutter** between all keys, ensuring the "tiled" look matches the reference screenshots exactly.
*   **Monospace**: All text (labels, popups, candidates) will strictly use `Typeface.MONOSPACE`.

### 3. "Glamorous" Popups
*   **Shape**: Rounded corners (4dp radius) to differentiate from the rectangular keys.
*   **Motion**: Scale-up and fade-in animation (`res/anim/popup_enter.xml`).
*   **Shadows**: Simulated via a subtle stroke or dark ring, as legacy elevation is unreliable.

## Implementation Plan

### Phase 1: Foundation (Colors & Styles) [COMPLETE]
*   **Step 1.1**: Define the 4 palettes in `colors.xml`. (Refined for Tonal Elevation)
*   **Step 1.2**: Create the "Master Selector" logic in `drawables/` to handle Pressed, Modifier, and Sticky states dynamically.

### Phase 2: The Cleanup (The Great Purge) [COMPLETE]
*   **Step 2.1**: Delete all legacy `input_*.xml` files.
*   **Step 2.2**: Create 4 unified layout files.
*   **Step 2.3**: Delete all obsolete bitmap assets and unused XML selectors.

### Phase 3: Layout Hardening [COMPLETE]
*   **Step 3.1**: Audit `kbd_full.xml` for `isModifier="true"`.
*   **Step 3.2**: Implement the "Glamorous" popup styles and animations. (Fixed black popup and refined geometry)

### Phase 4: Final Polish [COMPLETE]
*   **Step 4.1**: Build and verify.
*   **Step 4.2**: Neutralize any remaining icons to match the theme's text color.
*   **Step 4.3**: Implement high-fidelity "Orgasmic" fluid popups.

## Contextual History
*   **Past**: A "mess" of 14+ themes, inconsistent spacing, and mixed assets.
*   **Present**: Complete Tokyo Night overhaul with 4 variations. Fluid, high-performance popups with modern animations.
*   **Future**: Stability and potential support for more specialized IDE layouts.

## TODO: Future Modernization Plans
- [x] **Pixel-Perfect Geometry**: Set `key_bottom_gap` and `key_horizontal_gap` to `0dp` and use strict `1dp` insets to guarantee a uniform `2dp` gutter across all keys.
- [x] **Soft-Rect Aesthetic**: Update corner radii to `6dp` for a more modern, premium mobile feel.
- [x] **Tonal Elevation**: Refine the color contrast so Alphas and Modifiers use "Surface Tones" for a more unified look.
- [x] **Typographic Hierarchy**: Force `Typeface.BOLD` for main labels and significantly reduce the opacity/size of "Hint" characters to reduce visual clutter.
- [x] **Interaction Depth**: Enhance the "pop-up" animations with scale-up transitions.
- [ ] **Wireframe Iconography**: Replace any remaining filled icons with thin-stroke (1dp) wireframe versions.
- [ ] **TBD**