package com.guojiel.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by guojiel on 2018/12/4
 */
public class WarpHLayout extends ViewGroup {

    public WarpHLayout(Context context) {
        super(context);
    }

    public WarpHLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WarpHLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        View dontKeepSizeView = null;

        int childUseWidth = 0;
        int childMaxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if(layoutParams.keepSelfSize){
                childUseWidth += child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }else{
                dontKeepSizeView = child;
                childUseWidth += layoutParams.leftMargin + layoutParams.rightMargin;
            }
            childMaxHeight = Math.max(childMaxHeight, child.getMeasuredHeight());
        }

        int dontKeepSizeViewMaxWidth = widthSize - childUseWidth - getPaddingLeft() - getPaddingRight();
        if(dontKeepSizeView != null) {
            if (dontKeepSizeView.getMeasuredWidth() > dontKeepSizeViewMaxWidth) {
                int dViewWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, dontKeepSizeViewMaxWidth);
                int dViewHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, dontKeepSizeView.getMeasuredHeight());
                dontKeepSizeView.measure(dViewWidthMeasureSpec, dViewHeightMeasureSpec);
            }
        }
        if(heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }else{
            setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(childMaxHeight + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        newLayout();
    }

    private void newLayout(){
        int parentPaddingTop = getPaddingTop();
        int parentHeight = getMeasuredHeight() - parentPaddingTop - getPaddingBottom();
        int l = getPaddingLeft();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                continue;
            }
            LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int childLeft = l + childLp.leftMargin;
            int childTop = childLp.isTop() ? 0 : childLp.isBottom() ? parentHeight - childHeight : (parentHeight - childHeight) / 2;
            childTop += parentPaddingTop;
            int childRight = childLeft + childWidth;
            int childBottom = childTop + childHeight;
            child.layout(childLeft, childTop, childRight, childBottom);
            l += childLp.leftMargin + child.getMeasuredWidth() + childLp.rightMargin;
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams{

        public boolean keepSelfSize = true;
        public int gravity = 21;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.WarpHLayout_Layout);
            keepSelfSize = a.getBoolean(R.styleable.WarpHLayout_Layout_wl2KeepSelfSize, keepSelfSize);
            gravity = a.getInt(R.styleable.WarpHLayout_Layout_wl2Gravity, gravity);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public boolean isTop(){
            return gravity == 19;
        }

        public boolean isCenter(){
            return gravity == 21;
        }

        public boolean isBottom(){
            return gravity == 29;
        }

    }

}
