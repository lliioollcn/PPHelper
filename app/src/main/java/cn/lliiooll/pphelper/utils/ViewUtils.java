package cn.lliiooll.pphelper.utils;

import android.graphics.drawable.Drawable;
import de.robv.android.xposed.XposedHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ViewUtils {
    public static int findId(@NotNull String name) {
        return find("id", name);
    }


    public static int findDrawable(@NotNull String name) {
        return find("drawable", name);
    }

    public static int findDimen(@NotNull String name) {
        return find("dimen", name);
    }

    public static int find(String type, @NotNull String name) {
        int id = 0;
        PLog.log("开始寻找名为 {} 的id", name);
        Class<?> clazz = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.R$" + type);
        Field field = XposedHelpers.findField(clazz, name);
        if (field.getType() == int.class) {
            id = XposedHelpers.getStaticIntField(clazz, name);
            PLog.log("找到名为 {} 的id: {}", name, id);
        }
        return id;
    }
}
