# Changelog

All notable changes to this project will be documented in this file.

## [Tokyo Night Edition] - 2025-12-26

### Added
- **Data-Oriented Architecture**: Decoupled UI components from hardcoded color values. Introduced a semantic attribute system (`attrs.xml`) that maps roles (Base, Alpha, Mod, Highlight, Accent, Popup) to theme-specific colors.
- **Unified Component System**: Implemented `btn_key_tokyonight.xml` (Universal Key Drawable), `preview_tokyonight_dynamic.xml` (Universal Preview), and `popup_tokyonight_dynamic.xml` (Universal Shape).
- **Dynamic Theme Wrapping**: Modified `KeyboardSwitcher.java` to use `ContextThemeWrapper`, enabling the keyboard to swap its entire color palette at runtime without layout duplication.
- **Surgical Vector Tinting**: Transitioned all keyboard symbols (`sym_keyboard_*.xml`) to use `?attr/kbdColorText` for perfect theme adherence.

### Changed
- **Monolithic Geometry**: Refined Storm and Moon palettes to use consistent "Surface Tones," creating a unified visual slab effect.
- **Unified Selectors**: Replaced 14+ legacy `input_*.xml` layouts with 4 unified, attribute-driven layout files.

## [Tokyo Night Edition] - 2025-12-25

### Added
- **Official Color Source**: Integrated `tokyonight.nvim` repository as the single source of truth for palettes.
- **Surgical Highlights**: Implemented targeted coloring for arrow keys (Purple/Magenta) without flooding the background of other modifiers.
- **Modern Fluid Popups**: Implemented a new `popup_enter_modern.xml` animation with scale, slide, and fade effects.
- **Tokyo Night Design System**: Full implementation of Storm, Night, Day, and Moon palettes.
- **Neon Aesthetic**: Added Tokyo Night green accents to Storm theme popups and previews.
- **Monospace Typography**: Forced `Typeface.MONOSPACE` and `Typeface.BOLD` for all key labels for an IDE-like feel.

### Changed
- **Popup Physics**: Adjusted `key_preview_offset` to a negative value (-8dp) so popups extend upwards from the key top.
- **Vertical Tab Aesthetic**: Set popup height to 80dp for a more elegant "floating tab" appearance.
- **Pixel-Perfect Geometry**: Set all key gaps to `0dp` and used `1dp` insets to create a consistent `2dp` gutter.
- **Tonal Elevation**: Refined all palettes so Alphas, Modifiers, and Backgrounds use cohesive surface tones.
- **Refined Icons**: Updated the Shift Lock icon and other symbols to match the modern aesthetic.
- **Day Theme Fixes**: Corrected color alignment for the Day theme to ensure high contrast and proper "light mode" feel.

### Fixed
- **Purple Flood Fix**: Reverted `tn_storm_mod` to a professional dark tone, isolating the purple accent specifically to symbols.
- **Popup Geometry**: Fixed the black/boxy popup quirkiness with rounded `12dp` corners and vibrant strokes.
- **Selector Logic**: Corrected `btn_key_*.xml` selectors to point to their respective theme assets (Day, Moon, Night) instead of defaulting to Storm.
- **Variant Swap**: Fixed a bug where Storm and Moon backgrounds were swapped.

### Removed
- **Legacy Assets**: Purged all old Gingerbread, ICS, and Stone theme files and bitmap (`.9.png`) assets.

---

## Recent Commits
* [d91762f] 2025-12-25: fix: surgical color correction for arrows and popup positioning
* [b6f1742] 2025-12-25: feat: implement modern fluid popup animation and style for Storm theme
* [b4c83b9] 2025-12-25: style: set key popup and preview corners to Tokyo Night green for Storm theme
* [8d8d743] 2025-12-25: docs: update implementation plan and progress in GEMINI.md
* [84c7940] 2025-12-25: fix: correct drawable selectors for Day, Moon, and Night themes and refine Day palette
* [807aef6] 2025-12-25: fix: swap background colors for Storm and Moon variants
* [7ed0cdd] 2025-12-25: style: refine Tokyo Night tones, fix shift lock icon, and improve popup aesthetic
* [fb48b0b] 2025-12-25: feat: complete Tokyo Night theme overhaul and legacy asset purge