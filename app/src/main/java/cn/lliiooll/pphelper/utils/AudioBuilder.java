package cn.lliiooll.pphelper.utils;

public class AudioBuilder {

    public static Object build(String path, String text, int dur) {
        Class<?> clazz = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.data.post.AudioBean");
        if (clazz != null) {
            try {
                if (text == null || text.isEmpty()) {
                    return clazz.getConstructor(long.class, int.class, String.class).newInstance(System.currentTimeMillis(), dur, path);
                } else {
                    return clazz.getConstructor(long.class, int.class, String.class, String.class).newInstance(System.currentTimeMillis(), dur, path, text);
                }
            } catch (Throwable e) {
                PLog.log(e);
            }
        }
        return null;
    }
}
