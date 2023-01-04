package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import cn.lliiooll.pphelper.data.CommentBeanData;
import cn.lliiooll.pphelper.data.ServerImageBeanData;
import cn.lliiooll.pphelper.download.DownloadManager;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoMark extends BaseHook {

    public static NoMark INSTANCE = new NoMark();
    private final static String OBF_COMMENT_VIDEO = "cn.xiaochuankeji.zuiyouLite.common.CommentVideo";
    private static String DEOBF_COMMENT_VIDEO = "";

    public NoMark() {
        super("去水印", "noMark");
    }

    @Override
    public boolean init() {
        // 替换帖子右下角 “保存至相册”
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getParameterTypes().length == 5
                    && m.getParameterTypes()[0] == Activity.class
                    && m.getParameterTypes()[1] == String.class) {
                XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Object imageBean = param.args[2];
                        DownloadManager.download(new ServerImageBeanData(imageBean));
                        return null;
                    }
                });
            }
        }

        // 替换评论视频右上角下载图标
        Class<?> obfClazz = HybridClassLoader.load(DEOBF_COMMENT_VIDEO);
        Class<?> commentBeanClazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.data.CommentBean");
        for (Method m : obfClazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("u0")
                    && m.getParameterTypes().length == 1
                    && m.getParameterTypes()[0] == commentBeanClazz) {
                XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Object imageBean = param.args[0];
                        new CommentBeanData(imageBean).serverImageBean().forEach(DownloadManager::download);
                        return null;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean needObf() {
        return true;
    }

    @Override
    public void doObf(Map<String, List<String>> finds) {
        if (finds.containsKey(OBF_COMMENT_VIDEO)) {
            List<String> results = finds.get(OBF_COMMENT_VIDEO);
            results.forEach(v -> {
                String r = DexUtils.doReplace(v);
                Class<?> commentBeanClazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.data.CommentBean");
                Class<?> targetClazz = HybridClassLoader.load(r);
                if (targetClazz == null) {
                    doObf(DexUtils.reCache(obf()));
                    return;
                }
                for (Method m : targetClazz.getDeclaredMethods()) {
                    if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == commentBeanClazz) {
                        DEOBF_COMMENT_VIDEO = r;
                        PLog.d("找到类: " + r);
                        break;
                    }
                }
            });
        } else {
            doObf(DexUtils.reCache(obf()));
        }
    }

    @Override
    public Map<String, List<String>> obf() {
        return new HashMap<String, List<String>>() {{
            put(OBF_COMMENT_VIDEO, new ArrayList<String>() {{
                add("event_media_play_observer");
                add("event_on_play_review_comment");
                add("post");
                add("review");
                add("+%d");
                add("http://alfile.ippzone.com/img/mp4/id/");
                add("videocomment");
            }});
        }};
    }
}
