package cn.lliiooll.pphelper.lifecycle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import cn.lliiooll.pphelper.utils.AppUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IActivityManagerHandler implements InvocationHandler {

    private final Object mOrigin;

    public IActivityManagerHandler(Object origin) {
        mOrigin = origin;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Intent raw = (Intent) args[index];
                ComponentName component = raw.getComponent();
                Context hostApp = AppUtils.getHostAppInstance();
                if (hostApp != null && component != null
                        && hostApp.getPackageName().equals(component.getPackageName())
                        && ActProxyMgr.isModuleProxyActivity(component.getClassName())) {
                    Intent wrapper = new Intent();
                    wrapper.setClassName(component.getPackageName(),
                            AppUtils.getSettingActivity(AppUtils.getHostAppInstance().getPackageName()));
                    wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, raw);
                    args[index] = wrapper;
                }
            }
        }
        try {
            return method.invoke(mOrigin, args);
        } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }
}