package com.android.colorpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created by Robin on 15.07.2015.
 */
public class ColorPickerPreference extends DialogPreference implements ColorPickerSwatch.OnColorSelectedListener {
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected int mTitleResId = R.string.color_picker_default_title;
    protected int[] mColors = null;
    protected String[] mColorContentDescriptions = null;
    protected int mSelectedColor;
    protected int mColumns;
    protected int mSize;

    private ColorPickerPalette mPalette;
    private ProgressBar mProgress;

    private ColorPickerSwatch swatch;
    public static final int[] COLORS = new int[] {
            0xFFFF1744, 0xFFD50000,
            0xFFF50057, 0xFFC51162,
            0xFFD500F9, 0xFFAA00FF,
            0xFF651FFF, 0xFF6200EA,
            0xFF3D5AFE, 0xFF304FFE,
            0xFF2979FF, 0xFF2962FF,
            0xFF00B0FF, 0xFF0091EA,
            0xFF00E5FF, 0xFF00B8D4,
            0xFF1DE9B6, 0xFF00BFA5,
            0xFF00E676, 0xFF00C853,
            0xFF76FF03, 0xFF64DD17,
            0xFFC6FF00, 0xFFAEEA00,
            0xFFFFEA00, 0xFFFFD600,
            0xFFFFC400, 0xFFFFAB00,
            0xFFFF9100, 0xFFFF6D00,
            0xFFFF3D00, 0xFFDD2C00
    };

    public ColorPickerPreference(Context context) {
        super(context);

        mColors = COLORS;
        mColumns = 4;
        mSize = SIZE_SMALL;
    }
    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mColors = COLORS;
        mColumns = 4;
        mSize = SIZE_SMALL;
    }
    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mColors = COLORS;
        mColumns = 4;
        mSize = SIZE_SMALL;
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mColors = COLORS;
        mColumns = 4;
        mSize = SIZE_SMALL;
    }




    //region Old unused
    /*
    @Override
    protected View onCreateDialogView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.color_picker_dialog, null);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mPalette = (ColorPickerPalette) view.findViewById(R.id.color_picker);
        mPalette.init(mSize, mColumns, this);

        if (mColors != null && mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.GONE);
            refreshPalette();
            mPalette.setVisibility(View.VISIBLE);
        }

        return view;
    }*/

    /*
    @Override
    protected View onCreateView(ViewGroup parent) {
        //super.onCreateView(parent);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.color_picker_preference, parent, false);
        AppCompatTextView title = (AppCompatTextView)view.findViewById(R.id.title);
        AppCompatTextView summary = (AppCompatTextView)view.findViewById(R.id.summary);
        swatch = (ColorPickerSwatch)view.findViewById(R.id.swatch);

        if(title != null)
            title.setText(getTitle());

        if(summary != null)
            summary.setText(getSummary());

        if(swatch != null)
            swatch.setColor(mSelectedColor);

        return view;
    }*/
    //endregion


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        mSelectedColor = a.getInt(index, 0);
        return mSelectedColor;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int defaultColor = 0;
        if(defaultValue != null && defaultValue instanceof Integer)
            defaultColor = (Integer)defaultValue;

        setSelectedColor(restorePersistedValue ? getPersistedInt(defaultColor) : defaultColor);
    }

    @Override
    public void onColorSelected(int color) {
        if (color != mSelectedColor) {
            mSelectedColor = color;
            // Redraw palette to show checkmark on newly selected color before dismissing.
            persistInt(mSelectedColor);
            swatch.setColor(mSelectedColor);
            mPalette.drawPalette(mColors, mSelectedColor);
        }
        //if (getDialog() != null)
        //    getDialog().dismiss();
    }

    private void showProgressBarView() {
        if (mProgress != null && mPalette != null) {
            mProgress.setVisibility(View.VISIBLE);
            mPalette.setVisibility(View.GONE);
        }
    }

    private void setColors(int[] colors, int selectedColor) {
        if (mColors != colors || mSelectedColor != selectedColor) {
            mColors = colors;
            mSelectedColor = selectedColor;
            refreshPalette();
        }
    }

    private void setColors(int[] colors) {
        if (mColors != colors) {
            mColors = colors;
            refreshPalette();
        }
    }

    private void setSelectedColor(int color) {
        if (mSelectedColor != color) {
            mSelectedColor = color;
            refreshPalette();
        }
    }

    private void setColorContentDescriptions(String[] colorContentDescriptions) {
        if (mColorContentDescriptions != colorContentDescriptions) {
            mColorContentDescriptions = colorContentDescriptions;
            refreshPalette();
        }
    }

    private void refreshPalette() {
        if (mPalette != null && mColors != null) {
            mPalette.drawPalette(mColors, mSelectedColor, mColorContentDescriptions);
        }
    }
}
