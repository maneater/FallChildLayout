package com.maneater.android.view.fallchildlayout.fall;

import android.graphics.Canvas;

/**
 * Created by macbook on 2016/10/27.
 */
public interface AnimateChild {

    boolean onTouchDown();

    void setFrame(float left, float top);

    float getX();

    float getY();

    AnimateChild setX(float x);

    AnimateChild setY(float y);

    float getLeft();

    float getTop();

    float getTransX();

    float getTransY();

    float getScaleX();

    float getScaleY();

    float getRotation();

    AnimateChild setTransX(float transX);

    AnimateChild setTransY(float transY);

    AnimateChild setTrans(float transX, float transY);

    AnimateChild setScale(float scaleX, float scaleY);

    AnimateChild setScaleX(float scaleX);

    AnimateChild setScaleY(float scaleY);

    AnimateChild setRotation(float rotation);

    int getWidth();

    int getHeight();

    void onDraw(Canvas canvas);

    void setListener(ChildListener childListener);

    public interface ChildListener {
        void onClick(AnimateChild animateChild);
    }
}