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

import android.view.MotionEvent;

/**
 * Tracks finger swipe motion by recording movement history and computing velocity.
 * Uses a 4-event ring buffer to maintain the most recent touch points, allowing
 * velocity calculations based on the movement between events.
 */
class SwipeTracker {
    private static final int NUM_PAST = 4;
    private static final int LONGEST_PAST_TIME = 200;

    final EventRingBuffer mBuffer = new EventRingBuffer(NUM_PAST);

    private float mYVelocity;
    private float mXVelocity;

    /**
     * Records a motion event in the movement history buffer.
     * On ACTION_DOWN, clears the buffer and returns. For other actions,
     * processes all historical events and the current event within the
     * time window to maintain velocity computation accuracy.
     *
     * @param ev the MotionEvent to record
     */
    public void addMovement(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mBuffer.clear();
            return;
        }
        long time = ev.getEventTime();
        final int count = ev.getHistorySize();
        for (int i = 0; i < count; i++) {
            addPoint(ev.getHistoricalX(i), ev.getHistoricalY(i), ev.getHistoricalEventTime(i));
        }
        addPoint(ev.getX(), ev.getY(), time);
    }

    private void addPoint(float x, float y, long time) {
        final EventRingBuffer buffer = mBuffer;
        while (buffer.size() > 0) {
            long lastT = buffer.getTime(0);
            if (lastT >= time - LONGEST_PAST_TIME)
                break;
            buffer.dropOldest();
        }
        buffer.add(x, y, time);
    }

    /**
     * Computes the current velocity from the movement history buffer.
     * Velocity is calculated as an exponential moving average of instantaneous
     * velocities between each recorded point. Results are available via
     * {@link #getXVelocity()} and {@link #getYVelocity()}.
     *
     * @param units the multiplier for velocity (e.g., pixels per time unit)
     */
    public void computeCurrentVelocity(int units) {
        computeCurrentVelocity(units, Float.MAX_VALUE);
    }

    /**
     * Computes the current velocity from the movement history buffer with
     * a maximum velocity constraint.
     * Velocity is calculated as an exponential moving average of instantaneous
     * velocities between each recorded point, clamped to the specified maximum.
     *
     * @param units the multiplier for velocity (e.g., pixels per time unit)
     * @param maxVelocity the maximum allowed velocity magnitude
     */
    public void computeCurrentVelocity(int units, float maxVelocity) {
        final EventRingBuffer buffer = mBuffer;
        final float oldestX = buffer.getX(0);
        final float oldestY = buffer.getY(0);
        final long oldestTime = buffer.getTime(0);

        float accumX = 0;
        float accumY = 0;
        final int count = buffer.size();
        for (int pos = 1; pos < count; pos++) {
            final int dur = (int)(buffer.getTime(pos) - oldestTime);
            if (dur == 0) continue;
            float dist = buffer.getX(pos) - oldestX;
            float vel = (dist / dur) * units;   // pixels/frame.
            if (accumX == 0) accumX = vel;
            else accumX = (accumX + vel) * .5f;

            dist = buffer.getY(pos) - oldestY;
            vel = (dist / dur) * units;   // pixels/frame.
            if (accumY == 0) accumY = vel;
            else accumY = (accumY + vel) * .5f;
        }
        mXVelocity = accumX < 0.0f ? Math.max(accumX, -maxVelocity)
                : Math.min(accumX, maxVelocity);
        mYVelocity = accumY < 0.0f ? Math.max(accumY, -maxVelocity)
                : Math.min(accumY, maxVelocity);
    }

    /**
     * Returns the X-axis velocity computed from the recent movement history.
     * Valid only after calling {@link #computeCurrentVelocity(int)} or
     * {@link #computeCurrentVelocity(int, float)}.
     *
     * @return the X velocity in pixels per unit
     */
    public float getXVelocity() {
        return mXVelocity;
    }

    /**
     * Returns the Y-axis velocity computed from the recent movement history.
     * Valid only after calling {@link #computeCurrentVelocity(int)} or
     * {@link #computeCurrentVelocity(int, float)}.
     *
     * @return the Y velocity in pixels per unit
     */
    public float getYVelocity() {
        return mYVelocity;
    }

    /**
     * A circular ring buffer that stores recent motion events (position and timestamp).
     * Maintains up to {@link SwipeTracker#NUM_PAST} events, automatically discarding
     * the oldest event when the buffer reaches capacity. Provides efficient access to
     * events by relative index where 0 is the oldest event.
     */
    static class EventRingBuffer {
        private final int bufSize;
        private final float xBuf[];
        private final float yBuf[];
        private final long timeBuf[];
        private int top;  // points new event
        private int end;  // points oldest event
        private int count; // the number of valid data

        public EventRingBuffer(int max) {
            this.bufSize = max;
            xBuf = new float[max];
            yBuf = new float[max];
            timeBuf = new long[max];
            clear();
        }

        public void clear() {
            top = end = count = 0;
        }

        public int size() {
            return count;
        }

        // Position 0 points oldest event
        private int index(int pos) {
            return (end + pos) % bufSize;
        }

        private int advance(int index) {
            return (index + 1) % bufSize;
        }

        public void add(float x, float y, long time) {
            xBuf[top] = x;
            yBuf[top] = y;
            timeBuf[top] = time;
            top = advance(top);
            if (count < bufSize) {
                count++;
            } else {
                end = advance(end);
            }
        }

        public float getX(int pos) {
            return xBuf[index(pos)];
        }

        public float getY(int pos) {
            return yBuf[index(pos)];
        }

        public long getTime(int pos) {
            return timeBuf[index(pos)];
        }

        public void dropOldest() {
            count--;
            end = advance(end);
        }
    }
}