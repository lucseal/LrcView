import java.util.List;

public class LrcViewFakeCode extends View {
    private Paint mPaint;
    private List<LrcBean> mLrcList;
    private int mTime;
    private int mW, mH;
    private boolean isOverW;
    private int mAllLrcWidth;
    private int currentPoint;

    public void init() {
        mLrcList = new ArrayList<>();
    }

    public void setCurrent(int time) {
        mTime = time;
        invalidate();
    }

    public void setLrc(String lrcStr) {
        String[] strs = lrcStr.split("\n");
        int contW = 0;
        for (int i = 0; i < strs.length - 1; i++) {
            LrcBean lrc = new LrcBean();
            int start = parseLrcTime(strs[i]);
            int end = parseLrcTime(strs[i + 1]);

            if (start != 0 && end != 0 && end > start) {
                lrc.start = start;
                lrc.end = end;
                lrc.duration = end - start;
                lrc.content = parseLrcContent(strs[i]);
                lrc.tWidth = mPaint.measureText(lrc.content);
                contW += lrc.tWidth;
                mLrcList.add(lrc);
            }

        }

        isOverW = contW > mW;
        mAllLrcWidth = contW;
    }

    @Override
    public void onSizeChanged() {
        if (w != 0 && h != 0) {
            mW = w;
            mH = h;
            mPaint.setTextSize(w / 3);

            if(mLrcList != null && !mLrcList.isEmpty()) {
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

        if(lrc == null || lrc.isEmpty()) {
            return;
        }

        int wCount = 0;
        for (LrcBean lrc : mLrcList) {
            if (lrc.start < mTime && mTime < lrc.end) {
                int cellPercent = (mTime - start) / (lrc.duration);
                mPaint.setShader(new LinearGradient(0, cellPercent * lrc.tWidth, 0, 0,
                        new float[] { cellPercent, cellPercent }, new int[] { color1, color2 }));
                currentPoint = wCount + cellPercent * lrc.tWidth;
            } else if (mTime >= lrc.end) {
                mPaint.setShader(null);
                mPaint.setTextColor(color1);

            } else if (mTime <= start) {
                mPaint.setShader(null);
                mPaint.setTextColor(color2);

            }

            canvas.drawText(lrc.bean, contentW, baseLine, mPaint);

            wCount += contentW;
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
            LogUtil.log("moveX", "onAnimationUpdate: " + mMoveX);
        }

    }

    private class LrcBean {
        public int start;
        public int end;
        public int duration;
        public String content;
        public int tWidth;
    }
}