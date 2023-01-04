package cn.lliiooll.pphelper.data;

import de.robv.android.xposed.XposedHelpers;

import java.util.ArrayList;
import java.util.List;

public class CommentBeanData {

    private final Object imageBean;

    public CommentBeanData(Object imageBean) {
        this.imageBean = imageBean;
    }


    public List<ServerImageBeanData> serverImageBean() {
        List<Object> objs = (List<Object>) XposedHelpers.getObjectField(this.imageBean, "serverImages");
        List<ServerImageBeanData> beans = new ArrayList<>();
        objs.forEach(o -> beans.add(new ServerImageBeanData(o)));
        return beans;
    }
}
