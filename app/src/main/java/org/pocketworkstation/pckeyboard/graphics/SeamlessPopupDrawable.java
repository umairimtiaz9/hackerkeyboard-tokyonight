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

    // Constructor
    public SeamlessPopupDrawable(Context context) {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mKeyRect = new Rect();
        mPopupRect = new Rect();
    }

    // Public methods to set geometry and colors
    public void setColors(int backgroundColor, int strokeColor) {
        mBackgroundPaint.setColor(backgroundColor);
        mStrokePaint.setColor(strokeColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setKeyCornerRadius(float keyCornerRadius) {
        mKeyCornerRadius = keyCornerRadius;
    }

    // This method needs to be called by LatinKeyboardBaseView whenever geometry changes
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

    // Helper method to get current bounds for invalidation
    public Rect getCombinedBounds() {
        // This method should return the bounds of the drawable.
        // For a popup, this would be the combined bounds of the key replica and the popup content.
        // For simplicity, we'll use the popup rect as a base and expand if needed.
        Rect combinedBounds = new Rect(mKeyRect);
        combinedBounds.union(mPopupRect);
        return combinedBounds;
    }
}
