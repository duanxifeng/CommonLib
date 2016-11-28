package com.march.lib.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project  : CommonLib
 * Package  : com.march.lib.view
 * CreateAt : 2016/11/26
 * Describe : 九宫格解锁控件
 *
 * @author chendong
 */
public class LockView extends View {

    public static final String TAG = "LOCK_VIEW";
    private static final int INVALID_POINTER = -1;
    private static final int AUTO_START_SPACING = -1;
    private static final int DEFAULT_MIN_POINT_NUM = 4;
    // 激活的触摸点id
    private int activePointerId = INVALID_POINTER;


    // 四边的间隔，默认是控件的1／4
    private int startSpace;
    // 两点间隔
    private int internalSpace;

    // 点的半径
    private int pointRadius;
    // 动画scale的半径
    private float scalePointRadius;
    // 触摸半径，在点的一定范围内触发
    private int touchSensitiveRange;
    // 线宽度
    private int lineWidth;
    // 点颜色
    private int pointColor;
    // 线颜色
    private int lineColor;

    // 缩放的大小
    private float scaleMax;
    // 动画时间
    private int scaleAnimDuration = 150;
    // 本次绘制结束，调用init()方法恢复初始化
    private boolean isEventOver = false;


    class LockPoint {
        // 点的位置 0-8
        int index;
        //  点的x,y坐标
        float x, y;
        // 构造方法，初始化一个点
        LockPoint(int index, float x, float y) {
            this.index = index;
            this.x = x;
            this.y = y;
        }
        // 构造方法，从另一个点初始化
        LockPoint(LockPoint p) {
            this.x = p.x;
            this.y = p.y;
            this.index = p.index;
        }
        // 默认构造方法，初始化为一个空的点
        LockPoint() {
            this.x = -1;
            this.y = -1;
            this.index = -1;
        }
        // 判断该点是不是一个空的点
        boolean isEmpty() {
            return this.x == -1 && this.y == -1;
        }
        // 重新给位置赋值
        void init(float x, float y) {
            this.x = x;
            this.y = y;
        }
        // 设置为另一点的值
        void init(LockPoint p) {
            this.x = p.x;
            this.y = p.y;
            this.index = p.index;
        }
        // 判断一个位置是不是在该点触摸范围内,touchSensitiveRange为触摸有效半径
        boolean isTouchIn(float judgeX, float judgeY) {
            return judgeX < x + touchSensitiveRange &&
                    judgeX > x - touchSensitiveRange &&
                    judgeY < y + touchSensitiveRange &&
                    judgeY > y - touchSensitiveRange;
        }

        // 重写equals和hashCode
        @Override
        public boolean equals(Object o) {
            LockPoint p = (LockPoint) o;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return 2;
        }

        String out(String tag) {
            return tag + " : x = " + x + " , y = " + y;
        }
    }

    // 动画
    private ValueAnimator mScaleAnimator;
    // 初始化的九个点
    private LockPoint[] initLockPointArray;
    // 触摸过的点泪飙
    private List<LockPoint> historyPointList;

    // 触摸的点
    private LockPoint touchPoint;
    // 当前最后一个激活的点
    private LockPoint currentLockPoint;

    // 画线
    private Paint linePaint;
    // 画点
    private Paint pointPaint;

    // 监听
    private OnLockFinishListener listener;

