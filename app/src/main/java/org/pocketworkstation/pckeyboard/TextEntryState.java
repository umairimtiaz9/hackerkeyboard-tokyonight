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
import org.pocketworkstation.pckeyboard.Keyboard.Key;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class TextEntryState {
    
    private static final boolean DBG = false;

    private static final String TAG = "TextEntryState";

    private static boolean LOGGING = false;

    private static int sBackspaceCount = 0;

    private static int sSessionCount = 0;

    private static int sTypedChars;

    private static int sActualChars;

    public enum State {
        UNKNOWN,
        START,
        IN_WORD,
        PUNCTUATION_AFTER_WORD;
    }

    private static State sState = State.UNKNOWN;

    private static FileOutputStream sKeyLocationFile;
    private static FileOutputStream sUserActionFile;
    
    public static void newSession(Context context) {
        sSessionCount++;
        sBackspaceCount = 0;
        sTypedChars = 0;
        sActualChars = 0;
        sState = State.START;

        if (LOGGING) {
            try {
                sKeyLocationFile = context.openFileOutput("key.txt", Context.MODE_APPEND);
                sUserActionFile = context.openFileOutput("action.txt", Context.MODE_APPEND);
            } catch (IOException ioe) {
                Log.e("TextEntryState", "Couldn't open file for output: " + ioe);
            }
        }
    }

    public static void endSession() {
        if (sKeyLocationFile == null) {
            return;
        }
        try {
            sKeyLocationFile.close();
            // Write to log file
            // Write timestamp, settings,
            String out = DateFormat.format("MM:dd hh:mm:ss", Calendar.getInstance().getTime())
                    .toString()
                    + " BS: " + sBackspaceCount
                    + " saved: " + ((float) (sActualChars - sTypedChars) / sActualChars)
                    + "\n";
            sUserActionFile.write(out.getBytes());
            sUserActionFile.close();
            sKeyLocationFile = null;
            sUserActionFile = null;
        } catch (IOException ioe) {

        }
    }
    
    public static void acceptedDefault(CharSequence typedWord, CharSequence actualWord) {
    }

    public static void backToAcceptedDefault(CharSequence typedWord) {
    }

    public static void manualTyped(CharSequence typedWord) {
        sState = State.START;
        displayState();
    }

    public static void acceptedTyped(CharSequence typedWord) {
    }

    public static void selectedForCorrection() {
    }

    public static void typedCharacter(char c, boolean isSeparator) {
        boolean isSpace = c == ' ';
        switch (sState) {
            case IN_WORD:
                if (isSpace || isSeparator) {
                    sState = State.START;
                }
                break;
            case START:
            case UNKNOWN:
            case PUNCTUATION_AFTER_WORD:
                if (!isSpace && !isSeparator) {
                    sState = State.IN_WORD;
                } else {
                    sState = State.START;
                }
                break;
        }
        displayState();
    }

    public static void backspace() {
        sBackspaceCount++;
        displayState();
    }

    public static void reset() {
        sState = State.START;
        displayState();
    }

    public static State getState() {
        if (DBG) {
            Log.d(TAG, "Returning state = " + sState);
        }
        return sState;
    }

    public static boolean isCorrecting() {
        return false;
    }

    public static void keyPressedAt(Key key, int x, int y) {
        if (LOGGING && sKeyLocationFile != null && key.codes[0] >= 32) {
            String out = 
                    "KEY: " + (char) key.codes[0] 
                    + " X: " + x 
                    + " Y: " + y
                    + " MX: " + (key.x + key.width / 2)
                    + " MY: " + (key.y + key.height / 2) 
                    + "\n";
            try {
                sKeyLocationFile.write(out.getBytes());
            } catch (IOException ioe) {
                // TODO: May run out of space
            }
        }
    }

    private static void displayState() {
        if (DBG) {
            //Log.w(TAG, "State = " + sState, new Throwable());
            Log.i(TAG, "State = " + sState);
        }
    }
}

