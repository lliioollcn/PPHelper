package cn.lliiooll.pphelper.lifecycle;

import android.content.Intent;
import android.os.*;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ParasiticHandler implements Handler.Callback {

    private final Handler.Callback mDefault;

    public ParasiticHandler(Handler.Callback def) {
        mDefault = def;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 100) { // LAUNCH_ACTIVITY
            try {
                Object record = msg.obj;
                Field field_intent = record.getClass().getDeclaredField("intent");
                field_intent.setAccessible(true);
                Intent intent = (Intent) field_intent.get(record);
                Bundle bundle = null;
                try {
                    Field fExtras = Intent.class.getDeclaredField("mExtras");
                    fExtras.setAccessible(true);
                    bundle = (Bundle) fExtras.get(intent);
                } catch (Exception e) {
                    PLog.e(e);
                }
                if (bundle != null) {
                    bundle.setClassLoader(HybridClassLoader.clLoader);
                    //we do NOT have a custom Bundle, but the host may have
                    if (intent.hasExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT)) {
                        Intent realIntent = intent
                                .getParcelableExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT);
                        field_intent.set(record, realIntent);
                    }
                }
            } catch (Exception e) {
                PLog.e(e);
            }
        } else if (msg.what == 159) {
            // EXECUTE_TRANSACTION
            Object clientTransaction = msg.obj;
            try {
                if (clientTransaction != null) {
                    Method getCallbacks = Class
                            .forName("android.app.servertransaction.ClientTransaction")
                            .getDeclaredMethod("getCallbacks");
                    getCallbacks.setAccessible(true);
                    List clientTransactionItems = (List) getCallbacks.invoke(clientTransaction);
                    if (clientTransactionItems != null && clientTransactionItems.size() > 0) {
                        for (Object item : clientTransactionItems) {
                            Class c = item.getClass();
                            if (c.getName().contains("LaunchActivityItem")) {
                                Field fmIntent = c.getDeclaredField("mIntent");
                                fmIntent.setAccessible(true);
                                Intent wrapper = (Intent) fmIntent.get(item);
                                Bundle bundle = null;
                                try {
                                    Field fExtras = Intent.class.getDeclaredField("mExtras");
                                    fExtras.setAccessible(true);
                                    bundle = (Bundle) fExtras.get(wrapper);
                                } catch (Exception e) {
                                    PLog.e(e);
                                }
                                if (bundle != null) {
                                    bundle.setClassLoader(HybridClassLoader.clLoader);
                                    if (wrapper.hasExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT)) {
                                        Intent realIntent = wrapper.getParcelableExtra(
                                                ActProxyMgr.ACTIVITY_PROXY_INTENT);
                                        fmIntent.set(item, realIntent);
                                        if (Build.VERSION.SDK_INT >= 31) {
                                            IBinder token = (IBinder) clientTransaction.getClass()
                                                    .getMethod("getActivityToken").invoke(clientTransaction);
                                            Class<?> clazz_ActivityThread = Class.forName(
                                                    "android.app.ActivityThread");
                                            Method currentActivityThread = clazz_ActivityThread.getDeclaredMethod(
                                                    "currentActivityThread");
                                            currentActivityThread.setAccessible(true);
                                            Object activityThread = currentActivityThread.invoke(null);
                                            // Accessing hidden method Landroid/app/ClientTransactionHandler;->getLaunchingActivity(Landroid/os/IBinder;)Landroid/app/ActivityThread$ActivityClientRecord; (blocked, reflection, denied)
                                            // Accessing hidden method Landroid/app/ActivityThread;->getLaunchingActivity(Landroid/os/IBinder;)Landroid/app/ActivityThread$ActivityClientRecord; (blocked, reflection, denied)
                                            Object acr = activityThread.getClass()
                                                    .getMethod("getLaunchingActivity", IBinder.class)
                                                    .invoke(activityThread, token);
                                            if (acr != null) {
                                                Field fAcrIntent = acr.getClass().getDeclaredField("intent");
                                                fAcrIntent.setAccessible(true);
                                                fAcrIntent.set(acr, realIntent);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                PLog.e(e);
            }
        }
        if (mDefault != null) {
            return mDefault.handleMessage(msg);
        }
        return false;
    }
}
