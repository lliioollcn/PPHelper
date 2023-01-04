package cn.lliiooll.pphelper.hook;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class CustomData extends BaseHook {

    public static CustomData INSTANCE = new CustomData();

    public CustomData() {
        super("自定义个人数据", "customData");
    }

    @Override
    public boolean init() {
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("onCreateView")) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object obj = param.thisObject;
                        ViewGroup root = (ViewGroup) param.getResult();
                        //LinearLayout header = (LinearLayout) XposedHelpers.getObjectField(obj, "headerView");
                        TextView fans = root.findViewById(AppUtils.findId("id", "new_header_fans_count"));// 粉丝
                        TextView like = root.findViewById(AppUtils.findId("id", "new_header_like_count"));// 获赞
                        TextView attend = root.findViewById(AppUtils.findId("id", "new_header_attend_count"));// 关注数
                        TextView day = root.findViewById(AppUtils.findId("id", "new_header_day_count"));// 天数
                        TextView kun = root.findViewById(AppUtils.findId("id", "new_kun_count"));// 长度
                        TextView code = root.findViewById(AppUtils.findId("id", "new_header_code"));// 皮友号
                        //TODO: 在合适的地方修改数字
                    }
                });
            }
        }
        return true;
    }
}
