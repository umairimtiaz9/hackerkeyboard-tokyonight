package org.pocketworkstation.pckeyboard.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

import org.pocketworkstation.pckeyboard.Keyboard.Key;
import org.pocketworkstation.pckeyboard.LatinKeyboardBaseView;

/**
 * Custom drawable that renders a keyboard popup with smooth, seamless curves connecting
 * the trigger key to the popup menu.
 * 
 * This drawable implements a sophisticated path rendering algorithm that creates visually
 * appealing S-curves between the key and popup using dynamic radius scaling. When space is
 * limited, the corner radii are automatically reduced to maintain smooth transitions while
 * fitting within tight geometric constraints.
 * 
 * Key Features:
 * - Dynamic radius scaling for tight spaces
 * - Smooth S-curve connections on both left and right sides
 * - Configurable corner radii for popup and key elements
 * - Snap logic to align edges when nearly aligned (within corner radius distance)
 * - Support for custom colors and stroke widths
 * 
 * The drawing algorithm constructs a path that:
 * 1. Starts at the bottom-left corner of the key
 * 2. Curves up the left side, transitioning from key to popup
 * 3. Traces the popup outline with rounded corners
 * 4. Curves down the right side, transitioning from popup back to key
 * 5. Traces the bottom of the key with rounded corners and closes
 * 
 * @author Hacker's Keyboard
 */
public class SeamlessPopupDrawable extends Drawable {

    private Paint mBackgroundPaint;
    private Paint mStrokePaint;
    private Path mPath;
    private Rect mKeyRect; // Bounding box of the key being pressed
    private Rect mPopupRect; // Bounding box of the popup content
    private float mKeyHeight;
    private float mPopupHeight;
    private float mStrokeWidth;
    private float mCornerRadius;
    private float mKeyCornerRadius;
    private float mNeckHeight;
    private float mKeyTopY;
    private float mPopupTopY;

    /**
     * Constructs a new SeamlessPopupDrawable with initialized paint objects and path components.
     * 
     * @param context The Android context (used for initialization, though not currently required)
     */
    public SeamlessPopupDrawable(Context context) {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mKeyRect = new Rect();
        mPopupRect = new Rect();
    }

    /**
     * Sets the background and stroke colors for the popup drawable.
     * 
     * @param backgroundColor The fill color for the popup and key shapes
     * @param strokeColor The outline color for the stroke
     */
    public void setColors(int backgroundColor, int strokeColor) {
        mBackgroundPaint.setColor(backgroundColor);
        mStrokePaint.setColor(strokeColor);
        }

