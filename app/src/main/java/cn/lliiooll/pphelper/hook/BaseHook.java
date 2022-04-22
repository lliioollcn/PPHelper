package cn.lliiooll.pphelper.hook;

import android.view.View;
import cn.lliiooll.pphelper.config.ConfigManager;

public abstract class BaseHook {

    private final String name;
    private final String label;
    private View.OnClickListener clickListener = null;
    private String desc = "该功能未启用，如果启用后仍然显示此消息，请重启皮皮搞笑后再试";

    public BaseHook(String name, String label) {
        this.name = name;
        this.label = label;
    }

    /**
     * 初始化hook
     *
     * @return hook是否加载成功
     */
    public abstract boolean init();

    /**
     * @return hook是否启用
     */
    public boolean isEnable() {
        return ConfigManager.isEnable(this);
    }

    /**
     * 设置hook是否启用
     *
     * @param enable 启用(true)/禁用(false)
     */
    public void setEnable(boolean enable) {
        ConfigManager.setEnable(this, enable);
    }

    /**
     * @return Hook标签
     */
    public String getLabel() {
        return this.name;
    }

    /**
     * Hook的设置点击事件
     */
    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * 设置Hook的设置点击事件
     */
    public View.OnClickListener getClickListener() {
        return this.clickListener;
    }

    /**
     * @return Hook名称
     */
    public String getName() {
        return this.label;
    }

    /**
     * 设置Hook的介绍
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Hook的介绍
     */
    public String getDesc() {
        return this.desc;
    }
}
