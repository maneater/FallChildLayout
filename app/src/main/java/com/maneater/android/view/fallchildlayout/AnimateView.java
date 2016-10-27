package com.maneater.android.view.fallchildlayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * TODO
 */

public class AnimateView extends View implements AnimateView.ChildListener {
    private final int ANIMATOR_ID = 0x7f0b0000;
    private final int ANIMATOR_LISTENER_ID = 0x7f0b0001;

    //每次最少
    final private int perSizeMin = 1;
    //每次最多
    final private int perSizeMax = 3;
    //增加控件的最大时间间隔
    final private int perCreateMaxDelay = 500;
    //增加控件的最小时间间隔
    final private int perCreateMinDelay = 400;
    //下落时长
    final private int perChildFallDuration = 4000;
    //默认图片
    final private int mImageViewDrawable = R.drawable.pred_picone;

    public AnimateView(Context context) {
        this(context, null);
    }

    public AnimateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private final ArrayList<AnimateChild> childrenList = new ArrayList<AnimateChild>();

    public AnimateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (AnimateChild animateChild : childrenList) {
            canvas.save();

            canvas.translate(animateChild.getX(), animateChild.getY());
            canvas.rotate(animateChild.getRotation());
            canvas.scale(animateChild.getScaleX(), animateChild.getScaleY());
            animateChild.onDraw(canvas);

            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(createChildRunnable);
        postDelayed(createChildRunnable, 50);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(createChildRunnable);
    }

    private int mPreCreateSize = 0;
    private boolean createFinished = false;

    private ArrayList<AnimateChild> tmpChildList = new ArrayList<AnimateChild>();

    private Runnable createChildRunnable = new Runnable() {
        @Override
        public void run() {
            if (createFinished) {
                return;
            }
            tmpChildList.clear();

            if (getWidth() > 0 && getHeight() > 0) {
                int createSizeSeed = perSizeMin;
                if (mPreCreateSize == perSizeMin) {
                    createSizeSeed = Math.min(createSizeSeed + 1, perSizeMax);
                }
                int createSize = (int) (createSizeSeed + (perSizeMax - createSizeSeed + 0.5) * Math.random());
                int[] leftOffset = new int[createSize];
                for (int i = 0; i < createSize; i++) {
                    AnimateChild childView = addChildView(i, leftOffset);
                    leftOffset[i] = (int) childView.getLeft();
                    tmpChildList.add(childView);
                }
                mPreCreateSize = createSize;
                childrenList.addAll(tmpChildList);
                postInvalidate();
                postDelayed(this, (long) (perCreateMinDelay + Math.random() * (perCreateMaxDelay - perCreateMinDelay + 0.5f)));
            } else {
                postDelayed(this, 50);
            }
        }
    };

