package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class RecentSubmissionsWidget extends ViewGroup {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int bgColor = Color.BLACK;
    private boolean mShowText;
    private int cardHeight;

    public RecentSubmissionsWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RecentSubmissionsWidget,
                0, 0);

        try {
            mShowText = a.getBoolean(R.styleable.RecentSubmissionsWidget_showText, false);
            cardHeight = a.getInteger(R.styleable.RecentSubmissionsWidget_cardHeight, 20);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("Recent Submissions View", 0, 0, paint);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
        requestLayout();
    }
}
