package cn.lliiooll.pphelper.data;

import de.robv.android.xposed.XposedHelpers;

public class ServerImageBeanData {
    private final Object imageBean;

    public ServerImageBeanData(Object imageBean) {
        this.imageBean = imageBean;
    }


    public ServerVideoBeanData video() {
        return new ServerVideoBeanData(XposedHelpers.getObjectField(this.imageBean, "videoBean"));
    }

    public long id() {
        return XposedHelpers.getLongField(this.imageBean, "id");
    }
}
