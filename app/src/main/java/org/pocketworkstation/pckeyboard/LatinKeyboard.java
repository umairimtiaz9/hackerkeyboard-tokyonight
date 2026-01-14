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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;

import java.util.List;
import java.util.Locale;

public class LatinKeyboard extends Keyboard {

    private static final boolean DEBUG_PREFERRED_LETTER = true;
    private static final String TAG = "PCKeyboardLK";
    private static final int OPACITY_FULLY_OPAQUE = 255;
    private static final int SPACE_LED_LENGTH_PERCENT = 80;

    private Drawable mShiftLockIcon;
    private Drawable mShiftLockPreviewIcon;
    private Drawable mOldShiftIcon;
    private Drawable mSpaceIcon;
    private Drawable mSpaceAutoCompletionIndicator;
    private Drawable mSpacePreviewIcon;
    private Drawable mMicIcon;
    private Drawable mMicPreviewIcon;
    private Drawable mSettingsIcon;
    private Drawable mSettingsPreviewIcon;
    private Drawable m123MicIcon;
    private Drawable m123MicPreviewIcon;
    private final Drawable mButtonArrowLeftIcon;
    private final Drawable mButtonArrowRightIcon;
    private Key mShiftKey;
    private Key mEnterKey;
    private Key mF1Key;
    private final Drawable mHintIcon;
    private Key mSpaceKey;
    private Key m123Key;
    private final int[] mSpaceKeyIndexArray;
    private int mSpaceDragStartX;
    private int mSpaceDragLastDiff;
    private final Resources mRes;
    private final Context mContext;
    private int mMode;
    // Whether this keyboard has voice icon on it
    // private boolean mHasVoiceButton;
    // Whether voice icon is enabled at all
    // private boolean mVoiceEnabled;
    private final boolean mIsAlphaKeyboard;
    private final boolean mIsAlphaFullKeyboard;
    private final boolean mIsFnFullKeyboard;
    private CharSequence m123Label;
    private boolean mCurrentlyInSpace;
    private int[] mPrefLetterFrequencies;
    private int mPrefLetter;
    private int mPrefLetterX;
    private int mPrefLetterY;
    private int mPrefDistance;

    private int mExtensionResId;

    // TODO: remove this attribute when either Keyboard.mDefaultVerticalGap or Key.parent becomes
    // non-private.
    private final int mVerticalGap;

    private LatinKeyboard mExtensionKeyboard;

    private static final float SPACEBAR_DRAG_THRESHOLD = 0.51f;
    private static final float OVERLAP_PERCENTAGE_LOW_PROB = 0.70f;
    private static final float OVERLAP_PERCENTAGE_HIGH_PROB = 0.85f;
    // Minimum width of space key preview (proportional to keyboard width)
    private static final float SPACEBAR_POPUP_MIN_RATIO = 0.4f;
    // Minimum width of space key preview (proportional to screen height)
    private static final float SPACEBAR_POPUP_MAX_RATIO = 0.4f;
    // Height in space key the language name will be drawn. (proportional to space key height)
    private static final float SPACEBAR_LANGUAGE_BASELINE = 0.6f;
    // If the full language name needs to be smaller than this value to be drawn on space key,
    // its short language name will be used instead.
    private static final float MINIMUM_SCALE_OF_LANGUAGE_NAME = 0.8f;

