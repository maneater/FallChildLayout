package com.maneater.android.view.fallchildlayout.fall;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;


public class TextChild extends BaseChild {
    private String mText = null;
    private TextPaint mPaint = null;
    private Rect mTextBounds = new Rect();

    public TextChild(String mText) {
        this.mText = mText;
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(60);
        mPaint.setColor(Color.RED);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public int getWidth() {
        return mTextBounds.width();
    }

    @Override
    public int getHeight() {
        return mTextBounds.height();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(mText, -mTextBounds.left, -mTextBounds.top, mPaint);
    }
}
