package cn.lliiooll.pphelper.utils;

import android.view.View;
import android.view.ViewGroup;
import cn.lliiooll.pphelper.config.ConfigManager;
import de.robv.android.xposed.XposedHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HideList {

    public static final Map<String, Integer> postTypes = new HashMap<String, Integer>() {{
        put("HolderStoryTips", -100);
        put("RecommendTopicHolder", 16);
        put("PostViewHolderLiveCard", 211);
        put("LiveCardHolderRoot", 213);
        put("PostGameCardSingleVH", 216);
        put("PostVoiceHolderCard", 212);
        put("PostVoiceRoomHolderCard", 214);
        put("PostCurrencyHolderCard", 215);
        put("TopicCardHolder", 17);
        put("HotEventActivityCard", 299);
        put("VillageResourceBitsHolder", -101);
        put("视频", 11);
        put("Unknown:12", 12);
        put("Unknown:14", 14);
        put("图文帖", 1);
        put("Unknown:27", 27);
    }};
    public static final Map<String, String> myTypes = new HashMap<String, String>() {{
        put("功能入口> 共建家园", "my_tab_star_review");
        put("功能入口> 皮皮公益", "my_tab_public_welfare");
        put("功能入口> 我的背包", "my_tab_prize_package");
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
        Set<String> hides = ConfigManager.getSet(label_post);
        return hides != null && hides.contains(String.valueOf(type));
    }


    public static void hidePost(Integer type, boolean isChecked) {
        Set<String> hides = ConfigManager.getSet(label_post);
        if (hides == null) hides = new HashSet<>();
        if (isChecked) {
            hides.add(String.valueOf(type));
        } else {
            hides.remove(String.valueOf(type));
        }
        ConfigManager.setSet(label_post, hides);
    }

    public static boolean isHideMy(String type) {
        Set<String> hides = ConfigManager.getSet(label_my);
        return hides != null && hides.contains(String.valueOf(type));
    }

    public static void hideMy(String type, boolean isChecked) {
        Set<String> hides = ConfigManager.getSet(label_my);
        if (hides == null) hides = new HashSet<>();
        if (isChecked) {
            hides.add(String.valueOf(type));
        } else {
            hides.remove(String.valueOf(type));
        }
        ConfigManager.setSet(label_my, hides);

    }
}
