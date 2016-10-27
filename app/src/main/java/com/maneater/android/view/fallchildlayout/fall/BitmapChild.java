package com.maneater.android.view.fallchildlayout.fall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


public class BitmapChild extends BaseChild {

    private static final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap bitmap = null;

    public BitmapChild(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int getWidth() {
        return bitmap != null ? bitmap.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return bitmap != null ? bitmap.getHeight() : 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
        }
    }

}