    public interface OnLockFinishListener {
        /**
         *
         * @param lockView 控件
         * @param passWd 密码
         * @param passWsLength 密码长度
         * @return 当返回true时，画面将会定格在绘制结束后的状态
         * 返回false时，画面会重新初始化回初始状态
         */
        boolean onFinish(LockView lockView, String passWd, int passWsLength);
    }

    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockView);
        float density = getResources().getDisplayMetrics().density;

        pointRadius = (int) typedArray.getDimension(R.styleable.LockView_lock_pointRadius, (8 * density));
        scalePointRadius = pointRadius;
        touchSensitiveRange = (int) typedArray.getDimension(R.styleable.LockView_lock_touchSensitiveRange, pointRadius * 3);
        startSpace = (int) typedArray.getDimension(R.styleable.LockView_lock_startSpace, AUTO_START_SPACING);
        lineWidth = (int) typedArray.getDimension(R.styleable.LockView_lock_lineWidth, (5 * density));

        lineColor = typedArray.getColor(R.styleable.LockView_lock_lineColor, Color.WHITE);
        pointColor = typedArray.getColor(R.styleable.LockView_lock_pointColor, Color.WHITE);

        scaleAnimDuration = typedArray.getInt(R.styleable.LockView_lock_scaleAnimDuration, 180);
        scaleMax = typedArray.getFloat(R.styleable.LockView_lock_scaleMax, 2.5f);
        typedArray.recycle();

        historyPointList = new ArrayList<>();
        touchPoint = new LockPoint();
        currentLockPoint = new LockPoint();

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(pointColor);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setListener(OnLockFinishListener listener) {
        this.listener = listener;
    }


    /**
     * 开始缩放动画
     */
    private void startScaleAnimation() {

        if (mScaleAnimator == null) {
            mScaleAnimator = ValueAnimator.ofFloat(1f, scaleMax, 1f);
            mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float scale = (float) animation.getAnimatedValue();
                    scalePointRadius = pointRadius * scale;
                    postInvalidate();
                }
            });
            mScaleAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    scalePointRadius = pointRadius;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mScaleAnimator.setDuration(scaleAnimDuration);
        }
        if (mScaleAnimator.isRunning())
            mScaleAnimator.end();
        mScaleAnimator.start();
    }

    // 处理触摸事件，支持多点触摸
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // fast stop
        if (!isEnabled() || isEventOver)
            return false;
        // pointerIndex 是事件的在event中的下标
        int pointerIndex;
        // 获取事件掩码
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            // 重新初始化触摸点
            case MotionEvent.ACTION_DOWN:
                // pointerId 记录当前激活的pointerId
                activePointerId = event.getPointerId(0);
                // 根据pointerId查找事件在event中的位置
                pointerIndex = event.findPointerIndex(activePointerId);
                // 根据位置获取到具体的事件的坐标，这里获得的坐标就是我们要记住的那个指头的坐标
                touchPoint.init(event.getX(pointerIndex), event.getY(pointerIndex));
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动时还是根据激活的pointerId获取下标index,来进行后续操作，避免事件错乱
                pointerIndex = event.findPointerIndex(activePointerId);
                // pointerIndex < 0表示手指的事件获取不到了，结束响应事件
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    cancelLockDraw();
                    return false;
                }
                // 根据移动的位置获取坐标，初始化touchPoint的值
                touchPoint.init(event.getX(pointerIndex), event.getY(pointerIndex));
                LockPoint tempPoint;
                // 检索触摸点有没有在九个点中的某一个的触摸范围内
                for (int i = 0; i < initLockPointArray.length; i++) {
                    tempPoint = initLockPointArray[i];
                    if (!historyPointList.contains(tempPoint)
                            && tempPoint.isTouchIn(event.getX(pointerIndex), event.getY(pointerIndex))) {
                        LockPoint centerPoint = findCenterPoint(tempPoint);
                        if (!centerPoint.isEmpty()) {
                            activePoint(centerPoint);
                        }
                        activePoint(tempPoint);
                        break;
                    }
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                // 多指操作中 非 最后一个手指抬起时触发ACTION_POINTER_UP，此时要获取还在屏幕上的其他手指转移事件的对象
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
                // 最后的手指抬起触发 ACTION_UP
                pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    activePointerId = INVALID_POINTER;
                    return false;
                }
                // 发布绘制的结果，可能是监听回调之类的
                publishResult();
                // 置为-1
                activePointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_CANCEL:
                // 类似up
                cancelLockDraw();
                activePointerId = INVALID_POINTER;
                break;
        }
        postInvalidate();
        return true;
    }


    /**
     * 发布绘制结果
     */
    private void publishResult() {
        if (listener != null) {
            isEventOver = true;
            StringBuilder sb = new StringBuilder();
            for (LockPoint lockPoint : historyPointList) {
                sb.append(lockPoint.index);
            }
            String passWd = sb.toString();
            boolean isFinish = listener.onFinish(LockView.this, passWd, passWd.length());
            if (isFinish) {
                // 输入合法
                touchPoint.init(currentLockPoint);
            } else {
                // 输入不合法
                cancelLockDraw();
                isEventOver = false;
            }
        } else {
            cancelLockDraw();
        }
    }

    /**
     * 当一个手机抬起时，转移焦点
     *
     * @param ev 事件
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    /**
     * 检测当前激活的点和上一个激活点之间的是否有没有激发的点
     *
     * @param activePoint 当前被激发的点
     * @return 当前激活的点和上一个激活点之间的是否有没有激发的点，没有返回empty的{@link LockPoint#isEmpty()}
     */
    private LockPoint findCenterPoint(LockPoint activePoint) {
        LockPoint rstPoint = new LockPoint();
        // 只有一个点不需要比较
        if (historyPointList.size() < 1) {
            return rstPoint;
        }
        LockPoint tempPoint;
        // 获取上个点
        LockPoint preActivePoint = historyPointList.get(historyPointList.size() - 1);
        // 两个点是不是相邻的，是相邻的是坚决不会中间有点被空出来的
        if (isAdjacentPoint(preActivePoint, activePoint))
            return rstPoint;

        for (int i = 0; i < initLockPointArray.length; i++) {
            tempPoint = initLockPointArray[i];
            // 没有被触摸过 && 不是首点 && 不是尾点
            if (!historyPointList.contains(tempPoint) && !preActivePoint.equals(tempPoint) && !activePoint.equals(tempPoint)) {
                if (isInLine(preActivePoint, activePoint, tempPoint)) {
                    Log.e(TAG, "点在线上 " + tempPoint.out("temp") + " " + preActivePoint.out("pre") + " " + activePoint.out("active"));
                    rstPoint.init(tempPoint);
                    break;
                }
            }
        }
        return rstPoint;
    }


    /**
     * 检测相邻
     *
     * @param p1 点1
     * @param p2 点2
     * @return p1和p2是否相邻，斜对角也算相邻
     */
    private boolean isAdjacentPoint(LockPoint p1, LockPoint p2) {
        if ((p1.x == p2.x && Math.abs(p1.y - p2.y) == internalSpace)
                || (p1.y == p2.y && Math.abs(p1.x - p2.x) == internalSpace)
                || (Math.abs(p1.x - p2.x) == internalSpace && Math.abs(p1.y - p2.y) == internalSpace)) {
            Log.e(TAG, "相邻点，不处理");
            return true;
        }
        return false;
    }


    /**
     * 判断c点是不是在p1-p2的直线上
     *
     * @param p1 起始点
     * @param p2 终止点
     * @param c  判断的点
     * @return 是否在该线上
     */
    private boolean isInLine(LockPoint p1, LockPoint p2, LockPoint c) {
        float k1 = (p1.x - p2.x) * 1f / (p1.y - p2.y);
        float k2 = (p1.x - c.x) * 1f / (p1.y - c.y);
        return k1 == k2;
    }

    /**
     * 激活该点，该点将会添加到选中点列表中，然后执行动画
     *
     * @param tempPoint 被激活的点
     */
    private void activePoint(LockPoint tempPoint) {
        historyPointList.add(new LockPoint(tempPoint));
        currentLockPoint.init(tempPoint);
        startScaleAnimation();
        postInvalidate();
    }


    public void init() {
        isEventOver = false;
        cancelLockDraw();
    }

    /**
     * 结束绘制，恢复初始状态
     */
    private void cancelLockDraw() {
        touchPoint.init(-1, -1);
        currentLockPoint.init(-1, -1);
        historyPointList.clear();
        postInvalidate();
    }


    // onMeasure之后初始化数据
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getMeasuredWidth();
        // 将宽高设置为一样的，正方形
        setMeasuredDimension(size, size);
        // 初始化屏幕中的九个点的位置和下标
        if (initLockPointArray == null) {
            initLockPointArray = new LockPoint[9];
            // startSpace 为距离左边的距离，计算九个点的位置放在控件中间
            if (startSpace == AUTO_START_SPACING) {
                startSpace = size / 4;
            }
            // 计算每两个点之间的间隔
            internalSpace = (size - 2 * startSpace) / 2;
            // 初始化九个点的位置
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    initLockPointArray[index] = new LockPoint(index, startSpace + j * internalSpace, startSpace + i * internalSpace);
                    index++;
                }
            }
            // 为了在preview时能看到效果
            if (isInEditMode()) {
                historyPointList.addAll(Arrays.asList(initLockPointArray));
            }
        }
    }

    private void log(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            sb.append(obj.toString()).append("   ");
        }
        Log.e(TAG, sb.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // fast stop
        if (initLockPointArray == null)
            return;

        log(currentLockPoint.out("current"), touchPoint.out("touch"));

        // 画最后一个点和触摸的点之间的线
        if (currentLockPoint != null
                && currentLockPoint.x != -1 && currentLockPoint.y != -1
                && touchPoint.x != -1 && touchPoint.y != -1) {
            canvas.drawLine(currentLockPoint.x, currentLockPoint.y, touchPoint.x, touchPoint.y, linePaint);
        }

        // 绘制之前触过存储起来的的点
        if (historyPointList.size() > 0) {
            for (int i = 0; i < historyPointList.size() - 1; i++) {
                canvas.drawLine(historyPointList.get(i).x, historyPointList.get(i).y, historyPointList.get(i + 1).x, historyPointList.get(i + 1).y, linePaint);
            }
        }

        // 绘制九个点，当动画在执行时被激活的点会被放大
        LockPoint tempPoint;
        for (int i = 0; i < initLockPointArray.length; i++) {
            tempPoint = initLockPointArray[i];
            if (currentLockPoint != null && currentLockPoint.equals(tempPoint)) {
                canvas.drawCircle(tempPoint.x, tempPoint.y, scalePointRadius, pointPaint);
            } else {
                canvas.drawCircle(tempPoint.x, tempPoint.y, pointRadius, pointPaint);

            }
        }
    }
}
