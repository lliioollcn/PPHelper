package cn.lliiooll.pphelper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlurUtil {

    private static Bitmap blurMap = null;


    public static Bitmap rsBlur(View view, Bitmap src, int radius) {
        if (blurMap == null) {
            Bitmap blurMap = src;
            RenderScript renderScript = RenderScript.create(view.getContext());
            final Allocation input = Allocation.createFromBitmap(renderScript, blurMap);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());
            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlur.setInput(input);
            scriptIntrinsicBlur.setRadius(radius);
            scriptIntrinsicBlur.forEach(output);
            output.copyTo(blurMap);
            renderScript.destroy();
            BlurUtil.blurMap = blurMap;
        }
        return blurMap;
    }
}
