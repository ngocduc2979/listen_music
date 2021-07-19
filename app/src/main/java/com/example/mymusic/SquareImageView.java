package com.example.mymusic;

import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

public class SquareImageView extends CardView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
