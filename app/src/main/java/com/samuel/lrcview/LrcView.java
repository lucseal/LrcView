package com.samuel.lrcview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class LrcView extends View {
    private Paint mPaint;
    private List<LrcBean> mLrcList;
    private int mTime;
    private int mW, mH;
    private boolean isOverW;
    private int mAllLrcWidth;
    private float currentPoint;
    private float mMoveX;

    private static final int COLOR1 = Color.parseColor("#7728ee");
    private static final int COLOR2 = Color.parseColor("#000000");

    /**
     * 歌词变化颜色
     */
    private static final int[] COLORS = new int[]{
            Color.parseColor("#7728ee"),
            Color.parseColor("#000000")
    };


    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLrcList = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(0.006f);
    }

    public void setCurrent(int time) {
        mTime = time;
        invalidate();
    }

    public void setLrc(String lrcStr) {
        if (!mLrcList.isEmpty()) {
            mLrcList.clear();
        }

        String[] strs = lrcStr.split("\n");
        int contW = 0;
        for (int i = 0; i < strs.length - 1; i++) {
            LrcBean lrc = new LrcBean();
            int start = LrcUtil.parseTime(strs[i]);
            int end = LrcUtil.parseTime(strs[i + 1]);

            if (start != 0 && end != 0 && end > start) {
                lrc.start = start;
                lrc.end = end;
                lrc.duration = end - start;
                lrc.content = LrcUtil.parseContent(strs[i]);
                lrc.tWidth = mPaint.measureText(lrc.content);
                contW += lrc.tWidth;
                mLrcList.add(lrc);
            }

        }

        isOverW = contW > mW;
        mAllLrcWidth = contW;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            mW = w;
            mH = h;
            mPaint.setTextSize(w / 3);

            if (mLrcList != null && !mLrcList.isEmpty()) {
                int contW = 0;
                for (LrcBean lBean : mLrcList) {
                    lBean.tWidth = mPaint.measureText(lBean.content);
                    contW += lBean.tWidth;
                }
                isOverW = contW > mW;
                mAllLrcWidth = contW;
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mLrcList.isEmpty()) {
            return;
        }

        int wCount = 0;
        for (LrcBean lrc : mLrcList) {
            if (lrc.start < mTime && mTime < lrc.end) {
                int cellPercent = (mTime - lrc.start) / (lrc.duration);

                LinearGradient gradient = new LinearGradient(
                        0, 0,
                        cellPercent * lrc.tWidth, 0, COLORS,
                        new float[]{cellPercent, cellPercent}, Shader.TileMode.CLAMP);

                mPaint.setShader(gradient);
                currentPoint = wCount + cellPercent * lrc.tWidth;
            } else if (mTime >= lrc.end) {
                mPaint.setShader(null);
                mPaint.setColor(COLOR1);

            } else if (mTime <= lrc.start) {
                mPaint.setShader(null);
                mPaint.setColor(COLOR2);

            }

            canvas.drawText(lrc.content, wCount, mH / 3 * 2, mPaint);

            wCount += lrc.tWidth;
        }

        if (isOverW) {
            if (currentPoint >= mW / 2) {
                mMoveX = (currentPoint - mW / 2);
                if (currentPoint - mW / 2 > mAllLrcWidth - mW) {
                    mMoveX = mAllLrcWidth - mW;
                }

            } else {
                mMoveX = 0;
            }
            scrollTo((((int) mMoveX)), 0);
        }

    }

    private class LrcBean {
        int start;
        int end;
        int duration;
        String content;
        float tWidth;
    }
}