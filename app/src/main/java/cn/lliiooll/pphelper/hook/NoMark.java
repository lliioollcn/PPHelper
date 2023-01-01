package cn.lliiooll.pphelper.hook;

import android.os.Bundle;
import android.widget.Toast;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
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
