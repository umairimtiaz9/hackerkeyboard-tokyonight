/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.pocketworkstation.pckeyboard;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.pocketworkstation.pckeyboard.material.settings.SettingsDefinitions;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class KeyboardSwitcher implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = "PCKeyboardKbSw";

    public static final int MODE_NONE = 0;
    public static final int MODE_TEXT = 1;
    public static final int MODE_SYMBOLS = 2;
    public static final int MODE_PHONE = 3;
    public static final int MODE_URL = 4;
    public static final int MODE_EMAIL = 5;
    public static final int MODE_IM = 6;
    public static final int MODE_WEB = 7;

    // Main keyboard layouts without the settings key
    public static final int KEYBOARDMODE_NORMAL = R.id.mode_normal;
    public static final int KEYBOARDMODE_URL = R.id.mode_url;
    public static final int KEYBOARDMODE_EMAIL = R.id.mode_email;
    public static final int KEYBOARDMODE_IM = R.id.mode_im;
    public static final int KEYBOARDMODE_WEB = R.id.mode_webentry;
    // Main keyboard layouts with the settings key
    public static final int KEYBOARDMODE_NORMAL_WITH_SETTINGS_KEY = R.id.mode_normal_with_settings_key;
    public static final int KEYBOARDMODE_URL_WITH_SETTINGS_KEY = R.id.mode_url_with_settings_key;
    public static final int KEYBOARDMODE_EMAIL_WITH_SETTINGS_KEY = R.id.mode_email_with_settings_key;
    public static final int KEYBOARDMODE_IM_WITH_SETTINGS_KEY = R.id.mode_im_with_settings_key;
    public static final int KEYBOARDMODE_WEB_WITH_SETTINGS_KEY = R.id.mode_webentry_with_settings_key;

    // Symbols keyboard layout without the settings key
    public static final int KEYBOARDMODE_SYMBOLS = R.id.mode_symbols;
    // Symbols keyboard layout with the settings key
    public static final int KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY = R.id.mode_symbols_with_settings_key;

    public static final String DEFAULT_LAYOUT_ID = "10";
    public static final String PREF_KEYBOARD_LAYOUT = "pref_keyboard_layout";
    public static final String PREF_SETTINGS_KEY = "settings_key";
    private static final int[] THEMES = new int[] {
        R.layout.input_tokyonight_dynamic, // 0 -> Mapped to Storm as fallback
        R.layout.input_tokyonight_dynamic, // 1 -> Legacy Stone Bold
        R.layout.input_tokyonight_dynamic, // 2 -> Legacy Trans Neon
        R.layout.input_tokyonight_dynamic, // 3 -> Legacy Material Dark
        R.layout.input_tokyonight_dynamic, // 4 -> Legacy Material Light
        R.layout.input_tokyonight_dynamic, // 5 -> Legacy ICS Darker
        R.layout.input_tokyonight_dynamic, // 6 -> Legacy Material Black
        R.layout.input_tokyonight_dynamic, // 7 -> Legacy Gingerbread
        R.layout.input_tokyonight_dynamic, // 8
        R.layout.input_tokyonight_dynamic, // 9
        R.layout.input_tokyonight_dynamic, // 10 - Storm
        R.layout.input_tokyonight_dynamic, // 11 - Night
        R.layout.input_tokyonight_dynamic, // 12 - Day
        R.layout.input_tokyonight_dynamic, // 13 - Moon
    };

    private static final int[] STYLES = new int[] {
        R.style.Theme_TokyoNight_Storm, // 0
        R.style.Theme_TokyoNight_Storm, // 1
        R.style.Theme_TokyoNight_Storm, // 2
        R.style.Theme_TokyoNight_Storm, // 3
        R.style.Theme_TokyoNight_Storm, // 4
        R.style.Theme_TokyoNight_Storm, // 5
        R.style.Theme_TokyoNight_Storm, // 6
        R.style.Theme_TokyoNight_Storm, // 7
        R.style.Theme_TokyoNight_Storm, // 8
        R.style.Theme_TokyoNight_Storm, // 9
        R.style.Theme_TokyoNight_Storm, // 10
        R.style.Theme_TokyoNight_Night, // 11
        R.style.Theme_TokyoNight_Day,   // 12
        R.style.Theme_TokyoNight_Moon,  // 13
    };

    // Tables which contains resource ids for each character theme color
    private static final int KBD_PHONE = R.xml.kbd_phone;
    private static final int KBD_PHONE_SYMBOLS = R.xml.kbd_phone_symbols;
    private static final int KBD_SYMBOLS = R.xml.kbd_symbols;
    private static final int KBD_SYMBOLS_SHIFT = R.xml.kbd_symbols_shift;
    private static final int KBD_QWERTY = R.xml.kbd_qwerty;
    private static final int KBD_FULL = R.xml.kbd_full;
    private static final int KBD_FULL_FN = R.xml.kbd_full_fn;
    private static final int KBD_COMPACT = R.xml.kbd_compact;
    private static final int KBD_COMPACT_FN = R.xml.kbd_compact_fn;

    private LatinKeyboardView mInputView;
    private static final int[] ALPHABET_MODES = { KEYBOARDMODE_NORMAL,
            KEYBOARDMODE_URL, KEYBOARDMODE_EMAIL, KEYBOARDMODE_IM,
            KEYBOARDMODE_WEB, KEYBOARDMODE_NORMAL_WITH_SETTINGS_KEY,
            KEYBOARDMODE_URL_WITH_SETTINGS_KEY,
            KEYBOARDMODE_EMAIL_WITH_SETTINGS_KEY,
            KEYBOARDMODE_IM_WITH_SETTINGS_KEY,
            KEYBOARDMODE_WEB_WITH_SETTINGS_KEY };

    private LatinIME mInputMethodService;

    private KeyboardId mSymbolsId;
    private KeyboardId mSymbolsShiftedId;

    private KeyboardId mCurrentId;
    private final HashMap<KeyboardId, SoftReference<LatinKeyboard>> mKeyboards = new HashMap<KeyboardId, SoftReference<LatinKeyboard>>();

    private int mMode = MODE_NONE;
    /** One of the MODE_XXX values */
    private int mImeOptions;
    private boolean mIsSymbols;
    private int mFullMode;
    /**
     * mIsAutoCompletionActive indicates that auto completed word will be input
     * instead of what user actually typed.
     */
    private boolean mIsAutoCompletionActive;
    // private boolean mHasVoice;
    // private boolean mVoiceOnPrimary;
    private boolean mPreferSymbols;

    private static final int AUTO_MODE_SWITCH_STATE_ALPHA = 0;
    private static final int AUTO_MODE_SWITCH_STATE_SYMBOL_BEGIN = 1;
    private static final int AUTO_MODE_SWITCH_STATE_SYMBOL = 2;
    // The following states are used only on the distinct multi-touch panel
    // devices.
    private static final int AUTO_MODE_SWITCH_STATE_MOMENTARY = 3;
    private static final int AUTO_MODE_SWITCH_STATE_CHORDING = 4;
    private int mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_ALPHA;

    // Indicates whether or not we have the settings key
    private boolean mHasSettingsKey;
    private static final int SETTINGS_KEY_MODE_AUTO = R.string.settings_key_mode_auto;
    private static final int SETTINGS_KEY_MODE_ALWAYS_SHOW = R.string.settings_key_mode_always_show;
    // NOTE: No need to have SETTINGS_KEY_MODE_ALWAYS_HIDE here because it's not
    // being referred to
    // in the source code now.
    // Default is SETTINGS_KEY_MODE_AUTO.
    private static final int DEFAULT_SETTINGS_KEY_MODE = SETTINGS_KEY_MODE_AUTO;

    private int mLastDisplayWidth;

    private int mLayoutId;

    private static final KeyboardSwitcher sInstance = new KeyboardSwitcher();

    public static KeyboardSwitcher getInstance() {
        return sInstance;
    }

    private KeyboardSwitcher() {
        // Intentional empty constructor for singleton.
    }

    public static void init(LatinIME ims) {
        sInstance.mInputMethodService = ims;

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ims);
        sInstance.mLayoutId = Integer.valueOf(prefs.getString(
                PREF_KEYBOARD_LAYOUT, DEFAULT_LAYOUT_ID));

        sInstance.updateSettingsKeyState(prefs);
        prefs.registerOnSharedPreferenceChangeListener(sInstance);

        sInstance.mSymbolsId = sInstance.makeSymbolsId(false);
        sInstance.mSymbolsShiftedId = sInstance.makeSymbolsShiftedId(false);
    }

    /**
     * Sets the input locale, when there are multiple locales for input. If no
     * locale switching is required, then the locale should be set to null.
     *
     * @param locale
     *            the current input locale, or null for default locale with no
     *            locale button.
     */

    private KeyboardId makeSymbolsId(boolean hasVoice) {
        if (mFullMode == 1) {
            return new KeyboardId(KBD_COMPACT_FN, KEYBOARDMODE_SYMBOLS, true, false);
        } else if (mFullMode == 2) {
            return new KeyboardId(KBD_FULL_FN, KEYBOARDMODE_SYMBOLS, true, false);
        }
        return new KeyboardId(KBD_SYMBOLS,
                mHasSettingsKey ? KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY
                        : KEYBOARDMODE_SYMBOLS, false, false);
    }

    private KeyboardId makeSymbolsShiftedId(boolean hasVoice) {
        if (mFullMode > 0)
            return null;
        return new KeyboardId(KBD_SYMBOLS_SHIFT,
                mHasSettingsKey ? KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY
                        : KEYBOARDMODE_SYMBOLS, false, false);
    }

    public void makeKeyboards(boolean forceCreate) {
        mFullMode = LatinIME.sKeyboardSettings.keyboardMode;
        mSymbolsId = makeSymbolsId(false);
        mSymbolsShiftedId = makeSymbolsShiftedId(false);

        if (forceCreate)
            mKeyboards.clear();
        // Configuration change is coming after the keyboard gets recreated. So
        // don't rely on that.
        // If keyboards have already been made, check if we have a screen width
        // change and
        // create the keyboard layouts again at the correct orientation
        int displayWidth = mInputMethodService.getMaxWidth();
        if (displayWidth == mLastDisplayWidth)
            return;
        mLastDisplayWidth = displayWidth;
        if (!forceCreate)
            mKeyboards.clear();
    }

    /**
     * Represents the parameters necessary to construct a new LatinKeyboard,
     * which also serve as a unique identifier for each keyboard type.
     */
    private static class KeyboardId {
        // TODO: should have locale and portrait/landscape orientation?
        public final int mXml;
        public final int mKeyboardMode;
        /** A KEYBOARDMODE_XXX value */
        public final boolean mEnableShiftLock;
        public final boolean mHasVoice;
        public final float mKeyboardHeightPercent;
        public final boolean mUsingExtension;

        private final int mHashCode;

        public KeyboardId(int xml, int mode, boolean enableShiftLock,
                boolean hasVoice) {
            this.mXml = xml;
            this.mKeyboardMode = mode;
            this.mEnableShiftLock = enableShiftLock;
            this.mHasVoice = hasVoice;
            this.mKeyboardHeightPercent = LatinIME.sKeyboardSettings.keyboardHeightPercent;
            this.mUsingExtension = LatinIME.sKeyboardSettings.useExtension;

            this.mHashCode = Arrays.hashCode(new Object[] { xml, mode,
                    enableShiftLock, hasVoice });
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof KeyboardId && equals((KeyboardId) other);
        }

        private boolean equals(KeyboardId other) {
            return other != null
                    && other.mXml == this.mXml
                    && other.mKeyboardMode == this.mKeyboardMode
                    && other.mUsingExtension == this.mUsingExtension
                    && other.mEnableShiftLock == this.mEnableShiftLock
                    && other.mHasVoice == this.mHasVoice;
        }

        @Override
        public int hashCode() {
            return mHashCode;
        }
    }

    public void setVoiceMode(boolean enableVoice, boolean voiceOnPrimary) {
        // Voice mode removed
    }

    private boolean hasVoiceButton(boolean isSymbols) {
        return false;
    }

    public void setKeyboardMode(int mode, int imeOptions) {
        mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_ALPHA;
        mPreferSymbols = mode == MODE_SYMBOLS;
        if (mode == MODE_SYMBOLS) {
            mode = MODE_TEXT;
        }
        try {
            setKeyboardMode(mode, imeOptions, mPreferSymbols);
        } catch (RuntimeException e) {
            Log.e(TAG, "Got exception: " + mode + "," + imeOptions + ","
                    + mPreferSymbols + " msg=" + e.getMessage());
        }
    }

    private void setKeyboardMode(int mode, int imeOptions, boolean isSymbols) {
        if (mInputView == null)
            return;
        mMode = mode;
        mImeOptions = imeOptions;
        mIsSymbols = isSymbols;

        mInputView.setPreviewEnabled(mInputMethodService.getPopupOn());

        KeyboardId id = getKeyboardId(mode, imeOptions, isSymbols);
        LatinKeyboard keyboard = null;
        keyboard = getKeyboard(id);

        if (mode == MODE_PHONE) {
            mInputView.setPhoneKeyboard(keyboard);
        }

        mCurrentId = id;
        mInputView.setKeyboard(keyboard);
        keyboard.setShiftState(Keyboard.SHIFT_OFF);
        keyboard.setImeOptions(mInputMethodService.getResources(), mMode,
                imeOptions);
        keyboard.updateSymbolIcons(mIsAutoCompletionActive);
    }

    private LatinKeyboard getKeyboard(KeyboardId id) {
        SoftReference<LatinKeyboard> ref = mKeyboards.get(id);
        LatinKeyboard keyboard = (ref == null) ? null : ref.get();
        if (keyboard == null) {
            Resources orig = mInputMethodService.getResources();
            Configuration conf = orig.getConfiguration();
            Locale saveLocale = conf.locale;
            conf.locale = LatinIME.sKeyboardSettings.inputLocale;
            orig.updateConfiguration(conf, null);
            keyboard = new LatinKeyboard(mInputMethodService, id.mXml,
                    id.mKeyboardMode, id.mKeyboardHeightPercent);
            keyboard.setVoiceMode(false, false);
//            if (isFullMode()) {
//                keyboard.setExtension(new LatinKeyboard(mInputMethodService,
//                        R.xml.kbd_extension_full, 0, id.mRowHeightPercent));
//            } else if (isAlphabetMode()) { // TODO: not in full keyboard mode? Per-mode extension kbd?
//                keyboard.setExtension(new LatinKeyboard(mInputMethodService,
//                        R.xml.kbd_extension, 0, id.mRowHeightPercent));
//            }

            if (id.mEnableShiftLock) {
                keyboard.enableShiftLock();
            }
            mKeyboards.put(id, new SoftReference<LatinKeyboard>(keyboard));

            conf.locale = saveLocale;
            orig.updateConfiguration(conf, null);
        }
        return keyboard;
    }

    public boolean isFullMode() {
        return mFullMode > 0;
    }

    private KeyboardId getKeyboardId(int mode, int imeOptions, boolean isSymbols) {
        boolean hasVoice = hasVoiceButton(isSymbols);
        if (mFullMode > 0) {
            switch (mode) {
            case MODE_TEXT:
            case MODE_URL:
            case MODE_EMAIL:
            case MODE_IM:
            case MODE_WEB:
                return new KeyboardId(mFullMode == 1 ? KBD_COMPACT : KBD_FULL,
                        KEYBOARDMODE_NORMAL, true, hasVoice);
            }
        }
        // TODO: generalize for any KeyboardId
        int keyboardRowsResId = KBD_QWERTY;
        if (isSymbols) {
            if (mode == MODE_PHONE) {
                return new KeyboardId(KBD_PHONE_SYMBOLS, 0, false, hasVoice);
            } else {
                return new KeyboardId(
                        KBD_SYMBOLS,
                        mHasSettingsKey ? KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY
                                : KEYBOARDMODE_SYMBOLS, false, hasVoice);
            }
        }
        switch (mode) {
        case MODE_NONE:
            /* fall through */
        case MODE_TEXT:
            return new KeyboardId(keyboardRowsResId,
                    mHasSettingsKey ? KEYBOARDMODE_NORMAL_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_NORMAL, true, hasVoice);
        case MODE_SYMBOLS:
            return new KeyboardId(KBD_SYMBOLS,
                    mHasSettingsKey ? KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_SYMBOLS, false, hasVoice);
        case MODE_PHONE:
            return new KeyboardId(KBD_PHONE, 0, false, hasVoice);
        case MODE_URL:
            return new KeyboardId(keyboardRowsResId,
                    mHasSettingsKey ? KEYBOARDMODE_URL_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_URL, true, hasVoice);
        case MODE_EMAIL:
            return new KeyboardId(keyboardRowsResId,
                    mHasSettingsKey ? KEYBOARDMODE_EMAIL_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_EMAIL, true, hasVoice);
        case MODE_IM:
            return new KeyboardId(keyboardRowsResId,
                    mHasSettingsKey ? KEYBOARDMODE_IM_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_IM, true, hasVoice);
        case MODE_WEB:
            return new KeyboardId(keyboardRowsResId,
                    mHasSettingsKey ? KEYBOARDMODE_WEB_WITH_SETTINGS_KEY
                            : KEYBOARDMODE_WEB, true, hasVoice);
        }
        return null;
    }

    public int getKeyboardMode() {
        return mMode;
    }

    public boolean isAlphabetMode() {
        if (mCurrentId == null) {
            return false;
        }
        int currentMode = mCurrentId.mKeyboardMode;
        if (mFullMode > 0 && currentMode == KEYBOARDMODE_NORMAL)
            return true;
        for (Integer mode : ALPHABET_MODES) {
            if (currentMode == mode) {
                return true;
            }
        }
        return false;
    }

    public void setShiftState(int shiftState) {
        if (mInputView != null) {
            mInputView.setShiftState(shiftState);
        }
    }

    public void setFn(boolean useFn) {
        if (mInputView == null) return;
        int oldShiftState = mInputView.getShiftState();
        if (useFn) {
            LatinKeyboard kbd = getKeyboard(mSymbolsId);
            kbd.enableShiftLock();
            mCurrentId = mSymbolsId;
            mInputView.setKeyboard(kbd);
            mInputView.setShiftState(oldShiftState);
        } else {
            // Return to default keyboard state
            setKeyboardMode(mMode, mImeOptions, false);
            mInputView.setShiftState(oldShiftState);
        }
    }

    public void setCtrlIndicator(boolean active) {
        if (mInputView == null) return;
        mInputView.setCtrlIndicator(active);
    }

    public void setAltIndicator(boolean active) {
        if (mInputView == null) return;
        mInputView.setAltIndicator(active);
    }
    
    public void setMetaIndicator(boolean active) {
        if (mInputView == null) return;
        mInputView.setMetaIndicator(active);
    }
    
    public void toggleShift() {
        //Log.i(TAG, "toggleShift isAlphabetMode=" + isAlphabetMode() + " mSettings.fullMode=" + mSettings.fullMode);
        if (isAlphabetMode())
            return;
        if (mFullMode > 0) {
            boolean shifted = mInputView.isShiftAll();
            mInputView.setShiftState(shifted ? Keyboard.SHIFT_OFF : Keyboard.SHIFT_ON);
            return;
        }
        if (mCurrentId.equals(mSymbolsId)
                || !mCurrentId.equals(mSymbolsShiftedId)) {
            LatinKeyboard symbolsShiftedKeyboard = getKeyboard(mSymbolsShiftedId);
            mCurrentId = mSymbolsShiftedId;
            mInputView.setKeyboard(symbolsShiftedKeyboard);
            // Symbol shifted keyboard has a ALT_SYM key that has a caps lock style indicator.
            // To enable the indicator, we need to set the shift state appropriately.
            symbolsShiftedKeyboard.enableShiftLock();
            symbolsShiftedKeyboard.setShiftState(Keyboard.SHIFT_LOCKED);
            symbolsShiftedKeyboard.setImeOptions(mInputMethodService
                    .getResources(), mMode, mImeOptions);
        } else {
            LatinKeyboard symbolsKeyboard = getKeyboard(mSymbolsId);
            mCurrentId = mSymbolsId;
            mInputView.setKeyboard(symbolsKeyboard);
            symbolsKeyboard.enableShiftLock();
            symbolsKeyboard.setShiftState(Keyboard.SHIFT_OFF);
            symbolsKeyboard.setImeOptions(mInputMethodService.getResources(),
                    mMode, mImeOptions);
        }
    }

    public void onCancelInput() {
        // Snap back to the previous keyboard mode if the user cancels sliding
        // input.
        if (mAutoModeSwitchState == AUTO_MODE_SWITCH_STATE_MOMENTARY
                && getPointerCount() == 1)
            mInputMethodService.changeKeyboardMode();
    }

    public void toggleSymbols() {
        setKeyboardMode(mMode, mImeOptions, !mIsSymbols);
        if (mIsSymbols && !mPreferSymbols) {
            mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_SYMBOL_BEGIN;
        } else {
            mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_ALPHA;
        }
    }

    public boolean hasDistinctMultitouch() {
        return mInputView != null && mInputView.hasDistinctMultitouch();
    }

    public void setAutoModeSwitchStateMomentary() {
        mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_MOMENTARY;
    }

    public boolean isInMomentaryAutoModeSwitchState() {
        return mAutoModeSwitchState == AUTO_MODE_SWITCH_STATE_MOMENTARY;
    }

    public boolean isInChordingAutoModeSwitchState() {
        return mAutoModeSwitchState == AUTO_MODE_SWITCH_STATE_CHORDING;
    }

    public boolean isVibrateAndSoundFeedbackRequired() {
        return mInputView != null && !mInputView.isInSlidingKeyInput();
    }

    private int getPointerCount() {
        return mInputView == null ? 0 : mInputView.getPointerCount();
    }

    /**
     * Updates state machine to figure out when to automatically snap back to
     * the previous mode.
     */
    public void onKey(int key) {
        // Switch back to alpha mode if user types one or more non-space/enter
        // characters
        // followed by a space/enter
        switch (mAutoModeSwitchState) {
        case AUTO_MODE_SWITCH_STATE_MOMENTARY:
            // Only distinct multi touch devices can be in this state.
            // On non-distinct multi touch devices, mode change key is handled
            // by {@link onKey},
            // not by {@link onPress} and {@link onRelease}. So, on such
            // devices,
            // {@link mAutoModeSwitchState} starts from {@link
            // AUTO_MODE_SWITCH_STATE_SYMBOL_BEGIN},
            // or {@link AUTO_MODE_SWITCH_STATE_ALPHA}, not from
            // {@link AUTO_MODE_SWITCH_STATE_MOMENTARY}.
            if (key == LatinKeyboard.KEYCODE_MODE_CHANGE) {
                // Detected only the mode change key has been pressed, and then
                // released.
                if (mIsSymbols) {
                    mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_SYMBOL_BEGIN;
                } else {
                    mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_ALPHA;
                }
            } else if (getPointerCount() == 1) {
                // Snap back to the previous keyboard mode if the user pressed
                // the mode change key
                // and slid to other key, then released the finger.
                // If the user cancels the sliding input, snapping back to the
                // previous keyboard
                // mode is handled by {@link #onCancelInput}.
                mInputMethodService.changeKeyboardMode();
            } else {
                // Chording input is being started. The keyboard mode will be
                // snapped back to the
                // previous mode in {@link onReleaseSymbol} when the mode change
                // key is released.
                mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_CHORDING;
            }
            break;
        case AUTO_MODE_SWITCH_STATE_SYMBOL_BEGIN:
            if (key != LatinIME.ASCII_SPACE && key != LatinIME.ASCII_ENTER
                    && key >= 0) {
                mAutoModeSwitchState = AUTO_MODE_SWITCH_STATE_SYMBOL;
            }
            break;
        case AUTO_MODE_SWITCH_STATE_SYMBOL:
            // Snap back to alpha keyboard mode if user types one or more
            // non-space/enter
            // characters followed by a space/enter.
            if (key == LatinIME.ASCII_ENTER || key == LatinIME.ASCII_SPACE) {
                mInputMethodService.changeKeyboardMode();
            }
            break;
        }
    }

    public LatinKeyboardView getInputView() {
        return mInputView;
    }

    public void recreateInputView() {
        changeLatinKeyboardView(mLayoutId, true);
    }

    private void changeLatinKeyboardView(int newLayout, boolean forceReset) {
        if (mLayoutId != newLayout || mInputView == null || forceReset) {
            if (mInputView != null) {
                mInputView.closing();
            }
            if (THEMES.length <= newLayout) {
                newLayout = Integer.valueOf(DEFAULT_LAYOUT_ID);
            }

            LatinIMEUtil.GCUtils.getInstance().reset();
            boolean tryGC = true;
            for (int i = 0; i < LatinIMEUtil.GCUtils.GC_TRY_LOOP_MAX && tryGC; ++i) {
                try {
                    ContextThemeWrapper themeContext = new ContextThemeWrapper(mInputMethodService, STYLES[newLayout]);
                    LayoutInflater inflater = LayoutInflater.from(themeContext);
                    mInputView = (LatinKeyboardView) inflater.inflate(THEMES[newLayout], null);
                    tryGC = false;
                } catch (OutOfMemoryError e) {
                    tryGC = LatinIMEUtil.GCUtils.getInstance().tryGCOrWait(
                            mLayoutId + "," + newLayout, e);
                } catch (InflateException e) {
                    tryGC = LatinIMEUtil.GCUtils.getInstance().tryGCOrWait(
                            mLayoutId + "," + newLayout, e);
                }
            }
            mInputView.setExtensionLayoutResId(THEMES[newLayout]);
            mInputView.setOnKeyboardActionListener(mInputMethodService);

            // Calculate nav bar height for edge-to-edge padding
            int navBarHeight = 0;
            // Only apply this on Oreo+ where we enabled the edge-to-edge flags
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                 int resourceId = mInputMethodService.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                 if (resourceId > 0) {
                     navBarHeight = mInputMethodService.getResources().getDimensionPixelSize(resourceId);
                     // User requested 2.7x the system height for optimal clearance
                     navBarHeight = (int) (navBarHeight * 2.5f);
                 }
            }
            
            mInputView.setPadding(0, 0, 0, navBarHeight);
            mLayoutId = newLayout;

            // Update Navigation Bar Color to match the keyboard theme
            updateNavigationBarColor(newLayout);
        }
        mInputMethodService.mHandler.post(new Runnable() {
            public void run() {
                if (mInputView != null) {
                    mInputMethodService.setInputView(mInputView);
                }
                mInputMethodService.updateInputViewShown();
            }
        });
    }

    private void updateNavigationBarColor(int layoutId) {
        if (mInputMethodService == null || mInputMethodService.getWindow() == null) {
            return;
        }

        // Map layout ID to Tokyo Night theme ID
        // Note: Layout IDs 10, 11, 12, 13 correspond directly to Storm, Night, Day, Moon
        // Older/Legacy IDs map to Storm (default)
        int themeId;
        switch (layoutId) {
            case 11: themeId = TokyoNightPalette.THEME_NIGHT; break;
            case 12: themeId = TokyoNightPalette.THEME_DAY; break;
            case 13: themeId = TokyoNightPalette.THEME_MOON; break;
            default: themeId = TokyoNightPalette.THEME_STORM; break; // Default for 10 and legacy
        }

        TokyoNightPalette.Variant variant = TokyoNightPalette.getVariant(themeId);
        boolean isLight = TokyoNightPalette.isLightTheme(themeId);

        // Apply colors to the window
        // Note: InputMethodService.getWindow() returns the Dialog.
        // We need the Window of that Dialog to set the navigation bar color.
        android.app.Dialog dialog = mInputMethodService.getWindow();
        if (dialog == null) {
            return;
        }
        android.view.Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        
        // Edge-to-Edge: Make navigation bar transparent and layout behind it
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        // Handle light/dark navigation bar icons and edge-to-edge flags
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int flags = window.getDecorView().getSystemUiVisibility();
            
            // Enable edge-to-edge
            flags |= android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            
            if (isLight) {
                flags |= android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flags);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (PREF_KEYBOARD_LAYOUT.equals(key)) {
            changeLatinKeyboardView(Integer.valueOf(sharedPreferences
                    .getString(key, DEFAULT_LAYOUT_ID)), true);
        } else if (PREF_SETTINGS_KEY.equals(key)) {
            updateSettingsKeyState(sharedPreferences);
            recreateInputView();
        } else if (SettingsDefinitions.KEY_KEYBOARD_FONT.equals(key)) {
            recreateInputView();
        }
    }

    public void onAutoCompletionStateChanged(boolean isAutoCompletion) {
        if (isAutoCompletion != mIsAutoCompletionActive) {
            LatinKeyboardView keyboardView = getInputView();
            mIsAutoCompletionActive = isAutoCompletion;
            keyboardView.invalidateKey(((LatinKeyboard) keyboardView
                    .getKeyboard())
                    .onAutoCompletionStateChanged(isAutoCompletion));
        }
    }

    private void updateSettingsKeyState(SharedPreferences prefs) {
        Resources resources = mInputMethodService.getResources();
        final String settingsKeyMode = prefs.getString(
                PREF_SETTINGS_KEY, resources
                        .getString(DEFAULT_SETTINGS_KEY_MODE));
        // We show the settings key when 1) SETTINGS_KEY_MODE_ALWAYS_SHOW or
        // 2) SETTINGS_KEY_MODE_AUTO and there are two or more enabled IMEs on
        // the system
        if (settingsKeyMode.equals(resources
                .getString(SETTINGS_KEY_MODE_ALWAYS_SHOW))
                || (settingsKeyMode.equals(resources
                        .getString(SETTINGS_KEY_MODE_AUTO)))) {
            mHasSettingsKey = true;
        } else {
            mHasSettingsKey = false;
        }
    }
}