    /**
     * @param index
     * @return 可在这里返回任意View
     */
    protected AnimateChild createChildView(int index) {
        BitmapChild bitmapChild = new BitmapChild(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.pred_picone));
        return bitmapChild;
    }

    private AnimateChild addChildView(int index, int[] exceptOffset) {
        final AnimateChild childView = createChildView(index);
        int measuredHeight = childView.getHeight();
        int measuredWidth = childView.getWidth();


        childView.setFrame(createLeftMargin((int) (getWidth() * 0.8), measuredWidth, exceptOffset), -measuredHeight);

        childView.setRotation((float) (Math.random() * 45) * (Math.random() > 0.5f ? 1 : -1));

        final ValueAnimator animator = ValueAnimator.ofFloat((float) (getHeight() + measuredHeight * 1.5));
        final ChildAnimatorListener childAnimatorListener = new ChildAnimatorListener(childView);
//        childView.setTag(ANIMATOR_ID, animator);
//        childView.setTag(ANIMATOR_LISTENER_ID, childAnimatorListener);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(perChildFallDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                childView.setTransY((Float) animation.getAnimatedValue());
                postInvalidate();
            }
        });
        animator.addListener(childAnimatorListener);
        animator.start();
        childView.setListener(this);
        return childView;
    }

    private boolean isFinished = false;

    private AnimateChild clickView = null;


    public void stopAnimateChild() {
        isFinished = true;
        removeCallbacks(createChildRunnable);
        finishChildAnimator();
    }

    private void finishChildAnimator() {

        //TODO
//        int childSize = childrenList.size();
//        for (int i = 0; i < childSize; i++) {
//            AnimateChild view = childrenList.get(i);
//            ChildAnimatorListener listener = (ChildAnimatorListener) view.getTag(ANIMATOR_LISTENER_ID);
//            listener.setFinishWhenRepeat(true);
//        }
    }


    @Override
    public void onClick(AnimateChild animateChild) {
        if (isFinished) {
            return;
        }
        isFinished = true;
        clickView = animateChild;
        invalidate();

        finishChildAnimator();

        removeCallbacks(createChildRunnable);
        //TODO
//        Animator animator = (Animator) animateChild.getTag(ANIMATOR_ID);
//        if (animator != null) {
//            animator.cancel();
//        }

        float left = animateChild.getLeft();
        float transY = animateChild.getTop();

        int parentWidth = getWidth();
        int parentHeight = getHeight();

        int viewWidth = animateChild.getWidth();
        int viewHeight = animateChild.getHeight();

        int finalTranX = (parentWidth - viewWidth) / 2;

        Animator finalAnimator =
                ObjectAnimator.ofPropertyValuesHolder(animateChild,
                        PropertyValuesHolder.ofFloat("setTransX", finalTranX - left),
                        PropertyValuesHolder.ofFloat("setTransY", (parentHeight - viewHeight) / 2),
                        PropertyValuesHolder.ofFloat("scaleX", 3),
                        PropertyValuesHolder.ofFloat("scaleY", 3))
                        .setDuration(400);
        finalAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        finalAnimator.start();
    }


    private class ChildAnimatorListener implements Animator.AnimatorListener {

        private WeakReference<AnimateChild> targetView = null;

        public ChildAnimatorListener(AnimateChild targetView) {
            this.targetView = new WeakReference<AnimateChild>(targetView);
        }

        private boolean finishWhenRepeat = false;

        public void setFinishWhenRepeat(boolean finishWhenRepeat) {
            this.finishWhenRepeat = finishWhenRepeat;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            createFinished = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            createFinished = true;
            if (finishWhenRepeat) {
                animation.cancel();
                childrenList.remove(targetView.get());
                postInvalidate();
            }
        }
    }

    private int createLeftMargin(int maxValue, int viewWidth, int[] except) {
        int createCount = 0;
        int value = 0;
        do {
            createCount++;
            value = (int) (Math.random() * maxValue);

            boolean isRight = true;

            for (int anExcept : except) {
                int start = anExcept - viewWidth - viewWidth;
                int end = anExcept + viewWidth;
                if (value > start && value < end) {
                    isRight = false;
                    break;
                }
            }

            if (isRight) {
                Log.e("createLeftMargin", "createRight by " + createCount);
                return value;
            } else if (createCount >= 50) {
                Log.e("createLeftMargin", "createNotRight by " + createCount);
                return value;
            }
        } while (true);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX();
        float eventY = event.getY();

        RectF childBounds = new RectF();
        for (AnimateChild animateChild : childrenList) {
            childBounds.set(animateChild.getLeft(), animateChild.getTop(), animateChild.getLeft() + animateChild.getWidth(), animateChild.getTop() + animateChild.getHeight());
            if (childBounds.contains(eventX, eventY) && animateChild.onTouchDown()) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public static interface AnimateChild {

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

        AnimateChild setRotation(float rotation);

        int getWidth();

        int getHeight();

        void onDraw(Canvas canvas);

        void setListener(ChildListener childListener);
    }

    interface ChildListener {
        void onClick(AnimateChild animateChild);
    }

    public static abstract class BaseChild implements AnimateChild {


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

    public static class BitmapChild extends BaseChild {

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
}
