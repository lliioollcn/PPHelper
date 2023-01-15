package cn.lliiooll.pphelper.hook.zuiyouLite;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.download.DownloadManager;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.*;
import cn.lliiooll.pphelper.view.PDialog;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioDownloadHook extends BaseHook {

    public static AudioDownloadHook INSTANCE = new AudioDownloadHook();

    private static final String OBF_AIO1 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO1";
    private static final String OBF_AIO2 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO2";
    private static final String OBF_AIO3 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO3";
    private static String DEOBF_AIO1 = "";
    private static String DEOBF_AIO2 = "";
    private static String DEOBF_AIO3 = "";

    public AudioDownloadHook() {
        super("语音下载", "voiceDownload");
    }

    @Override
    public boolean init() {
        /*
        //TODO: 帖子界面语音下载
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.slide.ab.holder.PostReviewHolder");
        for (Method m1 : clazz.getDeclaredMethods()) {
            if (m1.getReturnType() == boolean.class && m1.getParameterTypes().length == 3 && m1.getParameterTypes()[1] == View.class && m1.getParameterTypes()[2] == int.class) {
                PLog.d("找到语音下载方法: " + m1.getName());
                XposedBridge.hookMethod(m1, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("调用语音下载方法: " + m1.getName());
                        Object commentBean = param.args[0];
                        View view = (View) param.args[1];
                        int i2 = (int) param.args[2];
                        Object audio = XposedHelpers.getObjectField(commentBean, "audio");
                        if (audio != null) {
                            String url = (String) XposedHelpers.getObjectField(audio, "url");
                            PLog.d("语音url: " + url);
                            new PDialog(view.getContext())
                                    .title("请选择操作")
                                    .success(() -> {
                                        DownloadManager.downloadVoice(url);
                                    })
                                    .cancel(() -> {
                                        //TODO: 原操作
                                        src(view, commentBean, i2, param.thisObject);
                                    })
                                    .show();
                        } else {
                            return src(view, commentBean, i2, param.thisObject);
                        }
                        return true;
                    }
                });
            }
        }

         */

        //TODO: 详细评论语音下载
        Class<?> clazz3 = HybridClassLoader.load(DEOBF_AIO3);
        List<Method> findMs = new ArrayList<>();
        for (Method m : clazz3.getDeclaredMethods()) {
            if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == View.class && m.getReturnType() == boolean.class) {
                PLog.d("过滤方法: " + m.getName());
                findMs.add(m);
            }

        }

        //TODO: 详细评论界面长按事件
        Method m = findMs.get(findMs.size()-1);
        XposedBridge.hookMethod(m, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                PLog.d(">>>>>>>>>>");
                PLog.d("调用方法: " + m.getName());
                PLog.d("来自: " + DEOBF_AIO3);
                View view = (View) param.args[0];
                for (Field f : clazz3.getDeclaredFields()) {
                    if (f.getType().getName().contains("CommentBean")) {
                        Object commentBean = XposedHelpers.getObjectField(param.thisObject, f.getName());
                        if (commentBean != null) {
                            PLog.d("评论bean不为null");
                            Object audio = XposedHelpers.getObjectField(commentBean, "audio");
                            if (audio != null) {
                                String url = (String) XposedHelpers.getObjectField(audio, "url");
                                PLog.d("语音url: " + url);
                                new PDialog(view.getContext())
                                        .title("请选择操作")
                                        .success(() -> {
                                            DownloadManager.downloadVoice(url);
                                        })
                                        .cancel(() -> {
                                            //TODO: 原操作
                                            src1(view, param.thisObject);
                                        })
                                        .show();
                            } else {
                                src1(view, param.thisObject);
                            }
                        } else {
                            PLog.d("评论bean为null!!!");
                            src1(view, param.thisObject);
                        }

                    }
                }
                PLog.d(">>>>>>>>>>");
                return true;
            }
        });

        return true;
    }

    private void src1(View view, Object thisObject) {
        Class<?> clazz3 = HybridClassLoader.load(DEOBF_AIO3);
        for (Field f : clazz3.getDeclaredFields()) {
            //if (f.getType().getSimpleName().length() == 2) {
            for (Method m : f.getType().getDeclaredMethods()) {
                if (m.getReturnType() == boolean.class && m.getParameterTypes().length == 2 && m.getParameterTypes()[0] == View.class && m.getParameterTypes()[1] == int.class) {
                    PLog.d("找到AIO4: " + f.getType().getName());
                    Object aio4Obj = XposedHelpers.getObjectField(thisObject, f.getName());
                    if (aio4Obj != null) {
                        XposedHelpers.callMethod(aio4Obj, m.getName(), view, -1);
                        break;
                    }
                }
            }
            // }
        }
    }

    private boolean src(View view, Object commentBean, int i2, Object thisObject) {
        Class<?> aio1Clazz = HybridClassLoader.load(DEOBF_AIO1);
        Class<?> aio2Clazz = HybridClassLoader.load(DEOBF_AIO2);
        for (Method m2 : aio1Clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(m2.getModifiers()) && m2.getParameterTypes().length == 1 && m2.getParameterTypes()[0] == Context.class && m2.getReturnType() == Activity.class) {
                PLog.d("找到放方法AIO1: " + m2.getName());
                Object actObj = XposedHelpers.callStaticMethod(aio1Clazz, m2.getName(), view.getContext());
                if (actObj == null) {
                    return false;
                }
                Activity activity = (Activity) actObj;
                if (j_g_v_h0_w_t0_b_h(commentBean) && i2 == -1) {
                    i2 = 0;
                }
                for (Method m3 : aio2Clazz.getDeclaredMethods()) {
                    if (m3.getParameterTypes().length == 0 && Modifier.isStatic(m3.getModifiers()) && m3.getReturnType() != void.class) {
                        PLog.d("找到放方法AIO2: " + m3.getName());
                        Object aio3Obj = XposedHelpers.callStaticMethod(aio2Clazz, m3.getName());
                        for (Method m4 : aio3Obj.getClass().getDeclaredMethods()) {
                            if (m4.getParameterTypes().length == 4 && !Modifier.isStatic(m4.getModifiers()) && m4.getParameterTypes()[0] == Activity.class && m4.getParameterTypes()[1] == Object.class & m4.getParameterTypes()[3] == int.class) {
                                PLog.d("找到放方法AIO3: " + m4.getName());
                                XposedHelpers.callMethod(aio3Obj, m4.getName(), activity, thisObject, commentBean, i2);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean j_g_v_h0_w_t0_b_h(Object ins) {
        if (ins == null) {
            return false;
        } else {
            Object serverImageBeanObj = XposedHelpers.getObjectField(ins, "serverImages");
            if (serverImageBeanObj == null) {
                return false;
            }
            List<?> serverImageBean = (List<?>) serverImageBeanObj;
            if (serverImageBean.isEmpty()) {
                return false;
            } else {
                if (serverImageBean.get(0) == null) {
                    return false;
                } else {
                    return serverImageBean.size() == 1;
                }
            }
        }
    }

    @Override
    public boolean needObf() {
        if (AppUtils.isUpdate()) {
            return true;
        }
        Map<String, List<String>> cache = PConfig.cache();
        List<String> keys = new ArrayList<>();
        keys.add(OBF_AIO1);
        keys.add(OBF_AIO2);
        keys.add(OBF_AIO3);
        AtomicBoolean need = new AtomicBoolean(false);
        keys.forEach(k -> {
            PLog.d("检查混淆类: " + k);
            if (cache.containsKey(k)) {
                PLog.d("包含混淆类: " + k);
                if (cache.getOrDefault(k, new ArrayList<>()).size() > 0) {
                    cache.get(k).forEach(c -> {
                        PLog.d("检查混淆类是否有效: " + c);
                        try {
                            HybridClassLoader.loadWithThrow(c);
                        } catch (Throwable e) {
                            PLog.d("混淆类失效: " + c);
                            need.set(true);
                        }
                    });
                } else {
                    need.set(true);
                }
            } else {
                PLog.d("不包含混淆类: " + k);
                need.set(true);
            }
        });

        return need.get();
    }

    @Override
    public Map<String, List<String>> obf() {
        return new HashMap<String, List<String>>() {{
            put(OBF_AIO1, new ArrayList<String>() {{
                add("^%d:%02d:%02d$");
                add("^%02d:%02d$");
                add("00:00");
            }});
            put(OBF_AIO2, new ArrayList<String>() {{
                add("巡查举报");
                add("开启存储权限才能正常下载");
                add("去设置");
                add("举报成功，感谢你对家园的贡献!");
            }});
            put(OBF_AIO3, new ArrayList<String>() {{
                add("event_on_play_review_comment");
                add("videocomment");
            }});
        }};
    }

    @Override
    public void doObf(Map<String, List<String>> finds) {
        finds.forEach((k, l) -> {
            if (k.equalsIgnoreCase(OBF_AIO1)) {
                l.forEach(v -> {
                    String c = DexUtils.doReplace(v);
                    Class<?> clazz = HybridClassLoader.load(c);
                    if (clazz != null) {
                        if (clazz.getDeclaredMethods().length > 1) {
                            for (Method m : clazz.getDeclaredMethods()) {
                                if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Context.class && m.getReturnType() == Activity.class) {
                                    PLog.d("找到类: " + c);
                                    DEOBF_AIO1 = c;
                                }
                            }
                        }
                    }
                });
            } else if (k.equalsIgnoreCase(OBF_AIO2)) {
                DEOBF_AIO2 = DexUtils.doReplace(l.get(0));
                PLog.d("找到类: " + DEOBF_AIO2);
            } else if (k.equalsIgnoreCase(OBF_AIO3)) {
                DEOBF_AIO3 = DexUtils.doReplace(l.get(0));
                PLog.d("找到类: " + DEOBF_AIO3);
            }
        });
    }
}
