package cn.lliiooll.pphelper.hook.zuiyouLite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.zuiyouLite.HidePostActivity;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.*;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RemovePost extends BaseHook {

    public static RemovePost INSTANCE = new RemovePost();

    public RemovePost() {
        super("移除指定类型帖子", "removePost");
    }

    @Override
    public boolean init() {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int type = (int) param.args[1];
                if (HideList.isHidePost(type)) {
                    param.args[1] = 666999;
                    PLog.d("屏蔽的帖子类型: " + type);
                } else {
                    PLog.d("未屏蔽的帖子类型: " + type);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int type = (int) param.args[1];
                if (type == 666999) {
                    View view = (View) XposedHelpers.getObjectField(param.getResult(), "itemView");
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                    view.setLayoutParams(params);
                    view.setPadding(0, 0, 0, 0);
                    view.setVisibility(View.GONE);
                    view.setOnClickListener(null);
                }
            }
        };

        List<String> clazz = new ArrayList<>();
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendTopicAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.recommend.partition.PartitionAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.postlist.BasePostListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.follow.myfollow.MyTopicPostAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.follow.myfollow.MyTopicAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.recommend.IndexListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.me.post.MyEyeListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.me.post.MyPostListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.me.post.LikedListPagerAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.me.post.MyCollectionPostListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.collection.CollectionAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.post.PostListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.follower.FollowerListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.fans.FansListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.comment.CommentListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.block.BlockPartListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.block.BlockTopicListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.user.block.BlockUserListAdapter");
        clazz.add("cn.xiaochuankeji.zuiyouLite.ui.home.FollowPostAdapter");
        clazz.forEach(c -> {
            Class<?> tc = HybridClassLoader.load(c);
            for (Method m : tc.getDeclaredMethods()) {
                if (m.getName().equalsIgnoreCase("onCreateViewHolder") && m.getParameterTypes().length == 2 && m.getParameterTypes()[1] == int.class) {
                    XposedBridge.hookMethod(m, hook);
                }
            }
        });

        Class<?> clazz1 = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostViewHolderSingleVideo");
        for (Method m : clazz1.getDeclaredMethods()) {
            if (m.getParameterTypes().length > 0 && m.getParameterTypes()[0].getName().contains("PostDataBean")) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("方法: " + m.getName() + " 被调用");
                        if (HideList.isHidePost(0x3c)){
                            Object postBean = param.args[0];
                            File dir = AppUtils.getHostAppInstance().getExternalFilesDir("helperJson");
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            Object activity = XposedHelpers.getObjectField(postBean, "activityBean");
                            if (activity != null) {
                                PLog.d("是广告");
                                //TODO: 广告移除
                                View view = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                params.width = 0;
                                params.height = 0;
                                view.setLayoutParams(params);
                                view.setPadding(0, 0, 0, 0);
                                view.setVisibility(View.GONE);
                                view.setOnClickListener(null);
                            }
                        }
                        /*
                        IOUtils.write(new GsonBuilder().serializeNulls().create().toJson(IOUtils.toMap(postBean)),
                                new File(dir, XposedHelpers.getLongField(postBean, "postId") + ".json"));
                        Thread.sleep(10L);

                         */

                    }
                });

            }
        }
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_bar, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        LinearLayout content = root.findViewById(R.id.pp_setting_bar_root);
        content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, HidePostActivity.class);
            ctx.startActivity(intent);
        });
        return root;
    }
}
