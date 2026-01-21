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
package org.pocketworkstation.pckeyboard

/**
 * Tracks the state of modifier keys (shift, ctrl, alt, etc.) to detect single-press,
 * chord, and long-press behaviors.
 *
 * The state machine manages three states:
 * - RELEASING: The modifier key is released or idle
 * - PRESSING: The modifier key is pressed but no other key has been pressed yet
 * - CHORDING: The modifier key is pressed and another key is being pressed simultaneously
 *
 * This allows the keyboard to distinguish between:
 * - A single modifier key press (not chording)
 * - A chord where the modifier is combined with another key (chording detected)
 */
class ModifierKeyState {

    private companion object {
        private const val RELEASING = 0
        private const val PRESSING = 1
        private const val CHORDING = 2
    }

    private var state: Int = RELEASING

    /**
     * Called when the modifier key is pressed.
     * Transitions the state from RELEASING to PRESSING.
     */
    fun onPress() {
        state = PRESSING
    }

    /**
     * Called when the modifier key is released.
     * Transitions the state back to RELEASING.
     */
    fun onRelease() {
        state = RELEASING
    }

    /**
     * Called when another key is pressed while the modifier key is held.
     * If the current state is PRESSING, transitions to CHORDING.
     */
    fun onOtherKeyPressed() {
        if (state == PRESSING) state = CHORDING
    }

    /**
     * Checks if the modifier key is currently involved in a chord.
     * @return true if a chord is being performed, false otherwise
     */
    fun isChording(): Boolean = state == CHORDING

    override fun toString(): String = "ModifierKeyState:$state"
}
