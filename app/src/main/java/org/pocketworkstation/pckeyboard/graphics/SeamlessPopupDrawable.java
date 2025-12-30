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

import org.pocketworkstation.pckeyboard.Key;
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

        float keyLeft = mKeyRect.left;
        float keyRight = mKeyRect.right;
        float keyBottom = mKeyRect.bottom;
        // Key Top is mKeyTopY.

        float popupLeft = mPopupRect.left;
        float popupRight = mPopupRect.right;
        float popupBottom = mPopupRect.bottom;
        float popupTop = mPopupRect.top; // mPopupTopY

        float filletRadius = mNeckHeight; // Reuse this parameter as fillet radius

        // Start at Key Bottom Left
        mPath.moveTo(keyLeft, keyBottom);

        // --- Left Side Connection ---
        if (keyLeft > popupLeft + filletRadius) {
            // Key is significantly inside the popup. Draw a fillet.
            // Line up to start of fillet
            mPath.lineTo(keyLeft, mKeyTopY + filletRadius);
            // Curve to the left onto the popup bottom
            // QuadTo using the corner (keyLeft, mKeyTopY) as control point
            mPath.quadTo(keyLeft, mKeyTopY, keyLeft - filletRadius, mKeyTopY);

            // Draw the Bottom-Left corner of the popup
            mPath.lineTo(popupLeft + mCornerRadius, mKeyTopY);
            mPath.arcTo(new RectF(popupLeft, popupBottom - 2 * mCornerRadius, popupLeft + 2 * mCornerRadius, popupBottom), 90f, 90f);
        } else {
            // Key is aligned or close to left edge. Vertical extrusion.
            // Continue up to the Top-Left curve start of the popup.
            mPath.lineTo(keyLeft, mKeyTopY);
            // If there's a small mismatch (keyLeft > popupLeft but < radius), ideally we just merge them.
            // For now, draw line to popupLeft to be safe.
            if (keyLeft != popupLeft) {
                mPath.lineTo(popupLeft, mKeyTopY);
            }
            mPath.lineTo(popupLeft, popupTop + mCornerRadius);
        }

        // --- Top Left Corner ---
        mPath.arcTo(new RectF(popupLeft, popupTop, popupLeft + 2 * mCornerRadius, popupTop + 2 * mCornerRadius), 180f, 90f);

        // --- Top Right Corner ---
        mPath.lineTo(popupRight - mCornerRadius, popupTop);
        mPath.arcTo(new RectF(popupRight - 2 * mCornerRadius, popupTop, popupRight, popupTop + 2 * mCornerRadius), 270f, 90f);

        // --- Right Side Connection ---
        if (keyRight < popupRight - filletRadius) {
             // Key is inside.
             // Line down to bottom-right corner start
             mPath.lineTo(popupRight, popupBottom - mCornerRadius);
             // Arc to bottom edge
             mPath.arcTo(new RectF(popupRight - 2 * mCornerRadius, popupBottom - 2 * mCornerRadius, popupRight, popupBottom), 0f, 90f);

             // Now at (popupRight - R, popupBottom).
             // Line left to start of fillet
             mPath.lineTo(keyRight + filletRadius, mKeyTopY); // mKeyTopY == popupBottom
             // Curve down to Key Right Edge
             mPath.quadTo(keyRight, mKeyTopY, keyRight, mKeyTopY + filletRadius);
             // Line down to Key Bottom Right
             mPath.lineTo(keyRight, keyBottom);
        } else {
             // Aligned or close.
             mPath.lineTo(popupRight, mKeyTopY);
             if (keyRight != popupRight) {
                 mPath.lineTo(keyRight, mKeyTopY);
             }
             mPath.lineTo(keyRight, keyBottom);
        }

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
    @Override
    public Rect getBounds() {
        // This method should return the bounds of the drawable.
        // For a popup, this would be the combined bounds of the key replica and the popup content.
        // For simplicity, we'll use the popup rect as a base and expand if needed.
        Rect combinedBounds = new Rect(mKeyRect);
        combinedBounds.union(mPopupRect);
        return combinedBounds;
    }
}
