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

### Phase 1: Foundation (Colors & Styles)
*   **Step 1.1**: Define the 4 palettes in `colors.xml`.
*   **Step 1.2**: Create the "Master Selector" logic in `drawables/` to handle Pressed, Modifier, and Sticky states dynamically.

### Phase 2: The Cleanup (The Great Purge)
*   **Step 2.1**: Delete all legacy `input_*.xml` files (Gingerbread, ICS, Stone, etc.).
*   **Step 2.2**: Create 4 unified layout files: `input_tokyonight_storm.xml`, `_night.xml`, `_day.xml`, `_moon.xml`.
*   **Step 2.3**: Delete all obsolete bitmap assets (`.9.png`) and unused XML selectors.

### Phase 3: Layout Hardening
*   **Step 3.1**: Audit `kbd_full.xml` to ensure `isModifier="true"` is set on `Esc`, `Tab`, `Ctrl`, `Alt`, `Arrows` to trigger the darker functional color.
*   **Step 3.2**: Implement the "Glamorous" popup styles and animations.

### Phase 4: Final Polish
*   **Step 4.1**: Build and verify.
*   **Step 4.2**: Neutralize any remaining blue/green icons to match the theme's text color.

## Contextual History
*   **Past**: A "mess" of 14+ themes, inconsistent spacing, and mixed assets.
*   **Present**: Partial modernization with Monospace font and a generic dark theme.
*   **Future**: A clean, unified codebase supporting 4 professional Tokyo Night variations with pixel-perfect layouts.