package cn.lliiooll.pphelper.hook;

import cn.lliiooll.pphelper.config.ConfigManager;

public abstract class BaseHook {

    private final String name;

    public BaseHook(String name) {
        this.name = name;
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
     * @return Hook名称
     */
    public String getName() {
        return this.name;
    }
}
