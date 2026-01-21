/*
 * Copyright (C) 2011 Darren Salt
 *
 * Licensed under the Apache License, Version 2.0 (the "Licence"); you may
 * not use this file except in compliance with the Licence. You may obtain
 * a copy of the Licence at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

package org.pocketworkstation.pckeyboard;

import java.text.Normalizer;

import android.os.Build;
import android.util.Log;

/**
 * Handles dead key accents and diacritical marks with Unicode normalization support.
 * <p>
 * Extends {@link ComposeSequence} to provide specialized handling for combining diacritical marks
 * (dead keys) such as grave accents, acute accents, circumflex, tilde, and other spacing/non-spacing
 * variants. This class maintains a mapping of compose sequences to their normalized Unicode
 * equivalents.
 * <p>
 * Key features:
 * <ul>
 *   <li>Automatic Unicode NFC normalization for composed characters on Android 2.3+</li>
 *   <li>Support for dead key sequences with spacing and non-spacing variants</li>
 *   <li>Fallback composition handling for unrecognized sequences</li>
 *   <li>Greek dialytika support with tone marks</li>
 * </ul>
 * <p>
 * The class maintains static compose mappings initialized in a static block, covering:
 * <ul>
 *   <li>Standard combining diacritical marks (U+0300–U+0314)</li>
 *   <li>Greek dialytika combinations with tonos</li>
 * </ul>
 * 
 * @see ComposeSequence
 * @see java.text.Normalizer
 */
public class DeadAccentSequence extends ComposeSequence {
    private static final String TAG = "HK/DeadAccent";

    /**
     * Constructs a DeadAccentSequence for handling dead key compositions.
     * 
     * @param user the {@link ComposeSequencing} callback for handling composed text output
     */
    public DeadAccentSequence(ComposeSequencing user) {
        super(user);
    }
    
    /**
     * Registers a dead key accent mapping with spacing and ASCII variants.
     * <p>
     * Stores multiple composition rules for a single diacritical mark:
     * <ul>
     *   <li>Combines the non-spacing mark with space to produce ASCII representation</li>
     *   <li>Combines the non-spacing mark with itself to produce spacing variant</li>
     *   <li>Combines the dead key placeholder with non-spacing mark to produce spacing variant</li>
     * </ul>
     * 
     * @param nonSpacing the combining (non-spacing) diacritical mark character
     * @param spacing the spacing variant of the diacritical mark (standalone representation)
     * @param ascii optional ASCII fallback representation (uses spacing if null)
     */
    private static void putAccent(String nonSpacing, String spacing, String ascii) {
        if (ascii == null) ascii = spacing;
        put("" + nonSpacing + " ", ascii);
        put(nonSpacing + nonSpacing, spacing);
        put(Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing, spacing);
    }
    
    /**
     * Retrieves the spacing variant for a combining diacritical mark.
     * <p>
     * Attempts to find a registered spacing representation for a non-spacing combining mark.
     * If no explicit mapping exists, attempts Unicode normalization. Falls back to the
     * non-spacing character itself if normalization produces no result.
     * 
     * @param nonSpacing the combining (non-spacing) diacritical mark character
     * @return the spacing variant representation, or the original character if no mapping exists
     */
    public static String getSpacing(char nonSpacing) {
        String spacing = get("" + Keyboard.DEAD_KEY_PLACEHOLDER + nonSpacing);
        if (spacing == null) spacing = DeadAccentSequence.normalize(" " + nonSpacing);
        if (spacing == null) return "" + nonSpacing;
        return spacing;
    }
    
