package cn.lliiooll.pphelper.aio;

public class AIOCommentPublish {

    public static void send(Object ins) { /*
        try {

            Class<?> clazz = DexKit.load(DexKit.OBF_PUBLISH_BUS);
            Class<?> clazz1 = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.draft.controller.VillageCommentDraftController");
            Class<?> clazz2 = DexKit.load(DexKit.OBF_PUBLISH_DATA);
            Object i = null;
            Method g = null;
            Method s = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0) {
                    i = XposedHelpers.callStaticMethod(clazz, m.getName());
                }
                if (m.getReturnType().getName().contains(clazz1.getName())) {
                    g = m;
                }
                if (m.getReturnType() == boolean.class && m.getName().equalsIgnoreCase("i") && m.getParameterTypes().length == 1) {
                    s = m;
                }
            }

            if (i == null || g == null || s == null) return;

            Object i2 = clazz2.getConstructor().newInstance();


            XposedHelpers.callMethod(i, s.getName(), i2);



            Class<?> clazz = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.ui.input.InputModel");
            Class<?> clazz1 = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview");
            if (clazz1 == null || clazz == null) return;
            Object i = null;
            for (Field f : clazz1.getDeclaredFields()) {
                if (f.getType().getName().contains(clazz.getName())) {
                    i = XposedHelpers.getObjectField(ins, f.getName());
                }
            }
        } catch (Throwable e) {
            PLog.log(e);
        }*/

    }
}