    private static int sSpacebarVerticalCorrection;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        this(context, xmlLayoutResId, 0, 0);
    }

    public LatinKeyboard(Context context, int xmlLayoutResId, int mode, float kbHeightPercent) {
        super(context, 0, xmlLayoutResId, mode, kbHeightPercent);
        final Resources res = context.getResources();
        //Log.i("PCKeyboard", "keyHeight=" + this.getKeyHeight());
        //this.setKeyHeight(30); // is useless, see http://code.google.com/p/android/issues/detail?id=4532
        mContext = context;
        mMode = mode;
        mRes = res;
        mShiftLockIcon = res.getDrawable(R.drawable.sym_keyboard_shift_locked);
        mShiftLockPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_shift_locked);
        setDefaultBounds(mShiftLockPreviewIcon);
        mSpaceIcon = res.getDrawable(R.drawable.sym_keyboard_space);
        mSpaceAutoCompletionIndicator = res.getDrawable(R.drawable.sym_keyboard_space_led);
        mSpacePreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_space);
        // mMICIcon and m123MicIcon no longer used but kept to avoid variable removal issues if referenced elsewhere
        mMicIcon = res.getDrawable(R.drawable.sym_keyboard_mic);
        mMicPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_mic);
        mSettingsIcon = res.getDrawable(R.drawable.sym_keyboard_settings);
        mSettingsPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_settings);
        setDefaultBounds(mMicPreviewIcon);
        mButtonArrowLeftIcon = res.getDrawable(R.drawable.sym_keyboard_language_arrows_left);
        mButtonArrowRightIcon = res.getDrawable(R.drawable.sym_keyboard_language_arrows_right);
        m123MicIcon = res.getDrawable(R.drawable.sym_keyboard_123_mic);
        m123MicPreviewIcon = res.getDrawable(R.drawable.sym_keyboard_feedback_123_mic);
        mHintIcon = res.getDrawable(R.drawable.hint_popup);
        setDefaultBounds(m123MicPreviewIcon);
        sSpacebarVerticalCorrection = res.getDimensionPixelOffset(
                R.dimen.spacebar_vertical_correction);
        mIsAlphaKeyboard = xmlLayoutResId == R.xml.kbd_qwerty;
        mIsAlphaFullKeyboard = xmlLayoutResId == R.xml.kbd_full;
        mIsFnFullKeyboard = xmlLayoutResId == R.xml.kbd_full_fn || xmlLayoutResId == R.xml.kbd_compact_fn;
        // The index of space key is available only after Keyboard constructor has finished.
        mSpaceKeyIndexArray = new int[] { indexOf(LatinIME.ASCII_SPACE) };
        // TODO remove this initialization after cleanup
        mVerticalGap = super.getVerticalGap();
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
            XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes == null) return key;
        switch (key.codes[0]) {
        case LatinIME.ASCII_ENTER:
            mEnterKey = key;
            break;
        case LatinKeyboardView.KEYCODE_F1:
            mF1Key = key;
            break;
        case LatinIME.ASCII_SPACE:
            mSpaceKey = key;
            break;
        case KEYCODE_MODE_CHANGE:
            m123Key = key;
            m123Label = key.label;
            break;
        }

        return key;
    }

    void setImeOptions(Resources res, int mode, int options) {
        mMode = mode;
        // TODO should clean up this method
        if (mEnterKey != null) {
            // Reset some of the rarely used attributes.
            mEnterKey.popupCharacters = null;
            mEnterKey.popupResId = 0;
            mEnterKey.text = null;
            switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                case EditorInfo.IME_ACTION_GO:
                    mEnterKey.iconPreview = null;
                    mEnterKey.icon = null;
                    mEnterKey.label = res.getText(R.string.label_go_key);
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    mEnterKey.iconPreview = null;
                    mEnterKey.icon = null;
                    mEnterKey.label = res.getText(R.string.label_next_key);
                    break;
                case EditorInfo.IME_ACTION_DONE:
                    mEnterKey.iconPreview = null;
                    mEnterKey.icon = null;
                    mEnterKey.label = res.getText(R.string.label_done_key);
                    break;
                case EditorInfo.IME_ACTION_SEARCH:
                    mEnterKey.iconPreview = res.getDrawable(
                            R.drawable.sym_keyboard_feedback_search);
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_SEND:
                    mEnterKey.iconPreview = null;
                    mEnterKey.icon = null;
                    mEnterKey.label = res.getText(R.string.label_send_key);
                    break;
                default:
                    // Keep Return key in IM mode, we have a dedicated smiley key.
                    mEnterKey.iconPreview = res.getDrawable(
                            R.drawable.sym_keyboard_feedback_return);
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                    mEnterKey.label = null;
                    break;
            }
            // Set the initial size of the preview icon
            if (mEnterKey.iconPreview != null) {
                setDefaultBounds(mEnterKey.iconPreview);
            }
        }
    }

    void enableShiftLock() {
        int index = getShiftKeyIndex();
        if (index >= 0) {
            mShiftKey = getKeys().get(index);
            mOldShiftIcon = mShiftKey.icon;
        }
    }

    @Override
    public boolean setShiftState(int shiftState) {
        if (mShiftKey != null) {
            // Tri-state LED tracks "on" and "lock" states, icon shows Caps state.
            mShiftKey.on = shiftState == SHIFT_ON || shiftState == SHIFT_LOCKED;
            mShiftKey.locked = shiftState == SHIFT_LOCKED || shiftState == SHIFT_CAPS_LOCKED;
            mShiftKey.icon = (shiftState == SHIFT_OFF || shiftState == SHIFT_ON || shiftState == SHIFT_LOCKED) ?
                    mOldShiftIcon : mShiftLockIcon;
            return super.setShiftState(shiftState, false);
        } else {
            return super.setShiftState(shiftState, true);
        }
    }

    /* package */ boolean isAlphaKeyboard() {
        return mIsAlphaKeyboard;
    }

    public void setExtension(LatinKeyboard extKeyboard) {
        mExtensionKeyboard = extKeyboard;
    }

    public LatinKeyboard getExtension() {
        return mExtensionKeyboard;
    }

    public void updateSymbolIcons(boolean isAutoCompletion) {
        updateDynamicKeys();
        updateSpaceBarForLocale(isAutoCompletion);
    }

    private void setDefaultBounds(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public void setVoiceMode(boolean hasVoiceButton, boolean hasVoice) {
        // Voice mode removed
        updateDynamicKeys();
    }

    private void updateDynamicKeys() {
        update123Key();
        updateF1Key();
    }

    private void update123Key() {
        // Update KEYCODE_MODE_CHANGE key only on alphabet mode, not on symbol mode.
        if (m123Key != null && mIsAlphaKeyboard) {
            m123Key.icon = null;
            m123Key.iconPreview = null;
            m123Key.label = m123Label;
        }
    }

    private void updateF1Key() {
        // Update KEYCODE_F1 key. Please note that some keyboard layouts have no F1 key.
        if (mF1Key == null)
            return;

        if (mIsAlphaKeyboard) {
            if (mMode == KeyboardSwitcher.MODE_URL) {
                setNonMicF1Key(mF1Key, "/", R.xml.popup_slash);
            } else if (mMode == KeyboardSwitcher.MODE_EMAIL) {
                setNonMicF1Key(mF1Key, "@", R.xml.popup_at);
            } else {
                setNonMicF1Key(mF1Key, ",", R.xml.popup_comma);
            }
        } else if (mIsAlphaFullKeyboard) {
            setSettingsF1Key(mF1Key);
        } else if (mIsFnFullKeyboard) {
            // No mic key support
        } else {  // Symbols keyboard
            setNonMicF1Key(mF1Key, ",", R.xml.popup_comma);
        }
    }

    private void setMicF1Key(Key key) {
        // Voice support removed
    }

    private void setSettingsF1Key(Key key) {
        if (key.shiftLabel != null && key.label != null) {
            key.codes = new int[] { key.label.charAt(0) };
            return; // leave key otherwise unmodified
        }
        final Drawable settingsHintDrawable = new BitmapDrawable(mRes,
                drawSynthesizedSettingsHintImage(key.width, key.height, mSettingsIcon, mHintIcon));
    	key.label = null;
    	key.icon = settingsHintDrawable;
    	key.codes = new int[] { LatinKeyboardView.KEYCODE_OPTIONS };
    	key.popupResId = R.xml.popup_mic;
    	key.iconPreview = mSettingsPreviewIcon;
    }
    
    private void setNonMicF1Key(Key key, String label, int popupResId) {
        if (key.shiftLabel != null) {
            key.codes = new int[] { key.label.charAt(0) };
            return; // leave key unmodified
        }
        key.label = label;
        key.codes = new int[] { label.charAt(0) };
        key.popupResId = popupResId;
        key.icon = mHintIcon;
        key.iconPreview = null;
    }

    public boolean isF1Key(Key key) {
        return key == mF1Key;
    }

    public static boolean hasPuncOrSmileysPopup(Key key) {
        return key.popupResId == R.xml.popup_punctuation || key.popupResId == R.xml.popup_smileys;
    }

    /**
     * @return a key which should be invalidated.
     */
    public Key onAutoCompletionStateChanged(boolean isAutoCompletion) {
        updateSpaceBarForLocale(isAutoCompletion);
        return mSpaceKey;
    }

    public boolean isLanguageSwitchEnabled() {
        return false;
    }

    private String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void updateSpaceBarForLocale(boolean isAutoCompletion) {
        if (mSpaceKey == null) return;
        mSpaceKey.icon = new BitmapDrawable(mRes, drawSpaceBar(OPACITY_FULLY_OPAQUE, isAutoCompletion));
    }

    // Compute width of text with specified text size using paint.
    private static int getTextWidth(Paint paint, String text, float textSize, Rect bounds) {
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    // Overlay two images: mainIcon and hintIcon.
    private Bitmap drawSynthesizedSettingsHintImage(
            int width, int height, Drawable mainIcon, Drawable hintIcon) {
        if (mainIcon == null || hintIcon == null)
            return null;
        Rect hintIconPadding = new Rect(0, 0, 0, 0);
        hintIcon.getPadding(hintIconPadding);
        final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(mRes.getColor(R.color.latinkeyboard_transparent), PorterDuff.Mode.CLEAR);

        // Draw main icon at the center of the key visual
        // Assuming the hintIcon shares the same padding with the key's background drawable
        final int drawableX = (width + hintIconPadding.left - hintIconPadding.right
                - mainIcon.getIntrinsicWidth()) / 2;
        final int drawableY = (height + hintIconPadding.top - hintIconPadding.bottom
                - mainIcon.getIntrinsicHeight()) / 2;
        setDefaultBounds(mainIcon);
        canvas.translate(drawableX, drawableY);
        mainIcon.draw(canvas);
        canvas.translate(-drawableX, -drawableY);

        // Draw hint icon fully in the key
        hintIcon.setBounds(0, 0, width, height);
        hintIcon.draw(canvas);
        return buffer;
    }

    private Bitmap drawSpaceBar(int opacity, boolean isAutoCompletion) {
        final int width = mSpaceKey.width;
        final int height = mSpaceIcon.getIntrinsicHeight();
        final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(mRes.getColor(R.color.latinkeyboard_transparent), PorterDuff.Mode.CLEAR);

        // Draw the spacebar icon at the bottom
        if (isAutoCompletion) {
            final int iconWidth = width * SPACE_LED_LENGTH_PERCENT / 100;
            final int iconHeight = mSpaceAutoCompletionIndicator.getIntrinsicHeight();
            int x = (width - iconWidth) / 2;
            int y = height - iconHeight;
            mSpaceAutoCompletionIndicator.setBounds(x, y, x + iconWidth, y + iconHeight);
            mSpaceAutoCompletionIndicator.draw(canvas);
        } else {
            final int iconWidth = mSpaceIcon.getIntrinsicWidth();
            final int iconHeight = mSpaceIcon.getIntrinsicHeight();
            int x = (width - iconWidth) / 2;
            int y = height - iconHeight;
            mSpaceIcon.setBounds(x, y, x + iconWidth, y + iconHeight);
            mSpaceIcon.draw(canvas);
        }
        return buffer;
    }

    private int getSpacePreviewWidth() {
        final int width = Math.min(
        		Math.max(mSpaceKey.width, (int)(getMinWidth() * SPACEBAR_POPUP_MIN_RATIO)), 
        		(int)(getScreenHeight() * SPACEBAR_POPUP_MAX_RATIO));
        return width;
    }
    
    private void updateLocaleDrag(int diff) {
        // Language switching is removed, so this method does nothing.
    }

    public int getLanguageChangeDirection() {
        return 0; // Language switching is removed
    }


    boolean isCurrentlyInSpace() {
        return mCurrentlyInSpace;
    }

    void setPreferredLetters(int[] frequencies) {
        mPrefLetterFrequencies = frequencies;
        mPrefLetter = 0;
    }

    void keyReleased() {
        mCurrentlyInSpace = false;
        mSpaceDragLastDiff = 0;
        mPrefLetter = 0;
        mPrefLetterX = 0;
        mPrefLetterY = 0;
        mPrefDistance = Integer.MAX_VALUE;
        // No language switcher, so no need to update locale drag
    }

    /**
     * Does the magic of locking the touch gesture into the spacebar when
     * switching input languages.
     */
    boolean isInside(LatinKey key, int x, int y) {
        final int code = key.codes[0];
        if (code == KEYCODE_SHIFT ||
                code == KEYCODE_DELETE) {
        	// Adjust target area for these keys
            y -= key.height / 10;
            if (code == KEYCODE_SHIFT) {
            	if (key.x == 0) {
            		x += key.width / 6;  // left shift
            	} else {
            		x -= key.width / 6;  // right shift
            	}
            }
            if (code == KEYCODE_DELETE) x -= key.width / 6;
        } else if (code == LatinIME.ASCII_SPACE) {
            y += LatinKeyboard.sSpacebarVerticalCorrection;
        } else if (mPrefLetterFrequencies != null) {
            // New coordinate? Reset
            if (mPrefLetterX != x || mPrefLetterY != y) {
                mPrefLetter = 0;
                mPrefDistance = Integer.MAX_VALUE;
            }
            // Handle preferred next letter
            final int[] pref = mPrefLetterFrequencies;
            if (mPrefLetter > 0) {
                if (DEBUG_PREFERRED_LETTER) {
                    if (mPrefLetter == code && !key.isInsideSuper(x, y)) {
                        Log.d(TAG, "CORRECTED !!!!!!");
                    }
                }
                return mPrefLetter == code;
            } else {
                final boolean inside = key.isInsideSuper(x, y);
                int[] nearby = getNearestKeys(x, y);
                List<Key> nearbyKeys = getKeys();
                if (inside) {
                    // If it's a preferred letter
                    if (inPrefList(code, pref)) {
                        // Check if its frequency is much lower than a nearby key
                        mPrefLetter = code;
                        mPrefLetterX = x;
                        mPrefLetterY = y;
                        for (int i = 0; i < nearby.length; i++) {
                            Key k = nearbyKeys.get(nearby[i]);
                            if (k != key && inPrefList(k.codes[0], pref)) {
                                final int dist = distanceFrom(k, x, y);
                                if (dist < (int) (k.width * OVERLAP_PERCENTAGE_LOW_PROB) &&
                                        (pref[k.codes[0]] > pref[mPrefLetter] * 3))  {
                                    mPrefLetter = k.codes[0];
                                    mPrefDistance = dist;
                                    if (DEBUG_PREFERRED_LETTER) {
                                        Log.d(TAG, "CORRECTED ALTHOUGH PREFERRED !!!!!!");
                                    }
                                    break;
                                }
                            }
                        }

                        return mPrefLetter == code;
                    }
                }

                // Get the surrounding keys and intersect with the preferred list
                // For all in the intersection
                //   if distance from touch point is within a reasonable distance
                //       make this the pref letter
                // If no pref letter
                //   return inside;
                // else return thiskey == prefletter;

                for (int i = 0; i < nearby.length; i++) {
                    Key k = nearbyKeys.get(nearby[i]);
                    if (inPrefList(k.codes[0], pref)) {
                        final int dist = distanceFrom(k, x, y);
                        if (dist < (int) (k.width * OVERLAP_PERCENTAGE_HIGH_PROB)
                                && dist < mPrefDistance)  {
                            mPrefLetter = k.codes[0];
                            mPrefLetterX = x;
                            mPrefLetterY = y;
                            mPrefDistance = dist;
                        }
                    }
                }
                // Didn't find any
                if (mPrefLetter == 0) {
                    return inside;
                } else {
                    return mPrefLetter == code;
                }
            }
        }


        return key.isInsideSuper(x, y);
    }

    private boolean inPrefList(int code, int[] pref) {
        if (code < pref.length && code >= 0) return pref[code] > 0;
        return false;
    }

    private int distanceFrom(Key k, int x, int y) {
        if (y > k.y && y < k.y + k.height) {
            return Math.abs(k.x + k.width / 2 - x);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public int[] getNearestKeys(int x, int y) {
        if (mCurrentlyInSpace) {
            return mSpaceKeyIndexArray;
        } else {
            // Avoid dead pixels at edges of the keyboard
            return super.getNearestKeys(Math.max(0, Math.min(x, getMinWidth() - 1)),
                    Math.max(0, Math.min(y, getHeight() - 1)));
        }
    }

    private int indexOf(int code) {
        List<Key> keys = getKeys();
        int count = keys.size();
        for (int i = 0; i < count; i++) {
            if (keys.get(i).codes[0] == code) return i;
        }
        return -1;
    }

    private int getTextSizeFromTheme(int style, int defValue) {
        TypedArray array = mContext.getTheme().obtainStyledAttributes(
                style, new int[] { android.R.attr.textSize });
        int resId = array.getResourceId(0, 0);
        if (resId >= array.length()) {
            Log.i(TAG, "getTextSizeFromTheme error: resId " + resId + " > " + array.length());
            return defValue;
        }
        int textSize = array.getDimensionPixelSize(resId, defValue);
        return textSize;
    }

    // TODO LatinKey could be static class
    class LatinKey extends Key {

        // functional normal state (with properties)
        private final int[] KEY_STATE_FUNCTIONAL_NORMAL = {
                android.R.attr.state_single
        };

        // functional pressed state (with properties)
        private final int[] KEY_STATE_FUNCTIONAL_PRESSED = {
                android.R.attr.state_single,
                android.R.attr.state_pressed
        };

        // functional checked state (for sticky modifiers like Shift/Alt/Ctrl)
        private final int[] KEY_STATE_FUNCTIONAL_CHECKED = {
                android.R.attr.state_single,
                android.R.attr.state_checked
        };

        // functional pressed + checked state
        private final int[] KEY_STATE_FUNCTIONAL_PRESSED_CHECKED = {
                android.R.attr.state_single,
                android.R.attr.state_pressed,
                android.R.attr.state_checked
        };

        public LatinKey(Resources res, Keyboard.Row parent, int x, int y,
                XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        // functional is used for styling.
        private boolean isFunctionalKey() {
            return modifier;
        }

        /**
         * Overriding this method so that we can reduce the target area for certain keys.
         */
        @Override
        public boolean isInside(int x, int y) {
            // TODO This should be done by parent.isInside(this, x, y)
            // if Key.parent were protected.
            boolean result = LatinKeyboard.this.isInside(this, x, y);
            return result;
        }

        boolean isInsideSuper(int x, int y) {
            return super.isInside(x, y);
        }

        @Override
        public int[] getCurrentDrawableState() {
            if (isFunctionalKey()) {
                if (pressed) {
                    return (sticky && (on || locked)) ? KEY_STATE_FUNCTIONAL_PRESSED_CHECKED : KEY_STATE_FUNCTIONAL_PRESSED;
                } else {
                    return (sticky && (on || locked)) ? KEY_STATE_FUNCTIONAL_CHECKED : KEY_STATE_FUNCTIONAL_NORMAL;
                }
            }
            return super.getCurrentDrawableState();
        }

        @Override
        public int squaredDistanceFrom(int x, int y) {
            // We should count vertical gap between rows to calculate the center of this Key.
            final int verticalGap = LatinKeyboard.this.mVerticalGap;
            final int xDist = this.x + width / 2 - x;
            final int yDist = this.y + (height + verticalGap) / 2 - y;
            return xDist * xDist + yDist * yDist;
        }
    }

}