    static {
        // space + combining diacritical
        // cf. http://unicode.org/charts/PDF/U0300.pdf
        putAccent("\u0300", "\u02cb", "`");  // grave
        putAccent("\u0301", "\u02ca", "´");  // acute
        putAccent("\u0302", "\u02c6", "^");  // circumflex
        putAccent("\u0303", "\u02dc", "~");  // small tilde
        putAccent("\u0304", "\u02c9", "¯");  // macron
        putAccent("\u0305", "\u00af", "¯");  // overline
        putAccent("\u0306", "\u02d8", null);  // breve
        putAccent("\u0307", "\u02d9", null);  // dot above
        putAccent("\u0308", "\u00a8", "¨");  // diaeresis
        putAccent("\u0309", "\u02c0", null);  // hook above
        putAccent("\u030a", "\u02da", "°");  // ring above
        putAccent("\u030b", "\u02dd", "\"");  // double acute 
        putAccent("\u030c", "\u02c7", null);  // caron
        putAccent("\u030d", "\u02c8", null);  // vertical line above
        putAccent("\u030e", "\"", "\"");  // double vertical line above
        putAccent("\u0313", "\u02bc", null);  // comma above
        putAccent("\u0314", "\u02bd", null);  // reversed comma above

        put("\u0308\u0301\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u0308\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u03ca", "\u0390");        // Greek Dialytika+Tonos, iota
        put("\u0308\u0301\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u0308\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u03cb", "\u03b0");        // Greek Dialytika+Tonos, upsilon
   }

	/**
	 * Applies Unicode NFC normalization to the input string.
	 * <p>
	 * Uses Java's {@link Normalizer} for Unicode NFC (Canonical Decomposition, followed by
	 * Canonical Composition) on Android 2.3 and later. Returns the input unchanged on
	 * earlier Android versions.
	 * 
	 * @param input the string to normalize
	 * @return the NFC-normalized string (or the input unchanged on older Android versions)
	 */
	private static String doNormalise(String input)
	{
		if (Build.VERSION.SDK_INT >= 9) {
			return Normalizer.normalize(input, Normalizer.Form.NFC);
		}
		return input;
	}

    /**
     * Normalizes a string using registered compose mappings or Unicode normalization.
     * <p>
     * Performs two-level normalization:
     * <ol>
     *   <li>Checks for an explicit registered mapping in the compose table</li>
     *   <li>Falls back to Unicode NFC normalization via {@link #doNormalise(String)}</li>
     * </ol>
     * This ensures that both pre-composed dead key sequences and standard Unicode
     * composable strings are properly handled.
     * 
     * @param input the string to normalize
     * @return the normalized string from the compose table, or the NFC-normalized result
     */
    public static String normalize(String input) {
    	String lookup = mMap.get(input);
        return lookup != null ? lookup : doNormalise(input);
    }
    
    /**
    * Executes the composition of a dead key with the next character.
    * <p>
    * Processes the next character code in the dead key composition sequence. Handles composition
    * in the following order:
    * <ol>
    *   <li>Attempts to find a pre-registered compose mapping via {@link #executeToString(int)}</li>
    *   <li>If mapping returns empty string (unrecognized), attempts Unicode NFC normalization
    *       by reversing the compose buffer and applying {@link #doNormalise(String)}</li>
    *   <li>Handles multiple combining accents by returning incomplete status</li>
    * </ol>
    * <p>
    * On successful composition, clears the compose buffer and sends the composed string to the
    * UI via {@link #composeUser}. Unrecognized or incomplete sequences return true to indicate
    * the sequence should be continued.
    * 
    * @param code the character code to compose with the current dead key
    * @return true if the sequence is incomplete or unrecognized (continue composing);
    *         false if the composition was successful and output sent to UI
    */
    public boolean execute(int code) {
    String composed = executeToString(code);
    if (composed != null) {
    //Log.i(TAG, "composed=" + composed + " len=" + composed.length());
    if (composed.equals("")) {
    // Unrecognised - try to use the built-in Java text normalisation
    int c = composeBuffer.codePointAt(composeBuffer.length() - 1);
    if (Character.getType(c) != Character.NON_SPACING_MARK) {
    StringBuilder buildComposed = new StringBuilder(10);
    buildComposed.append(composeBuffer);
    // FIXME? Put the combining character(s) temporarily at the end, else this won't work
    composed = doNormalise(buildComposed.reverse().toString());
    if (composed.equals("")) {
    return true; // incomplete :-)
    }
    } else {
    return true; // there may be multiple combining accents
    }
    }

    clear();
    composeUser.onText(composed);
    return false;
    }
    return true;
    }
}
