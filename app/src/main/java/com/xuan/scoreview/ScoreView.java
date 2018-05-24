package com.xuan.scoreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * com.xuan.scoreview
 *
 * @author by xuan on 2018/5/19
 * @version [版本号, 2018/5/19]
 * @update by xuan on 2018/5/19
 * @descript
 */
public class ScoreView extends View {

    private int starNormal;
    private int starFocus;
    private int number=5;

    private int beforeIndex=0;
    private int index=0;

    int bitmapPadding=0;
    float bitmapWidth=80;
    float bitmapHeight=80;

    private Bitmap normalBitmap,focusBitmap;

    public ScoreView(Context context) {
        this(context,null);
    }

    public ScoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context,attrs);

        normalBitmap= BitmapFactory.decodeResource(getResources(),starNormal);
        normalBitmap=initBitmap(normalBitmap);
        focusBitmap= BitmapFactory.decodeResource(getResources(),starFocus);
        focusBitmap=initBitmap(focusBitmap);
    }

    private Bitmap initBitmap(Bitmap bitmap) {
        float scaleWidth=bitmapWidth/bitmap.getWidth();
        float scaleHeight=bitmapHeight/bitmap.getHeight();
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ScoreView);

        starNormal=typedArray.getResourceId(R.styleable.ScoreView_starNormal,starNormal);
        if(starNormal==0){
            throw new RuntimeException("请设置 app:starNormal");
        }
        starFocus=typedArray.getResourceId(R.styleable.ScoreView_starFocus,starFocus);
        if(starFocus==0){
            throw new RuntimeException("请设置 app:starFocus");
        }
        number=typedArray.getInteger(R.styleable.ScoreView_number,number);


        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //实现星星宽高

        int width=normalBitmap.getWidth()*number+bitmapPadding*number;
        int height=normalBitmap.getHeight();

        setMeasuredDimension(width,height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable Paint paint)

        //循环画number个
        float left=0;
        for (int i = 0; i < number; i++) {
            // index>i 当前选中及其之前的变为选中状态
            // index==0&&i==0 第一个默认选中 并且保证最少1颗星
            if(index>i || index==0&&i==0){
                canvas.drawBitmap(focusBitmap,left,0,null);
            }
            canvas.drawBitmap(normalBitmap,left,0,null);
            left += normalBitmap.getWidth()+bitmapPadding;
        }
        }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //判断手指位置，根据当前位置刷新界面
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getMoveIndex(event);
                break;
            case MotionEvent.ACTION_MOVE:
                getMoveIndex(event);
                break;
            case MotionEvent.ACTION_UP:
                getMoveIndex(event);
                break;
            default:
                break;
        }

        return true;

        /*
        事件分发

        ViewGroup -
            public boolean dispatchTouchEvent(MotionEvent ev)

            private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
            View child, int desiredPointerIdBits)

            if (child == null) {
                handled = super.dispatchTouchEvent(event);
            } else {
                handled = child.dispatchTouchEvent(event);
            }

            如果 view的onTouchEvent返回false
            也就是 dispatchTransformedTouchEvent 返回false
            if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                // Child wants to receive touch within its bounds.
                mLastTouchDownTime = ev.getDownTime();
                if (preorderedList != null) {
                    // childIndex points into presorted list, find original index
                    for (int j = 0; j < childrenCount; j++) {
                        if (children[childIndex] == mChildren[j]) {
                            mLastTouchDownIndex = j;
                            break;
                        }
                    }
                } else {
                    mLastTouchDownIndex = childIndex;
                }
                mLastTouchDownX = ev.getX();
                mLastTouchDownY = ev.getY();

                newTouchTarget = addTouchTarget(child, idBitsToAssign); 这个地方就不会调用
                alreadyDispatchedToNewTouchTarget = true;
                break;
            }

            mFirstTouchTarget 就是null

            那么下面方法会 拦截一切事件 intercepted = true;
            if(actionMasked == MotionEvent.ACTION_DOWN||mFirstTouchTarget!=null) {
                intercepted = onInterceptTouchEvent(ev);
            } else {
                intercepted = true;
            }

            因为 intercepted = true 下面不会执行
            if (!canceled && !intercepted) {
                循环child
            }
            那么就不会分发给child

        View -
            public boolean dispatchTouchEvent(MotionEvent event)

            if (!result && onTouchEvent(event)) {
                result = true;
            }





        */
    }

    private void getMoveIndex(MotionEvent event) {
        // event.getRawX() 获取相当于屏幕的位置
        float moveX=event.getX();//相对于当前控件的位置
        index = (int) (moveX/normalBitmap.getWidth()+1);
        if(index<0){
            index=0;
        }
        if(index>number){
            index=number;
        }
        //每次滑动以后记录上一次滑动的坐标，如果下一次滑动和上一次滑动坐标一致，那么不要重复绘制
        if(index==beforeIndex){
            return;
        }
        beforeIndex=index;
        invalidate();
    }
}
