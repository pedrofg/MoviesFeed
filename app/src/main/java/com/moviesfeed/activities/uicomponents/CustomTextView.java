package com.moviesfeed.activities.uicomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.moviesfeed.R;

/**
 * Created by Pedro on 2017-02-05.
 */

public class CustomTextView extends EllipsizingTextView {

    public static final String FONTS_PATH = "fonts/";

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String fontName = styledAttrs.getString(R.styleable.CustomTextView_typeface);
        styledAttrs.recycle();

        if (fontName != null) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), FONTS_PATH + fontName);
            setTypeface(typeface);
        }
    }
}
