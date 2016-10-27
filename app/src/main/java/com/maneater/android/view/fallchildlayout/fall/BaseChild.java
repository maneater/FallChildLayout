package com.maneater.android.view.fallchildlayout.fall;

/**
 * Created by Administrator on 2016/10/27 0027.
 */

public abstract class BaseChild implements AnimateChild {


    float transX = 0;
    float transY = 0;
    float scaleX = 1;
    float scaleY = 1;
    float rotation = 0;

    float left = 0;
    float top = 0;

    private ChildListener childListener = null;


    @Override
    public void setListener(ChildListener childListener) {
        this.childListener = childListener;
    }

    @Override
    public boolean onTouchDown() {
        if (childListener != null) {
            childListener.onClick(this);
            return true;
        }
        return false;
    }

    @Override
    public void setFrame(float left, float top) {
        this.left = left;
        this.top = top;
    }

    @Override
    public float getX() {
        return left + getTransX();
    }

    @Override
    public float getY() {
        return top + getTransY();
    }

    @Override
    public AnimateChild setX(float x) {
        setTransX(x - left);
        return this;
    }

    @Override
    public AnimateChild setY(float y) {
        setTransY(y - top);
        return this;
    }

    @Override
    public float getLeft() {
        return this.left + getTransX();
    }

    @Override
    public float getTop() {
        return this.top + getTransY();
    }

    @Override
    public float getTransX() {
        return transX;
    }

    @Override
    public float getTransY() {
        return transY;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public AnimateChild setTransX(float transX) {
        this.transX = transX;
        return this;
    }

    @Override
    public AnimateChild setTransY(float transY) {
        this.transY = transY;
        return this;
    }


    @Override
    public AnimateChild setTrans(float transX, float transY) {
        this.transX = transX;
        this.transY = transY;
        return this;
    }

    @Override
    public AnimateChild setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        return this;
    }

    @Override
    public AnimateChild setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }
}
