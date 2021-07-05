package com.ryzze.lib.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ryzze.lib.utils.Dot.DotView;

public class BottomNavigationItemWithDot extends FrameLayout {
    private DotView mDotView;
    private int dotTop;
    private boolean hasMesPoint;

    private String title;
    private int mShiftedColor;
    private String fragment;
    private int activeItemWidth;
    private int inActiveItemWidth;
    private boolean isViewPager;

    private boolean isSlide;
    private final long SHIFTING_TIME=150L;

    private boolean canChangeBackColor;
    BottomNavigationItemWithDot(Context context) {
        super(context);

        final Resources res=getResources();
        mActiveMarginTop=res.getDimensionPixelSize(R.dimen.item_active_marginTop);
        mScaleInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_scaleInactive_marginTop);
        mShiftInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_shiftInactive_marginTop);
        mActiveMarginBottom=res.getDimensionPixelSize(R.dimen.item_active_marginBottom);
        mIconSize=res.getDimensionPixelSize(R.dimen.item_icon_size);
        mActiveTextSize=res.getDimensionPixelSize(R.dimen.item_active_text_size);
        mInactiveTextSize=res.getDimensionPixelSize(R.dimen.item_inactive_text_size);

    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mDotView.layout(getWidth() / 2 + BarUtils.dip2px(getContext(), 6)
                , dotTop
                , getWidth() / 2 + BarUtils.dip2px(getContext(), 6) + mDotView.getWidth()
                , dotTop + mDotView.getHeight());
    }


    void showNum(int num) {
        hasMesPoint = true;
        mDotView.showNum(num);
    }

    void disMissMes() {
        if(!hasMesPoint)return;
        hasMesPoint = false;
        mDotView.disMisMes();
    }



    void setDotTop(int dotTop) {
        if(!hasMesPoint)return;
        this.dotTop = dotTop;
    }


    //add imageview
    private @DrawableRes int iconRes;
    private @DrawableRes int iconRes2_selected;
    private ImageView mImageView;
    private TextView mTextView;

    final int mActiveMarginTop;
    private final int mScaleInactiveMarginTop;
    final int mShiftInactiveMarginTop;
    private final int mActiveMarginBottom;
    private final int mIconSize;
    private final int mActiveTextSize;
    private final int mInactiveTextSize;


    private int mPosition;
    private boolean isSelected;

    private boolean hasCorrect=true;
    private boolean initFinished;

    private Config config;
    private Fragment mFragment;


    void alphaAnim(float positionOffset) {


        if(isSlide){
            iconColorChange(positionOffset);

            if(config.switchMode==1){
                updateItemWidth(activeItemWidth-((activeItemWidth-inActiveItemWidth)*(positionOffset)));
                updateIconMarginTop(mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset);
                setDotTop((int) (mActiveMarginTop+(mShiftInactiveMarginTop-mActiveMarginTop)*positionOffset));

                ;            mTextView.setScaleX(1-positionOffset);
                mTextView.setScaleY(1-positionOffset);
            }
            else if(config.switchMode==0){
                updateIconMarginTop(mActiveMarginTop+(mScaleInactiveMarginTop-mActiveMarginTop)*positionOffset);

                setDotTop((int) (mActiveMarginTop+(mScaleInactiveMarginTop-mActiveMarginTop)*positionOffset));
                mTextView.setScaleX(7f/6-positionOffset/6);
                mTextView.setScaleY(7f/6-positionOffset/6);
            }
        }
    }

    private void updateIconMarginTop(float top) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        layoutParams.topMargin= (int) top;
        mImageView.setLayoutParams(layoutParams);
        if(mImageView_selected!=null){
            mImageView_selected.setLayoutParams(layoutParams);
        }

    }

    private void iconColorChange(float positionOffset) {
        int iconColor = BarUtils.getIconColor(positionOffset, Color.TRANSPARENT, config.activeColor, config.inActiveColor, 10);
        mImageView.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

        int iconColor1 = BarUtils.getIconColor(positionOffset, config.activeColor, Color.TRANSPARENT, Color.TRANSPARENT, 10);
        mImageView_selected.setColorFilter(iconColor1, PorterDuff.Mode.SRC_IN);

        mTextView.setTextColor(BarUtils.getOffsetColor(positionOffset,config.activeColor,config.inActiveColor,10));

    }

    void correctItem(boolean isSelected){

        if(canChangeBackColor){

            ((BottomNavigationBar) getParent().getParent()).correctBackColor();
        }

        setSelected(isSelected,false);

        if(config.switchMode==1){

//             updateItemWidthAnim(isSelected);
            updateItemWidth(isSelected?activeItemWidth:inActiveItemWidth);
            //top
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
            layoutParams.topMargin=isSelected?mActiveMarginTop:mShiftInactiveMarginTop;
            setDotTop(isSelected?mActiveMarginTop:mShiftInactiveMarginTop);
            mImageView.setLayoutParams(layoutParams);
            if(mImageView_selected!=null)mImageView_selected.setLayoutParams(layoutParams);

            //text sp
            mTextView.setScaleX(isSelected?1:0);
            mTextView.setScaleY(isSelected?1:0);

        }
        else if(config.switchMode==0){
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
            layoutParams.topMargin=isSelected?mActiveMarginTop:mScaleInactiveMarginTop;
            setDotTop(isSelected?mActiveMarginTop:mScaleInactiveMarginTop);
            mImageView.setLayoutParams(layoutParams);
            if(mImageView_selected!=null)mImageView_selected.setLayoutParams(layoutParams);

            mTextView.setScaleX(isSelected?7f/6:1);
            mTextView.setScaleY(isSelected?7f/6:1);
        }

    }

    private void updateItemWidth(float currentWidth){

        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) return;
        params.width = Math.round(currentWidth);
        setLayoutParams(params);
    }



    void setSelected(boolean isSelected, boolean isAnim) {
        this.isSelected=isSelected;
        hasCorrect=true;
        updateState(isAnim);
    }

    static class Config
    {
        private int activeColor;
        private int inActiveColor;
        private int itemBackGroundRes;
        private int switchMode;
        private boolean isSlide;

        Config(Config.Build build) {
            activeColor=build.activeColor;
            inActiveColor=build.inActiveColor;
            itemBackGroundRes=build.itemBackGroundRes;
            switchMode=build.switchMode;
            isSlide=build.isSlide;
        }

        public int getSwitchMode() {
            return switchMode;
        }

        public static class Build{
            private int activeColor;
            private int inActiveColor;
            private int itemBackGroundRes;
            private int switchMode;
            private boolean isSlide;
            public Config.Build setActiveColor(int activeColor) {
                this.activeColor = activeColor;
                return this;
            }

            public Config.Build setInActiveColor(int inActiveColor) {
                this.inActiveColor = inActiveColor;
                return this;
            }

            public Config.Build setItemBackGroundRes(int itemBackGroundRes) {
                this.itemBackGroundRes = itemBackGroundRes;
                return this;
            }

            public Config.Build setSwitchMode(int switchMode) {
                this.switchMode = switchMode;
                return this;
            }

            public Config.Build setIsSlide(boolean isSlide) {
                this.isSlide = isSlide;
                return this;
            }

            public Config build()
            {
                return new Config(this);
            }

        }
    }

    private void initDefaultOption(){
        if(isSlide&&iconRes2_selected==0){
            throw new RuntimeException("you need provide 2 pictures in Slide mode at least");
        }
        if(mPosition==0)isSelected=true;

        if(mShiftedColor!=0&&canChangeBackColor&&isSelected) (((BottomNavigationBar) getParent().getParent())).setFirstItemBackgroundColor(mShiftedColor);
        addImageAndText();
    }

    private void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }


    private ImageView mImageView_selected;

    private void addImageAndText(){

        inflate(getContext(),getLayoutRes(),this);
        mImageView= (ImageView) findViewById(R.id.bar_image);
        mTextView= (TextView) findViewById(R.id.bar_title);

        mImageView.setImageResource(iconRes);
        mTextView.setText(title);

        mDotView=new DotView(getContext());
        addView(mDotView);

        //initImage2
        if(iconRes2_selected!=0){
            mImageView_selected= (ImageView) findViewById(R.id.bar_image_selected);
            mImageView_selected.setImageResource(iconRes2_selected);
        }

        if(isSelected){
            mImageView.setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);
            if(mImageView_selected!=null)mImageView_selected.setColorFilter(config.activeColor,PorterDuff.Mode.SRC_IN);
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
            layoutParams.topMargin=mActiveMarginTop;
            if(mImageView_selected!=null)mImageView_selected.setLayoutParams(layoutParams);
        }
        else {
            if(mImageView_selected!=null)mImageView_selected.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        }

        switch (config.switchMode){

            case 1:
                if(isSelected){
                    mTextView.setScaleX(1);
                    mTextView.setScaleY(1);
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
                    layoutParams.topMargin=mActiveMarginTop;
                    mImageView.setLayoutParams(layoutParams);
                }
                dotTop=isSelected?mActiveMarginTop:mShiftInactiveMarginTop;
                break;
            case 0:
                if(isSelected){

                    ViewCompat.setScaleX(mTextView,7f/6);
                    ViewCompat.setScaleY(mTextView,7f/6);
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
                    layoutParams.topMargin=mActiveMarginTop;
                    mImageView.setLayoutParams(layoutParams);

                }

                if(!canChangeBackColor)setItemBackground(config.itemBackGroundRes);
                dotTop=isSelected?mActiveMarginTop:mScaleInactiveMarginTop;
                break;
            case 2:
                dotTop=mActiveMarginTop;
                if(!canChangeBackColor)setItemBackground(config.itemBackGroundRes);
                break;
        }

        if(!isViewPager)switchFragment(isSelected);
        mTextView.setTextColor(isSelected?config.activeColor:config.inActiveColor);
        tintImageColor(mImageView,isSelected?config.activeColor:config.inActiveColor);
    }


    private void updateState(boolean isAnim) {
        mTextView.setTextColor(isSelected?config.activeColor:config.inActiveColor);

        if(isSlide||iconRes2_selected!=0){
            if(isSelected){
                mImageView.setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);
                mImageView_selected.setColorFilter(config.activeColor,PorterDuff.Mode.SRC_IN);
            }
            else {
                mImageView_selected.setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);
                mImageView.setColorFilter(config.inActiveColor,PorterDuff.Mode.SRC_IN);
            }
        }
        else {

            tintImageColor(mImageView,isSelected?config.activeColor:config.inActiveColor);

        }

        if(!isViewPager)switchFragment(isSelected);
        if(!isAnim)return;
        switch (config.switchMode){
            case 0:
                scaleAnim();
                break;
            case 1:
                shiftAnim();
                break;
            case 2:
                break;
        }

    }

    private void scaleAnim() {
        final float start,end;
        if(isSelected){
            start=mScaleInactiveMarginTop;
            end=mActiveMarginTop;
        }
        else {
            start=mActiveMarginTop;
            end=mScaleInactiveMarginTop;
        }
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(start,end);;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float marginTop= (float) animation.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
                layoutParams.topMargin= (int) marginTop;
                mImageView.setLayoutParams(layoutParams);
                if(mImageView_selected!=null)mImageView_selected.setLayoutParams(layoutParams);
                setDotTop((int) marginTop);
            }
        });
        valueAnimator.setDuration(150);
        valueAnimator.start();

        ViewPropertyAnimatorCompat titleAnimator = ViewCompat.animate(mTextView)
                .setDuration(150)
                .scaleX(isSelected?7f/6:1f)
                .scaleY(isSelected?7f/6:1f);
        titleAnimator.start();
    }

    private void shiftAnim() {
        //no shifting
        if(activeItemWidth==inActiveItemWidth)return;
        updateItemWidth();
    }

    private void updateItemWidth(){
        final LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        ValueAnimator valueAnimator;
        if(isSelected) valueAnimator = ValueAnimator.ofFloat(getWidth(), activeItemWidth);
        else valueAnimator=ValueAnimator.ofFloat(getWidth(),inActiveItemWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;
                params.width = Math.round((float) animation.getAnimatedValue());
                setLayoutParams(params);

                float offset=animation.getAnimatedFraction();
                int marginTop;
                //margin top
                if(isSelected){
                    marginTop=mShiftInactiveMarginTop-(int)((mShiftInactiveMarginTop-mActiveMarginTop)*animation.getAnimatedFraction());
                    layoutParams.topMargin=marginTop;
                    mTextView.setScaleX(offset);
                    mTextView.setScaleY(offset);
                    setDotTop(marginTop);
                }
                else{
                    marginTop=mActiveMarginTop+(int)((mShiftInactiveMarginTop-mActiveMarginTop)*animation.getAnimatedFraction());
                    layoutParams.topMargin=marginTop;
                    mTextView.setScaleX(1-offset);
                    mTextView.setScaleY(1-offset);
                    setDotTop(marginTop);
                }
                mImageView.setLayoutParams(layoutParams);
                if(mImageView_selected!=null)mImageView_selected.setLayoutParams(layoutParams);


            }
        });
