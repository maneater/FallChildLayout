package com.maneater.android.view.fallchildlayout.fall;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.maneater.android.view.fallchildlayout.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AnimateView extends View implements AnimateChild.ChildListener {

    //每次最少
    final private int perSizeMin = 5;
    //每次最多
    final private int perSizeMax = 10;
    //增加控件的最大时间间隔
    final private int perCreateMaxDelay = 500;
    //增加控件的最小时间间隔
    final private int perCreateMinDelay = 400;
    //下落时长
    final private int perChildFallDuration = 10000;
    //默认图片
    final private int mImageViewDrawable = R.drawable.pred_picone;
    private int mMaxRotation = 45;

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
        mPaint.setStrokeWidth(100);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.GREEN);
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (AnimateChild animateChild : childrenList) {
            canvas.save();
            canvas.translate(animateChild.getX(), animateChild.getY());
            canvas.rotate(animateChild.getRotation(), animateChild.getWidth() / 2, animateChild.getHeight() / 2);
            canvas.scale(animateChild.getScaleX(), animateChild.getScaleY(), animateChild.getWidth() / 2, animateChild.getHeight() / 2);
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
                ArrayList<Range<Integer>> leftOffset = new ArrayList<>();
                leftOffset.add(new Range<Integer>(0, getWidth()));
                for (int i = 0; i < createSize; i++) {
                    AnimateChild childView = addChildView(i, leftOffset);
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
        return new BitmapChild(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.pred_picone));
    }

    private AnimateChild addChildView(int index, List<Range<Integer>> exceptOffset) {
        final AnimateChild childView = createChildView(index);
        int measuredHeight = childView.getHeight();
        int measuredWidth = childView.getWidth();


        childView.setFrame(createLeftMargin((int) (getWidth() * 0.8), measuredWidth, exceptOffset).getLower(), -measuredHeight);

        childView.setRotation((float) (Math.random() * mMaxRotation) * (Math.random() > 0.5f ? 1 : -1));

        final ValueAnimator animator = ValueAnimator.ofFloat((float) (getHeight() + measuredHeight * 1.5));
        final ChildAnimatorListener childAnimatorListener = new ChildAnimatorListener(childView);

        childAnimatorMap.put(childView, animator);
        childAnimatorListenerMap.put(animator, childAnimatorListener);

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
        for (AnimateChild animateChild : childrenList) {
            Animator animator = childAnimatorMap.get(animateChild);
            if (animator != null) {
                childAnimatorListenerMap.get(animator).setFinishWhenRepeat(true);
            }
        }
    }

    private HashMap<AnimateChild, Animator> childAnimatorMap = new HashMap<>();
    private HashMap<Animator, ChildAnimatorListener> childAnimatorListenerMap = new HashMap<>();

    private void removeChildAnimator(AnimateChild animateChild) {
        Animator animator = childAnimatorMap.remove(animateChild);
        childAnimatorListenerMap.remove(animator);
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    public void onClick(AnimateChild animateChild) {
        if (isFinished) {
            return;
        }
        isFinished = true;
        clickView = animateChild;
        invalidate();

        removeCallbacks(createChildRunnable);
        removeChildAnimator(animateChild);
        finishChildAnimator();

        int parentWidth = getWidth();
        int parentHeight = getHeight();

        int viewWidth = animateChild.getWidth();
        int viewHeight = animateChild.getHeight();


        Animator finalAnimator =
                ObjectAnimator.ofPropertyValuesHolder(animateChild,
                        PropertyValuesHolder.ofFloat("x", (parentWidth - viewWidth) / 2),
                        PropertyValuesHolder.ofFloat("y", (parentHeight - viewHeight) / 2),
                        PropertyValuesHolder.ofFloat("scaleX", 3),
                        PropertyValuesHolder.ofFloat("scaleY", 3),
                        PropertyValuesHolder.ofFloat("rotation", 0))
                        .setDuration(500);
        finalAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("--------", "ofPropertyValuesHolder");
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
                removeChildAnimator(targetView.get());
                childrenList.remove(targetView.get());
                postInvalidate();
            }
        }
    }

    private Range<Integer> createLeftMargin(int maxValue, int viewWidth, List<Range<Integer>> targetRanges) {

        Range<Integer> result = null;
//        Collections.sort(targetRanges);

        Range<Integer> targetRange = null;
        for (int i = 0; i < targetRanges.size(); i++) {
            Range<Integer> range = targetRanges.get(i);
            int contains = range.getUpper() - range.getLower();
            // have enough space
            if (contains >= viewWidth) {
                targetRange = range;
                int maxStart = range.getUpper() - viewWidth;
                int minStart = range.getLower();
                int start = (int) (minStart + Math.random() * (maxStart - minStart));
                result = new Range<>(start, start + viewWidth);
                break;
                // no use
            } else {
                //??
//                targetRanges.remove(i);
//                i--;
            }
        }
        if (targetRange != null) {
            int rangeStart1 = targetRange.getLower();
            int rangeEnd1 = result.getLower();

            int rangeStart2 = result.getUpper();
            int rangeEnd2 = targetRange.getUpper();
            targetRanges.remove(targetRange);

            if (rangeStart1 != rangeStart2) {
                targetRanges.add(new Range<Integer>(rangeStart1, rangeEnd1));
                targetRanges.add(new Range<Integer>(rangeStart2, rangeEnd2));
            }
        } else {
            int tmpStart = (int) (Math.random() * (maxValue - viewWidth));
            result = new Range<>(tmpStart, tmpStart + viewWidth);
        }

        return result;
    }

    private Matrix mTmpMatrix = new Matrix();
    private RectF mTmpBounds = new RectF();
    private float[] mTouchPoint = new float[2];
    private float[] mTouchPointMapped = new float[2];

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        mTouchPoint[0] = event.getX();
        mTouchPoint[1] = event.getY();

