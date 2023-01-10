package cn.lliiooll.pphelper.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingViewTouch implements View.OnTouchListener {
    private final WindowManager.LayoutParams lp;
    private final WindowManager wm;
    private int startX;
    private int startY;

    public FloatingViewTouch(WindowManager.LayoutParams lp, WindowManager windowManager) {
        this.lp = lp;
        this.wm = windowManager;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - startX;
                int movedY = nowY - startY;
                startX = nowX;
                startY = nowY;
                lp.x = lp.x + movedX;
                lp.y = lp.y + movedY;

                // 更新悬浮窗控件布局
                wm.updateViewLayout(view, lp);
                break;
            case MotionEvent.ACTION_UP:
                // 左右吸附
                if (lp.x > 0) {
                    lp.x = (wm.getDefaultDisplay().getWidth() / 2) - 20;
                }
                if (lp.x <= 0) {
                    lp.x = -(wm.getDefaultDisplay().getWidth() / 2) + 20;
                }
                // 更新悬浮窗控件布局
                wm.updateViewLayout(view, lp);
                break;
            default:
                break;
        }
        return false;
    }
}
