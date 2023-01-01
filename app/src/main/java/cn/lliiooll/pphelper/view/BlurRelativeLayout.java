package cn.lliiooll.pphelper.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import cn.lliiooll.pphelper.utils.BlurUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlurRelativeLayout extends RelativeLayout {

    private static Executor pool = new ThreadPoolExecutor(1, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    private int radius = 0;
    private boolean draw = false;

    public BlurRelativeLayout(Context context) {
        super(context);
    }

    public BlurRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlurRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setBlur(int radius) {
        this.radius = radius;
        if (this.radius < 0) {
            this.radius = 0;
        }
        if (this.radius > 25) {
            this.radius = 25;
        }
        setWillNotDraw(false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (draw || radius == 0) return;
        int width = getWidth();
        int height = getHeight();
        Bitmap source = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas bg = new Canvas(source);
        draw = true;
        draw(bg);
        draw = false;
        Bitmap blur = BlurUtil.rsBlur(this, source, radius);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        Rect s = new Rect(0, 0, width, height);
        Rect t = new Rect(0, 0, width, height);
        canvas.drawBitmap(blur, s, t, mPaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        onDraw(canvas);
    }
}
