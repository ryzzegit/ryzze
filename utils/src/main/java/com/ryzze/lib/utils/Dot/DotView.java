package com.ryzze.lib.utils.Dot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class DotView extends View {

    private String showNum = "";
    private boolean isShowJustDot;
    private Paint mDotPaint;
    private Rect textRect;
    private float padding;
    private float radiusBase;
    private float radius;
    private Context mContext;
    private PointF circleCenter;
    private PointF textPoint;
    private DragView dragView;

    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setTextSize(Utils.sp2px(mContext, 8));
        mDotPaint.setColor(Color.RED);
        textRect = new Rect();
        circleCenter = new PointF();
        textPoint = new PointF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        if (isShowJustDot()) {
            width = MeasureSpec.makeMeasureSpec(2 * Utils.dip2px(mContext, 3), MeasureSpec.EXACTLY);
        } else {
            width = MeasureSpec.makeMeasureSpec((int) (2 * radius + 1), MeasureSpec.EXACTLY);
        }
        super.onMeasure(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画点
        if (isShowJustDot) {
            canvas.drawCircle(circleCenter.x + Utils.dip2px(mContext, 3), circleCenter.y + Utils.dip2px(mContext, 3), Utils.dip2px(mContext, 3), mDotPaint);
            return;
        }
        updataPointF();
        if (!TextUtils.isEmpty(showNum)) {
            drawDotCircle(canvas);
            drawNumText(canvas);
        }
    }

    //限制调用一次requestLayout防止一直循环
    private boolean flagLimit;

    /**
     * 数字改变更新圆的大小
     */
    private void updataPointF() {
        mDotPaint.getTextBounds(showNum, 0, showNum.length(), textRect);
        radiusBase = (float) Math.sqrt(Math.pow(textRect.height() / 2, 2) + Math.pow(textRect.width() / 2, 2));
        radius = radiusBase + padding;
        circleCenter.set(radius, radius);
        textPoint.set(radius - textRect.width() / 2, radius + textRect.height() / 2);
        //半径有了值之后重新测量大小
        if (radius != 0 && !flagLimit) {
            requestLayout();
            flagLimit = true;
        }
    }

    private void drawDotCircle(Canvas canvas) {
        canvas.drawCircle(circleCenter.x, circleCenter.y, radius, mDotPaint);
    }

    private void drawNumText(Canvas canvas) {
        mDotPaint.setColor(Color.WHITE);
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint.setStrokeWidth(1);
        canvas.drawText(showNum, textPoint.x - (showNum.contains("1") ? Utils.dip2px(mContext, 1) : 0), textPoint.y, mDotPaint);
        mDotPaint.setColor(Color.RED);
        mDotPaint.setStyle(Paint.Style.FILL);
    }

    public void showNum(int num) {
        isShowJustDot = false;
        setOnTouchListener(null);
        if (getVisibility() == GONE) setVisibility(VISIBLE);
        if (num < 0) isShowJustDot = true;
        else if (num > 0) {
            showNum = num > 99 ? "99+" : String.valueOf(num);
        } else return;
        //在显示红点的时候才创建DragView，拖拽完成时候消失
        if (!isShowJustDot()) createDragView();
        setPaddingValue(num);
        refreshView();
    }

    public void disMisMes() {
        isShowJustDot = false;
        setVisibility(GONE);
        //小红点的时候还未初始化dragview所以不用释放
        if (dragView != null) dragView.disMissMes();
    }

    private void createDragView() {
        dragView = new DragView(mContext);
        dragView.rely(this);
    }

    private void setPaddingValue(int num) {
        if (num == 1) {
            padding = Utils.dip2px(mContext, 4);
            mDotPaint.setTextSize(Utils.sp2px(mContext, 9));
        } else if (num > 0 && num < 10) padding = Utils.dip2px(mContext, 4);
        else if (num > 99) padding = 2f;
        else padding = Utils.dip2px(mContext, 3);
    }

    private void refreshView() {
        if(Looper.getMainLooper()== Looper.myLooper())invalidate();
        else postInvalidate();
    }

    public String getShowNum() {
        return showNum;
    }

    public float getRadius() {
        return radius;
    }

    public int getNumWidth() {
        return textRect.width();
    }

    public int getNumHeight() {
        return textRect.height();
    }


    public int[] getCircleCenterOnRaw() {
        int[] position = new int[2];
        this.getLocationOnScreen(position);
        return position;
    }

    public boolean isShowJustDot() {
        return isShowJustDot;
    }


    public void onDestroy()
    {
        if(dragView!=null)dragView.onDestroy();
        mContext=null;
    }
}