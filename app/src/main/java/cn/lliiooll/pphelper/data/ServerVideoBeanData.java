package cn.lliiooll.pphelper.data;

import de.robv.android.xposed.XposedHelpers;

import java.util.ArrayList;
import java.util.List;

public class ServerVideoBeanData {
    private final Object videoBean;

    public ServerVideoBeanData(Object videoBean) {
        this.videoBean = videoBean;
    }


    public List<String> urls() {
        List<String> urls = new ArrayList<>();
        List<Object> h265Sources = (List<Object>) XposedHelpers.getObjectField(this.videoBean, "h265Sources");
        if (h265Sources != null){
            h265Sources.forEach(src -> {
                List<Object> videoUrls = (List<Object>) XposedHelpers.getObjectField(src, "urls");
                videoUrls.forEach(url -> {
                    urls.add((String) XposedHelpers.getObjectField(url, "url"));
                });
            });
        }

        List<Object> sources = (List<Object>) XposedHelpers.getObjectField(this.videoBean, "sources");
        if (sources != null){
            sources.forEach(src -> {
                List<Object> videoUrls = (List<Object>) XposedHelpers.getObjectField(src, "urls");
                videoUrls.forEach(url -> {
                    urls.add((String) XposedHelpers.getObjectField(url, "url"));
                });
            });
        }
        return urls;
    }



}