        /**
        * Sets the width of the stroke that outlines the popup and key shapes.
        * A stroke width of 0 will result in no outline being drawn.
        * 
        * @param strokeWidth The width of the stroke in pixels
        */
        public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        }

        /**
        * Sets the corner radius for the popup shape. This radius is used for the four corners
        * of the popup rectangle and influences the S-curve connections to the key.
        * 
        * @param cornerRadius The radius of the corners in pixels
        */
        public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
        }

        /**
        * Sets the corner radius for the key shape. This radius is used for the bottom-left
        * and bottom-right corners of the key rectangle that is part of the drawn path.
        * 
        * @param keyCornerRadius The radius of the key corners in pixels
        */
        public void setKeyCornerRadius(float keyCornerRadius) {
        mKeyCornerRadius = keyCornerRadius;
        }

        /**
        * Sets the geometric parameters that define the position and size of the key and popup.
        * This method must be called by LatinKeyboardBaseView whenever the popup geometry changes
        * (e.g., when the user presses a key or the keyboard layout changes).
        * 
        * The geometry parameters define the bounding rectangles and vertical positions that are
        * used by the updatePath() method to construct the smooth connecting path.
        * 
        * @param keyRect The bounding rectangle of the trigger key
        * @param popupRect The bounding rectangle of the popup content
        * @param keyHeight The height of the key
        * @param popupHeight The height of the popup
        * @param neckHeight The height of the connecting "neck" region between key and popup
        * @param keyTopY The Y coordinate of the top of the key
        * @param popupTopY The Y coordinate of the top of the popup
        */
        public void setGeometry(Rect keyRect, Rect popupRect, float keyHeight, float popupHeight, float neckHeight, float keyTopY, float popupTopY) {
        mKeyRect.set(keyRect);
        mPopupRect.set(popupRect);
        mKeyHeight = keyHeight;
        mPopupHeight = popupHeight;
        mNeckHeight = neckHeight;
        mKeyTopY = keyTopY;
        mPopupTopY = popupTopY;

        updatePath();
        invalidateSelf(); // Request redraw
        }

        /**
        * Internal method that reconstructs the path connecting the key and popup shapes.
        * 
        * This method implements the core path rendering algorithm:
        * - Applies snap logic to align edges when they're within corner radius distance
        * - Constructs smooth S-curves on left and right sides using arc operations
        * - Dynamically scales curve radii when space is tight to maintain smooth transitions
        * - Traces the popup outline with rounded corners
        * - Traces the key outline with rounded corners
        * - Closes the path to create a complete shape
        * 
        * The algorithm handles several geometric cases:
        * - Standard S-curves when there's sufficient space
        * - Dynamic radius scaling for tight spaces
        * - Straight transitions when edges are aligned or hanging out
        * - Snap tolerance to ensure crisp vertical lines when virtually aligned
        */
        private void updatePath() {
        mPath.reset();

        // Inset by half the stroke width to avoid clipping, as STROKE draws centered on the path.
        float halfStroke = mStrokeWidth / 2f;

        // Small snap tolerance to ensure crisp vertical lines when virtually aligned
        float snapDistance = mStrokeWidth;

        float keyLeft = mKeyRect.left + halfStroke;
        float keyRight = mKeyRect.right - halfStroke;
        float keyBottom = mKeyRect.bottom - halfStroke;
        float keyTop = mKeyTopY;

        float popupLeft = mPopupRect.left + halfStroke;
        float popupRight = mPopupRect.right - halfStroke;
        float popupTop = mPopupRect.top + halfStroke;
        // float popupBottom = mPopupRect.bottom; // Same as keyTop

        // Apply Snap Logic
        if (Math.abs(keyLeft - popupLeft) <= mCornerRadius) {
            popupLeft = keyLeft;
        }
        if (Math.abs(keyRight - popupRight) <= mCornerRadius) {
            popupRight = keyRight;
        }

        float filletRadius = mKeyCornerRadius;

        // --- Start Drawing ---

        // 1. Start at Key Left Edge (Just above bottom corner)
        mPath.moveTo(keyLeft, keyBottom - mKeyCornerRadius);

        // 2. Left Side Connection
        float leftDelta = keyLeft - popupLeft;

        if (leftDelta > 0) {
            // Key is inside Popup (Key Right of Popup Left)
            // We need to go Left to reach Popup

            float availableSpace = leftDelta;
            float requiredSpace = mCornerRadius + filletRadius;

            if (availableSpace >= requiredSpace) {
                // Standard S-Curve
                mPath.lineTo(keyLeft, keyTop + filletRadius);
                // Turn Left (CCW) onto shelf
                mPath.arcTo(new RectF(keyLeft - 2 * filletRadius, keyTop, keyLeft, keyTop + 2 * filletRadius), 0f, -90f);
                mPath.lineTo(popupLeft + mCornerRadius, keyTop);
                // Turn Right (CW) onto popup
                mPath.arcTo(new RectF(popupLeft, keyTop - 2 * mCornerRadius, popupLeft + 2 * mCornerRadius, keyTop), 90f, 90f);
            } else {
                // Tight Space - Dynamic Radius Scaling
                // Split the available delta between the two curves
                float dynamicRadius = availableSpace / 2f;

                mPath.lineTo(keyLeft, keyTop + dynamicRadius);
                // Turn Left (CCW)
                mPath.arcTo(new RectF(keyLeft - 2 * dynamicRadius, keyTop, keyLeft, keyTop + 2 * dynamicRadius), 0f, -90f);
                // No lineTo needed, we are at the midpoint
                // Turn Right (CW)
                mPath.arcTo(new RectF(popupLeft, keyTop - 2 * dynamicRadius, popupLeft + 2 * dynamicRadius, keyTop), 90f, 90f);
            }
        } else {
            // Key is aligned or hanging out (Key Left of Popup Left)
            // Just go straight up
            mPath.lineTo(keyLeft, keyTop);
            if (keyLeft != popupLeft) mPath.lineTo(popupLeft, keyTop);
            mPath.lineTo(popupLeft, popupTop + mCornerRadius);
        }

        // 3. Popup Top-Left Corner
        mPath.arcTo(new RectF(popupLeft, popupTop, popupLeft + 2 * mCornerRadius, popupTop + 2 * mCornerRadius), 180f, 90f);

        // 4. Popup Top Edge & Top-Right Corner
        mPath.lineTo(popupRight - mCornerRadius, popupTop);
        mPath.arcTo(new RectF(popupRight - 2 * mCornerRadius, popupTop, popupRight, popupTop + 2 * mCornerRadius), 270f, 90f);

        // 5. Right Side Connection
        float rightDelta = popupRight - keyRight;

        if (rightDelta > 0) {
            // Key is inside Popup (Key Left of Popup Right)
            // We need to go Left to reach Key

            float availableSpace = rightDelta;
            float requiredSpace = mCornerRadius + filletRadius;

            if (availableSpace >= requiredSpace) {
                 // Standard S-Curve
                 mPath.lineTo(popupRight, keyTop - mCornerRadius);
                 // Turn Right (CW) onto shelf
                 mPath.arcTo(new RectF(popupRight - 2 * mCornerRadius, keyTop - 2 * mCornerRadius, popupRight, keyTop), 0f, 90f);
                 mPath.lineTo(keyRight + filletRadius, keyTop);
                 // Turn Left (CCW) onto key
                 mPath.arcTo(new RectF(keyRight, keyTop, keyRight + 2 * filletRadius, keyTop + 2 * filletRadius), 270f, -90f);
            } else {
                 // Tight Space - Dynamic Radius Scaling
                 float dynamicRadius = availableSpace / 2f;

                 mPath.lineTo(popupRight, keyTop - dynamicRadius);
                 // Turn Right (CW)
                 mPath.arcTo(new RectF(popupRight - 2 * dynamicRadius, keyTop - 2 * dynamicRadius, popupRight, keyTop), 0f, 90f);
                 // Turn Left (CCW)
                 mPath.arcTo(new RectF(keyRight, keyTop, keyRight + 2 * dynamicRadius, keyTop + 2 * dynamicRadius), 270f, -90f);
            }
            mPath.lineTo(keyRight, keyBottom - mKeyCornerRadius);
        } else {
             // Aligned or hanging out
             mPath.lineTo(popupRight, mKeyTopY);
             if (keyRight != popupRight) mPath.lineTo(keyRight, mKeyTopY);
             mPath.lineTo(keyRight, keyBottom - mKeyCornerRadius);
        }

        // 6. Key Bottom-Right Corner
        mPath.arcTo(new RectF(keyRight - 2 * mKeyCornerRadius, keyBottom - 2 * mKeyCornerRadius, keyRight, keyBottom), 0f, 90f);

        // 7. Key Bottom Edge
        mPath.lineTo(keyLeft + mKeyCornerRadius, keyBottom);

        // 8. Key Bottom-Left Corner
        mPath.arcTo(new RectF(keyLeft, keyBottom - 2 * mKeyCornerRadius, keyLeft + 2 * mKeyCornerRadius, keyBottom), 90f, 90f);

        mPath.close();
        }

        /**
        * Renders the popup drawable onto the provided canvas.
        * 
        * This method draws both the background fill and the stroke outline of the connected
        * key-popup shape. The stroke is only drawn if the stroke width has been set to a
        * value greater than 0.
        * 
        * @param canvas The canvas on which to draw the popup drawable
        */
        @Override
        public void draw(Canvas canvas) {
        // Draw the background
        canvas.drawPath(mPath, mBackgroundPaint);

        // Draw the stroke (only if stroke width is greater than 0)
        if (mStrokeWidth > 0) {
            canvas.drawPath(mPath, mStrokePaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // Not typically used for drawables that directly control their own alpha/color
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        // Not typically used for drawables that directly control their own alpha/color
    }

    @Override
    public int getOpacity() {
        // OPAQUE is generally safe if the drawable always fills its bounds
        return android.graphics.PixelFormat.OPAQUE;
        }

        /**
        * Returns the combined bounding rectangle of both the key and popup shapes.
        * 
        * This method calculates the union of the key and popup rectangles, which represents
        * the full bounds of the drawable. This is useful for invalidation and layout calculations.
        * 
        * @return A rectangle that encompasses both the key and popup shapes
        */
        public Rect getCombinedBounds() {
        // This method should return the bounds of the drawable.
        // For a popup, this would be the combined bounds of the key replica and the popup content.
        // For simplicity, we'll use the popup rect as a base and expand if needed.
        Rect combinedBounds = new Rect(mKeyRect);
        combinedBounds.union(mPopupRect);
        return combinedBounds;
    }
}
