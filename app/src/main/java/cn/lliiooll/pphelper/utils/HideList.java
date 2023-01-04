package cn.lliiooll.pphelper.utils;

import java.util.*;

public class HideList {

    public static final Map<String, Integer> postTypes = new HashMap<String, Integer>() {{

    }};
    public static final Map<String, String> myTypes = new TreeMap<String, String>() {{
        put("功能入口> 共建家园", "my_tab_star_review");
        put("功能入口> 皮皮公益", "my_tab_public_welfare");
        put("功能入口> 我的背包", "my_tab_prize_package");
        put("功能入口> 皮皮短剧", "my_tab_skit_layout");
        put("功能入口> 免广告", "avoid_ad");
        put("功能入口> 皮皮直播", "my_tab_live");
        put("功能入口> 游戏中心", "gameCenterLayout");
        put("轮播台", "bannerView");
        put("功能入口> 每日抽奖", "my_tab_lottery_layout");
        put("功能入口> 帮助反馈", "my_tab_help");
        put("功能入口> 官方认证", "my_tab_apply_kol");
        put("功能入口> 吹水日记", "my_tab_tree_new_bee");
        put("功能入口> 锦旗墙", "my_tab_pennants_layout");
        put("个人信息> 全部", "headerView");
        put("个人数据> 全部", "myTabDataLayout");
        put("个人数据> 发帖", "!my_data_post");
        put("个人数据> 评论", "!my_data_comment");
        put("个人数据> 点赞", "!my_data_like");
        put("个人数据> 收藏", "!my_data_collect");
        put("个人数据> 浏览历史", "!my_data_history");
        put("个人数据> 插眼", "!my_data_mark_eye");
        put("个人数据> 下载", "!my_data_download");
    }};

    private static final String label_post = "PP_POST_HIDE";
    private static final String label_my = "PP_MY_HIDE";


    public static boolean isHidePost(Integer type) {
        Set<String> hides = PConfig.getSet(label_post);
        return hides != null && hides.contains(String.valueOf(type));
    }


    public static void hidePost(Integer type, boolean isChecked) {
        Set<String> hides = PConfig.getSet(label_post);
        if (hides == null) hides = new HashSet<>();
        if (isChecked) {
            hides.add(String.valueOf(type));
        } else {
            hides.remove(String.valueOf(type));
        }
        PConfig.setSet(label_post, hides);
    }

    public static boolean isHideMy(String type) {
        Set<String> hides = PConfig.getSet(label_my);
        return hides != null && hides.contains(String.valueOf(type));
    }

    public static void hideMy(String type, boolean isChecked) {
        Set<String> hides = PConfig.getSet(label_my);
        if (hides == null) hides = new HashSet<>();
        if (isChecked) {
            hides.add(String.valueOf(type));
        } else {
            hides.remove(String.valueOf(type));
        }
        PConfig.setSet(label_my, hides);

    }
}
