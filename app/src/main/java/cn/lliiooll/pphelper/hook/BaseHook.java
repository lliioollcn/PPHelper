package cn.lliiooll.pphelper.hook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.PConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseHook {

    private final String name;
    private final String label;

    public BaseHook(String name, String label) {
        this.name = name;
        this.label = label;
    }

    /**
     * 初始化操作
     *
     * @return 初始化是否成功
     */
    public abstract boolean init();

    /**
     * 反混淆操作
     *
     * @param finds 找到的类
     */
    public void doObf(Map<String, List<String>> finds) {

    }

    /**
     * @return 反混淆条件
     */
    public Map<String, List<String>> obf() {
        return new HashMap<>();
    }

    /**
     * @return 是否需要进行反混淆操作
     */
    public boolean needObf() {
        return false;
    }

    public boolean isEnable() {
        return PConfig.isEnable(getLabel(), true);
    }

    public void setEnable(boolean enable) {
        PConfig.setEnable(getLabel(), enable);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_normal, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        Switch sw = root.findViewById(R.id.set_nor_switch);
        sw.setChecked(isEnable());
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> setEnable(isChecked));
        return root;
    }
}
