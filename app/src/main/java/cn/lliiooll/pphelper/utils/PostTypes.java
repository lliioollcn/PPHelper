package cn.lliiooll.pphelper.utils;

import cn.lliiooll.pphelper.config.ConfigManager;

import java.util.*;

public class PostTypes {

    public static final Map<String, Integer> types = new HashMap<String, Integer>() {{
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
        put("图文帖", 1);
        put("Unknown:27", 27);
    }};

    private static final String label = "PP_POST_HIDE";


    public static boolean isHide(Integer type) {
        Set<String> hides = ConfigManager.getSet(label);
        return hides != null && hides.contains(String.valueOf(type));
    }


    public static void hide(Integer type, boolean isChecked) {
        Set<String> hides = ConfigManager.getSet(label);
        if (hides == null) hides = new HashSet<>();
        if (isChecked) {
            hides.add(String.valueOf(type));
        } else {
            hides.remove(String.valueOf(type));
        }
        ConfigManager.setSet(label, hides);
    }
}
