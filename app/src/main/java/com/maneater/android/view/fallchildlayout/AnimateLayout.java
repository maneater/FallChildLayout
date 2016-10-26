package com.maneater.android.view.fallchildlayout;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class AnimateLayout extends FrameLayout implements View.OnClickListener {

    //每次最少
    private int perSizeMin = 1;
    //每次最多
    private int perSizeMax = 3;
    //增加控件的最大时间间隔
    private int perCreateMaxDelay = 700;
    //增加控件的最小时间间隔
    private int perCreateMinDelay = 550;


    public AnimateLayout(Context context) {
        this(context, null);
    }

    public AnimateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isFinished) {
            return;
        }
        removeCallbacks(createChildRunnable);
        postDelayed(createChildRunnable, 0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(createChildRunnable);
    }

    private Runnable createChildRunnable = new Runnable() {
        @Override
        public void run() {
            int createSize = (int) (perSizeMin + (perSizeMax - perSizeMin + 0.5) * Math.random());
            int[] leftOffset = new int[createSize];
            for (int i = 0; i < createSize; i++) {
                View childView = addChildImageView(i, leftOffset);
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                leftOffset[i] = layoutParams.leftMargin;
            }
            postDelayed(this, (long) (perCreateMinDelay + Math.random() * (perCreateMaxDelay - perCreateMinDelay + 0.5f)));
        }
    };

    protected View createChildView(int index) {
        final ImageView imageView = new AppCompatImageView(getContext());
        imageView.setImageResource(R.drawable.red_package_animate_drawable);
        return imageView;
    }

    private View addChildImageView(int index, int[] exceptOffset) {
        final View childView = createChildView(index);
        LayoutParams layoutParams = generateDefaultLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        childView.measure(0, 0);
        int measuredHeight = childView.getMeasuredHeight();
        int measuredWidth = childView.getMeasuredWidth();

        layoutParams.leftMargin = createLeftMargin((int) (getWidth() * 0.8), measuredWidth, exceptOffset);
        layoutParams.topMargin = -measuredHeight;
        addView(childView, layoutParams);
        childView.setRotation((float) (Math.random() * 45) * (Math.random() > 0.5f ? 1 : -1));
        childView.animate().setInterpolator(new LinearInterpolator()).translationY((float) (getHeight() + measuredHeight * 1.5)).setDuration(5000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(childView);
                childView.setOnClickListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                childView.animate().setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        childView.setOnClickListener(AnimateLayout.this);
        return childView;
    }

    private int createLeftMargin(int maxValue, int viewWidth, int[] except) {
        int createCount = 0;
        do {
            createCount++;
            int value = (int) (Math.random() * maxValue);

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
            } else if (createCount >= 25) {
                Log.e("createLeftMargin", "createNotRight by " + createCount);
                return value;
            }
        } while (true);

    }

    private boolean isFinished = false;

    private View clickView = null;

    @Override
    public void onClick(final View view) {
        if (isFinished) {
            return;
        }
        isFinished = true;
        clickView = view;
        invalidate();

        removeCallbacks(createChildRunnable);
        view.animate().cancel();

        if (view instanceof ImageView && ((ImageView) view).getDrawable() instanceof Animatable) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        }

        int transX = (int) ViewCompat.getTranslationX(view);
        int transY = (int) ViewCompat.getTranslationY(view);

        int left = view.getLeft();
        int top = view.getTop();

        int parentWidth = getWidth();
        int parentHeight = getHeight();

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        int finalTranX = (parentWidth - viewWidth) / 2;
        view.animate().translationX(finalTranX - left).translationY((parentHeight - viewHeight) / 2).scaleX(3).scaleY(3).rotation(0).setDuration(400).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (childClickListener != null) {
                    childClickListener.onClick(view);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.animate().setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private ChildClickListener childClickListener = null;

    public void setChildClickListener(ChildClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }

    public interface ChildClickListener {
        void onClick(View view);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (clickView != null) {
            int index = indexOfChild(clickView);
            if (index >= 0) {
                if (i == childCount - 1) {
                    return index;
                } else if (i == index) {
                    return childCount - 1;
                }
            }
        }
        return super.getChildDrawingOrder(childCount, i);
    }
}
