# Tokyo Night Edition: Development Context & State
Generated: Thursday, December 25, 2025

## 1. Core Vision: The "Monolithic Console"
The objective is to refactor Hacker's Keyboard into a high-fidelity, developer-focused UI based on the Tokyo Night color scheme.

### Design Principles:
- **Unified Geometry**: All keys share the exact same background color to create a solid "monolithic" slab effect.
- **Tonal Elevation**: 
    - `kbdColorBase`: Deepest tone (#1E2030), acts as the "tray" or underlay.
    - `kbdColorAlpha/Mod`: The key color (#24283B), identical for all keys.
    - `kbdColorHighlight`: The interactive "pulse" (#3D59A1).
- **Syntax Highlighting Legends**:
    - Letters/Main Data: Soft Blue-White (#C0CAF5).
    - Function/Arrows/Accents: Magenta/Purple (#BB9AF7).
- **HUD Popups**: Floating tooltips with rounded corners (12dp), negative offset (-8dp), and neon green strokes (#9ECE6A).

## 2. Architectural Changes
- **Data-Oriented Refactor**: Decoupled UI from hardcoded hex values.
    - Created `attrs.xml` with semantic roles.
    - Updated `styles.xml` with 4 theme variants (Storm, Night, Day, Moon).
    - Modified `KeyboardSwitcher.java` to wrap the `LayoutInflater` with a `ContextThemeWrapper` based on the selected theme.
- **Unified Assets**: 
    - `btn_key_tokyonight.xml`: One selector for all themes/keys.
    - `preview_tokyonight_dynamic.xml`: One layout for all popups.
    - `popup_tokyonight_dynamic.xml`: One shape definition.

## 3. Current Technical Status
- **Build Status**: Functional.
- **Issue 1 (Resolved)**: "Purple Flood" - Reverted modifier backgrounds to dark indigo, restricted purple to symbols only.
- **Issue 2 (Resolved)**: Character clipping in popups - Switched to wrap_content with minHeight/minWidth and disabled font padding.
- **Issue 3 (In Progress)**: Popup placement inconsistency between different row configurations (Termux vs. Full Row).
- **Surgical Logic**: Arrow symbols are purple via `keyCursorColor` mapping in the theme.

## 4. Key Symbols & Palette (Storm Reference)
- Base: `#1E2030`
- Key: `#24283B`
- Text: `#C0CAF5`
- Accent: `#BB9AF7`
- Popup Stroke: `#9ECE6A`

## 5. Implementation Roadmap
- [x] Phase 1-4: Basic overhaul and cleanup.
- [x] Phase 5.1-5.3: Attribute system and Storm pilot.
- [x] Phase 5.4: Theme wrapping in Java.
- [x] Phase 5.5: Full rollout of 4 variants.
- [ ] Phase 6: Precision icon wireframing (1dp stroke).
- [x] Phase 7: Fine-tuning layout offsets for row-switching stability.
