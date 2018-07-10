package me.checkin.android;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;

import me.checkin.android.helper.BitmapHelper;


/**
 * Created by zhoujunchen
 * on 17/8/31.
 */
//;adb shell screenrecord /sdcard/test.mp4
public class CheckInMaskView extends android.support.v7.widget.AppCompatImageView {

    private boolean mComplete;

    private int mSrcWidth;
    private int mSrcHeight;
    private int mCurrentTop;
    private int mBeginHeight;

    private Rect mSrcRect, mDestRect;
    private Rect mDynamicRect;

    private Paint mBitPaint;
    private Bitmap mMaskBitmap, mSrcBitmap, mWaterBitmap;
    private PorterDuffXfermode srcInMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    public interface OnMaskListener {
        void onStartDrawMaskComplete();
    }

    public CheckInMaskView(Context context) {
        this(context, null);
    }

    public CheckInMaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckInMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSrcBitmap == null || mMaskBitmap == null || mComplete)
            return;
        int saveLayerCount = canvas.saveLayer(0, 0, mSrcWidth, mSrcHeight, mBitPaint, Canvas.ALL_SAVE_FLAG);
        // draw mask bitmap & rect
        canvas.drawRect(mDynamicRect, mBitPaint);
        canvas.drawBitmap(mMaskBitmap, mSrcRect, mDestRect, mBitPaint);
        mBitPaint.setXfermode(srcInMode);
        //draw src bitmap
        canvas.drawBitmap(mSrcBitmap, mSrcRect, mDestRect, mBitPaint);
        mBitPaint.setXfermode(null);
        //draw water bitmap
        if (mWaterBitmap != null && !mWaterBitmap.isRecycled())
            canvas.drawBitmap(mWaterBitmap, 0, 0, mBitPaint);
        canvas.restoreToCount(saveLayerCount);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = getResources().getDimensionPixelSize(R.dimen.check_in_min_wh);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    public void updateTodayCheckInUI(int convertDay, int mask, List<Integer> waterDrawables) {
        postDelayed(() -> {
            try {
                mSrcWidth = mSrcWidth > 0 ? mSrcWidth : getWidth();
                mSrcHeight = mSrcHeight > 0 ? mSrcHeight : getHeight();
                mSrcBitmap = BitmapHelper.scaleImage(BitmapHelper.decodeResource(getContext(), mask), mSrcWidth, mSrcHeight);
                mMaskBitmap = Bitmap.createBitmap(mSrcWidth, mSrcHeight, Bitmap.Config.ARGB_4444);

                mSrcRect = new Rect(0, 0, mSrcWidth, mSrcHeight);
                mDestRect = new Rect(0, 0, mSrcWidth, mSrcHeight);
                int currDay = convertDay % 9;
                mBeginHeight = mSrcHeight - (currDay <= 4 ? mSrcHeight / (currDay + 2) : 0);

                mCurrentTop = mBeginHeight;
                mDynamicRect = new Rect(0, mBeginHeight, mSrcWidth, mSrcHeight);

                if (convertDay == 9 || convertDay == 18 || convertDay == 27) {
                    invalidate();
                    return;
                }

                // begin compose water bitmaps
                if (waterDrawables.size() > 0) {
                    mWaterBitmap = Bitmap.createBitmap(mSrcWidth, mSrcHeight, Bitmap.Config.ARGB_4444);
                    Bitmap[] bitmaps = new Bitmap[waterDrawables.size()];
                    for (int i = 0; i < bitmaps.length; i++) {
                        bitmaps[i] = BitmapHelper.scaleImage(BitmapHelper.decodeResource(getContext(), waterDrawables.get(i)), mSrcWidth, mSrcHeight);
                    }
                    mWaterBitmap = BitmapHelper.addWatermarks(mWaterBitmap, bitmaps);
                }
                invalidate();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }, 100L);
    }


    public void start(OnMaskListener onMaskListener) {
        if (mMaskBitmap == null)
            return;
        ValueAnimator translationAnimator = ValueAnimator.ofFloat(mCurrentTop, 0).setDuration(400);
        translationAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mDynamicRect.top = (int) value;
            invalidate();
            if (value == 0) {
                if (onMaskListener != null) {
                    onMaskListener.onStartDrawMaskComplete();
                }
            }
        });
        translationAnimator.setDuration(1500);
        translationAnimator.start();
    }

    public void destroy() {
        if (mSrcBitmap != null && !mSrcBitmap.isRecycled()) {
            mSrcBitmap.recycle();
            mSrcBitmap = null;
        }
        if (mMaskBitmap != null && !mMaskBitmap.isRecycled()) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        if (mWaterBitmap != null && !mWaterBitmap.isRecycled()) {
            mWaterBitmap.recycle();
            mWaterBitmap = null;
        }
    }

    public void updateWh(int wh) {
        mSrcWidth = wh > 0 ? wh : getWidth();
        mSrcHeight = wh > 0 ? wh : getHeight();
        if (wh > 0) {
            getLayoutParams().width = mSrcWidth;
            getLayoutParams().height = mSrcHeight;
            requestLayout();
        }
    }

    public void complete() {
        mComplete = true;
        invalidate();
    }
}
