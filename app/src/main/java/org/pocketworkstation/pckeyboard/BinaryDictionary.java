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

import java.io.InputStream;
import java.nio.ByteBuffer;
import android.content.Context;

/**
 * Implements a static, compacted, binary dictionary of standard words.
 * Removed native implementation.
 */
public class BinaryDictionary extends Dictionary {

    protected static final int MAX_WORD_LENGTH = 48;

    public BinaryDictionary(Context context, int[] resId, int dicTypeId) {
    }

    public BinaryDictionary(Context context, InputStream[] streams, int dicTypeId) {
    }

    public BinaryDictionary(Context context, ByteBuffer byteBuffer, int dicTypeId) {
    }

    @Override
    public void getBigrams(final WordComposer codes, final CharSequence previousWord,
            final WordCallback callback, int[] nextLettersFrequencies) {
    }

    @Override
    public void getWords(final WordComposer codes, final WordCallback callback,
            int[] nextLettersFrequencies) {
    }

    @Override
    public boolean isValidWord(CharSequence word) {
        return false;
    }

    public int getSize() {
        return 0;
    }

    @Override
    public synchronized void close() {
    }
}