//        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.setDuration(SHIFTING_TIME);
        valueAnimator.start();
    }

    private void refreshView() {
        if(Looper.getMainLooper()== Looper.myLooper())invalidate();
        else postInvalidate();
    }

    void setFragment(String fragmentPackageName){
        if(TextUtils.isEmpty(tag))tag=fragmentPackageName;
        mFragment = getActivity().getSupportFragmentManager().findFragmentByTag(fragmentPackageName);
        if(mFragment!=null){
            getActivity().getSupportFragmentManager().beginTransaction().hide(mFragment).commitAllowingStateLoss();
        }
        else {
            try {
                Class<?> aClass = Class.forName(fragmentPackageName);
                mFragment= (Fragment) aClass.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("you may provide a wrong fragment's packageName");
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private AppCompatActivity getActivity(){
        return ((AppCompatActivity) getContext());
    }

    private void switchFragment(boolean isSelected) {
        if(mFragment==null)return;
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();

        if(!isSelected){
            if(mFragment.isAdded())
                fragmentTransaction.hide(mFragment);
        }
        else {
            if(mFragment.isAdded())fragmentTransaction.show(mFragment);
            else fragmentTransaction.add(getContainerId(),mFragment,tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private @IdRes int containerId;
    private String tag;
    private @IdRes int  getContainerId(){
        if(containerId!=0)return containerId;
        return containerId=((BottomNavigationBar) getParent().getParent()).getContainerId();
    }


    private void tintImageColor(ImageView imageView,int color){
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private int getLayoutRes() {
        int res=0;
        switch (config.switchMode){
            case 0:
                res=R.layout.bar_scale;
                break;
            case 1:
                res=R.layout.bar_shfit;
                break;
            case 2:
                res=R.layout.bar_still;
                break;
        }
        return res;
    }


    void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    Fragment getFragment() {
        if(mFragment==null){
            throw new RuntimeException("the fragment is null");
        }
        else return mFragment;
    }

    boolean isHasCorrect() {
        return hasCorrect;
    }

    void setConfig(Config config) {
        this.config=config;
    }

    void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    void setIconResSelected(int iconResSelected) {
        this.iconRes2_selected = iconResSelected;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void changeTitle(String title){
        this.title = title;
        mTextView.setText(title);
    }
    void setShiftedColor(int mShiftedColor) {
        this.mShiftedColor = mShiftedColor;
    }


    int getPosition() {
        return mPosition;
    }

    int getShiftedColor() {
        return mShiftedColor;
    }

    void setActiveItemWidth(int activeItemWidth) {
        this.activeItemWidth = activeItemWidth;
    }

    void setInActiveItemWidth(int inActiveItemWidth) {
        this.inActiveItemWidth = inActiveItemWidth;
    }

    void setIsViewPager(boolean isViewPager) {
        this.isViewPager = isViewPager;
    }

    void finishInit() {
        initFinished=true;
        initDefaultOption();
    }

    BottomNavigationItemWithDot setHasCorrect(boolean hasCorrect) {
        this.hasCorrect = hasCorrect;
        return this;
    }

    void setSlide(boolean slide) {
        isSlide = slide;
    }

    void setCanChangeBackColor(boolean canChangeBackColor) {
        this.canChangeBackColor = canChangeBackColor;
    }
}