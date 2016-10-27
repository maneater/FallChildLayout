package com.maneater.android.view.fallchildlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


/**
 * TODO
 */

public class AnimateView extends View {
    public AnimateView(Context context) {
        this(context, null);
    }

    public AnimateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    interface AnimateChild {
        AnimateChild setTransX(int transX);

        AnimateChild setTransY(int transY);

        AnimateChild setTrans(int transX, int transY);

        AnimateChild setScale(float scaleX, float scaleY);

        AnimateChild setRotation(float rotation);

        int getWidth();

        int getHeight();

        void onDraw(Canvas canvas);
    }
}
