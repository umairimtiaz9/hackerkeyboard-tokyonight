/*
 * Copyright (C) 2024 Tokyo Night Theme Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * Tokyo Night Color Palette for Hacker's Keyboard
 * Extracted from: https://github.com/folke/tokyonight.nvim
 *
 * This class provides a centralized, data-oriented color palette system
 * for all four Tokyo Night variants: Storm, Night, Moon, and Day.
 *
 * Usage:
 *   TokyoNightPalette.Variant storm = TokyoNightPalette.STORM;
 *   int bgColor = storm.bg;
 *   int textColor = storm.fg;
 */

package org.pocketworkstation.pckeyboard;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TokyoNightPalette {

    // Theme variant IDs
    public static final int THEME_STORM = 10;
    public static final int THEME_NIGHT = 11;
    public static final int THEME_DAY = 12;
    public static final int THEME_MOON = 13;

    @IntDef({THEME_STORM, THEME_NIGHT, THEME_DAY, THEME_MOON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeVariant {}

    /**
     * Base Variant class containing all color definitions for a Tokyo Night theme.
     * Organized by semantic purpose: backgrounds, foregrounds, accents, UI, and terminal colors.
     */
    public static class Variant {
        // ========== BACKGROUND COLORS ==========
        @ColorInt public final int bg;           // Main keyboard background
        @ColorInt public final int bgDark;       // Darker background (popups, statusline)
        @ColorInt public final int bgDark1;      // Darkest background
        @ColorInt public final int bgHighlight;  // Highlight background

        // ========== FOREGROUND COLORS ==========
        @ColorInt public final int fg;           // Main text color
        @ColorInt public final int fgDark;       // Darker text
        @ColorInt public final int fgGutter;     // Gutter text

        // ========== PRIMARY ACCENT COLORS ==========
        @ColorInt public final int blue;         // Primary blue
        @ColorInt public final int blue0;        // Dark blue
        @ColorInt public final int blue1;        // Bright cyan-blue
        @ColorInt public final int blue2;        // Cyan
        @ColorInt public final int blue5;        // Light cyan
        @ColorInt public final int blue6;        // Very light cyan
        @ColorInt public final int blue7;        // Dark blue-gray

        // ========== SECONDARY ACCENT COLORS ==========
        @ColorInt public final int cyan;         // Cyan
        @ColorInt public final int green;        // Green
        @ColorInt public final int green1;       // Teal-green
        @ColorInt public final int green2;       // Dark teal
        @ColorInt public final int magenta;      // Magenta
        @ColorInt public final int magenta2;     // Hot magenta
        @ColorInt public final int orange;       // Orange
        @ColorInt public final int purple;       // Purple
        @ColorInt public final int red;          // Red
        @ColorInt public final int red1;         // Dark red
        @ColorInt public final int teal;         // Teal
        @ColorInt public final int yellow;       // Yellow

        // ========== UI COLORS ==========
        @ColorInt public final int comment;      // Comment color
        @ColorInt public final int dark3;        // Dark gray
        @ColorInt public final int dark5;        // Medium gray
        @ColorInt public final int terminalBlack; // Terminal black

        // ========== GIT COLORS ==========
        @ColorInt public final int gitAdd;       // Git add
        @ColorInt public final int gitChange;    // Git change
        @ColorInt public final int gitDelete;    // Git delete

        // ========== SEMANTIC KEYBOARD ATTRIBUTES ==========
        @ColorInt public final int kbdColorBase;      // Main keyboard background
        @ColorInt public final int kbdColorAlpha;     // Alpha key background
        @ColorInt public final int kbdColorMod;       // Modifier key background
        @ColorInt public final int kbdColorHighlight; // Pressed/highlighted state
        @ColorInt public final int kbdColorText;      // Key text color
        @ColorInt public final int kbdColorAccent;    // Accent color (cursor keys)
        @ColorInt public final int kbdColorPopup;     // Popup border/stroke

        // ========== TERMINAL COLORS (ANSI) ==========
        @ColorInt public final int terminalRed;
        @ColorInt public final int terminalGreen;
        @ColorInt public final int terminalYellow;
        @ColorInt public final int terminalBlue;
        @ColorInt public final int terminalMagenta;
        @ColorInt public final int terminalCyan;
        @ColorInt public final int terminalWhite;
        @ColorInt public final int terminalBrightRed;
        @ColorInt public final int terminalBrightGreen;
        @ColorInt public final int terminalBrightYellow;
        @ColorInt public final int terminalBrightBlue;
        @ColorInt public final int terminalBrightMagenta;
        @ColorInt public final int terminalBrightCyan;
        @ColorInt public final int terminalBrightWhite;

        public Variant(
                // Background colors
                int bg, int bgDark, int bgDark1, int bgHighlight,
                // Foreground colors
                int fg, int fgDark, int fgGutter,
                // Primary accents
                int blue, int blue0, int blue1, int blue2, int blue5, int blue6, int blue7,
                // Secondary accents
                int cyan, int green, int green1, int green2, int magenta, int magenta2,
                int orange, int purple, int red, int red1, int teal, int yellow,
                // UI colors
                int comment, int dark3, int dark5, int terminalBlack,
                // Git colors
                int gitAdd, int gitChange, int gitDelete,
                // Semantic keyboard attributes
                int kbdColorBase, int kbdColorAlpha, int kbdColorMod,
                int kbdColorHighlight, int kbdColorText, int kbdColorAccent, int kbdColorPopup,
                // Terminal colors
                int terminalRed, int terminalGreen, int terminalYellow, int terminalBlue,
                int terminalMagenta, int terminalCyan, int terminalWhite,
                int terminalBrightRed, int terminalBrightGreen, int terminalBrightYellow,
                int terminalBrightBlue, int terminalBrightMagenta, int terminalBrightCyan,
                int terminalBrightWhite
        ) {
            this.bg = bg;
            this.bgDark = bgDark;
            this.bgDark1 = bgDark1;
            this.bgHighlight = bgHighlight;
            this.fg = fg;
            this.fgDark = fgDark;
            this.fgGutter = fgGutter;
            this.blue = blue;
            this.blue0 = blue0;
            this.blue1 = blue1;
            this.blue2 = blue2;
            this.blue5 = blue5;
            this.blue6 = blue6;
            this.blue7 = blue7;
            this.cyan = cyan;
            this.green = green;
            this.green1 = green1;
            this.green2 = green2;
            this.magenta = magenta;
            this.magenta2 = magenta2;
            this.orange = orange;
            this.purple = purple;
            this.red = red;
            this.red1 = red1;
            this.teal = teal;
            this.yellow = yellow;
            this.comment = comment;
            this.dark3 = dark3;
            this.dark5 = dark5;
            this.terminalBlack = terminalBlack;
            this.gitAdd = gitAdd;
            this.gitChange = gitChange;
            this.gitDelete = gitDelete;
            this.kbdColorBase = kbdColorBase;
            this.kbdColorAlpha = kbdColorAlpha;
            this.kbdColorMod = kbdColorMod;
            this.kbdColorHighlight = kbdColorHighlight;
            this.kbdColorText = kbdColorText;
            this.kbdColorAccent = kbdColorAccent;
            this.kbdColorPopup = kbdColorPopup;
            this.terminalRed = terminalRed;
            this.terminalGreen = terminalGreen;
            this.terminalYellow = terminalYellow;
            this.terminalBlue = terminalBlue;
            this.terminalMagenta = terminalMagenta;
            this.terminalCyan = terminalCyan;
            this.terminalWhite = terminalWhite;
            this.terminalBrightRed = terminalBrightRed;
            this.terminalBrightGreen = terminalBrightGreen;
            this.terminalBrightYellow = terminalBrightYellow;
            this.terminalBrightBlue = terminalBrightBlue;
            this.terminalBrightMagenta = terminalBrightMagenta;
            this.terminalBrightCyan = terminalBrightCyan;
            this.terminalBrightWhite = terminalBrightWhite;
        }
    }

    // ========== STORM VARIANT (ID: 10) ==========
    // Default dark theme with balanced contrast
    public static final Variant STORM = new Variant(
            // Background colors
            0xFF24283b, 0xFF1f2335, 0xFF1b1e2d, 0xFF292e42,
            // Foreground colors
            0xFFC0caf5, 0xFFa9b1d6, 0xFF3b4261,
            // Primary accents
            0xFF7aa2f7, 0xFF3d59a1, 0xFF2ac3de, 0xFF0db9d7, 0xFF89ddff, 0xFFb4f9f8, 0xFF394b70,
            // Secondary accents
            0xFF7dcfff, 0xFF9ece6a, 0xFF73daca, 0xFF41a6b5, 0xFFbb9af7, 0xFFff007c,
            0xFFff9e64, 0xFF9d7cd8, 0xFFf7768e, 0xFFdb4b4b, 0xFF1abc9c, 0xFFe0af68,
            // UI colors
            0xFF565f89, 0xFF545c7e, 0xFF737aa2, 0xFF414868,
            // Git colors
            0xFF449dab, 0xFF6183bb, 0xFF914c54,
            // Semantic keyboard attributes
            0xFF24283b, 0xFF24283b, 0xFF292e42, 0xFF7aa2f7, 0xFFC0caf5, 0xFF7dcfff, 0xFF565f89,
            // Terminal colors
            0xFFf7768e, 0xFF9ece6a, 0xFFe0af68, 0xFF7aa2f7, 0xFFbb9af7, 0xFF7dcfff, 0xFFa9b1d6,
            0xFFf7768e, 0xFF9ece6a, 0xFFe0af68, 0xFF7aa2f7, 0xFFbb9af7, 0xFF7dcfff, 0xFFC0caf5
    );

    // ========== NIGHT VARIANT (ID: 11) ==========
    // Slightly lighter dark theme, inherits most colors from Storm
    public static final Variant NIGHT = new Variant(
            // Background colors (darker than Storm)
            0xFF1a1b26, 0xFF16161e, 0xFF0c0e14, 0xFF292e42,
            // Foreground colors (inherited from Storm)
            0xFFC0caf5, 0xFFa9b1d6, 0xFF3b4261,
            // Primary accents (inherited from Storm)
            0xFF7aa2f7, 0xFF3d59a1, 0xFF2ac3de, 0xFF0db9d7, 0xFF89ddff, 0xFFb4f9f8, 0xFF394b70,
            // Secondary accents (inherited from Storm)
            0xFF7dcfff, 0xFF9ece6a, 0xFF73daca, 0xFF41a6b5, 0xFFbb9af7, 0xFFff007c,
            0xFFff9e64, 0xFF9d7cd8, 0xFFf7768e, 0xFFdb4b4b, 0xFF1abc9c, 0xFFe0af68,
            // UI colors (inherited from Storm)
            0xFF565f89, 0xFF545c7e, 0xFF737aa2, 0xFF414868,
            // Git colors (inherited from Storm)
            0xFF449dab, 0xFF6183bb, 0xFF914c54,
            // Semantic keyboard attributes
            0xFF1a1b26, 0xFF1a1b26, 0xFF16161e, 0xFF7aa2f7, 0xFFC0caf5, 0xFF7dcfff, 0xFF565f89,
            // Terminal colors (inherited from Storm)
            0xFFf7768e, 0xFF9ece6a, 0xFFe0af68, 0xFF7aa2f7, 0xFFbb9af7, 0xFF7dcfff, 0xFFa9b1d6,
            0xFFf7768e, 0xFF9ece6a, 0xFFe0af68, 0xFF7aa2f7, 0xFFbb9af7, 0xFF7dcfff, 0xFFC0caf5
    );

    // ========== MOON VARIANT (ID: 13) ==========
    // Medium dark theme with unique accent colors
    public static final Variant MOON = new Variant(
            // Background colors
            0xFF222436, 0xFF1e2030, 0xFF191b29, 0xFF2f334d,
            // Foreground colors
            0xFFc8d3f5, 0xFF828bb8, 0xFF3b4261,
            // Primary accents
            0xFF82aaff, 0xFF3e68d7, 0xFF65bcff, 0xFF0db9d7, 0xFF89ddff, 0xFFb4f9f8, 0xFF394b70,
            // Secondary accents
            0xFF86e1fc, 0xFFc3e88d, 0xFF4fd6be, 0xFF41a6b5, 0xFFc099ff, 0xFFff007c,
            0xFFff966c, 0xFFfca7ea, 0xFFff757f, 0xFFc53b53, 0xFF4fd6be, 0xFFffc777,
            // UI colors
            0xFF636da6, 0xFF545c7e, 0xFF737aa2, 0xFF444a73,
            // Git colors
            0xFFb8db87, 0xFF7ca1f2, 0xFFe26a75,
            // Semantic keyboard attributes
            0xFF222436, 0xFF222436, 0xFF2f334d, 0xFF82aaff, 0xFFc8d3f5, 0xFF86e1fc, 0xFF636da6,
            // Terminal colors
            0xFFff757f, 0xFFc3e88d, 0xFFffc777, 0xFF82aaff, 0xFFc099ff, 0xFF86e1fc, 0xFF828bb8,
            0xFFff757f, 0xFFc3e88d, 0xFFffc777, 0xFF82aaff, 0xFFc099ff, 0xFF86e1fc, 0xFFc8d3f5
    );

    // ========== DAY VARIANT (ID: 12) ==========
    // Light theme (inverted from Night with brightness adjustment)
    public static final Variant DAY = new Variant(
            // Background colors (inverted)
            0xFFf5f5f5, 0xFFe8e8e8, 0xFFf3f3f3, 0xFFd6d6d6,
            // Foreground colors (inverted)
            0xFF3f3f3f, 0xFF565656, 0xFFc4c4c4,
            // Primary accents (inverted)
            0xFF858eff, 0xFFc2a65e, 0xFFd53c21, 0xFFf24628, 0xFF762200, 0xFF4b0607, 0xFFc6b48f,
            // Secondary accents (inverted)
            0xFF823000, 0xFF613197, 0xFF8c2535, 0xFFbe594a, 0xFF44609b, 0xFF00f283,
            0xFF006199, 0xFF628227, 0xFF088791, 0xFF24b4b4, 0xFFe54363, 0xFF1f509f,
            // UI colors (inverted)
            0xFFa9a09f, 0xFFaba383, 0xFF8c859d, 0xFFbebf97,
            // Git colors (inverted)
            0xFFbb6254, 0xFF9e7c44, 0xFF6eb3ab,
            // Semantic keyboard attributes
            0xFFf5f5f5, 0xFFf5f5f5, 0xFFe8e8e8, 0xFF858eff, 0xFF3f3f3f, 0xFF823000, 0xFFa9a09f,
            // Terminal colors (inverted)
            0xFF088791, 0xFF613197, 0xFF1f509f, 0xFF858eff, 0xFF44609b, 0xFF823000, 0xFF565656,
            0xFF088791, 0xFF613197, 0xFF1f509f, 0xFF858eff, 0xFF44609b, 0xFF823000, 0xFF3f3f3f
    );

    /**
     * Get a variant by its theme ID.
     *
     * @param themeId The theme variant ID (THEME_STORM, THEME_NIGHT, THEME_DAY, THEME_MOON)
     * @return The corresponding Variant, or STORM if ID is not recognized
     */
    public static Variant getVariant(@ThemeVariant int themeId) {
        switch (themeId) {
            case THEME_NIGHT:
                return NIGHT;
            case THEME_DAY:
                return DAY;
            case THEME_MOON:
                return MOON;
            case THEME_STORM:
            default:
                return STORM;
        }
    }

    /**
     * Get a variant by its name.
     *
     * @param name The variant name ("storm", "night", "day", "moon")
     * @return The corresponding Variant, or STORM if name is not recognized
     */
    public static Variant getVariantByName(String name) {
        if (name == null) {
            return STORM;
        }
        switch (name.toLowerCase()) {
            case "night":
                return NIGHT;
            case "day":
                return DAY;
            case "moon":
                return MOON;
            case "storm":
            default:
                return STORM;
        }
    }

    /**
     * Get the theme ID for a variant name.
     *
     * @param name The variant name ("storm", "night", "day", "moon")
     * @return The corresponding theme ID
     */
    @ThemeVariant
    public static int getThemeIdByName(String name) {
        if (name == null) {
            return THEME_STORM;
        }
        switch (name.toLowerCase()) {
            case "night":
                return THEME_NIGHT;
            case "day":
                return THEME_DAY;
            case "moon":
                return THEME_MOON;
            case "storm":
            default:
                return THEME_STORM;
        }
    }

    /**
     * Get the name of a theme variant.
     *
     * @param themeId The theme variant ID
     * @return The variant name
     */
    public static String getThemeName(@ThemeVariant int themeId) {
        switch (themeId) {
            case THEME_NIGHT:
                return "night";
            case THEME_DAY:
                return "day";
            case THEME_MOON:
                return "moon";
            case THEME_STORM:
            default:
                return "storm";
        }
    }

    /**
     * Check if a theme is a light variant.
     *
     * @param themeId The theme variant ID
     * @return true if the theme is light (DAY), false otherwise
     */
    public static boolean isLightTheme(@ThemeVariant int themeId) {
        return themeId == THEME_DAY;
    }

    /**
     * Check if a theme is a dark variant.
     *
     * @param themeId The theme variant ID
     * @return true if the theme is dark, false otherwise
     */
    public static boolean isDarkTheme(@ThemeVariant int themeId) {
        return themeId != THEME_DAY;
    }
}
