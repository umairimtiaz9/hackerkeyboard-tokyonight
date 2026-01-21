/*
 * Copyright (C) 2010 Google Inc.
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

import org.pocketworkstation.pckeyboard.Keyboard.Key;

/**
 * Key detector optimized for small popup keyboards with minimal nearby key tracking.
 * This detector limits nearby key detection to a maximum of 1 key, making it ideal for
 * compact popup menus where precision and speed are prioritized over proximity correction.
 * 
 * Unlike the main keyboard detectors which track multiple nearby keys for advanced
 * error correction algorithms, this detector finds only the single nearest key to the
 * touch point, resulting in faster detection and cleaner input handling for popup
 * keyboard layouts.
 */
class MiniKeyboardKeyDetector extends KeyDetector {
    private static final int MAX_NEARBY_KEYS = 1;

    private final int mSlideAllowanceSquare;
    private final int mSlideAllowanceSquareTop;

    /**
     * Constructs a MiniKeyboardKeyDetector with the specified slide allowance.
     *
     * @param slideAllowance The allowance distance for slide gestures. This value is squared
     *                       internally to optimize distance calculations.
     */
    public MiniKeyboardKeyDetector(float slideAllowance) {
        super();
        mSlideAllowanceSquare = (int)(slideAllowance * slideAllowance);
        // Top slide allowance is slightly longer (sqrt(2) times) than other edges.
        mSlideAllowanceSquareTop = mSlideAllowanceSquare * 2;
    }

    /**
     * Returns the maximum number of nearby keys that can be detected.
     * For mini keyboards, this is always 1 since only the closest key is tracked.
     *
     * @return The maximum number of nearby keys (1 for mini keyboards)
     */
    @Override
    protected int getMaxNearbyKeys() {
        return MAX_NEARBY_KEYS;
    }

    /**
     * Detects the key at the given touch coordinates and optionally stores nearby key codes.
     * For mini keyboards, this method finds only the single closest key within the slide
     * allowance threshold. The threshold used depends on whether the touch is above the
     * keyboard (top allowance) or elsewhere (standard allowance).
     *
     * @param x The x-coordinate of the touch point
     * @param y The y-coordinate of the touch point
     * @param allKeys Array to store nearby key codes; if provided and a key is found,
     *                the primary code of the nearest key is stored at index 0
     * @return The index of the nearest key, or {@link LatinKeyboardBaseView#NOT_A_KEY}
     *         if no key is within the slide allowance threshold
     */
    @Override
    public int getKeyIndexAndNearbyCodes(int x, int y, int[] allKeys) {
        final Key[] keys = getKeys();
        final int touchX = getTouchX(x);
        final int touchY = getTouchY(y);
        int closestKeyIndex = LatinKeyboardBaseView.NOT_A_KEY;
        int closestKeyDist = (y < 0) ? mSlideAllowanceSquareTop : mSlideAllowanceSquare;
        final int keyCount = keys.length;
        for (int i = 0; i < keyCount; i++) {
            final Key key = keys[i];
            int dist = key.squaredDistanceFrom(touchX, touchY);
            if (dist < closestKeyDist) {
                closestKeyIndex = i;
                closestKeyDist = dist;
            }
        }
        if (allKeys != null && closestKeyIndex != LatinKeyboardBaseView.NOT_A_KEY)
            allKeys[0] = keys[closestKeyIndex].getPrimaryCode();
        return closestKeyIndex;
    }
}
