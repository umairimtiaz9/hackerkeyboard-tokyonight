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
        // Key Top is mKeyTopY.

        float popupLeft = mPopupRect.left + halfStroke;
        float popupRight = mPopupRect.right - halfStroke;
        float popupBottom = mPopupRect.bottom; // No inset needed for internal connection
        float popupTop = mPopupRect.top + halfStroke; // mPopupTopY

        // Apply Snap Logic - Aggressive snapping to ensure vertical lines
        if (Math.abs(keyLeft - popupLeft) <= mCornerRadius) {
            popupLeft = keyLeft;
        }
        if (Math.abs(keyRight - popupRight) <= mCornerRadius) {
            popupRight = keyRight;
        }

        // Use consistent radius for connections to match key corners
        float filletRadius = mKeyCornerRadius;

        // --- Start Drawing ---

        // 1. Start at Key Left Edge (Just above bottom corner)
        mPath.moveTo(keyLeft, keyBottom - mKeyCornerRadius);

        // 2. Line to Key Top-Left (Connection)
        float leftDelta = keyLeft - popupLeft;

        if (leftDelta > 0) {
            // Key is inside Popup (Typical case)
            if (leftDelta >= mCornerRadius + filletRadius) {
                // Enough space for full details
                mPath.lineTo(keyLeft, mKeyTopY + filletRadius);
                mPath.arcTo(new RectF(keyLeft - 2 * filletRadius, mKeyTopY, keyLeft, mKeyTopY + 2 * filletRadius), 0f, -90f); // Counter-clockwise arc? No.
                // We are going Up. We want to turn Right.
                // Previous logic: quadTo.
                // Let's use arcTo for precision.
                // Current Point: (keyLeft, mKeyTopY + filletRadius)
                // We want to end at: (keyLeft - filletRadius, mKeyTopY)
                // Center is (keyLeft - filletRadius, mKeyTopY + filletRadius)
                // Start Angle: 0 (Right). Sweep: -90 (Up to Top).
                // Wait, we are on the Right side of the circle?
                // Key Edge is X=keyLeft. Circle center is Left of that.
                // So we are at 0 degrees relative to center.
                // We want to go to -90 (Top).
                // Yes.
                // mPath.arcTo(new RectF(keyLeft - 2*filletRadius, mKeyTopY, keyLeft, mKeyTopY + 2*filletRadius), 0f, -90f);
                // But arcTo forces a lineTo if not at start.

                // Simpler: Just stick to quadTo for the connection fillet to be safe, but with full radius.
                 mPath.lineTo(keyLeft, mKeyTopY + filletRadius);
                 mPath.quadTo(keyLeft, mKeyTopY, keyLeft - filletRadius, mKeyTopY);

                 mPath.lineTo(popupLeft + mCornerRadius, mKeyTopY);
                 // Popup Corner: Bottom-Left of Popup
                 mPath.arcTo(new RectF(popupLeft, popupBottom - 2 * mCornerRadius, popupLeft + 2 * mCornerRadius, popupBottom), 90f, 90f);
            } else {
                // Tight space: Snapping failed but we are close.
                // We shouldn't be here if snapping is mCornerRadius.
                // But if we are... bridge the gap.
                // Draw line to intersection height
                mPath.lineTo(keyLeft, mKeyTopY + mCornerRadius);
                // Curve to popup
                mPath.quadTo(keyLeft, mKeyTopY, popupLeft, mKeyTopY - mCornerRadius);
                mPath.lineTo(popupLeft, popupTop + mCornerRadius);
            }
        } else {
            // Key is aligned (snapped) or hanging out
            mPath.lineTo(keyLeft, mKeyTopY);
            if (keyLeft != popupLeft) mPath.lineTo(popupLeft, mKeyTopY);
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
            // Key is inside Popup
            if (rightDelta >= mCornerRadius + filletRadius) {
                 // Enough space
                 mPath.lineTo(popupRight, popupBottom - mCornerRadius);
                 mPath.arcTo(new RectF(popupRight - 2 * mCornerRadius, popupBottom - 2 * mCornerRadius, popupRight, popupBottom), 0f, 90f);
                 mPath.lineTo(keyRight + filletRadius, mKeyTopY);
                 // Curve down onto key
                 mPath.quadTo(keyRight, mKeyTopY, keyRight, mKeyTopY + filletRadius);
            } else {
                 // Tight space
                 mPath.lineTo(popupRight, popupBottom - mCornerRadius);
                 mPath.quadTo(popupRight, mKeyTopY, keyRight, mKeyTopY + mCornerRadius);
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