//        Log.d("onTouchEvent：mTouch:", Arrays.toString(mTouchPoint));
//        mTmpMatrix.reset();
//        mTmpMatrix.setScale(0, 0, 0, 0);
//        mTmpMatrix.mapPoints(mTouchPointMapped, mTouchPoint);
//        Log.d("onTouchEvent：mapped：", Arrays.toString(mTouchPointMapped));


        for (AnimateChild animateChild : childrenList) {

            mTouchPoint[0] = event.getX();
            mTouchPoint[1] = event.getY();

            Log.d("onTouchEvent：mTouch:", Arrays.toString(mTouchPoint));
            Log.d("onTouchEvent：children：", Arrays.toString(new float[]{animateChild.getWidth() / 2, animateChild.getHeight() / 2, animateChild.getRotation(), animateChild.getScaleX()}));

            mTmpMatrix.reset();
//            mTmpMatrix.setTranslate(-animateChild.getX(), -animateChild.getY());

            mTmpMatrix.setRotate(
                    -animateChild.getRotation(),
                    animateChild.getX() + animateChild.getWidth() / 2,
                    animateChild.getY() + animateChild.getHeight() / 2);

            mTmpMatrix.preScale(
                    1 / animateChild.getScaleX(), 1 / animateChild.getScaleY(),
                    animateChild.getX() + animateChild.getWidth() / 2,
                    animateChild.getY() + animateChild.getHeight() / 2);

            mTmpMatrix.mapPoints(mTouchPointMapped, mTouchPoint);

            Log.d("onTouchEvent：mapped：", Arrays.toString(mTouchPointMapped));

            mTmpBounds.set(
                    animateChild.getX(),
                    animateChild.getY(),
                    animateChild.getX() + animateChild.getWidth(),
                    animateChild.getY() + animateChild.getHeight());

            if (mTmpBounds.contains(mTouchPointMapped[0], mTouchPointMapped[1]) && animateChild.onTouchDown()) {
                invalidate();
                return true;
            }
        }
        return true;
    }


}
