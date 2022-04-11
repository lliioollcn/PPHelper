package cn.lliiooll.pphelper.utils;

import de.robv.android.xposed.XposedHelpers;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ViewUtils {
    public static int findId(@NotNull String name) {
        int id = 0;
        PLog.log("开始寻找名为 {} 的样式id", name);
        Class<?> clazz = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.R$id");
        Field field = XposedHelpers.findField(clazz, name);
        if (field.getType() == int.class) {
            id = XposedHelpers.getStaticIntField(clazz, name);
            PLog.log("找到名为 {} 的样式id: {}", name, id);
        }
        return id;
    }
}
