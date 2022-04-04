package cn.lliiooll.pphelper.lifecycle;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import cn.lliiooll.pphelper.startup.HookEntry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PackageManagerInvocationHandler implements InvocationHandler {

    private final Object target;

    public PackageManagerInvocationHandler(Object target) {
        if (target == null) {
            throw new NullPointerException("IPackageManager == null");
        }
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // prototype: ActivityInfo getActivityInfo(in ComponentName className, int flags, int userId)
        try {
            if ("getActivityInfo".equals(method.getName())) {
                ActivityInfo ai = (ActivityInfo) method.invoke(target, args);
                if (ai != null) {
                    return ai;
                }
                ComponentName component = (ComponentName) args[0];
                // before Android 13 flag was int; >= Android 13, flag is long
                long flags = ((Number) args[1]).longValue();
                if (HookEntry.getPackageName().equals(component.getPackageName())
                        && ActProxyMgr.isModuleProxyActivity(component.getClassName())) {
                    return CounterfeitActivityInfoFactory.makeProxyActivityInfo(component.getClassName(), flags);
                } else {
                    return null;
                }
            } else {
                return method.invoke(target, args);
            }
        } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }
}