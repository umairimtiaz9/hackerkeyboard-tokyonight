# Changelog

All notable changes to this project will be documented in this file.

## [Tokyo Night Edition] - 2025-12-25

### Added
- **Modern Fluid Popups**: Implemented a new `popup_enter_modern.xml` animation with scale, slide, and fade effects.
- **Tokyo Night Design System**: Full implementation of Storm, Night, Day, and Moon palettes.
- **Neon Aesthetic**: Added Tokyo Night green (`#9ECE6A`) accents to Storm theme popups and previews.
- **Monospace Typography**: Forced `Typeface.MONOSPACE` and `Typeface.BOLD` for all key labels for an IDE-like feel.

### Changed
- **Pixel-Perfect Geometry**: Set all key gaps to `0dp` and used `1dp` insets to create a consistent `2dp` gutter.
- **Tonal Elevation**: Refined all palettes so Alphas, Modifiers, and Backgrounds use cohesive surface tones.
- **Refined Icons**: Updated the Shift Lock icon and other symbols to match the modern aesthetic.
- **Day Theme Fixes**: Corrected color alignment for the Day theme to ensure high contrast and proper "light mode" feel.

### Fixed
- **Popup Geometry**: Fixed the black/boxy popup quirkiness with rounded `12dp` corners and vibrant strokes.
- **Selector Logic**: Corrected `btn_key_*.xml` selectors to point to their respective theme assets (Day, Moon, Night) instead of defaulting to Storm.
- **Variant Swap**: Fixed a bug where Storm and Moon backgrounds were swapped.

### Removed
- **Legacy Assets**: Purged all old Gingerbread, ICS, and Stone theme files and bitmap (`.9.png`) assets.

---

## Recent Commits
* [b6f1742] 2025-12-25: feat: implement modern fluid popup animation and style for Storm theme
* [b4c83b9] 2025-12-25: style: set key popup and preview corners to Tokyo Night green for Storm theme
* [8d8d743] 2025-12-25: docs: update implementation plan and progress in GEMINI.md
* [84c7940] 2025-12-25: fix: correct drawable selectors for Day, Moon, and Night themes and refine Day palette
* [807aef6] 2025-12-25: fix: swap background colors for Storm and Moon variants
* [7ed0cdd] 2025-12-25: style: refine Tokyo Night tones, fix shift lock icon, and improve popup aesthetic
* [fb48b0b] 2025-12-25: feat: complete Tokyo Night theme overhaul and legacy asset purge
