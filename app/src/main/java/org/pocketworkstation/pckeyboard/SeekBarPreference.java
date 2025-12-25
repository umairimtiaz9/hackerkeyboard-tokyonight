package org.pocketworkstation.pckeyboard;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

/**
 * SeekBarPreference provides a dialog for editing float-valued preferences with a slider.
 */
public class SeekBarPreference extends Preference {

    private float mMin;
    private float mMax;
    protected float mVal;
    protected float mPrevVal;
    private float mStep;
    private boolean mAsPercent;
    private boolean mLogScale;
    private String mDisplayFormat;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
        mMin = a.getFloat(R.styleable.SeekBarPreference_minValue, 0.0f);
        mMax = a.getFloat(R.styleable.SeekBarPreference_maxValue, 100.0f);
        mStep = a.getFloat(R.styleable.SeekBarPreference_step, 0.0f);
        mAsPercent = a.getBoolean(R.styleable.SeekBarPreference_asPercent, false);
        mLogScale = a.getBoolean(R.styleable.SeekBarPreference_logScale, false);
        mDisplayFormat = a.getString(R.styleable.SeekBarPreference_displayFormat);
        a.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getFloat(index, 0.0f);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (getPersistedFloat(-1.234f) != -1.234f) {
            mVal = getPersistedFloat(0.0f);
        } else if (defaultValue != null) {
            if (defaultValue instanceof Float) {
                mVal = (Float) defaultValue;
            } else {
                try {
                    mVal = Float.parseFloat(defaultValue.toString());
                } catch (NumberFormatException e) {
                    mVal = 0.0f;
                }
            }
        } else {
            mVal = 0.0f;
        }
        mPrevVal = mVal;
    }

    private String formatFloatDisplay(Float val) {
        if (mAsPercent) {
            return String.format("%d%%", (int) (val * 100));
        }
        if (mDisplayFormat != null) {
            return String.format(mDisplayFormat, val);
        } else {
            return Float.toString(val);
        }
    }
    
    protected void setVal(Float val) {
        mVal = val;
    }
    
    protected void savePrevVal() {
        mPrevVal = mVal;
    }

    protected void restoreVal() {
        mVal = mPrevVal;
    }

    protected String getValString() {
        return Float.toString(mVal);
    }
    
    private float percentToSteppedVal(int percent, float min, float max, float step, boolean logScale) {
        float val;
        if (logScale) {
            val = (float) Math.exp(percentToSteppedVal(percent, (float) Math.log(min), (float) Math.log(max), step, false));
        } else {
            float delta = percent * (max - min) / 100;
            if (step != 0.0f) {
                delta = Math.round(delta / step) * step;
            }
            val = min + delta;
        }
        val = Float.valueOf(String.format(Locale.US, "%.2g", val));
        return val;
    }

    private int getPercent(float val, float min, float max) {
        if (max == min) return 0;
        return (int) (100 * (val - min) / (max - min));
    }
    
    private int getProgressVal() {
        if (mLogScale) {
            return getPercent((float) Math.log(mVal), (float) Math.log(mMin), (float) Math.log(mMax));
        } else {
            return getPercent(mVal, mMin, mMax);
        }
    }

    @Override
    protected void onClick() {
        showDialog();
    }

    private void showDialog() {
        Context context = getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.seek_bar_dialog, null);
        final SeekBar seek = view.findViewById(R.id.seekBarPref);
        final TextView minText = view.findViewById(R.id.seekMin);
        final TextView maxText = view.findViewById(R.id.seekMax);
        final TextView valText = view.findViewById(R.id.seekVal);

        valText.setText(formatFloatDisplay(mVal));
        minText.setText(formatFloatDisplay(mMin));
        maxText.setText(formatFloatDisplay(mMax));
        seek.setProgress(getProgressVal());

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float newVal = percentToSteppedVal(progress, mMin, mMax, mStep, mLogScale);
                    if (newVal != mVal) {
                        onChange(newVal);
                    }
                    mVal = newVal;
                    seek.setProgress(getProgressVal());
                }
                valText.setText(formatFloatDisplay(mVal));
            }
        });

        new AlertDialog.Builder(context)
            .setTitle(getTitle())
            .setView(view)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (callChangeListener(mVal)) {
                    persistValue(mVal);
                    mPrevVal = mVal;
                    notifyChanged();
                }
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                mVal = mPrevVal;
            })
            .show();
    }

    public void onChange(float val) {
        // override in subclasses
    }

    protected void persistValue(float val) {
        persistFloat(val);
    }

    @Override
    public CharSequence getSummary() {
        return formatFloatDisplay(mVal);
    }
}
